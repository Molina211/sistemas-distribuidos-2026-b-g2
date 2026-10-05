package co.edu.corhuila.abbr.workflow.app;

import co.edu.corhuila.abbr.workflow.adapter.in.http.AuthFilter;
import co.edu.corhuila.abbr.workflow.adapter.in.http.CorrelationFilter;
import co.edu.corhuila.abbr.workflow.adapter.in.http.Rs256Verifier;
import co.edu.corhuila.abbr.workflow.adapter.out.participants.HttpParticipants;
import co.edu.corhuila.abbr.workflow.adapter.out.state.InMemorySagaStore;
import co.edu.corhuila.abbr.workflow.application.usecase.RunPlaceOrderSaga;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/** Composition root of the workflow. Every limit — timeouts, retries — is explicit here. */
@SpringBootApplication(scanBasePackages = "co.edu.corhuila.abbr.workflow")
public class WorkflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowApplication.class, args);
    }

    @Bean
    RunPlaceOrderSaga placeOrderSaga(@Value("${workflow.orders-api-url}") String orders,
                                     @Value("${workflow.inventory-api-url}") String inventory,
                                     @Value("${workflow.payments-api-url}") String payments,
                                     @Value("${workflow.service-token}") String token,
                                     @Value("${workflow.http.timeout-seconds}") long timeoutSeconds,
                                     @Value("${workflow.http.attempts}") int attempts,
                                     @Value("${workflow.http.backoff-ms}") long backoffMs) {
        if (token.isBlank()) {
            throw new IllegalStateException("SERVICE_TOKEN is required: every participant validates the workflow's calls");
        }
        var p = new HttpParticipants(orders, inventory, payments, token, Duration.ofSeconds(timeoutSeconds), attempts,
                Duration.ofMillis(backoffMs));
        return new RunPlaceOrderSaga(p, p, p, new InMemorySagaStore(), new InMemorySagaStore.UuidGenerator());
    }

    /** JWT_PUBLIC_KEY (one line with literal \n escapes is accepted) or JWT_PUBLIC_KEY_FILE. */
    @Bean
    Rs256Verifier tokenVerifier(@Value("${JWT_PUBLIC_KEY:}") String pem,
                                @Value("${JWT_PUBLIC_KEY_FILE:}") String file) throws IOException {
        String key = !pem.isBlank() ? pem.replace("\\n", "\n")
                : !file.isBlank() ? Files.readString(Path.of(file)) : "";
        return new Rs256Verifier(key);
    }

    @Bean
    FilterRegistrationBean<CorrelationFilter> correlationFilter() {
        FilterRegistrationBean<CorrelationFilter> bean = new FilterRegistrationBean<>(new CorrelationFilter());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    FilterRegistrationBean<AuthFilter> authFilter(Rs256Verifier verifier, ObjectMapper json) {
        FilterRegistrationBean<AuthFilter> bean = new FilterRegistrationBean<>(new AuthFilter(verifier, json));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return bean;
    }
}
