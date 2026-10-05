// Command secure-config is the week 09 challenge: a minimal stand-in for the
// security microservice that refuses to start with an invalid configuration
// and keeps a new capability behind a feature flag.
package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"os"
	"strings"
	"time"
)

func main() {
	cfg, err := LoadConfig(os.Getenv)
	if err != nil {
		fmt.Fprintf(os.Stderr, "invalid configuration, not starting:\n%v\n", err)
		os.Exit(1)
	}

	log.Printf("listening on %s, FEATURE_TOKEN_REVOCATION=%t", cfg.HTTPAddr, cfg.FeatureTokenRevocation)
	srv := &http.Server{
		Addr:              cfg.HTTPAddr,
		Handler:           newMux(cfg),
		ReadHeaderTimeout: 5 * time.Second,
		WriteTimeout:      15 * time.Second,
		IdleTimeout:       60 * time.Second,
	}
	log.Fatal(srv.ListenAndServe())
}

func newMux(cfg Config) *http.ServeMux {
	mux := http.NewServeMux()
	mux.HandleFunc("GET /health", func(w http.ResponseWriter, _ *http.Request) {
		writeJSON(w, http.StatusOK, map[string]string{"status": "UP"})
	})
	// Token revocation is new: with the flag off the route is not registered,
	// so the endpoint does not exist (404) instead of half-working.
	if cfg.FeatureTokenRevocation {
		mux.HandleFunc("POST /api/v1/tokens/revocations", revokeToken)
	}
	return mux
}

// revokeToken only validates and accepts the request: storing the revoked
// token id is the job of the real security service, not of this challenge.
func revokeToken(w http.ResponseWriter, r *http.Request) {
	var body struct {
		TokenID string `json:"tokenId"`
	}
	if err := json.NewDecoder(http.MaxBytesReader(w, r.Body, 1<<10)).Decode(&body); err != nil || strings.TrimSpace(body.TokenID) == "" {
		writeJSON(w, http.StatusBadRequest, map[string]string{"error": "VALIDATION_ERROR", "message": "tokenId is required"})
		return
	}
	writeJSON(w, http.StatusAccepted, map[string]string{"tokenId": body.TokenID, "status": "REVOKED"})
}

func writeJSON(w http.ResponseWriter, status int, v any) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	json.NewEncoder(w).Encode(v)
}
