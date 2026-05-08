# Navigation Models Reference

## Hub & Spoke

```
        [Hub/Dashboard]
       /       |       \
[Detail]  [Detail]  [Detail]
    \         |        /
        [Hub/Dashboard]
```

**Use for**: dashboards, content browsing, e-commerce catalogs
**Rules**:
- Hub always reachable (back nav or tab)
- Detail views push onto stack
- Cross-link to sibling spokes via hub, not directly

---

## Linear Flow

```
[Step 1] → [Step 2] → [Step 3] → [Confirm] → [Success]
    ↑          ↑          ↑
  [Back]     [Back]     [Back]
```

**Use for**: onboarding, checkout, setup wizard, multi-step form
**Rules**:
- Back always available (except step 1)
- Progress indicator visible
- Skip optional steps when possible
- Validate before advancing
- Exit/cancel available

---

## Tab Navigation

```
[Tab 1] [Tab 2] [Tab 3] [Tab 4] [Tab 5]
─────────────────────────────────────────
[              Content                  ]
```

**Use for**: 3–5 co-equal top-level areas
**Rules**:
- Max 5 tabs (iOS HIG and Material guidelines)
- Each tab maintains its own navigation stack
- Active tab highlighted
- Labels always visible (not icon-only for primary nav)
- Bottom tab bar on mobile (thumb zone)

---

## Hierarchical / Drawer

```
[≡ Menu]
├── Section A
│   ├── Item 1
│   └── Item 2
├── Section B
└── Section C
```

**Use for**: settings, documentation, admin panels, many categories
**Rules**:
- Collapsible sections for depth
- Current location indicated
- Search available at root level
- Max 3 levels deep

---

## Onboarding Flow (Elite Pattern)

```
[Screen 1: Promise]
  "What you'll achieve" — one strong headline + visual
  CTA: "Get Started" (not "Sign Up")

[Screen 2: Immediate value]
  One action that delivers value BEFORE full signup
  Minimal form (email only if needed)
  Progress: "1 of 3"

[Screen 3: Personalization]
  Max 3 questions, visual not text
  Skip always visible

[Screen 4: Aha Moment]
  First real success moment
  Genuine celebration (not excessive)
  "You just [did value thing]"
```

---

## Screen Inventory Template

| Screen | Purpose | Primary action | States needed |
|---|---|---|---|
| Home | Browse content | Open item | Loading, empty, error |
| Detail | View single item | Share/Save | Loading, error |
| Create | Add new item | Submit | Validation, submitting, success |
| Settings | Configure | Save | Loading, success |
| Search | Find content | Select result | Loading, empty, error |

---

## Key Distance Rules

- Core feature: accessible in ≤3 taps from home
- Settings: ≤2 taps (usually persistent in nav)
- Help/support: ≤3 taps
- Destructive actions: ≥2 taps (prevent accidental)
- Account deletion: ≥3 taps (requires explicit intent)
