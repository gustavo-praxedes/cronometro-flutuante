# Google Stitch & Search Scripts Reference

## Search Engine Scripts (ai-tools/scripts/)

Three scripts form the UI/UX search engine:

| Script | Role |
|---|---|
| `core.py` | BM25 search engine — indexes CSV data files |
| `search.py` | CLI entry point — domain search + design system generation |
| `design_system.py` | Design system generator — aggregates multi-domain results |

## Requirements
- Python 3.8+
- CSV data files in `data/` folder (see Data Files section below)

## Usage

```bash
# Domain search
python ai-tools/scripts/search.py "SaaS dashboard dark mode" --domain style

# Stack-specific guidelines
python ai-tools/scripts/search.py "list performance" --stack react-native

# Full design system (most useful)
python ai-tools/scripts/search.py "fintech mobile app" --design-system -p "MyBank"

# Markdown output
python ai-tools/scripts/search.py "beauty spa" --design-system -p "Serenity" --format markdown
```

## Available Domains

| Domain | Searches |
|---|---|
| `style` | UI styles: glassmorphism, minimalism, brutalism, dark mode |
| `color` | Color palettes by product type |
| `chart` | Chart types and library recommendations |
| `landing` | Landing page patterns and CTA strategies |
| `product` | Product type recommendations (SaaS, fintech, etc.) |
| `ux` | UX best practices and anti-patterns |
| `typography` | Font pairings and Google Fonts |
| `prompt` | CSS keywords and implementation checklists |
| `react` | React/Next.js performance patterns |
| `web` | Web interface ARIA/accessibility guidelines |

## Available Stacks

`html-tailwind` · `react` · `nextjs` · `vue` · `nuxtjs` · `svelte`
`swiftui` · `react-native` · `flutter` · `shadcn`

## Auto-Detection
`search.py` auto-detects the best domain from the query when `--domain` is omitted.

## Data Files Needed (NOT included)
The scripts require CSV files in a `data/` folder:
```
data/
├── styles.csv
├── colors.csv
├── charts.csv
├── landing.csv
├── products.csv
├── ux-guidelines.csv
├── typography.csv
├── prompts.csv
├── ui-reasoning.csv
├── icons.csv
├── react-performance.csv
├── web-interface.csv
└── stacks/
    ├── html-tailwind.csv
    ├── react.csv
    ├── nextjs.csv
    ├── react-native.csv
    └── ... (one per stack)
```
These CSV files are the knowledge base of the ui-ux-pro-max system.
Without them, the scripts will return "File not found" errors.

---

## Google Stitch (AI UI Generator — No Scripts Needed)

Prompt template:
```
[Screen type] for [context]
Key Features: [bullet list]
Visual Style: [color scheme] [aesthetic] [layout]
Platform: [iOS / Responsive Web / Desktop]
```

Rules:
- Specific beats generic — name components, layout, aesthetic
- Multi-screen: list each screen as bullet before generating
- Iterate with annotations, not full rewrites
- Export → treat as high-fidelity wireframe, not production code

---

## Magic UI (21st.dev — No Scripts Needed)

1. Push for unconventional style ("avant-garde", "immersive", "glassmorphic")
2. Request 3 variations minimum
3. Select direction
4. Integrate: TypeScript + accessible + responsive
