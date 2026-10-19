// Package logging writes the structured log contract of CP2: one JSON object per line with the same
// field names in every service, and trace_id, span_id and correlation_id taken from the context.
package logging

import (
	"context"
	"io"
	"log/slog"
	"time"

	"github.com/Molina211/sistemas-distribuidos-2026-b-g2/11-week/workshop/03-instrumentation/event-relay/trace"
)

// New returns a logger whose lines carry service, version and environment, with the CP2 field names.
func New(w io.Writer, service, version, environment string, level slog.Level) *slog.Logger {
	json := slog.NewJSONHandler(w, &slog.HandlerOptions{Level: level, ReplaceAttr: contractNames})
	return slog.New(contextHandler{json}).With("service", service, "version", version, "environment", environment)
}

// contractNames renames slog's own keys to the contract: timestamp in UTC with milliseconds, and message.
func contractNames(groups []string, a slog.Attr) slog.Attr {
	if len(groups) > 0 {
		return a
	}
	switch a.Key {
	case slog.TimeKey:
		return slog.String("timestamp", a.Value.Time().UTC().Format("2006-01-02T15:04:05.000Z"))
	case slog.MessageKey:
		return slog.String("message", a.Value.String())
	}
	return a
}

// contextHandler adds the trace fields of the request context to every line.
type contextHandler struct{ slog.Handler }

func (h contextHandler) Handle(ctx context.Context, r slog.Record) error {
	if c, ok := trace.From(ctx); ok {
		r.AddAttrs(slog.String("trace_id", c.TraceID), slog.String("span_id", c.SpanID))
	}
	if id := trace.CorrelationFrom(ctx); id != "" {
		r.AddAttrs(slog.String("correlation_id", id))
	}
	return h.Handler.Handle(ctx, r)
}

func (h contextHandler) WithAttrs(a []slog.Attr) slog.Handler {
	return contextHandler{h.Handler.WithAttrs(a)}
}

func (h contextHandler) WithGroup(n string) slog.Handler {
	return contextHandler{h.Handler.WithGroup(n)}
}

// Log writes one line of the contract. The event name and the duration are required by CP2, so they are
// arguments and not optional attributes; domain fields go in attrs.
func Log(ctx context.Context, l *slog.Logger, level slog.Level, event, message string, took time.Duration, attrs ...slog.Attr) {
	all := append([]slog.Attr{slog.String("event", event), slog.Int64("duration_ms", took.Milliseconds())}, attrs...)
	l.LogAttrs(ctx, level, message, all...)
}
