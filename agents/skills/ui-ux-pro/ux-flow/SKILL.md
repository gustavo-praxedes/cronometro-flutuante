---
name: ui-ux-pro/ux-flow
description: "UX flow specialist. Use when designing user flows, navigation structure, information architecture, onboarding sequences, screen maps, hub-and-spoke navigation, linear flows, multi-step forms, or any experience that spans multiple screens. Produces flow diagrams, screen inventories, and navigation decisions."
tags: [ux-flow, navigation, information-architecture, onboarding, user-journey]
---

# UX Flow Specialist

## Scope
Navigation design · Information architecture · Screen flows · Onboarding · Multi-step UX

## Quick Decision

| Task | Read |
|---|---|
| Navigation model selection | `references/navigation-models.md` |
| Onboarding flow design | `references/onboarding.md` |
| Information architecture | `references/information-architecture.md` |
| Flow diagram conventions | `references/flow-conventions.md` |

## Navigation Models

| Model | Use for |
|---|---|
| Hub & Spoke | Dashboards, detail views, content browsing |
| Linear | Onboarding, checkout, forms, setup wizards |
| Tab (3–5) | Top-level app areas with equal importance |
| Hierarchical | Deep content trees (settings, docs) |
| Overlay | Filters, options, contextual actions |

## Flow Rules (Always Apply)

- Every flow: clear entry point + clear exit/success condition
- Key features: ≤3 taps from home
- Non-root screens: back navigation available
- Loading/empty/error: explicit recovery path defined
- Destructive actions: confirmation + undo path
- Risky steps: escape hatch visible

## Information Architecture Principles

- **Progressive disclosure**: reveal complexity only when needed
- **Miller's Law**: chunk into ≤7 items per level
- **Hick's Law**: minimize decisions per screen
- **Info pyramid**: hero → KPIs → detail → secondary

## Output Format

Always deliver:
1. ASCII flow diagram
2. Screen inventory (name + purpose + states)
3. Edge cases (loading, empty, error per screen)
4. Navigation decisions with rationale
5. Recommended next: page scaffold or component

## Flow Diagram Convention

```
[Entry] → (Decision) → [Screen] → [Screen]
                    ↘ [Alt path]

Legend:
[Screen]     — UI screen
(Decision)   — conditional branch
→            — primary flow
↘            — alternative/edge case
⊗            — exit/success
```
