---
name: ui-ux-pro/components
description: "UI component specialist. Use when building any UI component: buttons, cards, forms, modals, dialogs, inputs, lists, navigation, tabs, tooltips, dropdowns, data tables, empty states, skeletons. Covers React, Angular, SwiftUI, Jetpack Compose, and cross-platform. Enforces all UI states (loading/error/empty/success) and accessible primitives."
tags: [components, ui, forms, modal, button, card, react, swiftui, compose]
---

# Components Specialist

## Scope
UI component creation · State management · Forms · Dialogs · Lists · Navigation

## Quick Decision

| Task | Read |
|---|---|
| Component states (loading/error/empty) | `references/states.md` |
| Form patterns + validation | `references/forms.md` |
| Dialog / modal patterns | `references/dialogs.md` |
| Accessible primitives guide | `references/primitives.md` |
| Platform-specific examples | `references/platform-examples.md` |

## Required UI States (Every Component)

Every data-dependent component MUST implement all 4:

```
Error    → Show always; never swallow; show near the action
Loading  → Only when no data; skeleton > spinner for known shapes
Empty    → Every list; one clear next action always
Success  → Confirm with toast; undo for destructive
```

### Loading Decision Tree
```
Has error?        → Show error + retry button
Loading + no data? → Show skeleton/spinner
Has data?         → Show data
Data is empty?    → Show empty state
```

## Component Requirements Checklist

Before delivering any component:

- [ ] All 4 UI states handled
- [ ] Buttons disabled during async ops
- [ ] `aria-label` on icon-only buttons
- [ ] Touch target min 44×44px
- [ ] Keyboard focus visible
- [ ] `className` passthrough (web) or style prop (native)
- [ ] Semantic tokens only (no hardcoded values)
- [ ] NEVER rebuild keyboard/focus behavior by hand

## Atomic Structure

```
atoms/      → Button, Input, Icon, Badge, Spinner, Avatar, Toggle
molecules/  → Card, FormField, NavItem, Toast, SearchBar
organisms/  → Header, Sidebar, Modal, DataTable, CommandPalette
patterns/   → OnboardingFlow, DashboardLayout, SearchWithFilters
```

## Accessible Primitive Libraries

| Platform | Library | Use for |
|---|---|---|
| Web | Radix UI / Base UI / React Aria | Any keyboard/focus behavior |
| iOS | SwiftUI native | All native controls |
| Android | Jetpack Compose + Material 3 | All native controls |
| Cross | @expo/ui | Compose or SwiftUI in Expo |

**Rule**: Never mix primitive systems on the same interaction surface.

## Anti-Patterns

- Button not disabled during submission → always disable
- Error swallowed silently → always surface
- List without empty state → always add
- Spinner when data exists → skeleton only when no data
- Rebuilding keyboard behavior → use primitives
