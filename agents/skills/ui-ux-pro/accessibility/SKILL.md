---
name: ui-ux-pro/accessibility
description: "Accessibility specialist. Use for: WCAG 2.2 AA audits, ARIA implementation, keyboard navigation, color contrast checks, focus management, screen reader support, touch target sizing, reduced-motion, form accessibility, and accessibility code fixes. Works across Web, iOS, Android, and cross-platform."
tags: [accessibility, wcag, aria, a11y, contrast, keyboard, screen-reader]
---

# Accessibility Specialist

## Scope
WCAG 2.2 AA · ARIA · Keyboard nav · Contrast · Focus · Screen readers · Touch targets

## Quick Decision

| Task | Read |
|---|---|
| Full WCAG audit | `references/wcag-checklist.md` |
| ARIA patterns | `references/aria-patterns.md` |
| Color contrast | `references/contrast.md` |
| Keyboard & focus | `references/keyboard-focus.md` |

## WCAG 2.2 AA — 4 Pillars

**Perceivable**
- Text contrast: 4.5:1 minimum (normal text), 3:1 (large text/UI)
- Non-text contrast: 3:1 for UI components and graphics
- Alt text on all meaningful images (`alt=""` for decorative)
- Info never conveyed by color alone

**Operable**
- Touch targets: ≥44×44px (iOS) / ≥48×48dp (Android) / ≥44×44px CSS
- All interactive elements reachable by keyboard
- Tab order matches visual reading order
- Visible focus indicator on all interactive elements
- `prefers-reduced-motion` respected

**Understandable**
- Visible label on every form input
- Errors associated with the field they describe (`aria-describedby`)
- Error messages specific and actionable (not "Invalid")
- Document language declared (`lang` attribute)

**Robust**
- Semantic HTML first (before ARIA)
- ARIA only when native semantics insufficient
- No fake buttons (`div` with click) — use `<button>`
- No fake links — use `<a href>`

## Critical Rules

```
Icon-only button   → MUST have aria-label
Loading states     → Announce to screen readers (aria-live or aria-busy)
Error messages     → Link to field via aria-describedby
Invalid fields     → aria-invalid="true"
Dialog/Modal       → aria-labelledby + aria-describedby + focus trap
Looping animation  → Must pause when off-screen
```

## Never Do

- Rebuild keyboard/focus behavior from scratch
- Block paste in inputs
- Use color as the only indicator
- `tabindex` > 0 (breaks natural order)
- Skip heading levels (h1 → h3)
- `aria-hidden` on focusable elements

## Audit Output Format

```
ISSUE: [what is broken]
HEURISTIC: [which WCAG criterion]
SEVERITY: Critical / High / Medium / Low
FIX: [specific code change]
```

## Severity Classification

- **Critical**: blocks task completion for disabled users
- **High**: significant barrier, workaround possible
- **Medium**: degraded experience
- **Low**: minor issue, cosmetic impact
