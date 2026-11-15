// Package booking is a copy, reduced for the week 14 challenge, of how Multitour handles money: integers in minor units.
package booking

import (
	"errors"
	"math"
)

var ErrInvalid = errors.New("seats must be at least 1 and the unit price cannot be negative")
var ErrOverflow = errors.New("the total does not fit in minor units")

// TotalCents is the price of a reservation in minor units (totalCents), never a float.
func TotalCents(seats int, unitCents int64) (int64, error) {
	if seats < 1 || unitCents < 0 {
		return 0, ErrInvalid
	}
	if unitCents > math.MaxInt64/int64(seats) {
		return 0, ErrOverflow
	}
	return int64(seats) * unitCents, nil
}
