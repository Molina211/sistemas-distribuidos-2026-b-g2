package co.edu.corhuila.abbr.worker.adapter.out.orders;

import co.edu.corhuila.abbr.worker.adapter.Correlation;
import co.edu.corhuila.abbr.worker.application.port.out.OrdersApi;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.MDC;

/** Outbound adapter to <abbr>-orders-api: its HTTP contract, never its database. */
public class OrdersHttpClient implements OrdersApi {

    private final String base;
    private final String token; // service token: the API validates it like any other
    private final Duration timeout;
    private final int attempts;
    private final Duration backoff;
    private final HttpClient http;
    private final ObjectMapper json = new ObjectMapper();

    /** timeout bounds the connection and every request; attempts bounds the retries. */
    public OrdersHttpClient(String baseUrl, String token, Duration timeout, int attempts, Duration backoff) {
        this.base = baseUrl;
        this.token = token;
        this.timeout = timeout;
        this.attempts = attempts;
        this.backoff = backoff;
        this.http = HttpClient.newBuilder().connectTimeout(timeout).build();
    }

    @Override
    public List<PendingOrder> listPendingCreatedBefore(Instant before, int limit) {
        String q = "?status=PENDING&limit=" + limit + "&createdBefore="
                + URLEncoder.encode(before.truncatedTo(ChronoUnit.SECONDS).toString(), StandardCharsets.UTF_8);
        List<PendingOrder> out = new ArrayList<>();
        for (JsonNode n : call("GET", "/api/v1/orders" + q).path("data")) {
            out.add(new PendingOrder(n.get("id").asText(), Instant.parse(n.get("createdAt").asText())));
        }
        return out;
    }

    /** Safe to retry: the orders API makes it idempotent. */
    @Override
    public void cancel(String orderId) {
        call("POST", "/api/v1/orders/" + URLEncoder.encode(orderId, StandardCharsets.UTF_8) + "/cancel");
    }

    /**
     * Bounded retry with exponential backoff and full jitter, only on errors a retry
     * can fix — network errors, 429 and 5xx. A 4xx is the caller's mistake: thrown at once.
     */
    private JsonNode call(String method, String path) {
        RuntimeException last = null;
        for (int attempt = 0; attempt < attempts; attempt++) {
            if (attempt > 0) {
                // Full jitter: many workers failing together do not retry together.
                sleep(ThreadLocalRandom.current().nextLong(backoff.toMillis() << attempt));
            }
            HttpRequest.Builder req = HttpRequest.newBuilder(URI.create(base + path))
                    .timeout(timeout)
                    .header("Authorization", "Bearer " + token)
                    .method(method, HttpRequest.BodyPublishers.noBody());
            String correlationId = MDC.get(Correlation.MDC_KEY);
            if (correlationId != null) {
                req.header("X-Correlation-Id", correlationId);
            }
            try {
                HttpResponse<String> resp = http.send(req.build(), HttpResponse.BodyHandlers.ofString());
                int status = resp.statusCode();
                if (status < 400) {
                    return resp.body().isEmpty() ? json.createObjectNode() : json.readTree(resp.body());
                }
                JsonNode envelope = readQuietly(resp.body());
                var error = new IllegalStateException("%s %s: status %d %s (traceId %s)".formatted(method, path, status,
                        envelope.path("error").asText(), envelope.path("traceId").asText()));
                if (status != 429 && status < 500) {
                    throw error;
                }
                last = error;
            } catch (IOException e) {
                last = new IllegalStateException(method + " " + path + ": " + e.getMessage(), e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
        }
        throw new IllegalStateException("after " + attempts + " attempts", last);
    }

    private JsonNode readQuietly(String body) {
        try {
            return json.readTree(body);
        } catch (IOException e) {
            return json.createObjectNode();
        }
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
