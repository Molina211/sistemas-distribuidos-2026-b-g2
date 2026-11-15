package notices

import (
	"strings"
	"testing"
)

func TestSubject(t *testing.T) {
	if got := Subject("  Travesia  "); got != "Travesia: your password was reset" {
		t.Errorf("trimmed name: got %q", got)
	}
	if got := Subject(""); got != "Multitour: your password was reset" {
		t.Errorf("empty name: got %q", got)
	}
	if got := Subject(strings.Repeat("á", 80)); !strings.HasPrefix(got, strings.Repeat("á", 60)+":") {
		t.Errorf("long name is not cut at 60 runes: %q", got)
	}
}
