package co.edu.corhuila.abbr.orders.adapter.in.http;

import co.edu.corhuila.abbr.orders.adapter.in.http.ApiError.FieldError;
import co.edu.corhuila.abbr.orders.adapter.in.http.ApiError.ValidationException;
import co.edu.corhuila.abbr.orders.application.port.in.OrderUseCases;
import co.edu.corhuila.abbr.orders.application.port.in.OrderUseCases.ListOrdersQuery;
import co.edu.corhuila.abbr.orders.application.port.in.OrderUseCases.OrderPage;
import co.edu.corhuila.abbr.orders.application.port.in.OrderUseCases.PlaceOrderCommand;
import co.edu.corhuila.abbr.orders.application.port.in.OrderUseCases.PlaceOrderResult;
import co.edu.corhuila.abbr.orders.domain.model.Order;
import co.edu.corhuila.abbr.orders.domain.model.Status;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Inbound adapter. Validates the SHAPE of the input (types, formats, ranges)
 * before building a command, depends on the use case ports only, and holds no
 * business rules.
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private static final Pattern UUID = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    /** totalCents is read as BigDecimal so that 12.5 is rejected instead of silently truncated. */
    public record PlaceOrderRequest(String customerId, BigDecimal totalCents) { }

    /** The public contract. The domain entity is never serialized directly. */
    public record OrderResponse(String id, String customerId, long totalCents, String status, String createdAt) {
        static OrderResponse of(Order o) {
            return new OrderResponse(o.id(), o.customerId(), o.totalCents(), o.status().name(),
                    o.createdAt().truncatedTo(ChronoUnit.SECONDS).toString());
        }
    }

    public record PageMeta(int page, int limit, long total, long totalPages) { }

    public record PageResponse(List<OrderResponse> data, PageMeta meta) { }

    private final OrderUseCases.PlaceOrder place;
    private final OrderUseCases.GetOrder get;
    private final OrderUseCases.ListOrders list;
    private final OrderUseCases.ConfirmOrder confirm;
    private final OrderUseCases.CancelOrder cancel;

    public OrderController(OrderUseCases.PlaceOrder place, OrderUseCases.GetOrder get, OrderUseCases.ListOrders list,
                           OrderUseCases.ConfirmOrder confirm, OrderUseCases.CancelOrder cancel) {
        this.place = place;
        this.get = get;
        this.list = list;
        this.confirm = confirm;
        this.cancel = cancel;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> place(
            @RequestBody PlaceOrderRequest body,
            @RequestHeader(name = "Idempotency-Key", required = false) String key) {
        List<FieldError> problems = new ArrayList<>();
        if (body.customerId() == null || !UUID.matcher(body.customerId()).matches()) {
            problems.add(new FieldError("customerId", "must be a UUID"));
        }
        BigDecimal total = body.totalCents();
        if (total == null || total.signum() <= 0 || total.stripTrailingZeros().scale() > 0
                || total.compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) > 0) {
            problems.add(new FieldError("totalCents", "must be a positive integer (minor units)"));
        }
        if (key == null || key.length() < 8 || key.length() > 128) {
            problems.add(new FieldError("Idempotency-Key", "header required, 8 to 128 characters"));
        }
        if (!problems.isEmpty()) {
            throw new ValidationException("the request has invalid fields", problems);
        }
        PlaceOrderResult result = place.placeOrder(new PlaceOrderCommand(body.customerId(), total.longValueExact(), key));
        return ResponseEntity.status(result.created() ? HttpStatus.CREATED : HttpStatus.OK) // 200: a retry
                .header("Location", "/api/v1/orders/" + result.id())
                .body(Map.of("id", result.id()));
    }

    @GetMapping
    public PageResponse list(@RequestParam(required = false) String status,
                             @RequestParam(required = false) String createdBefore,
                             @RequestParam(required = false) String page,
                             @RequestParam(required = false) String limit) {
        List<FieldError> problems = new ArrayList<>();
        Status statusFilter = null;
        if (status != null) {
            try {
                statusFilter = Status.valueOf(status);
            } catch (IllegalArgumentException e) {
                problems.add(new FieldError("status", "must be PENDING, CONFIRMED or CANCELLED"));
            }
        }
        Instant before = null;
        if (createdBefore != null) {
            try {
                before = Instant.parse(createdBefore);
            } catch (DateTimeParseException e) {
                problems.add(new FieldError("createdBefore", "must be an RFC 3339 date-time"));
            }
        }
        int pageNumber = parse(page, 1, 1, Integer.MAX_VALUE, "page", "must be an integer >= 1", problems);
        int pageSize = parse(limit, 20, 1, 100, "limit", "must be an integer between 1 and 100", problems);
        if (!problems.isEmpty()) {
            throw new ValidationException("the query has invalid parameters", problems);
        }
        OrderPage result = list.listOrders(new ListOrdersQuery(statusFilter, before, pageNumber, pageSize));
        long totalPages = (result.total() + result.limit() - 1) / result.limit();
        return new PageResponse(result.items().stream().map(OrderResponse::of).toList(),
                new PageMeta(result.page(), result.limit(), result.total(), totalPages));
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable String id) {
        return OrderResponse.of(get.getOrder(requireUuid(id)));
    }

    @PostMapping("/{id}/confirm")
    public OrderResponse confirm(@PathVariable String id) {
        return OrderResponse.of(confirm.confirmOrder(requireUuid(id)));
    }

    @PostMapping("/{id}/cancel")
    public OrderResponse cancel(@PathVariable String id) {
        return OrderResponse.of(cancel.cancelOrder(requireUuid(id)));
    }

    private static String requireUuid(String id) {
        if (!UUID.matcher(id).matches()) {
            throw new ValidationException("the order id must be a UUID", List.of(new FieldError("id", "must be a UUID")));
        }
        return id;
    }

    private static int parse(String raw, int fallback, int min, int max, String field, String message,
                             List<FieldError> problems) {
        if (raw == null) {
            return fallback;
        }
        try {
            int n = Integer.parseInt(raw);
            if (n >= min && n <= max) {
                return n;
            }
        } catch (NumberFormatException ignored) {
            // reported below
        }
        problems.add(new FieldError(field, message));
        return fallback;
    }
}
