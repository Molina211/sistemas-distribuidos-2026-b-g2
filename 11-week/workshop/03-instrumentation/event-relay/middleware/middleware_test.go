package middleware

import (
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/Molina211/sistemas-distribuidos-2026-b-g2/11-week/workshop/03-instrumentation/event-relay/trace"
)

func serve(t *testing.T, header http.Header) (*httptest.ResponseRecorder, trace.Context) {
	t.Helper()
	var seen trace.Context
	h := TraceContext(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		seen, _ = trace.From(r.Context())
	}))
	req := httptest.NewRequest(http.MethodGet, "/health", nil)
	req.Header = header
	rec := httptest.NewRecorder()
	h.ServeHTTP(rec, req)
	return rec, seen
}

func TestAnIncomingTraceIsContinuedAndReturned(t *testing.T) {
	rec, seen := serve(t, http.Header{"Traceparent": {"00-7c1e9a52d0b84f63a5e2c8b14d7f3096-a1b2c3d4e5f60718-01"}, "X-Correlation-Id": {"inact-3f9c1d20"}})
	back, ok := trace.Parse(rec.Header().Get("traceparent"))
	if !ok || back.TraceID != "7c1e9a52d0b84f63a5e2c8b14d7f3096" || back.SpanID == "a1b2c3d4e5f60718" || back != seen {
		t.Fatalf("answered %q, handler saw %+v", rec.Header().Get("traceparent"), seen)
	}
	if rec.Header().Get("X-Correlation-Id") != "inact-3f9c1d20" {
		t.Errorf("correlation id was not kept: %q", rec.Header().Get("X-Correlation-Id"))
	}
}

func TestARequestWithoutUsableIdsStartsANewTrace(t *testing.T) {
	rec, seen := serve(t, http.Header{"Traceparent": {"garbage"}, "X-Correlation-Id": {"not safe"}})
	if _, ok := trace.Parse(rec.Header().Get("traceparent")); !ok || seen.TraceID == "" {
		t.Fatalf("no new trace: %q", rec.Header().Get("traceparent"))
	}
	if id := rec.Header().Get("X-Correlation-Id"); !trace.ValidCorrelation(id) || id == "not safe" {
		t.Errorf("unsafe correlation id was not replaced: %q", id)
	}
}
