# Theming Reference (Light + Dark)

## Dark Theme Color Scale

```css
/* Backgrounds: deep to elevated */
--bg-1: hsl(240, 6%, 10%);  /* page background */
--bg-2: hsl(240, 5%, 12%);  /* card background */
--bg-3: hsl(240, 5%, 14%);  /* elevated card */
--bg-4: hsl(240, 4%, 18%);  /* input background */
--bg-5: hsl(240, 4%, 22%);  /* hover surface */
--bg-6: hsl(240, 4%, 26%);  /* active surface */

/* Text */
--text-primary:   #FFFFFF;
--text-secondary: #A1A1AA;
--text-muted:     #71717A;

/* Borders */
--border-subtle:  hsla(0, 0%, 100%, 0.08);
--border-default: hsla(0, 0%, 100%, 0.12);
--border-strong:  hsla(0, 0%, 100%, 0.20);
```

## Light Theme Rules

```css
/* Text minimums */
--text-primary:   #0F172A;  /* slate-900, never lighter */
--text-secondary: #475569;  /* slate-600 minimum */
--text-muted:     #64748B;  /* slate-500 minimum */

/* Cards */
--bg-card: rgba(255, 255, 255, 0.85);  /* NOT transparent */
--border-card: #E2E8F0;                /* visible */

/* Glassmorphism light mode */
--glass-bg:     rgba(255, 255, 255, 0.80);  /* minimum opacity */
--glass-border: rgba(0, 0, 0, 0.08);
```

## Glassmorphism Patterns

### Dark glass card
```css
.glass-card {
  backdrop-filter: blur(12px);
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.10);
  border-radius: var(--radius-xl);
}
```

### Light glass card
```css
.glass-card-light {
  backdrop-filter: blur(12px);
  background: rgba(255, 255, 255, 0.80);  /* NOT 0.05! */
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: var(--radius-xl);
}
```

## Anti-Patterns

| Bad | Good |
|-----|------|
| bg-white/10 in light mode | bg-white/80 minimum |
| text-gray-400 for body | text-slate-600 minimum |
| border-white/10 in light mode | border-gray-200 |
| Purple gradients | Brand color with semantic purpose |
| Glow as affordance | Elevation shadows |

## Dark Mode Implementation (Tailwind)

```js
// tailwind.config.js
export default {
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        surface: {
          DEFAULT: '#ffffff',
          dark: 'hsl(240, 6%, 10%)',
        }
      }
    }
  }
}
```

## Component Token Usage

```tsx
// Always use semantic tokens
<div className="bg-surface dark:bg-surface-dark text-foreground dark:text-foreground-dark">

// Never hardcode
<div className="bg-white text-gray-900">  // BAD
```
