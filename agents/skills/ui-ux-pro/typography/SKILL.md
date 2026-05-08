---
name: ui-ux-pro/typography
description: "Typography specialist. Use when choosing fonts, setting type scales, establishing line heights and spacing, pairing display and body fonts, or fixing readability issues. Covers Web (Tailwind/CSS), iOS (SF Pro system), Android (Roboto/Material), and cross-platform. Avoids generic AI font choices."
tags: [typography, fonts, type-scale, readability, font-pairing, line-height]
---

# Typography Specialist

## Scope
Font selection · Type scale · Line height · Pairing · Readability · Platform-specific

## Quick Decision

| Task | Read |
|---|---|
| Font pairing guide | `references/font-pairing.md` |
| Type scale + CSS | `references/type-scale.md` |
| Platform typography | `references/platform-type.md` |

## Type Scale

| Token | Size | Weight | Line Height | Use |
|---|---|---|---|---|
| display | 48px | 700 | 1.1 | Hero headlines |
| h1 | 36px | 700 | 1.2 | Page titles |
| h2 | 28px | 600 | 1.3 | Section titles |
| h3 | 22px | 600 | 1.4 | Card titles |
| body | 16px | 400 | 1.6 | Body text |
| small | 14px | 400 | 1.5 | Secondary text |
| caption | 12px | 400 | 1.5 | Metadata |
| label | 14px | 500 | 1.4 | Form labels |
| mono | 14px | 400 | 1.5 | Code |

## Core Rules

```
text-balance  → headings (prevents orphaned words)
text-pretty   → body/paragraphs
tabular-nums  → numbers, data, counters
line-length   → 65–75 chars (measure) for body
min 16px      → body text on mobile
```

## Font Pairing Strategy

- 1 expressive display font (personality, headings)
- 1 restrained body font (readability, body)
- Max 2 font families per project
- Contrast personalities: don't pair similar fonts

## AVOID (Generic AI Fonts)

```
❌ Inter (overused)
❌ Roboto (generic)
❌ Arial (system default)
❌ Helvetica Neue (dated)
❌ System-ui as primary (no personality)
```

## USE INSTEAD

```
Display/Headings: Playfair Display, Fraunces, Syne, Cabinet Grotesk,
                  Clash Display, Cormorant, Literata, Plus Jakarta Sans

Body: Source Serif 4, Lora, DM Sans, Instrument Sans, Nunito,
      Figtree, Onest, Geist

Mono: JetBrains Mono, Geist Mono, Fira Code, Commit Mono
```

## Letter Spacing Rules

- NEVER modify unless explicitly requested
- Exception: all-caps labels can use slight tracking (0.05–0.08em)
- Display fonts: sometimes need negative tracking at large sizes

## Tailwind Typography Classes

```html
<!-- Headings -->
<h1 class="text-balance font-bold text-4xl tracking-tight">

<!-- Body -->
<p class="text-pretty text-base leading-relaxed">

<!-- Data/numbers -->
<span class="tabular-nums font-mono text-sm">

<!-- Dense UI label -->
<span class="truncate text-sm font-medium">

<!-- Long text (articles) -->
<p class="line-clamp-3 text-pretty">
```
