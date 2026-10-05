package co.edu.corhuila.abbr.worker.adapter.out.orders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.edu.corhuila.abbr.worker.adapter.Correlation;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class OrdersHttpClientTest {

    record Seen(String path, String auth, String correlation) { }

    /** A throw-away HTTP server that answers every request with status and body. */
    static HttpServer serve(int status, String body, List<Seen> seen) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            seen.add(new Seen(exchange.getRequestURI().toString(), exchange.getRequestHeaders().getFirst("Authorization"),
                    exchange.getRequestHeaders().getFirst("X-Correlation-Id")));
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        return server;
    }

    static OrdersHttpClient client(HttpServer server) {
        return new OrdersHttpClient("http://127.0.0.1:" + server.getAddress().getPort(), "svc-token",
                Duration.ofSeconds(2), 3, Duration.ofMillis(1));
    }

    @Test
    void sendsCredentialsAndCorrelationAndReadsThePage() throws IOException {
        List<Seen> seen = new ArrayList<>();
        HttpServer server = serve(200, """
                {"data":[{"id":"a","createdAt":"2026-09-01T10:00:00Z"}],"meta":{"page":1,"limit":50,"total":1,"totalPages":1}}
                """, seen);
        MDC.put(Correlation.MDC_KEY, "run-1");
        try {
            var orders = client(server).listPendingCreatedBefore(Instant.now(), 50);
            assertEquals(List.of("a"), orders.stream().map(o -> o.id()).toList());
        } finally {
            MDC.clear();
            server.stop(0);
        }
        assertEquals("Bearer svc-token", seen.get(0).auth());
        assertEquals("run-1", seen.get(0).correlation());
        assertEquals(true, seen.get(0).path().contains("limit=50"));
    }

    @ParameterizedTest
    @CsvSource({"503, 3", "429, 3", "422, 1", "401, 1"})
    void retriesServerErrorsButNotClientErrors(int status, int calls) throws IOException {
        List<Seen> seen = new ArrayList<>();
        HttpServer server = serve(status, "{\"error\":\"X\",\"traceId\":\"t\"}", seen);
        try {
            assertThrows(IllegalStateException.class, () -> client(server).cancel("a"));
        } finally {
            server.stop(0);
        }
        assertEquals(calls, seen.size());
    }
}
