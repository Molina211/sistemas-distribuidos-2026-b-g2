package metrics

import (
	"strings"
	"testing"
	"time"
)

func TestTheCounterAndTheHistogramAreWrittenInTheTextFormat(t *testing.T) {
	r := New()
	r.Observe("security", "ok", 80*time.Millisecond)
	r.Observe("security", "timeout", 5*time.Second)
	r.Observe("security", "ok", 30*time.Millisecond)

	text := r.Text()
	for _, want := range []string{
		`relay_deliveries_total{consumer="security",outcome="ok"} 2`,
		`relay_deliveries_total{consumer="security",outcome="timeout"} 1`,
		`relay_delivery_duration_seconds_bucket{consumer="security",le="0.05"} 1`,
		`relay_delivery_duration_seconds_bucket{consumer="security",le="0.1"} 2`,
		`relay_delivery_duration_seconds_bucket{consumer="security",le="5"} 3`,
		`relay_delivery_duration_seconds_bucket{consumer="security",le="+Inf"} 3`,
		`relay_delivery_duration_seconds_count{consumer="security"} 3`,
	} {
		if !strings.Contains(text, want) {
			t.Errorf("missing %q in:\n%s", want, text)
		}
	}
}
