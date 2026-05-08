---
name: ui-ux-pro/ux-copy
description: "UX microcopy specialist. Use when writing or reviewing button labels, error messages, empty states, toast notifications, form labels, placeholders, confirmation dialogs, onboarding copy, and any short product text. Enforces casual-but-polite tone, active voice, and specific actionable language."
tags: [ux-copy, microcopy, error-messages, empty-states, toasts, forms, tone]
---

# UX Copy Specialist

## Scope
Buttons · Errors · Empty states · Toasts · Forms · Dialogs · Onboarding

## Quick Decision

| Task | Read |
|---|---|
| All surface patterns | `references/copy-patterns.md` |
| Tone + voice guide | `references/tone.md` |
| Error message guide | `references/errors.md` |

## Tone Rules

```
✅ Casual but polite
✅ Active voice ("Save changes" not "Changes will be saved")
✅ Positive framing when honest
✅ Plain language — no jargon
✅ Concise — every word earns its place
✅ Blame system not user
```

## By Surface (Quick Reference)

**Buttons** — verb + object, no filler
```
✅ "Save changes"  "Delete account"  "Send invite"
❌ "Submit"  "OK"  "Confirm"  "Click here"
```

**Empty states** — friendly + what to do
```
✅ "No items yet. Create your first one."
✅ "Nothing here. Start by adding a project."
❌ "No data found."  "Empty."
```

**Errors** — blame system, give exit
```
✅ "Couldn't save. Check your connection and try again."
✅ "Something went wrong on our end. Try again."
❌ "Error 500"  "Invalid request"  "Something went wrong."
```

**Toasts** — short, result-focused
```
✅ "Saved"  "Deleted"  "Invite sent"
✅ "Deleted · Undo" (for reversible)
❌ "The operation was completed successfully."
```

**Form labels** — visible, clear, no asterisk-only for required
```
✅ "Email address (required)"  "First name"
❌ "Email *"  "Name"  (with no visible hint it's required)
```

**Placeholders** — hint, not substitute for label
```
✅ "name@company.com"  "Search projects..."
❌ "Enter your email address" (too verbose)
❌ Using placeholder INSTEAD of label
```

**Dialogs** — state action + consequence
```
✅ "Delete this project? This cannot be undone."
✅ "Remove Sarah? She'll lose access immediately."
❌ "Are you sure?"  "Confirm action"
```

## Anti-Patterns

```
❌ Generic: "Submit", "OK", "Error", "Invalid"
❌ Raw errors: "Error 422: Unprocessable entity"
❌ User blame: "You entered an invalid email"
❌ Vague confirm: "Are you sure you want to do this?"
❌ Missing next step in error messages
❌ Placeholder as label (disappears on type)
```
