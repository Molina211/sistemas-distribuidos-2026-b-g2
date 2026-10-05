package main

import (
	"errors"
	"fmt"
	"io/fs"
	"os"
	"strconv"
	"strings"
)

// Config is everything the service reads from its environment.
type Config struct {
	HTTPAddr               string
	DatabaseURL            string
	JWTPrivateKey          string
	FeatureTokenRevocation bool
}

// LoadConfig checks every variable before returning, so a bad deploy reports
// all of its problems in one failed start instead of one per restart.
// Error messages name the variable, never its value.
func LoadConfig(getenv func(string) string) (Config, error) {
	var errs []error

	required := func(name string) string {
		v := strings.TrimSpace(getenv(name))
		if v == "" {
			errs = append(errs, fmt.Errorf("%s is required", name))
		}
		return v
	}

	// A secret comes either in NAME or as a file path in NAME_FILE. The file form
	// is how Docker secrets and secret stores inject a value without exposing it
	// in the process environment.
	secret := func(name string) string {
		path := strings.TrimSpace(getenv(name + "_FILE"))
		if path == "" {
			return required(name)
		}
		b, err := os.ReadFile(path)
		if err != nil {
			// err carries the path, which may be a secret pasted by mistake.
			reason := "cannot be read"
			if errors.Is(err, fs.ErrNotExist) {
				reason = "points to a missing file"
			}
			errs = append(errs, fmt.Errorf("%s_FILE %s", name, reason))
			return ""
		}
		v := strings.TrimSpace(string(b))
		if v == "" {
			errs = append(errs, fmt.Errorf("%s_FILE points to an empty file", name))
		}
		return v
	}

	// A flag defaults to off, but a typo such as "ture" is an error, not a
	// silent "off".
	flag := func(name string) bool {
		v := strings.TrimSpace(getenv(name))
		if v == "" {
			return false
		}
		on, err := strconv.ParseBool(v)
		if err != nil {
			errs = append(errs, fmt.Errorf("%s must be true or false", name))
		}
		return on
	}

	cfg := Config{
		HTTPAddr:               required("HTTP_ADDR"),
		DatabaseURL:            secret("DATABASE_URL"),
		JWTPrivateKey:          secret("JWT_PRIVATE_KEY"),
		FeatureTokenRevocation: flag("FEATURE_TOKEN_REVOCATION"),
	}
	return cfg, errors.Join(errs...)
}
