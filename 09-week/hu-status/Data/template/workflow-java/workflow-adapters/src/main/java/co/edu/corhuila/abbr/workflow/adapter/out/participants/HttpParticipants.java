package co.edu.corhuila.abbr.workflow.adapter.out.participants;

import co.edu.corhuila.abbr.workflow.adapter.Correlation;
import co.edu.corhuila.abbr.workflow.application.port.out.Participants.Inventory;
import co.edu.corhuila.abbr.workflow.application.port.out.Participants.Orders;
import co.edu.corhuila.abbr.workflow.application.port.out.Participants.Payments;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.MDC;

/**
 * Outbound adapter to the domain APIs taking part in the saga. Each call goes through
 * the domain's public contract, with the workflow's own service token and the
 * correlation id of the request.
 */
public class HttpParticipants implements Inventory, Payments, Orders {

    private final String orders;
    private final String inventory;
    private final String payments;
    private final String token;
    private final Duration timeout;
    private final int attempts;
    private final Duration backoff;
    private final HttpClient http;
    private final ObjectMapper json = new ObjectMapper();

    /** timeout bounds the connection and every request; attempts bounds the retries. */
    public HttpParticipants(String ordersUrl, String inventoryUrl, String paymentsUrl, String token,
                            Duration timeout, int attempts, Duration backoff) {
        this.orders = ordersUrl;
        this.inventory = inventoryUrl;
        this.payments = paymentsUrl;
        this.token = token;
        this.timeout = timeout;
        this.attempts = attempts;
        this.backoff = backoff;
        this.http = HttpClient.newBuilder().connectTimeout(timeout).build();
    }

    public void reserve(String key, String orderId, String sku, int qty) {
        post(inventory + "/api/v1/reservations", key, Map.of("orderId", orderId, "sku", sku, "quantity", qty));
    }
    public void release(String orderId) { post(inventory + "/api/v1/reservations/" + q(orderId) + "/release", null, null); }
    public void charge(String key, String orderId, long amount) {
        post(payments + "/api/v1/charges", key, Map.of("orderId", orderId, "amountCents", amount));
    }
    public void refund(String orderId) { post(payments + "/api/v1/charges/" + q(orderId) + "/refund", null, null); }
    public void confirm(String orderId) { post(orders + "/api/v1/orders/" + q(orderId) + "/confirm", null, null); }
    public void cancel(String orderId) { post(orders + "/api/v1/orders/" + q(orderId) + "/cancel", null, null); }

    /**
     * Bounded retry with exponential backoff and full jitter, only on errors a retry can
     * fix — network errors, 429 and 5xx. Retrying is safe because every do carries an
     * idempotency key and every undo is idempotent.
     */
    private void post(String url, String key, Object body) {
        String payload;
        try {
            payload = body == null ? "" : json.writeValueAsString(body);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        RuntimeException last = null;
        for (int attempt = 0; attempt < attempts; attempt++) {
            if (attempt > 0) {
                sleep(ThreadLocalRandom.current().nextLong(backoff.toMillis() << attempt));
            }
            HttpRequest.Builder req = HttpRequest.newBuilder(URI.create(url)).timeout(timeout)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(payload));
            if (key != null) {
                req.header("Idempotency-Key", key);
            }
            String correlationId = MDC.get(Correlation.MDC_KEY);
            if (correlationId != null) {
                req.header("X-Correlation-Id", correlationId);
            }
            try {
                HttpResponse<String> resp = http.send(req.build(), HttpResponse.BodyHandlers.ofString());
                int status = resp.statusCode();
                if (status < 300) {
                    return;
                }
                JsonNode envelope = readQuietly(resp.body());
                var error = new IllegalStateException("POST %s: status %d %s (traceId %s)".formatted(url, status,
                        envelope.path("error").asText(), envelope.path("traceId").asText()));
                if (status != 429 && status < 500) {
                    throw error;
                }
                last = error;
            } catch (IOException e) {
                last = new IllegalStateException("POST " + url + ": " + e.getMessage(), e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
        }
        throw new IllegalStateException("after " + attempts + " attempts", last);
    }

    private JsonNode readQuietly(String body) {
        try {
            return json.readTree(body.isEmpty() ? "{}" : body);
        } catch (IOException e) {
            return json.createObjectNode();
        }
    }

    private static String q(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
