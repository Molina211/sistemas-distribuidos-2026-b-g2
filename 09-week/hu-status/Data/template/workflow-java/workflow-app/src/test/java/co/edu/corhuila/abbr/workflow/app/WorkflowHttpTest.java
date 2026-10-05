package co.edu.corhuila.abbr.workflow.app;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.time.Instant;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * The real application. The participants point to a port where nothing listens,
 * so every saga fails at its first step and is compensated.
 */
@SpringBootTest(properties = {"SERVICE_TOKEN=svc", "INVENTORY_API_URL=http://127.0.0.1:9", "HTTP_ATTEMPTS=1"})
@AutoConfigureMockMvc
class WorkflowHttpTest {

    static final KeyPair KEYS = generate();
    static final String VALID = """
            {"orderId":"11111111-1111-4111-8111-111111111111","sku":"A-1","quantity":2,"amountCents":1500}""";

    @Autowired
    MockMvc mvc;

    @DynamicPropertySource
    static void publicKey(DynamicPropertyRegistry registry) {
        String pem = "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getMimeEncoder().encodeToString(KEYS.getPublic().getEncoded()) + "\n-----END PUBLIC KEY-----\n";
        registry.add("JWT_PUBLIC_KEY", () -> pem);
    }

    @Test
    void theWorkflowFollowsTheSameContractAsADomainApi() throws Exception {
        mvc.perform(post("/api/v1/sagas/place-order").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.error", is("UNAUTHORIZED")));

        mvc.perform(post("/api/v1/sagas/place-order").header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"orderId\":\"x\",\"quantity\":0}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.details", hasSize(5)));

        mvc.perform(post("/api/v1/sagas/place-order").header("Authorization", bearer())
                        .header("Idempotency-Key", "req-00000001").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("COMPENSATED")))
                .andExpect(jsonPath("$.failedStep", is("reserve-stock")))
                .andExpect(jsonPath("$.error").doesNotExist());

        mvc.perform(post("/api/v1/sagas/place-order").header("Authorization", bearer())
                        .header("Idempotency-Key", "req-00000001").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isOk());

        mvc.perform(get("/api/v1/sagas/nope").header("Authorization", bearer()))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.error", is("NOT_FOUND")));
    }

    private static String bearer() throws Exception {
        Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        String header = enc.encodeToString("{\"alg\":\"RS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = enc.encodeToString(("{\"sub\":\"user-1\",\"exp\":" + (Instant.now().getEpochSecond() + 3600) + "}")
                .getBytes(StandardCharsets.UTF_8));
        Signature rsa = Signature.getInstance("SHA256withRSA");
        rsa.initSign(KEYS.getPrivate());
        rsa.update((header + "." + payload).getBytes(StandardCharsets.US_ASCII));
        return "Bearer " + header + "." + payload + "." + enc.encodeToString(rsa.sign());
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
