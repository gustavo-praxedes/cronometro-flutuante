---
name: ui-ux-pro/design-system
description: "Design system specialist. Use for: setting up a design system from scratch, managing design tokens (colors/spacing/radius/shadow/motion), theming (light/dark), brand color setup, component library structure, and keeping JSON tokens in sync with CSS variables."
tags: [design-system, tokens, theming, brand, setup]
---

# Design System Specialist

## Scope
Token management · Theming · System setup · Brand configuration · Component structure

## Quick Decision

| Task | Go to |
|---|---|
| First time setup | Read `references/setup.md` |
| Token structure + naming | Read `references/tokens.md` |
| Dark/light theming | Read `references/theming.md` |
| Component library structure | Read `references/component-structure.md` |

## Core Rules

- **JSON → CSS → Component** — keep all three in sync
- Semantic names only (`bg-card` not `bg-white`)
- Every color token needs light + dark variant
- Never hardcode hex values in components
- Extend existing scale before adding new tokens

## Token Scale (Standard)

```
Spacing:  xs=4px sm=8px md=16px lg=24px xl=32px 2xl=48px 3xl=64px
Radius:   sm=4px md=8px lg=12px xl=16px full=9999px
Shadow:   sm/md/lg/xl (semantic elevation, not decorative)
Motion:   fast=150ms normal=250ms slow=400ms
```

## Setup Wizard Sequence

1. App type → determines layout density + navigation model
2. Brand primary color → generate full scale (50–900)
3. Semantic mappings → `brand`, `surface`, `on-surface`, `error`, `success`
4. Dark mode variants → all semantic tokens get dark values
5. Typography → display font + body font + scale
6. First screen scaffold

## Output Format

Always deliver:
- Token file (JSON or CSS variables)
- Usage example
- Dark mode variant
- Migration notes if updating existing system
