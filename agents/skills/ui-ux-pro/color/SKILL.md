---
name: ui-ux-pro/color
description: "Color specialist. Use when building color palettes, configuring design tokens for colors, setting up dark/light mode, ensuring color contrast compliance (WCAG), choosing brand colors, or reviewing color anti-patterns. Covers Web CSS, Tailwind, iOS, Android, and cross-platform."
tags: [color, palette, dark-mode, contrast, tokens, brand-color, wcag]
---

# Color Specialist

## Scope
Palette design · Tokens · Dark/light mode · Contrast · Brand color · Anti-patterns

## Quick Decision

| Task | Read |
|---|---|
| Full palette structure | `references/palette.md` |
| Dark mode implementation | `references/dark-mode.md` |
| Contrast checking | `references/contrast.md` |
| Anti-patterns | `references/anti-patterns.md` |

## Palette Structure

```
Brand:    primary / hover / light / subtle / surface
Semantic: success / warning / error / info
Neutral:  bg-1..6 (elevation scale) / text-primary/secondary/muted / border-subtle/default/strong
Dataviz:  7-color accessible palette (purple/blue/green/yellow/red/pink/cyan)
```

## Core Rules

- 1 accent color per view (max 2)
- Never information by color alone (add icon or label)
- Semantic tokens in code, never hardcoded hex
- All colors need light + dark variants
- Test both modes before shipping

## Contrast Requirements (WCAG 2.2 AA)

| Type | Minimum |
|---|---|
| Normal text (<18px) | 4.5:1 |
| Large text (≥18px or ≥14px bold) | 3:1 |
| UI components + icons | 3:1 |
| Disabled states | Exempt |

## Anti-Patterns (Stop These)

```
❌ Purple gradients on white — universal AI slop marker
❌ Glow effects as primary affordance
❌ Evenly-balanced rainbow palettes (no hierarchy)
❌ bg-white/10 in light mode (invisible)
❌ text-gray-400 for body text (fails contrast)
❌ border-white/10 in light mode (invisible)
❌ Information by color alone (red = required field)
```

## Light Mode Minimums

```
text-body:    #475569 (slate-600) minimum — NEVER lighter
text-heading: #0F172A (slate-900)
card-bg:      rgba(255,255,255,0.80) minimum — NEVER transparent
border:       #E2E8F0 (slate-200) — must be visible
```

## Dark Mode Scale

```
bg-1: hsl(240, 6%, 10%)   — page background
bg-2: hsl(240, 5%, 12%)   — card
bg-3: hsl(240, 5%, 14%)   — elevated card
bg-4: hsl(240, 4%, 18%)   — input
bg-5: hsl(240, 4%, 22%)   — hover
bg-6: hsl(240, 4%, 26%)   — active
```

## Semantic Color Mapping

| Role | Light | Dark |
|---|---|---|
| Success | #22C55E | #4ADE80 |
| Warning | #F59E0B | #FBBF24 |
| Error | #EF4444 | #F87171 |
| Info | #3B82F6 | #60A5FA |
