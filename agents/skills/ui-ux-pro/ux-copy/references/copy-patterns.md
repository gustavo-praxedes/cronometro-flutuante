# UX Copy Patterns Reference

## Buttons

### Action Hierarchy
```
Primary CTA: strong verb + object
  "Save changes"   "Send invite"    "Delete account"
  "Start free trial"  "Create project"  "Add team member"

Secondary: softer framing
  "Cancel"   "Go back"   "Skip for now"

Tertiary/link: minimal weight
  "Learn more"   "View details"   "See all"
```

### Context-specific CTAs

| Context | Generic (❌) | Specific (✅) |
|---|---|---|
| Onboarding | "Get started" | "Set up my workspace" |
| Signup | "Sign up" | "Create free account" |
| Upgrade | "Upgrade" | "Start Pro trial" |
| Delete | "Delete" | "Delete this project" |
| Save | "Save" | "Save changes" |
| Send | "Send" | "Send to team" |
| Pay | "Pay" | "Pay $29/month" |

---

## Empty States

### Formula
```
[Friendly observation] + [Why it's empty] + [Clear next action]
```

### Patterns

**No items yet:**
```
"Nothing here yet"
"Create your first project to get started."
[Create project button]
```

**Search with no results:**
```
"No results for '[query]'"
"Try different keywords or check for typos."
[Clear search] [Browse all]
```

**Filter with no matches:**
```
"No [items] match your filters"
"Try removing some filters to see more."
[Clear filters]
```

**Awaiting action from others:**
```
"Waiting on your team"
"No activity yet. Share this link to get started."
[Copy link]
```

---

## Error Messages

### Formula
```
[What went wrong] + [Why if helpful] + [What to do next]
```

### By Error Type

**Network / connectivity:**
```
✅ "Couldn't connect. Check your internet and try again."
❌ "Network error"   ❌ "ERR_CONNECTION_REFUSED"
```

**Save failed:**
```
✅ "Couldn't save your changes. Try again or copy your work."
❌ "Save failed"   ❌ "Error 500"
```

**Permission:**
```
✅ "You don't have access to this. Ask your admin for permission."
❌ "403 Forbidden"   ❌ "Access denied"
```

**Not found:**
```
✅ "We couldn't find this page. It may have moved or been deleted."
❌ "404 Not Found"   ❌ "Page does not exist"
```

**Form validation:**
```
✅ "Enter a valid email address (example: name@company.com)"
❌ "Invalid email"   ❌ "Bad format"
```

**Generic / unknown:**
```
✅ "Something went wrong on our end. Try again in a moment."
❌ "An error occurred"   ❌ "Error"
```

---

## Toast Notifications

### Rules
- Max 2–3 words for success
- Always provide undo for destructive
- Duration: 3–5 seconds
- Position: top-right (desktop) or bottom (mobile)

### Patterns

**Success:**
```
"Saved"
"Sent"
"Deleted"
"Invite sent"
"Changes saved"
```

**With undo:**
```
"Deleted · Undo"
"Archived · Undo"
"Removed from team · Undo"
```

**Error:**
```
"Couldn't save. Try again."
"Failed to send. Retry?"
```

---

## Form Labels and Helpers

### Label Rules
- Always visible (never placeholder-only)
- Required: say so in label text, not just asterisk+color
- Format: "Email address" not "Email Address" (sentence case)

```html
<!-- Good -->
<label>Email address (required)
  <input type="email" placeholder="name@company.com" />
</label>

<!-- Bad -->
<label>Email *
  <input placeholder="Enter your email address here" />
</label>
```

### Helper Text
```
Password: "At least 8 characters with a number"
Username: "Letters, numbers, and underscores only"
Billing: "You won't be charged until after your trial"
```

---

## Confirmation Dialogs

### Formula
```
Title:   [Verb + object] (not "Are you sure?")
Body:    [Consequence] — specific and real
Confirm: [Verb that matches title]
Cancel:  "Cancel" or "Keep [object]"
```

### Examples

```
Title:   "Delete this project?"
Body:    "This will permanently delete 'Marketing Q4' and all 47 files inside. This cannot be undone."
Confirm: "Delete project"
Cancel:  "Keep project"
```

```
Title:   "Remove Sarah from the team?"
Body:    "Sarah will lose access to all projects immediately."
Confirm: "Remove Sarah"
Cancel:  "Cancel"
```

```
Title:   "Cancel your subscription?"
Body:    "You'll keep access until March 15, 2025. After that, your data will be read-only for 30 days."
Confirm: "Cancel subscription"
Cancel:  "Keep subscription"
```
