# Análise do Menu de Configurações — Settings

## Estrutura Atual

3 seções no menu lateral, 9 destinos flat (sem sub-abas):

### Geral (settings_section_general)
| Destino | Subtítulo | Grupo | Settings |
|---------|-----------|-------|----------|
| **Aparência** | Temas, cores e fontes | Tema | ThemeSelector, FontSelector |
| | | Cores | bgColor, textColor (ColorPickerDialog) |
| **Comportamento** | Auto-início, botões e limites | Geral | 9 toggles: autoLaunch, showHours, showSeconds, showButtons, keepScreenOn, focusModeEnabled, isBeepEnabled, isVibrationEnabled |
| | | Limite de Tempo | TimeLimitField (importado de feature/stopwatch) |
| **Overlay** | Escala, raio e posição | Overlay | Scale 0.5x-1.5x, CornerRadius 0-50dp |

### Tools (settings_section_tools)
| Destino | Estado |
|---------|--------|
| **Cronômetro** | Placeholder "Em desenvolvimento" |
| **Contagem Regressiva** | Placeholder "Em desenvolvimento" |

### Info (settings_section_info)
| Destino | Subtítulo | Conteúdo |
|---------|-----------|----------|
| **Sobre** | Informações e links do projeto | Card identidade + Ações (Apoiar, Bug, Código Fonte) + Informações (Novidades) |
| **Apoiar** | Apoie o desenvolvimento | Hero card + botão Ko-fi |
| **Novidades** | Novidades desta versão | Changelog parseado + botão "Verificar Atualizações" |
| **Atualizações** | Verificar atualizações | Badge "Nova Versão" + changelog preview + download/install |

---

## Configs Gerais vs Específicas de Ferramenta

**Todas as settings atuais são GERAIS do app** — armazenadas em `OverlayConfig` no `OverlayDataStore`.

NENHUMA config específica de ferramenta existe. Stopwatch e Countdown são placeholders vazios.

**Problema:** `timeLimitSeconds` é config geral mas só faz sentido para Stopwatch (usado em `StopwatchViewModel`). Countdown usa `totalSeconds` direto do seletor, ignora `timeLimitSeconds`. Está no lugar errado.

---

## Menus Repetidos / Duplicados

1. **Support duplicado:** Aparece como aba própria no menu **Info** E como ação "Apoiar o Projeto" dentro do **About**. O link em About simplesmente navega para Support — mesma tela, dois caminhos.

2. **Changelog duplicado:** Aparece como aba própria **Novidades** no menu **Info** E como ação "Novidades da Versão" dentro do **About**. Ambos levam ao `ChangelogPanel`.

3. **Updates duplicado:** Aparece como aba própria **Atualizações** no menu **Info** E o `ChangelogPanel` tem botão "Verificar Atualizações" que navega para `UpdatesPanel`. Lógica de update espalhada entre Changelog e Updates.

4. **TimeLimitField repetido:** Usado em `BehaviorPanel` (settings), `StopwatchSettings` (feature), `CountdownConfigSheet` (feature) — importado do mesmo lugar, mas semanticamente pertence ao stopwatch.

---

## Ícones Usados de Forma Errada

| Local | Ícone | Problema |
|-------|-------|----------|
| `SettingsMenuPanel.kt:224` — search bar | `KronoIcons.Action.Settings` (engrenagem) | Ícone de engrenagem usado como lupa/busca. Comentário diz "reuse magnifier-like icon" mas settings gear não representa busca. **Deveria ser `Icons.Rounded.Search`** |
| `SettingsSearchBar.kt` — botão limpar | `KronoIcons.Navigation.Close` (X) | Correto, sem problema |
| `ChangelogPanel.kt:33` — ItemType.FEAT | `KronoIcons.Action.Sparkle` (AutoAwesome) | OK, mas genérico. Poderia ser `Rocket` ou `NewReleases` |
| `ChangelogPanel.kt:37` — ItemType.OTHER | `KronoIcons.Action.Check` (Check) | Check não representa "outro". Deveria ser `Circle` ou `Remove` |
| `ChangelogPanel.kt:35` — ItemType.PERF | `KronoIcons.Status.Speed` (Speed) | Correto |
| `ChangelogPanel.kt:36` — ItemType.DOCS | `KronoIcons.Status.Doc` (Article) | Correto |
| `ChangelogPanel.kt:34` — ItemType.FIX | `KronoIcons.Status.Bug` | OK, mas colide com "Relatar Bug" no About que também usa Bug |
| `AboutPanel.kt:106` — "Apoiar" | `KronoIcons.Status.Favorite` (coração) | Coração é OK para apoiar, mas `Favorite` (rounded) != `Heart` (outlined). Inconsistência de estilo |
| `UpdatesPanel.kt:131` — toast download | `KronoIcons.Action.Download` | Correto |
| `UpdatesPanel.kt:256` — instalado | `KronoIcons.Action.Check` | Correto |

### Duplicatas no KronoIcons.kt

| Conflito | Onde fica |
|----------|-----------|
| `Settings.History` = `Icons.Outlined.History` vs `Status.History` = `Icons.Rounded.History` | Mesmo conceito, estilos diferentes |
| `Settings.Heart` = `Icons.Outlined.Favorite` vs `Status.Favorite` = `Icons.Rounded.Favorite` | Mesmo conceito, estilos diferentes |
| `Settings.Info` = `Icons.Outlined.Info` vs `Status.Info` = `Icons.Rounded.Info` | Mesmo conceito, estilos diferentes |
| `Feature.Countdown` = `Icons.Outlined.HourglassBottom` vs `Feature.HourglassBottom` = `Icons.Rounded.HourglassBottom` | Ambos hourglass bottom, um outline outro rounded, nomes confusos |

---

## Problemas Arquiteturais

1. **NarrowScreen back icon bug:** `SettingsScreen.kt:224-227`
   ```kotlin
   imageVector = if (selectedDestination != null)
       KronoIcons.Navigation.Back
   else
       KronoIcons.Navigation.Back,
   ```
   Ambos os branches retornam `Back`, o condicional é inútil. Quando `selectedDestination == null`, deveria mostrar `Menu` ou `Close` — não `Back` no nível raiz.

2. **totalLifetimeMs = 0L fixo:** `SettingsScreen.kt:173`
   O parâmetro `totalLifetimeMs` é passado como `0L` constante, nunca recebe dado real do DataStore.

3. **SettingsGroup vs SectionCard:** `SettingsGroup.kt` é um componente público para grupos dentro dos painéis. `SectionCard` em `SettingsMenuPanel.kt` é uma versão privada quase idêntica mas sem label — poderiam ser unificados.

4. **BehaviorPanel mistura concerns:** Contém toggle de overlay (`showHours`, `showSeconds`, `showButtons`, `autoLaunch`, `keepScreenOn`) misturados com toggles de notificação (`isBeepEnabled`, `isVibrationEnabled`) e foco (`focusModeEnabled`). Esses grupos deveriam ser separados.

5. **TimeLimitField no BehaviorPanel:** Config de limite de tempo é específica do cronômetro mas está no painel de comportamento geral. Já existe `StopwatchSettings.kt` com outro `TimeLimitField` — duplicação de UI.

6. **Stopwatch e Countdown sem settings:** Placeholders existem, indicam que settings específicas estão planejadas mas não implementadas.

7. **SearchBar usa BasicTextField:** Em vez de `OutlinedTextField` ou `TextField` do Material3, perde comportamentos como label, placeholder styling e acessibilidade.

---

## Resumo Visual da Árvore

```
Configurações
├── ─── GERAL ───
│   ├── Aparência       [Paleta]     → Tema (auto/light/dark), Fonte, Cores (bg/text)
│   ├── Comportamento   [Tune]       → Geral (9 toggles), Limite de Tempo
│   └── Overlay         [WebAsset]   → Escala, Raio
├── ─── TOOLS ───
│   ├── Cronômetro      [Timer]      → 🛠 Em desenvolvimento
│   └── Contagem Regr.  [Hourglass]  → 🛠 Em desenvolvimento
└── ─── INFO ───
    ├── Sobre           [Info]       → Card identidade, Ações (Apoiar, Bug, Código, Novidades)
    ├── Apoiar          [Coração]    → Ko-fi donation
    ├── Novidades       [História]   → Changelog + Verificar atualizações
    └── Atualizações    [Update]     → Baixar/Instalar APK
```

---

## Recomendações (resumo)

1. Separar configs de overlay (`showHours`, `showSeconds`, etc.) de configs de comportamento (`focusMode`, `beep`, `vibration`)
2. Mover `timeLimitSeconds` para stopwatch feature (já planejado em `docs/planos/fase-6.1-limpeza-e-reestruturacao.md`)
3. Implementar settings reais de Stopwatch e Countdown
4. Remover redundância: About → Support (deixar só Support no menu)
5. Remover redundância: About → Changelog (deixar só Changelog no menu)
6. Adicionar `Icons.Rounded.Search` para barra de busca
7. Unificar `SectionCard` e `SettingsGroup`
8. Corrigir NarrowScreen back icon (exibir menu/close quando na raiz)
9. Passar `totalLifetimeMs` real do DataStore
10. Eliminar ícones duplicados em KronoIcons (escolher rounded OU outlined para cada conceito)
