---
name: ui-ux-pro/ux-feedback
description: "UX feedback states specialist. Use when adding or reviewing loading states, error states, empty states, and success states to any UI component, page, or flow. Covers all platforms: Web, iOS, Android, and cross-platform. Ensures no component ships with only the happy path."
tags: [loading, error, empty, success, states, feedback, skeleton, toast]
---

# UX Feedback States Specialist

## Scope
Loading · Error · Empty · Success states — for any platform, any component

## The 4 Required States

Every data-dependent UI must implement all 4. No exceptions.

| State | When | Pattern |
|---|---|---|
| Loading | Fetching or mutating | Skeleton (known shape) or spinner (unknown) |
| Error | Request failed | Message + retry; near the failing action |
| Empty | No data found | Message + one clear next action |
| Success | Action completed | Toast or inline; undo for destructive |

## Quick Decision

| Task | Read |
|---|---|
| Loading skeleton patterns | `references/loading.md` |
| Error patterns + hierarchy | `references/errors.md` |
| Empty state patterns | `references/empty-states.md` |
| Success + toast patterns | `references/success.md` |

## Loading Rules

- Skeleton when layout shape is known → prevents layout shift
- Spinner when shape unknown → modal actions, inline ops
- Delay skeleton ~200ms to avoid flash on fast responses
- Never show loading spinner when cached data exists

## Error Rules

- Always surface; never `catch (e) { console.log(e) }`
- Show near the action that caused it
- Plain language (not error codes or stack traces)
- Always provide recovery path (retry, go back, contact)
- Localize error to affected section only if rest of page works

## Empty State Rules

- Every list MUST have empty state
- Include: friendly message + explanation + one CTA
- Zero counts still render meaningfully (show "0 items" not blank)
- Different messages for: no data yet vs no results found

## Success Rules

- Lightweight confirmation (toast preferred)
- Show for all mutations that aren't immediately obvious
- Undo available for all reversible destructive actions
- Duration: 3–5 seconds for toasts

## Anti-Patterns

```
❌ Loading spinner when data already cached
❌ Error swallowed silently (console.log only)
❌ Empty list with blank white space
❌ "Something went wrong" with no recovery path
❌ Success confirmation missing
❌ Undo unavailable for destructive actions
```
