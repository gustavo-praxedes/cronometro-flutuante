# Scripts — Status Completo

## ✅ TODOS IMPLEMENTADOS

### audit/scripts/
- `scan-antipatterns.js` — detecta anti-padrões: h-screen, hex hardcoded, z-index arbitrário, div+onClick, ScrollView+map, AsyncStorage com tokens, etc.

### color/scripts/
- `check-contrast.js` — verifica WCAG 2.2 AA em todos os pares de cores (texto normal 4.5:1, grande 3:1, UI 3:1)

### components/scripts/
- `audit-states.js` — detecta componentes sem loading/error/empty/success states, catches vazios, botões sem disabled

### design-system/scripts/
- `validate-tokens.js` — verifica sincronismo JSON ↔ CSS variables ↔ Tailwind config, hex hardcoded no src
- `export-tokens.js` — converte tokens.json → CSS variables + tailwind.config.tokens.js automaticamente

### accessibility/scripts/
- `check-a11y.sh` — roda axe-core WCAG 2.2 AA em URL ou arquivo HTML local

### typography/scripts/
- `audit-typography.js` — detecta Inter/Roboto/Arial como fonte primária, tamanhos fora da escala, line-height problemático

### web/scripts/
- `init-artifact.sh` — scaffold React + Vite + Tailwind 3.4.1 + shadcn/ui (40+ componentes)
- `bundle-artifact.sh` — empacota em bundle.html único
- `shadcn-components.tar.gz` — componentes shadcn/ui pré-compilados

### mobile/scripts/
- `mobile_audit.py` — 50+ checks em código React Native/Flutter (touch targets, FlatList, SecureStore, etc.)
- `verify_ui.sh` — Android ADB: screenshot + dump hierarquia UI + JS logs

### ai-tools/scripts/
- `core.py` — motor de busca BM25 para a knowledge base CSV
- `search.py` — CLI: busca por domínio, stack e geração de design system
- `design_system.py` — gerador de design system completo (multi-domínio)
  ⚠️ Requer arquivos CSV em `ai-tools/data/` — não incluídos neste pacote

---

## 📋 USO RÁPIDO

```bash
# Anti-padrões no código
node audit/scripts/scan-antipatterns.js ./src

# Contraste de cores (pares built-in + arquivo de tokens)
node color/scripts/check-contrast.js src/index.css

# Estados faltando em componentes
node components/scripts/audit-states.js ./src

# Tokens em sincronia
node design-system/scripts/validate-tokens.js --tokens tokens.json --css src/index.css --tailwind tailwind.config.js

# Exportar tokens
node design-system/scripts/export-tokens.js tokens.json

# Acessibilidade (requer URL ou path)
bash accessibility/scripts/check-a11y.sh http://localhost:3000

# Tipografia
node typography/scripts/audit-typography.js ./src

# Auditoria mobile
python mobile/scripts/mobile_audit.py ./src

# Verificação Android (emulador rodando)
bash mobile/scripts/verify_ui.sh nome_tela

# Scaffold web artifact
bash web/scripts/init-artifact.sh meu-app

# Busca de estilos/paletas (requer data/ CSVs)
python ai-tools/scripts/search.py "dark SaaS dashboard" --design-system
```

---

## ⚠️ DATA FILES NECESSÁRIOS (ai-tools/scripts/)

`core.py`, `search.py` e `design_system.py` requerem CSVs em `ai-tools/data/`:

```
ai-tools/data/
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
    └── ... (um por stack)
```
