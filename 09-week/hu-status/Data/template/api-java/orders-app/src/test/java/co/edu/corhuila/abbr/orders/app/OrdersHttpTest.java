package co.edu.corhuila.abbr.orders.app;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.time.Instant;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OrdersHttpTest {

    static final String CUSTOMER = "11111111-1111-4111-8111-111111111111";
    static final KeyPair KEYS = generate();

    @Autowired
    MockMvc mvc;

    @DynamicPropertySource
    static void publicKey(DynamicPropertyRegistry registry) {
        String pem = "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getMimeEncoder().encodeToString(KEYS.getPublic().getEncoded())
                + "\n-----END PUBLIC KEY-----\n";
        registry.add("JWT_PUBLIC_KEY", () -> pem);
    }

    @ParameterizedTest
    @ValueSource(strings = {"missing", "alg-none", "expired", "tampered"})
    void rejectsInvalidCredentials(String kind) throws Exception {
        var request = get("/api/v1/orders");
        switch (kind) {
            case "alg-none" -> request.header("Authorization", "Bearer " + token("none", 3600));
            case "expired" -> request.header("Authorization", "Bearer " + token("RS256", -120));
            case "tampered" -> request.header("Authorization", "Bearer " + token("RS256", 3600) + "x");
            default -> { }
        }
        mvc.perform(request).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.error", is("UNAUTHORIZED")));
    }

    @Test
    void errorEnvelopeCarriesTheCorrelationId() throws Exception {
        mvc.perform(get("/api/v1/orders/not-a-uuid").header("Authorization", bearer())
                        .header("X-Correlation-Id", "trace-abc-123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.traceId", is("trace-abc-123")))
                .andExpect(header().string("X-Correlation-Id", "trace-abc-123"));
    }

    @Test
    void validationNamesEveryInvalidField() throws Exception {
        mvc.perform(post("/api/v1/orders").header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"customerId\":\"c-1\",\"totalCents\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details", hasSize(3)));
    }

    @Test
    void placeOrderIsIdempotentOverHttp() throws Exception {
        String body = "{\"customerId\":\"" + CUSTOMER + "\",\"totalCents\":1500}";
        String first = mvc.perform(post("/api/v1/orders").header("Authorization", bearer())
                        .header("Idempotency-Key", "retry-key-0001").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        mvc.perform(post("/api/v1/orders").header("Authorization", bearer())
                        .header("Idempotency-Key", "retry-key-0001").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(first.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1"))));
    }

    @Test
    void listRejectsAnUnboundedLimit() throws Exception {
        mvc.perform(get("/api/v1/orders?limit=500").header("Authorization", bearer()))
                .andExpect(status().isBadRequest());
    }

    private static String bearer() throws Exception {
        return "Bearer " + token("RS256", 3600);
    }

    private static String token(String alg, long expiresIn) throws Exception {
        Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        String header = enc.encodeToString(("{\"alg\":\"" + alg + "\",\"typ\":\"JWT\"}").getBytes(StandardCharsets.UTF_8));
        String payload = enc.encodeToString(("{\"sub\":\"user-1\",\"exp\":"
                + (Instant.now().getEpochSecond() + expiresIn) + "}").getBytes(StandardCharsets.UTF_8));
        Signature rsa = Signature.getInstance("SHA256withRSA");
        rsa.initSign(KEYS.getPrivate());
        rsa.update((header + "." + payload).getBytes(StandardCharsets.US_ASCII));
        return header + "." + payload + "." + enc.encodeToString(rsa.sign());
    }

    private static KeyPair generate() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
