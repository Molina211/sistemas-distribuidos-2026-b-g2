package booking

import (
	"errors"
	"math"
	"testing"
)

func TestTotalCents(t *testing.T) {
	cases := []struct {
		name  string
		seats int
		unit  int64
		want  int64
		err   error
	}{
		{"three seats", 3, 150000, 450000, nil},
		{"free tour", 2, 0, 0, nil},
		{"no seats", 0, 100, 0, ErrInvalid},
		{"negative price", 1, -1, 0, ErrInvalid},
		{"overflow", 2, math.MaxInt64, 0, ErrOverflow},
	}
	for _, c := range cases {
		got, err := TotalCents(c.seats, c.unit)
		if got != c.want || !errors.Is(err, c.err) {
			t.Errorf("%s: got %d, %v; want %d, %v", c.name, got, err, c.want, c.err)
		}
	}
}
