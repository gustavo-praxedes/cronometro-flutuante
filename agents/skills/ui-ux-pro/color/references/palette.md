# Color Palette Reference

## Complete Palette Structure

```json
{
  "color": {
    "brand": {
      "primary":  "#6C63FF",
      "hover":    "#5A52E0",
      "light":    "#A37EF5",
      "subtle":   "rgba(108,99,255,0.12)",
      "surface":  "#F0EEFF"
    },
    "semantic": {
      "success":       "#22C55E",
      "success-light": "#DCFCE7",
      "warning":       "#F59E0B",
      "warning-light": "#FEF3C7",
      "error":         "#EF4444",
      "error-light":   "#FEE2E2",
      "info":          "#3B82F6",
      "info-light":    "#DBEAFE"
    },
    "neutral": {
      "50":  "#F9FAFB",
      "100": "#F3F4F6",
      "200": "#E5E7EB",
      "300": "#D1D5DB",
      "400": "#9CA3AF",
      "500": "#6B7280",
      "600": "#4B5563",
      "700": "#374151",
      "800": "#1F2937",
      "900": "#111827",
      "950": "#030712"
    },
    "dataviz": {
      "purple": "#8251EE",
      "blue":   "#3B82F6",
      "green":  "#10B981",
      "yellow": "#F59E0B",
      "red":    "#EF4444",
      "pink":   "#EC4899",
      "cyan":   "#06B6D4"
    }
  }
}
```

## Dark Mode Color Scale

```css
/* Backgrounds (elevation scale) */
--bg-1: hsl(240, 6%, 10%);   /* page background */
--bg-2: hsl(240, 5%, 12%);   /* card surface */
--bg-3: hsl(240, 5%, 14%);   /* elevated card */
--bg-4: hsl(240, 4%, 18%);   /* input background */
--bg-5: hsl(240, 4%, 22%);   /* hover state */
--bg-6: hsl(240, 4%, 26%);   /* active state */

/* Text */
--text-primary:   #FFFFFF;
--text-secondary: #A1A1AA;
--text-muted:     #71717A;

/* Borders */
--border-subtle:  hsla(0, 0%, 100%, 0.06);
--border-default: hsla(0, 0%, 100%, 0.12);
--border-strong:  hsla(0, 0%, 100%, 0.20);
```

## Contrast Ratios (WCAG)

| Pair | Ratio | Pass? |
|---|---|---|
| White on #6C63FF (brand) | 3.2:1 | ✅ Large text only |
| White on #5A52E0 (brand-hover) | 4.5:1 | ✅ All text |
| #0F172A on white | 19.7:1 | ✅ |
| #475569 on white | 5.1:1 | ✅ |
| #6B7280 on white | 4.6:1 | ✅ (barely) |
| #9CA3AF on white | 2.4:1 | ❌ |

**Rule**: neutral-500 (#6B7280) is the minimum for body text on white.
Prefer neutral-600 (#4B5563) for safety margin.

## Contrast Checker Formula

```
Relative luminance L = 0.2126 R + 0.7152 G + 0.0722 B
Contrast ratio = (L1 + 0.05) / (L2 + 0.05)

Minimum targets:
- Normal text: 4.5:1
- Large text (≥18px or ≥14px bold): 3:1
- UI components: 3:1
```

## Dataviz Palette Rules

- Use in order when adding series (don't skip)
- All 7 colors distinguishable by colorblind users
- Always pair color with shape/pattern/label for data
- Never use more than 7 series without grouping

## Anti-Pattern Colors

```
❌ #6366F1 (indigo-500) on white → AI cliché
❌ #8B5CF6 (violet-500) on white gradient → AI marker
❌ Using the same purple as your neighbor
❌ Teal + coral (#14B8A6 + #F97316) → overused combo
```
