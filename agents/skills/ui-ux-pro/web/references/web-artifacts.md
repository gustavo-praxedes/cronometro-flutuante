# Web Artifacts Builder Reference

## When to Use
Complex claude.ai artifacts needing: state management, routing, shadcn/ui components.
NOT for: simple single-file HTML/JSX artifacts.

## Stack
React 18 + TypeScript + Vite + Parcel + Tailwind CSS 3.4.1 + shadcn/ui

## Scripts Location
All scripts live in `web/scripts/`:
- `init-artifact.sh` — scaffolds the full project
- `bundle-artifact.sh` — bundles to single HTML file
- `shadcn-components.tar.gz` — 40+ pre-built shadcn/ui components

## Workflow

```bash
# Step 1: Initialize (run from desired parent folder)
bash web/scripts/init-artifact.sh <project-name>
cd <project-name>

# Step 2: Develop
# Edit src/ files

# Step 3: Bundle to single HTML
bash ../web/scripts/bundle-artifact.sh
# Output: bundle.html (self-contained, all assets inlined)

# Step 4: Share bundle.html as artifact
```

## Requirements
- Node.js 18+ (auto-detected; Vite version pinned for Node 18)
- pnpm (auto-installed if missing)
- `shadcn-components.tar.gz` must be in the same folder as `init-artifact.sh`

## What init-artifact.sh Sets Up
- React + TypeScript via Vite
- Tailwind CSS 3.4.1 with shadcn/ui theming (CSS variables)
- Path aliases (`@/`) in tsconfig + vite.config
- 40+ shadcn/ui components pre-installed from tarball
- All Radix UI + form dependencies
- Parcel `.parcelrc` for bundling

## shadcn/ui Components Available
Full list: https://ui.shadcn.com/docs/components

Key components included:
- Button, Input, Label, Textarea, Select
- Card, Badge, Avatar, Separator
- Dialog, Sheet, Popover, Tooltip
- DropdownMenu, ContextMenu, Menubar
- Table, Form (react-hook-form + zod)
- Tabs, Accordion, Collapsible
- Toast (Sonner), Alert, AlertDialog
- Calendar, Command, Drawer

## bundle-artifact.sh Output
Installs: parcel, @parcel/config-default, parcel-resolver-tspaths, html-inline
Builds with Parcel (no source maps) → inlines all JS/CSS into `bundle.html`

## Anti-Slop Rules (Enforced)
```
❌ Centered layouts as default
❌ Purple gradients
❌ Uniform rounded corners everywhere
❌ Inter as headline font
```

## Project Structure After Init
```
<project-name>/
├── index.html
├── src/
│   ├── main.tsx
│   ├── App.tsx
│   ├── components/
│   │   └── ui/        ← shadcn components
│   └── lib/
│       └── utils.ts   ← cn() helper
├── tailwind.config.js
├── tsconfig.json
└── vite.config.ts
```
