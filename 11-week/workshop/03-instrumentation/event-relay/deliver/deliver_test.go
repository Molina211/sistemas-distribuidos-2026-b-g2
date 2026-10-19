package deliver

import (
	"context"
	"io"
	"log/slog"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
	"time"

	"github.com/Molina211/sistemas-distribuidos-2026-b-g2/11-week/workshop/03-instrumentation/event-relay/metrics"
	"github.com/Molina211/sistemas-distribuidos-2026-b-g2/11-week/workshop/03-instrumentation/event-relay/middleware"
	"github.com/Molina211/sistemas-distribuidos-2026-b-g2/11-week/workshop/03-instrumentation/event-relay/trace"
)

const traceID = "7c1e9a52d0b84f63a5e2c8b14d7f3096"

// fakeSecurity answers like security would, behind the same middleware, and keeps what it received.
func fakeSecurity(status int, seen *http.Header, seenTrace *trace.Context) *httptest.Server {
	return httptest.NewServer(middleware.TraceContext(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		*seen = r.Header.Clone()
		*seenTrace, _ = trace.From(r.Context())
		w.WriteHeader(status)
	})))
}

func newDeliverer() (*Deliverer, *metrics.Registry) {
	m := metrics.New()
	return &Deliverer{Client: &http.Client{Timeout: 200 * time.Millisecond}, Metrics: m, Log: slog.New(slog.NewTextHandler(io.Discard, nil))}, m
}

func TestTheTraceIdReceivedIsTheTraceIdSent(t *testing.T) {
	var header http.Header
	var server trace.Context
	srv := fakeSecurity(http.StatusOK, &header, &server)
	defer srv.Close()
	d, _ := newDeliverer()
	ctx := trace.With(context.Background(), trace.Context{TraceID: traceID, SpanID: "a1b2c3d4e5f60718", Sampled: true}, "inact-3f9c1d20")

	if err := d.Deliver(ctx, "security", srv.URL, "d41d8cd9-8f00-4b20-b204-e9800998ecf8", []byte(`{}`)); err != nil {
		t.Fatal(err)
	}
	sent, ok := trace.Parse(header.Get("traceparent"))
	if !ok || sent.TraceID != traceID || sent.SpanID == "a1b2c3d4e5f60718" {
		t.Fatalf("security received traceparent %q", header.Get("traceparent"))
	}
	if server.TraceID != traceID {
		t.Errorf("the middleware of security saw trace_id %q, want %q", server.TraceID, traceID)
	}
	if header.Get("X-Correlation-Id") != "inact-3f9c1d20" || header.Get("Idempotency-Key") != "d41d8cd9-8f00-4b20-b204-e9800998ecf8" {
		t.Errorf("headers lost: %v", header)
	}
}

func TestARefusalAndAnUnreachableConsumerAreCountedWithTheirOutcome(t *testing.T) {
	var header http.Header
	var server trace.Context
	refusing := fakeSecurity(http.StatusServiceUnavailable, &header, &server)
	defer refusing.Close()
	d, m := newDeliverer()

	if err := d.Deliver(context.Background(), "security", refusing.URL, "e1", nil); err == nil {
		t.Fatal("a 503 must be an error")
	}
	if err := d.Deliver(context.Background(), "security", "http://127.0.0.1:1", "e2", nil); err == nil {
		t.Fatal("an unreachable consumer must be an error")
	}
	text := m.Text()
	for _, want := range []string{`outcome="rejected"} 1`, `outcome="error"} 1`} {
		if !contains(text, want) {
			t.Errorf("missing %q in:\n%s", want, text)
		}
	}
}

func contains(s, sub string) bool { return strings.Contains(s, sub) }
