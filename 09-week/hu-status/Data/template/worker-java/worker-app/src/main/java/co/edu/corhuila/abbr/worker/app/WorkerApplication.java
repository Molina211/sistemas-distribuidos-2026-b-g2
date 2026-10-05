package co.edu.corhuila.abbr.worker.app;

import co.edu.corhuila.abbr.worker.adapter.in.scheduler.JobRunner;
import co.edu.corhuila.abbr.worker.adapter.out.orders.OrdersHttpClient;
import co.edu.corhuila.abbr.worker.application.usecase.ExpireStaleOrders;
import java.time.Clock;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Composition root of the worker. Every limit — timeouts, retries, batch size — is explicit here. */
@SpringBootApplication
@EnableScheduling
public class WorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkerApplication.class, args);
    }

    @Bean
    JobRunner expireStaleOrders(@Value("${worker.orders-api-url}") String url,
                                @Value("${worker.service-token}") String token,
                                @Value("${worker.pending-ttl-minutes}") long ttlMinutes,
                                @Value("${worker.batch-size}") int batchSize,
                                @Value("${worker.run-timeout-seconds}") long runTimeoutSeconds,
                                @Value("${worker.http.timeout-seconds}") long httpTimeoutSeconds,
                                @Value("${worker.http.attempts}") int attempts,
                                @Value("${worker.http.backoff-ms}") long backoffMs) {
        if (token.isBlank()) {
            throw new IllegalStateException("SERVICE_TOKEN is required: the orders API validates every call");
        }
        var client = new OrdersHttpClient(url, token, Duration.ofSeconds(httpTimeoutSeconds), attempts,
                Duration.ofMillis(backoffMs));
        return new JobRunner(new ExpireStaleOrders(client, Clock.systemUTC(), Duration.ofMinutes(ttlMinutes), batchSize),
                Duration.ofSeconds(runTimeoutSeconds));
    }
}
