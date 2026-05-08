---
name: ui-ux-pro/motion
description: "Motion and animation specialist. Use when adding animations, transitions, micro-interactions, scroll effects, page transitions, or any motion to Web, iOS, Android, or cross-platform apps. Enforces performance rules: only transform/opacity, respects prefers-reduced-motion, max 200ms for interactions."
tags: [motion, animation, transition, micro-interaction, framer-motion, reanimated]
---

# Motion Specialist

## Scope
Animations · Transitions · Micro-interactions · Scroll effects · Page transitions

## Quick Decision

| Task | Read |
|---|---|
| Timing + easing guide | `references/timing.md` |
| Framer Motion patterns | `references/framer-motion.md` |
| React Native / Reanimated | `references/reanimated.md` |
| SwiftUI animations | `references/swiftui-motion.md` |
| Anti-patterns | `references/anti-patterns.md` |

## Hard Rules (No Exceptions)

```
✅ Animate ONLY: transform, opacity
❌ NEVER animate: width, height, top, left, margin, padding
❌ NEVER exceed 200ms for interaction feedback
❌ NEVER animate unless explicitly requested
❌ NEVER custom easing unless explicitly requested
✅ ALWAYS pause looping animations when off-screen
✅ ALWAYS respect prefers-reduced-motion
```

## Timing Scale

| Purpose | Duration | Easing |
|---|---|---|
| Interaction feedback (hover, tap) | 150ms | ease-out |
| Small transitions (tooltip, badge) | 200ms | ease-out |
| Page/route transitions | 250–300ms | ease-in-out |
| Modal open | 250ms | ease-out |
| Reveal / entrance | 300ms | ease-out |
| Spring / bounce | 400ms | cubic-bezier(0.34,1.56,0.64,1) |
| Stagger between items | 50–100ms delay | ease-out |

## Reduced Motion

```css
@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after {
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
  }
}
```

## Performance Rules

- `will-change: transform` ONLY during active animation, remove after
- Never animate `box-shadow` or `filter` continuously
- Never animate large `blur()` or `backdrop-filter` surfaces
- Use GPU compositor path: `transform: translate3d()` or `translateX/Y`

## When Animation Adds Value

| Adds value | Does not add value |
|---|---|
| Showing what changed | Decorative loops |
| Indicating relationship | Gratuitous entrance |
| Communicating state change | Animation for animation's sake |
| Guiding attention | Heavy page-load sequences |
