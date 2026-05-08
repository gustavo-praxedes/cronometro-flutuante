# Design System Setup Reference

## Setup Wizard (Step by Step)

### Step 1 — App Type
Determines: layout density, navigation model, component complexity

| App Type | Density | Nav Model | Primary Pattern |
|---|---|---|---|
| SaaS Dashboard | Medium–High | Sidebar + Top | Data-heavy cards |
| E-commerce | Medium | Tab + Category | Product grid |
| Fintech | Medium | Tab + Drill-down | Charts + numbers |
| Social | High | Tab | Feed + cards |
| Productivity | High | Sidebar | Lists + editor |
| Portfolio | Low | Simple | Big visuals |

### Step 2 — Brand Color
Generate full scale from primary:

```
50  → Lightest tint (backgrounds, badges)
100 → Light (hover on light bg)
200 → Light-medium
300 → Medium (icons on white)
400 → Medium (large text on white — 3:1)
500 → Primary color (large text on white — 4.5:1 target)
600 → Primary darker (normal text on white — 4.5:1)
700 → Deep (hover state)
800 → Deeper (active state)
900 → Darkest (high-contrast text)
```

### Step 3 — Semantic Mappings

```css
:root {
  --color-brand: [primary-600];
  --color-brand-hover: [primary-700];
  --color-surface: #FFFFFF;
  --color-surface-secondary: [neutral-50];
  --color-on-surface: [neutral-900];
  --color-on-surface-secondary: [neutral-600];
  --color-border: [neutral-200];
  --color-border-subtle: [neutral-100];
}

[data-theme="dark"] {
  --color-brand: [primary-400];        /* Lighter in dark */
  --color-surface: hsl(240, 6%, 10%);
  --color-surface-secondary: hsl(240, 5%, 14%);
  --color-on-surface: #FFFFFF;
  --color-on-surface-secondary: #A1A1AA;
  --color-border: hsla(0,0%,100%,0.12);
  --color-border-subtle: hsla(0,0%,100%,0.06);
}
```

### Step 4 — Typography

```css
:root {
  --font-display: 'Playfair Display', Georgia, serif;  /* Example */
  --font-body: 'Source Serif 4', Georgia, serif;        /* Example */
  --font-mono: 'JetBrains Mono', monospace;
}
```

### Step 5 — First Screen Scaffold

Typical structure:
```
AppShell
├── Sidebar / TopNav (nav tokens)
└── Main
    ├── PageHeader (h1 + actions)
    ├── ContentArea
    │   ├── StatsRow (4 metric cards)
    │   ├── PrimaryChart
    │   └── DataTable / List
    └── (optional) RightPanel
```

## Token Sync Protocol

When updating any token:

```
1. Update tokens/[category].json
2. Regenerate CSS variables (manual or Style Dictionary)
3. Update dark mode variant
4. Search codebase for any hardcoded equivalent
5. Update Storybook/docs
6. Verify contrast ratios still pass
```

## Tailwind Config Template

```js
// tailwind.config.js
export default {
  content: ['./src/**/*.{ts,tsx}'],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        brand: {
          DEFAULT: 'var(--color-brand)',
          hover:   'var(--color-brand-hover)',
        },
        surface: {
          DEFAULT:   'var(--color-surface)',
          secondary: 'var(--color-surface-secondary)',
        },
        border: {
          DEFAULT: 'var(--color-border)',
          subtle:  'var(--color-border-subtle)',
        },
      },
      fontFamily: {
        display: 'var(--font-display)',
        sans:    'var(--font-body)',
        mono:    'var(--font-mono)',
      },
    },
  },
};
```
