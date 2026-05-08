---
name: ui-ux-pro/ai-tools
description: "AI design tools specialist. Use when working with Google Stitch (AI UI generator), Magic UI by 21st.dev, the UI/UX search engine scripts, DESIGN.md system documentation, or any AI-assisted design workflow. Covers prompt engineering for UI generation, design system documentation for AI, and integrating AI-generated UI into production codebases."
tags: [ai-tools, stitch, magic-ui, ai-design, prompt-engineering, design-md, search]
---

# AI Tools Specialist

## Scope
Google Stitch · Magic UI (21st.dev) · UI/UX Search Engine · DESIGN.md

## Quick Decision

| Task | Read / Run |
|---|---|
| Google Stitch prompts | `references/stitch.md` |
| Magic UI generation | `references/magic-ui.md` |
| DESIGN.md documentation | `references/design-md.md` |
| Search styles/colors/fonts/stacks | `scripts/search.py` |
| Generate full design system | `scripts/search.py --design-system` |

## Search Engine Scripts (scripts/)

Three scripts provide a searchable UI/UX knowledge base:

```bash
# Style search
python ai-tools/scripts/search.py "glassmorphism dark SaaS" --domain style

# Color palette for product type
python ai-tools/scripts/search.py "fintech mobile" --domain color

# Stack guidelines
python ai-tools/scripts/search.py "list performance" --stack react-native

# Full design system (recommended starting point)
python ai-tools/scripts/search.py "beauty spa mobile" --design-system -p "Serenity"
```

**Note**: Scripts require CSV data files in `ai-tools/data/` — see `references/stitch.md` for full list.

## Google Stitch

Prompt template:
```
[Screen type] for [context]
Key Features: [list]
Visual Style: [color] [aesthetic] [layout]
Platform: [Mobile/Web/Responsive]
```

Rules:
- Specific beats generic
- Multi-screen: list each screen as bullet first
- Annotate to iterate (not full rewrites)
- Treat output as high-fidelity wireframe — not production-ready

## Magic UI (21st.dev)

1. Push for unconventional style descriptions
2. Request 3 variations minimum
3. Select direction, then integrate
4. Always: TypeScript + accessible + responsive

## DESIGN.md

Structure for AI design documentation:
1. Visual Theme & Atmosphere
2. Color Palette & Roles (descriptive name + hex + role)
3. Typography Rules
4. Component Stylings
5. Layout Principles

Language rule: descriptive not technical
("Generously rounded" not "rounded-xl") ("Ocean-deep Cerulean (#0077B6)" not "blue")
