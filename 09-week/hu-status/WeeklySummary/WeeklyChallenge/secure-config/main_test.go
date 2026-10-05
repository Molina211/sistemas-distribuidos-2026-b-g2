package main

import (
	"net/http"
	"net/http/httptest"
	"os"
	"path/filepath"
	"strings"
	"testing"
)

func env(m map[string]string) func(string) string {
	return func(k string) string { return m[k] }
}

func validEnv() map[string]string {
	return map[string]string{
		"HTTP_ADDR":       ":8080",
		"DATABASE_URL":    "test-database-url",
		"JWT_PRIVATE_KEY": "test-private-key",
	}
}

func TestLoadConfigReportsEveryMissingVariableAtOnce(t *testing.T) {
	_, err := LoadConfig(env(nil))
	if err == nil {
		t.Fatal("expected an error with an empty environment")
	}
	for _, name := range []string{"HTTP_ADDR", "DATABASE_URL", "JWT_PRIVATE_KEY"} {
		if !strings.Contains(err.Error(), name) {
			t.Errorf("error does not name %s: %v", name, err)
		}
	}
}

func TestLoadConfigFlagDefaultsToOff(t *testing.T) {
	cfg, err := LoadConfig(env(validEnv()))
	if err != nil {
		t.Fatal(err)
	}
	if cfg.FeatureTokenRevocation {
		t.Error("FEATURE_TOKEN_REVOCATION should default to false")
	}
}

func TestLoadConfigRejectsMalformedFlag(t *testing.T) {
	e := validEnv()
	e["FEATURE_TOKEN_REVOCATION"] = "ture"
	if _, err := LoadConfig(env(e)); err == nil {
		t.Error("expected an error for a malformed flag")
	}
}

func TestLoadConfigReadsSecretFromFile(t *testing.T) {
	path := filepath.Join(t.TempDir(), "database_url")
	if err := os.WriteFile(path, []byte("from-secret-store\n"), 0o600); err != nil {
		t.Fatal(err)
	}
	e := validEnv()
	delete(e, "DATABASE_URL")
	e["DATABASE_URL_FILE"] = path

	cfg, err := LoadConfig(env(e))
	if err != nil {
		t.Fatal(err)
	}
	if cfg.DatabaseURL != "from-secret-store" {
		t.Errorf("DatabaseURL = %q", cfg.DatabaseURL)
	}
}

func TestLoadConfigNeverPrintsSecretValues(t *testing.T) {
	// The secret value pasted into NAME_FILE by mistake must not be echoed back.
	e := validEnv()
	delete(e, "DATABASE_URL")
	e["DATABASE_URL_FILE"] = "postgres://u:s3cr3t@h/db"
	_, err := LoadConfig(env(e))
	if err == nil || strings.Contains(err.Error(), "s3cr3t") {
		t.Errorf("error leaks a secret or is missing: %v", err)
	}
}

func TestRevocationRouteFollowsTheFlag(t *testing.T) {
	for _, tc := range []struct {
		flag bool
		want int
	}{{false, http.StatusNotFound}, {true, http.StatusAccepted}} {
		rec := httptest.NewRecorder()
		req := httptest.NewRequest(http.MethodPost, "/api/v1/tokens/revocations", strings.NewReader(`{"tokenId":"abc"}`))
		newMux(Config{FeatureTokenRevocation: tc.flag}).ServeHTTP(rec, req)
		if rec.Code != tc.want {
			t.Errorf("flag=%t: status %d, want %d", tc.flag, rec.Code, tc.want)
		}
	}
}

func TestRevocationRejectsMissingTokenID(t *testing.T) {
	rec := httptest.NewRecorder()
	req := httptest.NewRequest(http.MethodPost, "/api/v1/tokens/revocations", strings.NewReader(`{}`))
	newMux(Config{FeatureTokenRevocation: true}).ServeHTTP(rec, req)
	if rec.Code != http.StatusBadRequest {
		t.Errorf("status %d, want 400", rec.Code)
	}
}
