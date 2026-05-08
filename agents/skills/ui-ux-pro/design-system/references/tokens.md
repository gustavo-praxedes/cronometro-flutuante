# Design Tokens Reference

## Token Categories

### Colors
```json
{
  "color": {
    "brand": {
      "primary": "#6C63FF",
      "primary-dark": "#5A52E0",
      "accent": "#FF6B6B",
      "surface": "#F8F7FF"
    },
    "semantic": {
      "success": "#22C55E",
      "warning": "#F59E0B",
      "error": "#EF4444",
      "info": "#3B82F6"
    },
    "neutral": {
      "900": "#111827", "800": "#1F2937", "600": "#4B5563",
      "400": "#9CA3AF", "200": "#E5E7EB", "50": "#F9FAFB"
    }
  }
}
```

### Typography
```json
{
  "typography": {
    "display": { "size": "48px", "weight": "700", "lineHeight": "1.1" },
    "h1":      { "size": "36px", "weight": "700", "lineHeight": "1.2" },
    "h2":      { "size": "28px", "weight": "600", "lineHeight": "1.3" },
    "h3":      { "size": "22px", "weight": "600", "lineHeight": "1.4" },
    "body":    { "size": "16px", "weight": "400", "lineHeight": "1.6" },
    "small":   { "size": "14px", "weight": "400", "lineHeight": "1.5" },
    "caption": { "size": "12px", "weight": "400", "lineHeight": "1.5" }
  }
}
```

### Spacing
```json
{
  "spacing": {
    "xs": "4px", "sm": "8px", "md": "16px",
    "lg": "24px", "xl": "32px", "2xl": "48px", "3xl": "64px"
  }
}
```

### Radius
```json
{
  "radius": {
    "sm": "4px", "md": "8px", "lg": "12px", "xl": "16px", "full": "9999px"
  }
}
```

### Shadow (Elevation)
```json
{
  "shadow": {
    "sm": "0 1px 3px rgba(0,0,0,0.12)",
    "md": "0 4px 12px rgba(0,0,0,0.15)",
    "lg": "0 8px 24px rgba(0,0,0,0.18)",
    "xl": "0 20px 60px rgba(0,0,0,0.22)"
  }
}
```

### Motion
```json
{
  "motion": {
    "fast":   "150ms ease-out",
    "normal": "250ms ease-in-out",
    "slow":   "400ms cubic-bezier(0.34, 1.56, 0.64, 1)"
  }
}
```

## CSS Variables (from JSON)

```css
:root {
  /* Brand */
  --color-brand: #6C63FF;
  --color-brand-dark: #5A52E0;

  /* Semantic */
  --color-success: #22C55E;
  --color-warning: #F59E0B;
  --color-error: #EF4444;
  --color-info: #3B82F6;

  /* Spacing */
  --space-xs: 4px; --space-sm: 8px; --space-md: 16px;
  --space-lg: 24px; --space-xl: 32px;

  /* Radius */
  --radius-sm: 4px; --radius-md: 8px;
  --radius-lg: 12px; --radius-xl: 16px;

  /* Motion */
  --motion-fast: 150ms ease-out;
  --motion-normal: 250ms ease-in-out;
}

[data-theme="dark"] {
  --color-brand: #7C74FF;
  /* ... dark overrides */
}
```

## Token Naming Convention

```
[category]-[variant]-[modifier]
color-brand-primary
color-semantic-error
space-md
radius-lg
shadow-xl
motion-fast
```

## Sync Checklist

- [ ] JSON token updated
- [ ] CSS variable updated
- [ ] Dark mode variant updated
- [ ] Component usage verified
- [ ] No hardcoded values remain
