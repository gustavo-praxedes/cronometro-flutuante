---
name: ui-ux-pro/quality
description: "Universal quality validator. Called by the orchestrator after any significant UI/UX output. Checks work against all specialist standards: accessibility, token usage, states, motion, typography, copy, mobile, and visual anti-patterns. Produces a Pass/Fail verdict with prioritized fixes. Works on any platform and any output type."
tags: [quality, validation, checklist, review, cross-domain, final-check]
---

# Quality Validator

## Scope
Cross-domain final check — runs after any specialist output

## When Called

The orchestrator calls this skill after:
- A component is built
- A flow is designed
- A screen is completed
- A design system is configured
- An audit is delivered

## Validation Checklist

### 1. UI States
- [ ] Error state: surfaced to user, not swallowed
- [ ] Loading: only shown when no data exists
- [ ] Empty: collection has empty state + next action
- [ ] Success: mutation has confirmation feedback
- [ ] Buttons: disabled during async, shows loading indicator

### 2. Accessibility
- [ ] Icon-only buttons have `aria-label`
- [ ] Form errors linked via `aria-describedby`
- [ ] `aria-invalid` on invalid fields
- [ ] Touch targets ≥ 44px / 44pt / 48dp
- [ ] Keyboard focus visible
- [ ] No info conveyed by color alone
- [ ] `prefers-reduced-motion` respected

### 3. Design Tokens
- [ ] No hardcoded hex colors
- [ ] No improvised shadows/radius
- [ ] Semantic token names used
- [ ] Light + dark variants both covered

### 4. Motion
- [ ] Only transform/opacity animated
- [ ] Max 200ms for interaction feedback
- [ ] No animation unless explicitly requested
- [ ] Looping animations pause when off-screen

### 5. Typography
- [ ] `text-balance` on headings
- [ ] `text-pretty` on body
- [ ] `tabular-nums` on data/numbers
- [ ] Min 16px body on mobile
- [ ] No generic fonts (Inter/Arial/Roboto) as primary

### 6. Color
- [ ] 1 accent per view (max 2)
- [ ] Light mode text ≥ slate-600 (#475569)
- [ ] Light mode cards ≥ 80% opacity
- [ ] No purple gradients on white
- [ ] No glow as primary affordance

### 7. UX Copy
- [ ] No "Submit" / "OK" / "Error occurred" / generic labels
- [ ] Errors blame system not user
- [ ] Empty states have specific next action
- [ ] Button labels are verb + object

### 8. Mobile (if applicable)
- [ ] Safe area accounted for
- [ ] No horizontal overflow
- [ ] Touch targets correctly sized
- [ ] Offline state defined
- [ ] Native conventions followed (iOS or Android)

### 9. Visual Design
- [ ] No AI-slop aesthetic (purple gradient / centered card grid)
- [ ] Clear aesthetic direction
- [ ] Typography has personality (not default system fonts)

### 10. Platform-Specific
**Web**: `h-dvh` not `h-screen` · fixed z-index scale · `className` passthrough
**iOS**: `contentInsetAdjustmentBehavior` · SF Symbols · safe areas
**Android**: LazyColumn for lists · Host wrapper · Material 3

---

## Verdict Format

```
QUALITY CHECK RESULT: [PASS / PASS WITH NOTES / FAIL]

Critical issues (must fix before shipping):
1. [issue] → [fix]

High priority (fix soon):
1. [issue] → [fix]

Notes (optional improvements):
- [suggestion]
```

## Scoring Guide

| Score | Verdict |
|---|---|
| 0 critical, 0–2 high | PASS |
| 0 critical, 3+ high | PASS WITH NOTES |
| 1+ critical | FAIL — do not ship |
