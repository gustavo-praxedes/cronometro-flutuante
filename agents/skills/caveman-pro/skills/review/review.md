---
name: caveman-review
description: >
  Ultra-compressed code review comments. One line per finding: location, problem, fix.
  Use when user says "review this PR", "code review", "review the diff", "/review",
  or invokes /caveman-review. Auto-triggers when reviewing pull requests or diffs.
---

# Caveman Review

One line per finding. Location, problem, fix. No throat-clearing.

## Format

`L<line>: <problem>. <fix>.`

Multi-file: `<file>:L<line>: ...`

## Severity Prefixes (when mixing severity)

- `🔴 bug:` — broken behavior, will cause incident
- `🟡 risk:` — works but fragile (race, missing null check, swallowed error)
- `🔵 nit:` — style, naming, micro-optim. Author can ignore
- `❓ q:` — genuine question, not suggestion

## Drop

- "I noticed that...", "It seems like...", "You might want to consider..."
- "This is just a suggestion but..." — use `nit:` instead
- "Great work!", "Looks good overall but..."
- Restating what the line does
- Hedging (perhaps, maybe, I think) — if unsure, use `q:`

## Keep

- Exact line numbers
- Exact symbol/function/variable names in backticks
- Concrete fix, not "consider refactoring"
- The *why* if fix isn't obvious

## Examples

❌ "I noticed that on line 42 you're not checking if the user object is null before accessing the email property..."

✅ `L42: 🔴 bug: user can be null after .find(). Add guard before .email.`

❌ "Have you considered what happens if the API returns a 429?"

✅ `L23: 🟡 risk: no retry on 429. Wrap in withBackoff(3).`

## Auto-Clarity

Write full prose for:
- Security findings (CVE-class — need full explanation + reference)
- Architectural disagreements (need rationale)
- Onboarding contexts (new author needs the "why")

Resume terse after.

## Output

Comments ready to paste into PR. Does not approve/request-changes, does not run linters.
