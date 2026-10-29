package httpadapter_test

import (
	"io"
	"net/http"
	"strings"
	"testing"
)

// Story #584 (STRIDE row E, week 13 workshop): a token of one tenant must not act on another tenant.
// The memberships routes are not built yet, so this runs on a route that exists today:
// POST /api/v1/security/internal/tenants/{tenantId}/administrators, reserved to the SERVICE role.
// It reuses the helpers of the package tests (adminHandler, postAdmin, tokenWith, fakeCreator).
const otherTenant = "7a1d9e44-0b3c-4c8e-a2f5-6d1c0e9b8a77"

func TestW13_ATokenOfTenantACannotCreateAnAdministratorInTenantB(t *testing.T) {
	route := "/api/v1/security/internal/tenants/" + otherTenant + "/administrators"
	for name, token := range map[string]string{
		"administrator of tenant A": tokenWith(t, []string{"TENANT_ADMINISTRATOR"}, accountTenant, ""),
		"customer of tenant A":      tokenWith(t, []string{"FINAL_CUSTOMER"}, accountTenant, ""),
	} {
		svc := &fakeCreator{reg: temporaryAdmin(), created: true}
		rec := postAdmin(adminHandler(svc, io.Discard), route, token, "application/json", adminBody, adminKey)
		if rec.Code != http.StatusForbidden || !strings.Contains(rec.Body.String(), `"FORBIDDEN"`) {
			t.Errorf("%s: got %d %s, want 403 FORBIDDEN", name, rec.Code, rec.Body)
		}
		if len(svc.commands) != 0 {
			t.Errorf("%s: the use case ran %d times, want 0", name, len(svc.commands))
		}
		if strings.Contains(rec.Body.String(), otherTenant) {
			t.Errorf("%s: the answer repeats the id of tenant B: %s", name, rec.Body)
		}
	}
}
