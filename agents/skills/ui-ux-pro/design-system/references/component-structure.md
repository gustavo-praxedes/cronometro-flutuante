# Component Library Structure Reference

## Folder Structure

```
design-system/
├── tokens/
│   ├── colors.json
│   ├── typography.json
│   ├── spacing.json
│   ├── shadows.json
│   ├── motion.json
│   └── radius.json
├── components/
│   ├── atoms/        → Button, Input, Icon, Badge, Spinner, Avatar, Toggle
│   ├── molecules/    → Card, FormField, NavItem, Toast, SearchBar
│   └── organisms/   → Header, Sidebar, Modal, DataTable, CommandPalette
├── patterns/
│   ├── onboarding.md
│   ├── empty-states.md
│   ├── loading.md
│   └── errors.md
└── guidelines/
    ├── voice-tone.md
    ├── accessibility.md
    └── imagery.md
```

## Component File Structure

```tsx
// atoms/Button.tsx

import { cva, type VariantProps } from "class-variance-authority";
import { cn } from "@/lib/utils";
import type { ButtonHTMLAttributes } from "react";

const buttonVariants = cva(
  // Base styles — always applied
  [
    "inline-flex items-center justify-center gap-2",
    "rounded-md font-medium transition-colors",
    "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand",
    "disabled:pointer-events-none disabled:opacity-50",
  ],
  {
    variants: {
      variant: {
        primary:   "bg-brand text-white hover:bg-brand-hover",
        secondary: "bg-surface border border-border hover:bg-surface-hover",
        ghost:     "hover:bg-surface-hover",
        destructive: "bg-error text-white hover:bg-error/90",
      },
      size: {
        sm:   "h-8 px-3 text-sm",
        md:   "h-10 px-4 text-sm",
        lg:   "h-11 px-6 text-base",
        icon: "size-10",  // size-* for squares
      },
    },
    defaultVariants: { variant: "primary", size: "md" },
  }
);

interface ButtonProps
  extends ButtonHTMLAttributes<HTMLButtonElement>,
    VariantProps<typeof buttonVariants> {
  isLoading?: boolean;
}

export function Button({
  variant,
  size,
  className,
  isLoading,
  disabled,
  children,
  ...props
}: ButtonProps) {
  return (
    <button
      data-slot="button"
      className={cn(buttonVariants({ variant, size }), className)}
      disabled={disabled || isLoading}
      aria-busy={isLoading}
      {...props}
    >
      {isLoading && <Spinner size="sm" aria-hidden />}
      {children}
    </button>
  );
}
```

## Setup Checklist (New Design System)

- [ ] Token files created (JSON + CSS)
- [ ] Brand color configured + dark mode variant
- [ ] Typography scale set
- [ ] Spacing scale confirmed
- [ ] `cn()` utility installed (clsx + tailwind-merge)
- [ ] CVA installed (class-variance-authority)
- [ ] Accessible primitive library chosen (Radix / Base UI)
- [ ] `data-slot` convention documented
- [ ] Storybook or similar docs system configured
- [ ] Dark mode toggle implemented
