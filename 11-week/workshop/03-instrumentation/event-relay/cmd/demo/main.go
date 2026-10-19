// Command demo delivers one TenantInactivated event to a local stand-in for security and prints the log
// lines of both sides, to show that they share one trace_id, followed by the /metrics text.
package main

import (
	"context"
	"fmt"
	"log/slog"
	"net/http"
	"net/http/httptest"
	"os"
	"time"

	"github.com/Molina211/sistemas-distribuidos-2026-b-g2/11-week/workshop/03-instrumentation/event-relay/deliver"
	"github.com/Molina211/sistemas-distribuidos-2026-b-g2/11-week/workshop/03-instrumentation/event-relay/logging"
	"github.com/Molina211/sistemas-distribuidos-2026-b-g2/11-week/workshop/03-instrumentation/event-relay/metrics"
	"github.com/Molina211/sistemas-distribuidos-2026-b-g2/11-week/workshop/03-instrumentation/event-relay/middleware"
	"github.com/Molina211/sistemas-distribuidos-2026-b-g2/11-week/workshop/03-instrumentation/event-relay/trace"
)

const eventID = "d41d8cd9-8f00-4b20-b204-e9800998ecf8"

func main() {
	securityLog := logging.New(os.Stdout, "security", "2.0.0", "dev", slog.LevelInfo)
	security := httptest.NewServer(middleware.TraceContext(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		logging.Log(r.Context(), securityLog, slog.LevelInfo, "tenant.event.applied", "tenant copy updated", time.Millisecond,
			slog.String("event_id", r.Header.Get("Idempotency-Key")), slog.String("tenant_status", "INACTIVE"))
	})))
	defer security.Close()

	registry := metrics.New()
	relay := &deliver.Deliverer{
		Client:  &http.Client{Timeout: 5 * time.Second},
		Metrics: registry,
		Log:     logging.New(os.Stdout, "worker-event-relay", "2.0.0", "dev", slog.LevelInfo),
	}
	// A run of the relay starts the trace; in the target design the trace comes from the outbox event.
	ctx := trace.With(context.Background(), trace.New(), trace.NewCorrelation())
	if err := relay.Deliver(ctx, "security", security.URL, eventID, []byte(`{"eventType":"TenantInactivated"}`)); err != nil {
		fmt.Fprintln(os.Stderr, "delivery failed:", err)
		os.Exit(1)
	}
	fmt.Println("--- /metrics ---")
	fmt.Print(registry.Text())
}
