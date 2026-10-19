package logging

import (
	"bytes"
	"context"
	"encoding/json"
	"log/slog"
	"testing"
	"time"

	"github.com/Molina211/sistemas-distribuidos-2026-b-g2/11-week/workshop/03-instrumentation/event-relay/trace"
)

func TestALineCarriesEveryRequiredFieldOfTheContract(t *testing.T) {
	var out bytes.Buffer
	ctx := trace.With(context.Background(), trace.Context{TraceID: "7c1e9a52d0b84f63a5e2c8b14d7f3096", SpanID: "a1b2c3d4e5f60718"}, "inact-3f9c1d20")
	Log(ctx, New(&out, "worker-event-relay", "2.0.0", "qa", slog.LevelInfo), slog.LevelWarn,
		"event.delivery.timeout", "security did not answer", 5*time.Second, slog.String("event_id", "d41d8cd9"))

	var line map[string]any
	if err := json.Unmarshal(out.Bytes(), &line); err != nil {
		t.Fatalf("not JSON: %v: %s", err, out.String())
	}
	for _, field := range []string{"timestamp", "level", "service", "version", "environment", "trace_id", "span_id",
		"correlation_id", "event", "message", "duration_ms"} {
		if _, ok := line[field]; !ok {
			t.Errorf("field %q is missing in %s", field, out.String())
		}
	}
	if line["level"] != "WARN" || line["duration_ms"] != float64(5000) || line["event_id"] != "d41d8cd9" {
		t.Errorf("wrong values in %s", out.String())
	}
}
