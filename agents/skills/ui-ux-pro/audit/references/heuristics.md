# UX Heuristics Audit Reference

## Pre-Audit Questions

1. Who is the primary user?
2. What is the core task this screen/flow enables?
3. What platform / context (mobile, desktop, web app)?
4. Is this a new audit or regression check?

---

## Nielsen's 10 — Applied to Mobile + Web

### 1. Visibility of System Status
**Web**: Loading indicators, progress bars, success states
**Mobile**: Skeleton screens, activity indicators, haptic feedback

Check:
- [ ] Does user know when something is loading?
- [ ] Is operation outcome always communicated?
- [ ] Are background processes visible?
- [ ] Does button state change on press?

### 2. Match Between System and Real World
Check:
- [ ] Language matches user vocabulary (not technical jargon)
- [ ] Icons are universally understood or labeled
- [ ] Metaphors reflect real-world equivalents

### 3. User Control and Freedom
Check:
- [ ] Can user undo last action?
- [ ] Is there a clear exit from every flow?
- [ ] Can user cancel long operations?
- [ ] Back navigation always available (non-root screens)
- [ ] Destructive actions have confirmation + undo

### 4. Consistency and Standards
Check:
- [ ] Same components look the same everywhere
- [ ] Platform conventions followed (iOS swipe, Android back)
- [ ] Terms consistent across the product
- [ ] Navigation patterns consistent between sections

### 5. Error Prevention
Check:
- [ ] Destructive actions require confirmation
- [ ] Forms validate before submission
- [ ] Inputs have clear format hints
- [ ] Irreversible actions clearly labeled as such

### 6. Recognition Rather Than Recall
Check:
- [ ] Actions visible, not hidden in menus
- [ ] Previously entered data remembered
- [ ] Search results show context
- [ ] Icons have visible labels

### 7. Flexibility and Efficiency
Check:
- [ ] Power users can use shortcuts
- [ ] Bulk actions available where relevant
- [ ] Frequently used actions are prominent
- [ ] Less frequent actions are accessible but not cluttering

### 8. Aesthetic and Minimalist Design
Check:
- [ ] No irrelevant information on screen
- [ ] Visual hierarchy is clear
- [ ] White space used intentionally
- [ ] Information shown progressively

### 9. Help Users Recognize, Diagnose, and Recover from Errors
Check:
- [ ] Error messages in plain language
- [ ] Error messages specify the problem
- [ ] Error messages suggest solution
- [ ] Errors appear near the problem location
- [ ] Recovery path always provided

### 10. Help and Documentation
Check:
- [ ] Onboarding explains the product
- [ ] Empty states guide next action
- [ ] Help accessible within 2 taps
- [ ] Tooltips on complex controls

---

## Mobile-Specific Additional Checks

- [ ] One-handed use possible for primary actions
- [ ] Primary CTA in thumb zone (bottom 40% of screen)
- [ ] No critical information behind scroll on first view
- [ ] Forms avoid bottom of screen (keyboard overlap)
- [ ] Tap targets min 44pt/48dp with adequate spacing
- [ ] Device orientation handled (portrait + landscape if needed)

---

## Severity Scoring Guide

| Count | Assessment |
|---|---|
| 0–2 Critical | Healthy — minor polish needed |
| 3–5 Critical | Significant issues — prioritize fixes |
| 6+ Critical | Redesign or major rework needed |
| Any flow-blocker | Do not ship without fix |
