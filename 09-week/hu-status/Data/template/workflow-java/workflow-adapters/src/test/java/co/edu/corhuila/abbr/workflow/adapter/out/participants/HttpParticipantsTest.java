package co.edu.corhuila.abbr.workflow.adapter.out.participants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.edu.corhuila.abbr.workflow.adapter.Correlation;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.MDC;

class HttpParticipantsTest {

    /** A throw-away HTTP server that answers every request with status, recording the headers. */
    static HttpServer serve(int status, List<Headers> seen) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            exchange.getRequestBody().readAllBytes();
            seen.add(exchange.getRequestHeaders());
            exchange.sendResponseHeaders(status, -1);
            exchange.close();
        });
        server.start();
        return server;
    }

    static HttpParticipants participants(HttpServer server) {
        String url = "http://127.0.0.1:" + server.getAddress().getPort();
        return new HttpParticipants(url, url, url, "svc-token", Duration.ofSeconds(2), 3, Duration.ofMillis(1));
    }

    @Test
    void callsCarryTokenCorrelationAndIdempotencyKey() throws IOException {
        List<Headers> seen = new ArrayList<>();
        HttpServer server = serve(201, seen);
        MDC.put(Correlation.MDC_KEY, "req-1");
        try {
            participants(server).charge("s1:charge-payment", "o1", 100);
        } finally {
            MDC.clear();
            server.stop(0);
        }
        assertEquals("Bearer svc-token", seen.get(0).getFirst("Authorization"));
        assertEquals("req-1", seen.get(0).getFirst("X-Correlation-Id"));
        assertEquals("s1:charge-payment", seen.get(0).getFirst("Idempotency-Key"));
    }

    @ParameterizedTest
    @CsvSource({"503, 3", "429, 3", "422, 1", "401, 1"})
    void retriesServerErrorsButNotClientErrors(int status, int calls) throws IOException {
        List<Headers> seen = new ArrayList<>();
        HttpServer server = serve(status, seen);
        try {
            assertThrows(IllegalStateException.class, () -> participants(server).confirm("o1"));
        } finally {
            server.stop(0);
        }
        assertEquals(calls, seen.size());
    }
}
