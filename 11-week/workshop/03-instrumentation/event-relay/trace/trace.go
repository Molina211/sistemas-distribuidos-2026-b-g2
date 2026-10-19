// Package trace carries the W3C trace context (traceparent) and the correlation id of one request
// through the worker-event-relay, so every log line and every outbound call shows the same trace_id.
package trace

import (
	"context"
	"crypto/rand"
	"encoding/hex"
	"regexp"
)

const (
	// HeaderTraceparent is the W3C header that carries trace_id and span_id between services.
	HeaderTraceparent = "traceparent"
	// HeaderCorrelation is the id the team already sends between services (ADR-004 D8).
	HeaderCorrelation = "X-Correlation-Id"
)

var (
	traceparent = regexp.MustCompile(`^00-([0-9a-f]{32})-([0-9a-f]{16})-([0-9a-f]{2})$`)
	safeID      = regexp.MustCompile(`^[A-Za-z0-9._-]{1,128}$`)
)

// Context is one hop of a trace: the journey id and the id of the hop.
type Context struct {
	TraceID string
	SpanID  string
	Sampled bool
}

// New starts a trace with random ids.
func New() Context { return Context{TraceID: randomHex(16), SpanID: randomHex(8), Sampled: true} }

// Parse reads a traceparent header. It refuses a wrong version, wrong lengths and all-zero ids.
func Parse(header string) (Context, bool) {
	m := traceparent.FindStringSubmatch(header)
	if m == nil || m[1] == zeros(32) || m[2] == zeros(16) {
		return Context{}, false
	}
	flags, _ := hex.DecodeString(m[3])
	return Context{TraceID: m[1], SpanID: m[2], Sampled: flags[0]&1 == 1}, true
}

// Child keeps the trace_id and opens a new span, for the next hop.
func (c Context) Child() Context {
	return Context{TraceID: c.TraceID, SpanID: randomHex(8), Sampled: c.Sampled}
}

// String formats the traceparent header.
func (c Context) String() string {
	flags := "00"
	if c.Sampled {
		flags = "01"
	}
	return "00-" + c.TraceID + "-" + c.SpanID + "-" + flags
}

// ValidCorrelation says whether an incoming X-Correlation-Id is safe to write in a header and a log line.
func ValidCorrelation(id string) bool { return safeID.MatchString(id) }

// NewCorrelation returns a fresh correlation id.
func NewCorrelation() string { return "cor-" + randomHex(6) }

type key int

const (
	traceKey key = iota
	correlationKey
)

// With returns ctx carrying the trace context and the correlation id.
func With(ctx context.Context, c Context, correlation string) context.Context {
	return context.WithValue(context.WithValue(ctx, traceKey, c), correlationKey, correlation)
}

// From returns the trace context kept in ctx.
func From(ctx context.Context) (Context, bool) {
	c, ok := ctx.Value(traceKey).(Context)
	return c, ok
}

// CorrelationFrom returns the correlation id kept in ctx, or "".
func CorrelationFrom(ctx context.Context) string {
	id, _ := ctx.Value(correlationKey).(string)
	return id
}

func randomHex(n int) string {
	b := make([]byte, n)
	_, _ = rand.Read(b) // does not fail on the supported platforms
	return hex.EncodeToString(b)
}

func zeros(n int) string {
	b := make([]byte, n)
	for i := range b {
		b[i] = '0'
	}
	return string(b)
}
