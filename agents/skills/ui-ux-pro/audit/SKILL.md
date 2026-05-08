---
name: ui-ux-pro/audit
description: "Audit and review specialist. Use for: UX heuristic audits (Nielsen's 10), design system compliance reviews, code reviews for UI quality, visual validation, accessibility audits, or any systematic evaluation of an existing interface. Works across Web, Mobile, and Desktop. Always produces prioritized findings with specific fixes."
tags: [audit, review, heuristics, ux-audit, code-review, visual-validation, compliance]
---

# Audit & Review Specialist

## Scope
UX audits · Code reviews · Visual validation · Design system compliance · A11y audits

## Quick Decision

| Task | Read |
|---|---|
| UX heuristic audit | `references/heuristics.md` |
| Design system review | `references/design-system-review.md` |
| Code review checklist | `references/code-review.md` |
| Visual validation | `references/visual-validation.md` |

## Audit Output Format (Always Use)

```
FINDING: [what is wrong — objective description]
HEURISTIC/RULE: [which principle is violated]
SEVERITY: Critical / High / Medium / Low
LOCATION: [file:line or screen:component]
FIX: [specific, concrete change]
```

## Severity Classification

| Level | Meaning |
|---|---|
| Critical | Blocks task completion — ship-stopper |
| High | Significant barrier; workaround exists |
| Medium | Degraded experience; not blocking |
| Low | Minor polish issue |

## Nielsen's 10 Heuristics

1. **System status** — user always knows what's happening
2. **Real-world language** — system speaks user's language
3. **User control** — undo, redo, escape always available
4. **Consistency** — same things look + work the same
5. **Error prevention** — design prevents mistakes
6. **Recognition > recall** — info visible, not memorized
7. **Flexibility** — works for novice and expert
8. **Aesthetic minimalism** — no irrelevant information
9. **Error recovery** — errors are plain language + fix path
10. **Help** — documentation exists and is findable

## Code Review Checklist

**Tokens**
- [ ] No hardcoded hex colors
- [ ] No improvised shadow/radius values
- [ ] CSS variables or semantic tokens used

**Components**
- [ ] `className` passthrough present
- [ ] CVA for variants (if >2)
- [ ] `data-slot` attribute
- [ ] Semantic HTML (button is button, not div)

**States**
- [ ] Error state surfaced to user
- [ ] Loading only when no data
- [ ] Empty state for collections
- [ ] Buttons disabled during async
- [ ] Success feedback present

**Accessibility**
- [ ] `aria-label` on icon-only buttons
- [ ] Form errors linked via `aria-describedby`
- [ ] Touch targets ≥ 44px
- [ ] Focus visible
- [ ] `prefers-reduced-motion` respected

## Visual Validation Rules

- Start with: **"From visual evidence, I observe..."**
- Never declare success without concrete visual proof
- Never infer from code — only from what renders
- Check both light and dark modes
- Check at 375px (mobile), 768px (tablet), 1280px (desktop)
- Verify: contrast ratios, touch targets, focus states

## Anti-Patterns to Call Out

```
❌ Purple gradient on white
❌ Spinner when cached data exists
❌ Error swallowed silently
❌ List with no empty state
❌ Button not disabled during submission
❌ Icon-only button without aria-label
❌ Hardcoded colors
❌ h-screen instead of h-dvh
❌ Arbitrary z-index values
```
