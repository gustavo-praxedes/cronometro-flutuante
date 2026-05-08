# UI/UX Pro — Design Intelligence System

Master orchestrator + 16 specialist skills + 15 scripts para UI/UX
de nível de produção em Web, Mobile (iOS/Android) e Desktop.

---

## Estrutura Completa

```
ui-ux-pro/
├── SKILL.md                          ← Orchestrator — PONTO DE ENTRADA
├── DATABASE.md                       ← Base de conhecimento
├── README.md
├── SCRIPTS_TODO.md                   ← Status e uso de todos os scripts
│
├── design-system/
│   ├── SKILL.md
│   ├── references/
│   │   ├── tokens.md
│   │   ├── theming.md
│   │   ├── component-structure.md
│   │   └── setup.md
│   └── scripts/
│       ├── validate-tokens.js        ← Valida sincronismo JSON↔CSS↔Tailwind
│       └── export-tokens.js          ← Gera CSS vars + Tailwind config de tokens.json
│
├── components/
│   ├── SKILL.md
│   ├── references/
│   │   ├── states.md
│   │   └── forms.md
│   └── scripts/
│       └── audit-states.js           ← Detecta estados faltando (loading/error/empty)
│
├── accessibility/
│   ├── SKILL.md
│   ├── references/
│   │   └── wcag-checklist.md
│   └── scripts/
│       └── check-a11y.sh             ← axe-core WCAG 2.2 AA em URL ou HTML
│
├── ux-flow/
│   ├── SKILL.md
│   └── references/
│       └── navigation-models.md
│
├── ux-feedback/
│   ├── SKILL.md
│   └── references/
│       └── loading.md
│
├── motion/
│   ├── SKILL.md
│   └── references/
│       └── timing.md
│
├── typography/
│   ├── SKILL.md
│   ├── references/
│   │   └── font-pairing.md
│   └── scripts/
│       └── audit-typography.js       ← Detecta Inter/Roboto/Arial, escala errada
│
├── color/
│   ├── SKILL.md
│   ├── references/
│   │   └── palette.md
│   └── scripts/
│       └── check-contrast.js         ← WCAG 2.2 AA em pares de cores
│
├── mobile/
│   ├── SKILL.md
│   ├── references/
│   │   ├── ios.md
│   │   ├── android.md
│   │   ├── react-native.md
│   │   └── mobile-audit.md
│   └── scripts/
│       ├── mobile_audit.py           ← 50+ checks RN/Flutter automáticos
│       └── verify_ui.sh              ← Android ADB: screenshot + hierarquia + logs
│
├── web/
│   ├── SKILL.md
│   ├── references/
│   │   ├── react.md
│   │   ├── angular.md
│   │   ├── tailwind.md
│   │   ├── dark-theme.md
│   │   └── web-artifacts.md
│   └── scripts/
│       ├── init-artifact.sh          ← Scaffold React+Vite+Tailwind+shadcn/ui
│       ├── bundle-artifact.sh        ← Bundle para bundle.html único
│       └── shadcn-components.tar.gz  ← 40+ componentes pré-compilados
│
├── visual-design/
│   ├── SKILL.md
│   └── references/
│       ├── aesthetic-direction.md
│       ├── glassmorphism.md
│       └── canvas-design.md
│
├── ux-copy/
│   ├── SKILL.md
│   └── references/
│       └── copy-patterns.md
│
├── audit/
│   ├── SKILL.md
│   ├── references/
│   │   └── heuristics.md
│   └── scripts/
│       └── scan-antipatterns.js      ← 18 anti-padrões detectados automaticamente
│
├── persuasion/
│   ├── SKILL.md
│   └── references/
│       └── friction-reduction.md
│
├── ai-tools/
│   ├── SKILL.md
│   ├── references/
│   │   ├── stitch.md
│   │   ├── magic-ui.md
│   │   └── design-md.md
│   └── scripts/
│       ├── core.py                   ← Motor BM25 (requer data/ CSVs)
│       ├── search.py                 ← CLI de busca + design system
│       └── design_system.py          ← Gerador de design system
│
└── quality/
    └── SKILL.md
```

---

## Scripts — Referência Rápida

| Script | Pasta | Comando |
|---|---|---|
| Anti-padrões | `audit/scripts/` | `node scan-antipatterns.js ./src` |
| Contraste cores | `color/scripts/` | `node check-contrast.js src/index.css` |
| Estados faltando | `components/scripts/` | `node audit-states.js ./src` |
| Validar tokens | `design-system/scripts/` | `node validate-tokens.js --css src/index.css` |
| Exportar tokens | `design-system/scripts/` | `node export-tokens.js tokens.json` |
| A11y WCAG | `accessibility/scripts/` | `bash check-a11y.sh http://localhost:3000` |
| Tipografia | `typography/scripts/` | `node audit-typography.js ./src` |
| Mobile audit | `mobile/scripts/` | `python mobile_audit.py ./src` |
| Android verify | `mobile/scripts/` | `bash verify_ui.sh tela_nome` |
| Web artifact | `web/scripts/` | `bash init-artifact.sh meu-app` |
| Busca UI/UX | `ai-tools/scripts/` | `python search.py "query" --design-system` |

---

## Como Usar

**1. Ponto de entrada:** `SKILL.md` na raiz (orchestrator)
**2. Specialists:** cada pasta tem seu próprio `SKILL.md`
**3. Detalhes técnicos:** pasta `references/` de cada specialist
**4. Automação:** pasta `scripts/` de cada specialist
**5. Quality check:** orchestrator chama `quality/SKILL.md` no final

---

## Plataformas Cobertas

| Plataforma | Specialists principais |
|---|---|
| Web (React/Next.js) | web, components, design-system, visual-design |
| Web (Angular) | web, components, design-system |
| iOS (SwiftUI/Expo) | mobile → ios.md |
| Android (Compose/Expo) | mobile → android.md |
| Cross-platform (Expo Router) | mobile, components |
| Desktop | web + visual-design |
| Qualquer | accessibility, ux-flow, ux-feedback, motion, typography, color, audit |

---

## Princípios de Design (Todos os Specialists)

1. **UI States obrigatórios** — Loading/Error/Empty/Success — sem exceção
2. **Tokens semânticos** — sem hex hardcoded em componentes
3. **Acessibilidade por padrão** — WCAG 2.2 AA em tudo
4. **Anti-AI-slop** — direção estética clara, nunca genérico
5. **Touch-first no mobile** — 44pt mínimo, thumb zone, safe areas
6. **Motion com propósito** — só transform/opacity, max 200ms, prefers-reduced-motion
7. **Tipografia com personalidade** — sem Inter/Roboto/Arial como headline
8. **Quality check no final** — todo output passa por `quality/SKILL.md`
