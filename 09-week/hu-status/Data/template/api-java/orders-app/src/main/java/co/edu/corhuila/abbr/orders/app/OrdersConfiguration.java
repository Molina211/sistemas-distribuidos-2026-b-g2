package co.edu.corhuila.abbr.orders.app;

import co.edu.corhuila.abbr.orders.adapter.in.http.AuthFilter;
import co.edu.corhuila.abbr.orders.adapter.in.http.CorrelationFilter;
import co.edu.corhuila.abbr.orders.adapter.in.http.Rs256Verifier;
import co.edu.corhuila.abbr.orders.adapter.out.persistence.InMemoryOrderRepository;
import co.edu.corhuila.abbr.orders.adapter.out.persistence.JdbcOrderRepository;
import co.edu.corhuila.abbr.orders.adapter.out.persistence.UuidGenerator;
import co.edu.corhuila.abbr.orders.application.port.out.OrderRepository;
import co.edu.corhuila.abbr.orders.application.usecase.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Composition root: the only place that knows every concrete type. The pool and
 * its limits are built here explicitly, so they are read in code review instead
 * of hidden in defaults.
 */
@Configuration
public class OrdersConfiguration {

    @Bean
    OrderRepository orderRepository(@Value("${orders.database.url:}") String url,
                                    @Value("${orders.database.user:}") String user,
                                    @Value("${orders.database.password:}") String password,
                                    @Value("${orders.database.pool-max:10}") int poolMax,
                                    @Value("${orders.database.statement-timeout-ms:5000}") int statementTimeoutMs) {
        if (url.isBlank()) {
            return new InMemoryOrderRepository();
        }
        HikariConfig pool = new HikariConfig();
        pool.setJdbcUrl(url);
        pool.setUsername(user);
        pool.setPassword(password);
        pool.setMaximumPoolSize(poolMax);                            // sized against max_connections
        pool.setConnectionTimeout(Duration.ofSeconds(5).toMillis()); // wait for a free connection
        pool.setMaxLifetime(Duration.ofMinutes(30).toMillis());
        pool.setConnectionInitSql("SET statement_timeout = " + statementTimeoutMs);
        HikariDataSource dataSource = new HikariDataSource(pool);
        return new JdbcOrderRepository(new JdbcTemplate(dataSource),
                new TransactionTemplate(new DataSourceTransactionManager(dataSource)));
    }

    @Bean
    OrderService orderService(OrderRepository repository) {
        return new OrderService(repository, new UuidGenerator(), Clock.systemUTC());
    }

    /**
     * JWT_PUBLIC_KEY (the PEM itself; a one-line value with literal \n escapes, as
     * a .env file holds it, is accepted) or JWT_PUBLIC_KEY_FILE.
     */
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
