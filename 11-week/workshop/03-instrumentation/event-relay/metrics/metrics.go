// Package metrics keeps the two metrics of the relay journey and writes them in the Prometheus text format:
// a counter of deliveries by outcome and a histogram of their duration (CP4 names them).
package metrics

import (
	"fmt"
	"net/http"
	"sort"
	"strings"
	"sync"
	"time"
)

// Buckets are in seconds. The relay gives up a delivery after 5 s, so the last finite bucket is that limit.
var Buckets = []float64{0.05, 0.1, 0.25, 0.5, 1, 2.5, 5}

type histogram struct {
	buckets []uint64 // cumulative counts, one per entry of Buckets
	count   uint64
	sum     float64
}

// Registry is safe for concurrent use.
type Registry struct {
	mu       sync.Mutex
	counters map[[2]string]uint64 // consumer, outcome
	hist     map[string]*histogram
}

func New() *Registry {
	return &Registry{counters: map[[2]string]uint64{}, hist: map[string]*histogram{}}
}

// Observe records one delivery to a consumer. The outcome is one of ok, rejected, timeout or error, so
// the number of series stays small and does not grow with tenants or events.
func (r *Registry) Observe(consumer, outcome string, took time.Duration) {
	r.mu.Lock()
	defer r.mu.Unlock()
	r.counters[[2]string{consumer, outcome}]++
	h := r.hist[consumer]
	if h == nil {
		h = &histogram{buckets: make([]uint64, len(Buckets))}
		r.hist[consumer] = h
	}
	seconds := took.Seconds()
	for i, upper := range Buckets {
		if seconds <= upper {
			h.buckets[i]++
		}
	}
	h.count++
	h.sum += seconds
}

// Handler serves /metrics.
func (r *Registry) Handler() http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, _ *http.Request) {
		w.Header().Set("Content-Type", "text/plain; version=0.0.4")
		_, _ = fmt.Fprint(w, r.Text())
	})
}

func (r *Registry) Text() string {
	r.mu.Lock()
	defer r.mu.Unlock()
	var b strings.Builder
	b.WriteString("# HELP relay_deliveries_total Deliveries of events to a consumer, by outcome.\n# TYPE relay_deliveries_total counter\n")
	keys := make([][2]string, 0, len(r.counters))
	for k := range r.counters {
		keys = append(keys, k)
	}
	sort.Slice(keys, func(i, j int) bool { return keys[i][0]+keys[i][1] < keys[j][0]+keys[j][1] })
	for _, k := range keys {
		fmt.Fprintf(&b, "relay_deliveries_total{consumer=%q,outcome=%q} %d\n", k[0], k[1], r.counters[k])
	}
	b.WriteString("# HELP relay_delivery_duration_seconds Duration of a delivery to a consumer.\n# TYPE relay_delivery_duration_seconds histogram\n")
	consumers := make([]string, 0, len(r.hist))
	for c := range r.hist {
		consumers = append(consumers, c)
	}
	sort.Strings(consumers)
	for _, c := range consumers {
		h := r.hist[c]
		for i, upper := range Buckets {
			fmt.Fprintf(&b, "relay_delivery_duration_seconds_bucket{consumer=%q,le=\"%g\"} %d\n", c, upper, h.buckets[i])
		}
		fmt.Fprintf(&b, "relay_delivery_duration_seconds_bucket{consumer=%q,le=\"+Inf\"} %d\n", c, h.count)
		fmt.Fprintf(&b, "relay_delivery_duration_seconds_sum{consumer=%q} %g\nrelay_delivery_duration_seconds_count{consumer=%q} %d\n", c, h.sum, c, h.count)
	}
	return b.String()
}
