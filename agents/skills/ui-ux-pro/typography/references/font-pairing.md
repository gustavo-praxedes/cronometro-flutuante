# Font Pairing Reference

## Pairing Principles

- Contrast personalities: don't pair similar fonts
- One expressive (display/headings), one restrained (body)
- Max 2 families per project
- Both must be web-safe or properly loaded

## Curated Pairings

### Luxury / Editorial
```
Display: Cormorant Garamond (serif, elegant)
Body:    Source Serif 4 (readable, warm)
Mono:    JetBrains Mono
Vibe:    Fashion, finance, premium SaaS
```

### Modern / Clean
```
Display: Syne (geometric, distinctive)
Body:    DM Sans (neutral, readable)
Mono:    Geist Mono
Vibe:    Tech, productivity, developer tools
```

### Bold / Expressive
```
Display: Clash Display (geometric, strong)
Body:    Instrument Sans (clean, approachable)
Mono:    Commit Mono
Vibe:    Creative agencies, portfolios, landing pages
```

### Warm / Organic
```
Display: Playfair Display (classic, trustworthy)
Body:    Nunito (rounded, friendly)
Mono:    Fira Code
Vibe:    Health, food, lifestyle, editorial
```

### Experimental / Distinctive
```
Display: Cabinet Grotesk (variable, modern)
Body:    Figtree (geometric, neutral)
Mono:    JetBrains Mono
Vibe:    Startups, innovation, creative SaaS
```

### Technical / Minimal
```
Display: Plus Jakarta Sans (clean, versatile)
Body:    Onest (geometric, neutral)
Mono:    Geist Mono
Vibe:    Developer tools, dashboards, fintech
```

### Serif-Heavy / Literary
```
Display: Fraunces (optical, literary)
Body:    Lora (warm, readable serif)
Mono:    Commit Mono
Vibe:    Publishing, journalism, knowledge tools
```

## Google Fonts Loading (Web)

```html
<!-- Preconnect for performance -->
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>

<!-- Variable font preferred (single file) -->
<link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300..800&display=swap" rel="stylesheet">
```

```css
:root {
  --font-display: 'Plus Jakarta Sans', sans-serif;
  --font-body: 'Onest', sans-serif;
}
```

## Sizing + Weight Pairing

| Context | Size | Weight | Font |
|---|---|---|---|
| Hero headline | 48–72px | 700–800 | Display |
| Page title | 32–40px | 700 | Display |
| Section title | 24–28px | 600–700 | Display or Body |
| Card title | 18–22px | 600 | Body |
| Body text | 16–18px | 400 | Body |
| Caption | 12–14px | 400 | Body |
| Label | 13–14px | 500 | Body |
| Code | 13–14px | 400 | Mono |

## Platform Native Fonts

| Platform | System Font | When to use |
|---|---|---|
| iOS | SF Pro | Native iOS apps |
| macOS | SF Pro | Native macOS apps |
| Android | Roboto / Google Sans | Native Android apps |
| Windows | Segoe UI | Windows apps |
| Web | (custom) | Always use custom for branding |
