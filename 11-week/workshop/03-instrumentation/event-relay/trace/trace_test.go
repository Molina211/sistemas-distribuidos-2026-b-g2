package trace

import "testing"

func TestParseAcceptsAWellFormedTraceparent(t *testing.T) {
	c, ok := Parse("00-7c1e9a52d0b84f63a5e2c8b14d7f3096-a1b2c3d4e5f60718-01")
	if !ok || c.TraceID != "7c1e9a52d0b84f63a5e2c8b14d7f3096" || c.SpanID != "a1b2c3d4e5f60718" || !c.Sampled {
		t.Fatalf("parsed %+v ok=%v", c, ok)
	}
}

func TestParseRefusesWhatIsNotATraceparent(t *testing.T) {
	for _, h := range []string{"", "garbage", "01-7c1e9a52d0b84f63a5e2c8b14d7f3096-a1b2c3d4e5f60718-01",
		"00-00000000000000000000000000000000-a1b2c3d4e5f60718-01",
		"00-7c1e9a52d0b84f63a5e2c8b14d7f3096-0000000000000000-01",
		"00-7C1E9A52D0B84F63A5E2C8B14D7F3096-a1b2c3d4e5f60718-01"} {
		if _, ok := Parse(h); ok {
			t.Errorf("%q was accepted", h)
		}
	}
}

func TestChildKeepsTheTraceAndChangesTheSpan(t *testing.T) {
	parent := New()
	child := parent.Child()
	if child.TraceID != parent.TraceID || child.SpanID == parent.SpanID {
		t.Fatalf("parent %+v child %+v", parent, child)
	}
	if back, ok := Parse(child.String()); !ok || back != child {
		t.Fatalf("round trip gave %+v ok=%v", back, ok)
	}
}

func TestOnlySafeCorrelationIdsAreKept(t *testing.T) {
	if !ValidCorrelation("inact-3f9c1d20") || ValidCorrelation("not safe") || ValidCorrelation("") {
		t.Fatal("the safe-id rule is wrong")
	}
}
