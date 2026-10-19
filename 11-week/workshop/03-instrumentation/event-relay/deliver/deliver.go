// Package deliver sends one outbox event to a consumer (security, in the journey of CP1) and copies the trace
// context to the call, so the consumer continues the same trace. It also records the two metrics of CP4.
package deliver

import (
	"bytes"
	"context"
	"errors"
	"log/slog"
	"net"
	"net/http"
	"time"

	"github.com/Molina211/sistemas-distribuidos-2026-b-g2/11-week/workshop/03-instrumentation/event-relay/logging"
	"github.com/Molina211/sistemas-distribuidos-2026-b-g2/11-week/workshop/03-instrumentation/event-relay/metrics"
	"github.com/Molina211/sistemas-distribuidos-2026-b-g2/11-week/workshop/03-instrumentation/event-relay/trace"
)

type Deliverer struct {
	Client  *http.Client
	Metrics *metrics.Registry
	Log     *slog.Logger
}

// Deliver posts the body to url as an event. The call carries a child span of the trace in ctx, the
// correlation id and the id of the event as Idempotency-Key, as the contract of security asks.
func (d *Deliverer) Deliver(ctx context.Context, consumer, url, eventID string, body []byte) error {
	current, ok := trace.From(ctx)
	if !ok {
		current = trace.New()
	}
	correlation := trace.CorrelationFrom(ctx)
	if correlation == "" {
		correlation = trace.NewCorrelation()
	}
	hop := current.Child()
	ctx = trace.With(ctx, hop, correlation)

	req, err := http.NewRequestWithContext(ctx, http.MethodPost, url, bytes.NewReader(body))
	if err != nil {
		return err
	}
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Idempotency-Key", eventID)
	req.Header.Set(trace.HeaderTraceparent, hop.String())
	req.Header.Set(trace.HeaderCorrelation, correlation)

	start := time.Now()
	resp, err := d.Client.Do(req)
	took := time.Since(start)
	if err != nil {
		outcome := "error"
		var netErr net.Error
		if errors.Is(err, context.DeadlineExceeded) || (errors.As(err, &netErr) && netErr.Timeout()) {
			outcome = "timeout"
		}
		d.finish(ctx, consumer, outcome, took, eventID, 0)
		return err
	}
	defer resp.Body.Close()
	if resp.StatusCode >= 200 && resp.StatusCode < 300 {
		d.finish(ctx, consumer, "ok", took, eventID, resp.StatusCode)
		return nil
	}
	d.finish(ctx, consumer, "rejected", took, eventID, resp.StatusCode)
	return errors.New(consumer + " answered " + resp.Status)
}

func (d *Deliverer) finish(ctx context.Context, consumer, outcome string, took time.Duration, eventID string, status int) {
	d.Metrics.Observe(consumer, outcome, took)
	level := slog.LevelInfo
	if outcome != "ok" {
		level = slog.LevelError
	}
	logging.Log(ctx, d.Log, level, "event.delivery."+outcome, "delivery to "+consumer+": "+outcome, took,
		slog.String("event_id", eventID), slog.Int("http_status", status))
}
