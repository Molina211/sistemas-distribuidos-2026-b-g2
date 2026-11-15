// Package notices is a copy, reduced for the week 14 challenge, of the subject line of a business e-mail.
package notices

import "strings"

const maxTenant = 60

// Subject never lets a long or padded tenant name break the subject line.
func Subject(tenantName string) string {
	name := strings.TrimSpace(tenantName)
	if r := []rune(name); len(r) > maxTenant {
		name = string(r[:maxTenant])
	}
	if name == "" {
		return "Multitour: your password was reset"
	}
	return name + ": your password was reset"
}
