// Package middleware reads or creates the trace context of an incoming request and returns it in the answer.
package middleware

import (
	"net/http"

	"github.com/Molina211/sistemas-distribuidos-2026-b-g2/11-week/workshop/03-instrumentation/event-relay/trace"
)

// TraceContext continues the trace of the caller (a new span with the same trace_id) or starts one when the
// caller sent no valid traceparent, reuses a safe X-Correlation-Id or creates one, puts both in the request
// context and writes them in the answer headers.
func TraceContext(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		c, ok := trace.Parse(r.Header.Get(trace.HeaderTraceparent))
		if ok {
			c = c.Child()
		} else {
			c = trace.New()
		}
		correlation := r.Header.Get(trace.HeaderCorrelation)
		if !trace.ValidCorrelation(correlation) {
			correlation = trace.NewCorrelation()
		}
		w.Header().Set(trace.HeaderTraceparent, c.String())
		w.Header().Set(trace.HeaderCorrelation, correlation)
		next.ServeHTTP(w, r.WithContext(trace.With(r.Context(), c, correlation)))
	})
}
