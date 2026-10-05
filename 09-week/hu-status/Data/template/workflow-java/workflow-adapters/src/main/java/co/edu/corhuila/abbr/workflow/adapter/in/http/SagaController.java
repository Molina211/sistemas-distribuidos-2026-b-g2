package co.edu.corhuila.abbr.workflow.adapter.in.http;

import co.edu.corhuila.abbr.workflow.adapter.in.http.ApiError.FieldError;
import co.edu.corhuila.abbr.workflow.adapter.in.http.ApiError.ValidationException;
import co.edu.corhuila.abbr.workflow.application.port.in.GetSaga;
import co.edu.corhuila.abbr.workflow.application.port.in.StartPlaceOrderSaga;
import co.edu.corhuila.abbr.workflow.application.port.in.StartPlaceOrderSaga.PlaceOrderRequest;
import co.edu.corhuila.abbr.workflow.domain.saga.SagaInstance;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Inbound adapter of the workflow: starts sagas and reports their state. Same rules
 * as any domain API: validated token, validated input, the shared error envelope.
 */
@RestController
@RequestMapping("/api/v1/sagas")
public class SagaController {

    private static final Logger LOG = LoggerFactory.getLogger(SagaController.class);
    private static final Pattern UUID = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    public record StartRequest(String orderId, String sku, BigDecimal quantity, BigDecimal amountCents) { }

    /**
     * The public contract. The detail of a failure stays in the saga state and the
     * logs: it names internal hosts, and a client has no use for it.
     */
    public record SagaResponse(String id, String orderId, String status, List<String> completedSteps,
                               @JsonInclude(JsonInclude.Include.NON_EMPTY) String failedStep) {
        static SagaResponse of(SagaInstance s) {
            return new SagaResponse(s.id(), s.orderId(), s.status().name(), s.completed(), s.failedStep());
        }
    }

    private final StartPlaceOrderSaga start;
    private final GetSaga get;

    public SagaController(StartPlaceOrderSaga start, GetSaga get) {
        this.start = start;
        this.get = get;
    }

    @PostMapping("/place-order")
    public ResponseEntity<SagaResponse> placeOrder(@RequestBody StartRequest body,
                                                   @RequestHeader(name = "Idempotency-Key", required = false) String key) {
        List<FieldError> problems = new ArrayList<>();
        if (body.orderId() == null || !UUID.matcher(body.orderId()).matches()) {
            problems.add(new FieldError("orderId", "must be a UUID"));
        }
        if (body.sku() == null || body.sku().isEmpty() || body.sku().length() > 40) {
            problems.add(new FieldError("sku", "must have 1 to 40 characters"));
        }
        if (!positiveInt(body.quantity())) {
            problems.add(new FieldError("quantity", "must be a positive integer"));
        }
        if (!positiveInt(body.amountCents())) {
            problems.add(new FieldError("amountCents", "must be a positive integer (minor units)"));
        }
        if (key == null || key.length() < 8 || key.length() > 128) {
            problems.add(new FieldError("Idempotency-Key", "header required, 8 to 128 characters"));
        }
        if (!problems.isEmpty()) {
            throw new ValidationException("the request has invalid fields", problems);
        }
        var result = start.start(new PlaceOrderRequest(body.orderId(), body.sku(), body.quantity().intValueExact(),
                body.amountCents().longValueExact(), key));
        SagaInstance saga = result.saga();
        if (result.created() && !saga.error().isEmpty()) {
            LOG.warn("saga did not complete sagaId={} status={} error={}", saga.id(), saga.status(), saga.error());
        }
        return ResponseEntity.status(result.created() ? HttpStatus.CREATED : HttpStatus.OK) // 200: a retry
                .header("Location", "/api/v1/sagas/" + saga.id())
                .body(SagaResponse.of(saga));
    }

    @GetMapping("/{id}")
    public SagaResponse get(@PathVariable String id) {
        return SagaResponse.of(get.get(id));
    }

    private static boolean positiveInt(BigDecimal v) {
        return v != null && v.signum() > 0 && v.stripTrailingZeros().scale() <= 0
                && v.compareTo(BigDecimal.valueOf(Integer.MAX_VALUE)) <= 0;
    }
}
