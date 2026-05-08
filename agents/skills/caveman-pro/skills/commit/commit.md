---
name: caveman-commit
description: >
  Ultra-compressed commit message generator. Conventional Commits format. Subject ≤50 chars,
  body only when "why" isn't obvious. Use when user says "write a commit", "commit message",
  "generate commit", "/commit", or invokes /caveman-commit. Auto-triggers when staging changes.
---

# Caveman Commit

Terse commit messages. Conventional Commits. Why over what.

## Format

`<type>(<scope>): <imperative summary>`

- `<scope>` optional
- Types: `feat`, `fix`, `refactor`, `perf`, `docs`, `test`, `chore`, `build`, `ci`, `style`, `revert`
- Imperative: "add", "fix", "remove" — not "added", "adds"
- ≤50 chars preferred, hard cap 72
- No trailing period

## Body (only if needed)

Add body for: non-obvious *why*, breaking changes, migration notes, linked issues.

- Wrap at 72 chars
- Bullets `-` not `*`
- Reference issues at end: `Closes #42`, `Refs #17`

## Never Include

- "This commit does X" — diff says what
- "I", "we", "now", "currently"
- "As requested by..." — use Co-authored-by trailer
- "Generated with Claude Code" or AI attribution
- Emoji (unless project convention)
- Restating file name when scope covers it

## Examples

```
feat(api): add GET /users/:id/profile

Mobile client needs profile data without full user payload
to reduce LTE bandwidth on cold-launch screens.

Closes #128
```

```
feat(api)!: rename /v1/orders to /v1/checkout

BREAKING CHANGE: clients on /v1/orders must migrate to /v1/checkout
before 2026-06-01. Old route returns 410 after that date.
```

## Auto-Clarity

Always include body for: breaking changes, security fixes, data migrations, reverts. Future debuggers need context.

## Output

Code block ready to paste. Does not run `git commit`, does not stage files.
