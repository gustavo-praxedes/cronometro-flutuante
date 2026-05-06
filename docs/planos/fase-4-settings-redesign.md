# FASE 4 — Settings Redesign

### Passo 4.1 — Recursos: strings e ícones
- [ ] Adicionar strings em `strings.xml`:
  - `settings_appearance`, `settings_behavior`, `settings_overlay`
  - `settings_stopwatch`, `settings_countdown`
  - `settings_section_general`, `settings_section_tools`, `settings_section_info`
  - `settings_about`, `settings_support`, `settings_changelog`, `settings_updates`
- [ ] Adicionar ícones em `KronoIcons.kt` se ausentes:
  - `Appearance` → `Icons.Outlined.Palette`
  - `Behavior` → `Icons.Outlined.Tune`
  - `Overlay` → `Icons.Outlined.WebAsset`
  - `Countdown` → `Icons.Outlined.HourglassBottom`
  - `Heart` → `Icons.Outlined.Favorite`
  - `History` → `Icons.Outlined.History`
  - `Update` → `Icons.Outlined.SystemUpdate`
  - `Info` → `Icons.Outlined.Info`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Sem strings duplicadas

**Aguardar aprovação → commit: `chore: add settings strings and icons`**

---

### Passo 4.2 — Criar SettingsDestination
- [ ] Criar `ui/settings/SettingsDestination.kt`
  - Sealed class com destinos: `Appearance`, `Behavior`, `Overlay`, `Stopwatch`, `Countdown`, `About`, `Support`, `Changelog`, `Updates`
  - Cada destino carrega `titleRes` e `icon` via `KronoIcons`

**Verificar antes do commit:**
- [ ] Compilar sem erros

**Aguardar aprovação → commit: `feat: add SettingsDestination sealed class`**

---

### Passo 4.3 — Criar painéis de configuração
- [ ] Criar `ui/settings/AppearancePanel.kt`
  - Migrar: `selectedTheme`, `selectedFont`, `backgroundColor`, `textColor`, `bgOpacity`, `textOpacity`
  - `ColorPickerDialog` permanece em `ui/`
  - Usar `KronoTokens`
- [ ] Criar `ui/settings/BehaviorPanel.kt`
  - Migrar: `autoLaunch`, `keepScreenOn`, `focusModeEnabled`
  - Usar `KronoTokens`
- [ ] Criar `ui/settings/OverlayPanel.kt`
  - Migrar: `scale`, `cornerRadius`, `showButtons`
  - Usar `KronoTokens`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Visual idêntico ao atual para cada painel

**Aguardar aprovação → commit: `feat: add AppearancePanel, BehaviorPanel, OverlayPanel`**

---

### Passo 4.4 — Criar painéis de informação (sem diálogos separados)
- [ ] Criar `ui/settings/AboutPanel.kt`
  - Integrar conteúdo de `AboutDialog.kt`
  - Créditos, versão, links
  - Remover `AboutDialog.kt`
- [ ] Criar `ui/settings/SupportPanel.kt`
  - Integrar conteúdo de `DonationDialog.kt`
  - Remover `DonationDialog.kt`
- [ ] Criar `ui/settings/ChangelogPanel.kt`
  - Integrar conteúdo de `ChangelogDialog.kt`
  - Lê `res/raw/changelog.md`
  - Remover `ChangelogDialog.kt`
- [ ] Criar `ui/settings/UpdatesPanel.kt`
  - Integrar conteúdo de `UpdateDialog.kt`
  - Badge no menu quando atualização pendente
  - Remover `UpdateDialog.kt`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Conteúdo de cada painel idêntico ao dialog original
- [ ] Nenhuma referência quebrada aos diálogos removidos

**Aguardar aprovação → commit: `feat: integrate About, Support, Changelog, Updates as settings panels`**

---

### Passo 4.5 — Criar SettingsMenuPanel
- [ ] Criar `ui/settings/SettingsMenuPanel.kt`
  - Seção GERAL: Aparência, Comportamento, Overlay
  - Seção FERRAMENTAS: Cronômetro, Cronômetro Regressivo (via `ToolRegistry.all()`)
  - Seção SOBRE (rodapé fixo): Sobre, Apoio, Changelog, Atualizações
  - Badge em Atualizações quando versão pendente
  - Usar `KronoTokens` para espaçamentos, shapes, divisores
  - Usar `KronoTheme` para cores de seleção (`secondaryContainer`)
  - `HorizontalDivider` entre seções

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Menu renderiza corretamente

**Aguardar aprovação → commit: `feat: add SettingsMenuPanel`**

---

### Passo 4.6 — Criar SettingsPanelHost
- [ ] Criar `ui/settings/SettingsPanelHost.kt`
  - Despachar para painel correto via `when(destination)`
  - Painéis de ferramentas via `tool.settingsPanelContent()`
  - Recebe `config`, `dataStore`, `scope`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Cada destino renderiza painel correto

**Aguardar aprovação → commit: `feat: add SettingsPanelHost dispatcher`**

---

### Passo 4.7 — Reescrever SettingsScreen
- [ ] Reescrever `ui/settings/SettingsScreen.kt`
  - Estado interno `selectedDestination` via `remember`
  - Mobile: `SettingsMenuPanel` → seleciona → `SettingsPanelHost` em tela cheia
  - Transição: `AnimatedContent` com `slideInHorizontally + fadeIn`
  - Back arrow no topo do painel direito → `selected = null`
  - Back arrow no painel esquerdo → sai das configurações
  - Sem `NavHost` adicional
  - Usar `KronoTokens` para duração de animação

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Navegação completa: menu → painel → voltar
- [ ] Transição animada correta
- [ ] Todos os painéis acessíveis
- [ ] Visual idêntico ao planejado

**Aguardar aprovação → commit: `feat: settings two-panel navigation`**
Quando screenWidthDp >= 600:
□ Renderizar Row { SettingsMenuPanel(weight=0.35f) | SettingsPanelHost(weight=0.65f) }
□ selectedDestination inicia com Appearance
□ Back arrow some do painel esquerdo
□ Menu permanece visível
□ Item selecionado destacado via KronoTheme.colorScheme.secondaryContainer
```