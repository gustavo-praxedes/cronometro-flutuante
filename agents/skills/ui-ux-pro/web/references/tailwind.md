# Tailwind CSS Reference

## Core Utilities (Always Use)

```
cn()        → clsx + tailwind-merge (class merging)
h-dvh       → dynamic viewport height (not h-screen)
size-*      → squares (not w-* + h-* separately)
```

## Z-Index Scale (Fixed — No Arbitrary Values)

```css
z-10  → sticky headers, dropdowns
z-20  → floating elements, tooltips
z-30  → modals, dialogs
z-40  → notifications, toasts
z-50  → highest (global overlays)
```

## Safe Area (Fixed Elements)

```html
<!-- Bottom nav safe area -->
<nav class="fixed bottom-0 pb-[env(safe-area-inset-bottom)]">

<!-- Fixed header -->
<header class="fixed top-0 pt-[env(safe-area-inset-top)]">
```

## Typography Classes

```html
<h1 class="text-balance font-bold tracking-tight">         <!-- headings -->
<p  class="text-pretty leading-relaxed">                   <!-- body -->
<span class="tabular-nums font-mono text-sm">              <!-- data -->
<p  class="line-clamp-3 text-pretty">                      <!-- truncated -->
<span class="truncate">                                     <!-- single line -->
```

## Responsive Pattern (Mobile-First)

```html
<!-- Mobile → Tablet → Desktop -->
<div class="
  grid grid-cols-1
  sm:grid-cols-2
  lg:grid-cols-3
  gap-4
">
```

## Dark Mode Pattern

```html
<!-- Class-based dark mode -->
<div class="bg-white dark:bg-neutral-900 text-neutral-900 dark:text-white">

<!-- CSS variable approach (preferred) -->
<div class="bg-surface text-foreground">
```

## Tailwind Config — Semantic Tokens

```js
// tailwind.config.js
export default {
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        brand:    { DEFAULT: 'var(--color-brand)', hover: 'var(--color-brand-hover)' },
        surface:  { DEFAULT: 'var(--color-surface)', secondary: 'var(--color-surface-secondary)' },
        foreground: { DEFAULT: 'var(--color-foreground)', muted: 'var(--color-foreground-muted)' },
        border:   { DEFAULT: 'var(--color-border)', subtle: 'var(--color-border-subtle)' },
        error:    'var(--color-error)',
        success:  'var(--color-success)',
        warning:  'var(--color-warning)',
      },
      fontFamily: {
        sans:    ['var(--font-body)', 'sans-serif'],
        display: ['var(--font-display)', 'serif'],
        mono:    ['var(--font-mono)', 'monospace'],
      },
    },
  },
};
```

## Component Patterns

```tsx
// cn() usage
import { cn } from "@/lib/utils";

<div className={cn(
  "base rounded-lg p-4 transition-colors",
  isActive    && "ring-2 ring-brand",
  isDisabled  && "opacity-50 cursor-not-allowed",
  className,  // always allow override at end
)} />

// CVA + cn combo
const variants = cva("base", {
  variants: {
    variant: { primary: "bg-brand text-white", ghost: "bg-transparent" },
    size:    { sm: "px-2 py-1 text-sm", md: "px-4 py-2", lg: "px-6 py-3 text-lg" },
  },
  defaultVariants: { variant: "primary", size: "md" },
});

<button className={cn(variants({ variant, size }), className)} />
```

## Glass Effects

```html
<!-- Dark glass card -->
<div class="
  backdrop-blur-md
  bg-white/5
  border border-white/10
  rounded-xl
">

<!-- Light glass card -->
<div class="
  backdrop-blur-md
  bg-white/80
  border border-black/8
  rounded-xl
">
```

## Anti-Patterns

```
❌ h-screen      → h-dvh
❌ w-10 h-10     → size-10
❌ z-[999]       → use z-index scale (z-10/20/30/40/50)
❌ bg-purple-500 gradient on white → AI slop
❌ will-change: transform always on → only during animation
❌ No className passthrough on components
```
