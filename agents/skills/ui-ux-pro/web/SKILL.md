---
name: ui-ux-pro/web
description: "Web UI specialist. Use for React, Angular, Next.js, Vue, Tailwind CSS, Radix UI, shadcn/ui, dark theme implementations, and web-specific UI patterns. Covers component architecture, state management patterns, accessible web primitives, and Tailwind design system integration."
tags: [web, react, angular, nextjs, tailwind, radix, shadcn, dark-theme]
---

# Web Specialist

## Scope
React · Angular · Next.js · Tailwind · Radix UI · shadcn/ui · Web patterns

## Quick Decision

| Task | Read |
|---|---|
| React UI patterns | `references/react.md` |
| Angular signals + patterns | `references/angular.md` |
| Tailwind rules + classes | `references/tailwind.md` |
| Radix UI / shadcn patterns | `references/radix.md` |
| Dark theme implementation | `references/dark-theme.md` |

## Framework Choice

| Need | Use |
|---|---|
| Full-stack / SSR | Next.js |
| SPA, team knows React | React + Vite |
| Enterprise, template-heavy | Angular |
| Lightweight | Vue + Vite |
| Pre-built accessible components | shadcn/ui (Radix-based) |
| Headless primitives | Radix UI directly |

## Tailwind Core Rules

```
cn()         → always use (clsx + tailwind-merge) for conditional classes
h-dvh        → not h-screen (safe on mobile browsers)
size-*       → for square elements instead of w-* + h-*
z-index      → fixed scale: 10/20/30/50 (no arbitrary z-*)
safe-area    → env(safe-area-inset-*) for fixed elements
```

## Radix UI Principles

- **Headless**: behavior only, you add styling
- **asChild**: always use to avoid extra wrapper DOM nodes
- **Portal**: use for dropdowns/modals to escape overflow
- **Accessibility**: built-in, never override keyboard behavior
- **Controlled vs uncontrolled**: use controlled when syncing to URL/store

## Component Requirements (Web)

- `className` passthrough on all components
- `cn()` for class merging
- CVA for variants (when >2 variants exist)
- `data-slot` attribute for identification
- `asChild` support where element type matters (links, buttons)
- Semantic tokens only — no hardcoded hex

## Never Do (Web)

```
❌ h-screen → use h-dvh
❌ Arbitrary z-index → use fixed scale
❌ Rebuild keyboard behavior → use Radix/Base UI
❌ Nested <button> inside <button> → use asChild
❌ useEffect for render logic → use derived state
❌ Purple gradients → find another direction
❌ will-change outside active animation
```

## Scripts (web/scripts/)

| Script | Use |
|---|---|
| `init-artifact.sh` | Scaffold React + Vite + Tailwind + shadcn/ui project |
| `bundle-artifact.sh` | Bundle to single self-contained `bundle.html` |
| `shadcn-components.tar.gz` | 40+ pre-built shadcn/ui components |

```bash
# Create new artifact project
bash web/scripts/init-artifact.sh my-app
cd my-app

# After development: bundle to single HTML
bash ../web/scripts/bundle-artifact.sh

# Full docs → references/web-artifacts.md
```

Requirements: Node 18+, pnpm (auto-installed if missing)
