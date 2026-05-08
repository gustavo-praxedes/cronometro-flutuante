# Dark Theme Implementation Reference

## Stack
React 18 + Tailwind CSS 3 + Framer Motion 11 + TypeScript

## CSS Variables Setup

```css
/* globals.css */
:root {
  /* Brand */
  --color-brand:       #8251EE;
  --color-brand-hover: #9366F5;
  --color-brand-light: #A37EF5;
  --color-brand-subtle: rgba(130, 81, 238, 0.15);

  /* Neutral backgrounds */
  --bg-1: hsl(240, 6%, 10%);
  --bg-2: hsl(240, 5%, 12%);
  --bg-3: hsl(240, 5%, 14%);
  --bg-4: hsl(240, 4%, 18%);
  --bg-5: hsl(240, 4%, 22%);
  --bg-6: hsl(240, 4%, 26%);

  /* Text */
  --text-primary:   #FFFFFF;
  --text-secondary: #A1A1AA;
  --text-muted:     #71717A;

  /* Borders */
  --border-subtle:  hsla(0, 0%, 100%, 0.08);
  --border-default: hsla(0, 0%, 100%, 0.12);
  --border-strong:  hsla(0, 0%, 100%, 0.20);

  /* Semantic */
  --color-success: #10B981;
  --color-warning: #F59E0B;
  --color-error:   #EF4444;
  --color-info:    #3B82F6;
}

html { color-scheme: dark; }

body {
  background: var(--bg-1);
  color: var(--text-primary);
}
```

## Glass Utility Classes

```css
@layer components {
  .glass {
    backdrop-filter: blur(12px);
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.10);
  }
  .glass-card {
    @apply glass rounded-xl;
  }
  .glass-panel {
    backdrop-filter: blur(20px);
    background: rgba(0, 0, 0, 0.40);
    border: 1px solid rgba(255, 255, 255, 0.05);
  }
  .glass-overlay {
    backdrop-filter: blur(4px);
    background: rgba(0, 0, 0, 0.60);
  }
}
```

## Scrollbar Styling

```css
::-webkit-scrollbar       { width: 8px; height: 8px; }
::-webkit-scrollbar-track { background: var(--bg-2); }
::-webkit-scrollbar-thumb { background: var(--bg-5); border-radius: 9999px; }
::-webkit-scrollbar-thumb:hover { background: var(--bg-6); }
```

## Focus Ring

```css
*:focus-visible {
  outline: none;
  box-shadow: 0 0 0 2px var(--color-brand);
}
```

## App Shell Structure

```tsx
// main.tsx
import { BrowserRouter } from 'react-router-dom';
import { AnimatePresence } from 'framer-motion';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>
);

// App.tsx
export default function App() {
  return (
    <AppShell>
      <AnimatePresence mode="wait">
        <Routes>
          <Route path="/" element={<Dashboard />} />
        </Routes>
      </AnimatePresence>
    </AppShell>
  );
}
```

## Typography Mapping (Dark)

| Element | Classes |
|---|---|
| Page title | `text-2xl font-semibold text-[var(--text-primary)]` |
| Section title | `text-lg font-semibold text-[var(--text-primary)]` |
| Card title | `text-base font-medium text-[var(--text-primary)]` |
| Body | `text-sm text-[var(--text-secondary)]` |
| Caption | `text-xs text-[var(--text-muted)]` |
| Label | `text-sm font-medium text-[var(--text-secondary)]` |

## Color Usage Guide

| Use | Color | Class / Variable |
|---|---|---|
| Primary CTA | Brand | `bg-[var(--color-brand)]` |
| Page background | bg-1 | `bg-[var(--bg-1)]` |
| Card | bg-2 | `bg-[var(--bg-2)]` |
| Elevated card | bg-3 | `bg-[var(--bg-3)]` |
| Input | bg-2 | `bg-[var(--bg-2)]` |
| Default border | border-default | `border-[var(--border-default)]` |
| Subtle border | border-subtle | `border-[var(--border-subtle)]` |
