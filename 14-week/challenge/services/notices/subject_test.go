package notices

import (
	"strings"
	"testing"
)

func TestSubject(t *testing.T) {
	if got := Subject("  Travesia  "); got != "Travesia: your password was reset" {
		t.Errorf("trimmed name: got %q", got)
	}
	for _, blank := range []string{"", "   ", "\t\n"} {
		if got := Subject(blank); got != "Multitour: your password was reset" {
			t.Errorf("blank name %q: got %q", blank, got)
		}
	}
	if got := Subject(strings.Repeat("á", 80)); !strings.HasPrefix(got, strings.Repeat("á", 60)+":") {
		t.Errorf("long name is not cut at 60 runes: %q", got)
	}
}
