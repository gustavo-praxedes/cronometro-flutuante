# Plano — Settings Visual Redesign

## Análise do Estado Atual

**Problemas identificados:**
- Menu esquerdo: itens sem peso visual, sem estado ativo destacado
- Labels de seção (GERAL, FERRAMENTAS, SOBRE) pequenas e sem hierarquia clara
- Painel direito: sem título/header, conteúdo solto sem agrupamento
- Preview de cor: retângulo preto sem label contextual, parece quebrado
- Dropdowns de Tema e Fonte: ocupam metade da largura sem consistência
- Espaçamento inconsistente entre elementos do painel direito
- Sem separação visual entre grupos de configurações
- Itens do menu sem `contentDescription` visual de ação (sem chevron)

**Referência aplicada:**
- Itens agrupados em cards com fundo sutil e cantos arredondados
- Header do painel direito com título da seção selecionada
- Labels de seção com cor primária e peso tipográfico
- Estado ativo no menu com `secondaryContainer` + indicador lateral
- Rows de configuração com label + valor atual alinhados
- Color preview integrado ao row com swatch circular + nome da cor

---

## Regras
- Manter comportamento e lógica intactos — apenas visual
- Usar exclusivamente `KronoTokens`, `KronoType`, `KronoTheme`, `KronoIcons`
- App totalmente operacional após cada commit
- Aguardar verificação e aprovação do dev antes de cada commit
- Nunca compilar — dev compila e verifica

---

## PASSO 1 — Menu esquerdo: grupos e itens

### Investigar
- [ ] Abrir `SettingsMenuPanel.kt`
- [ ] Identificar como seções e itens são renderizados atualmente

### Implementar
- [ ] Envolver itens de cada seção em `Surface` com `tonalElevation` sutil
  - Shape via `KronoTokens.Shape.Card`
  - Cor via `MaterialTheme.colorScheme.surfaceVariant` com alpha baixo
- [ ] Aplicar estado ativo no item selecionado:
  - Fundo `MaterialTheme.colorScheme.secondaryContainer`
  - Texto e ícone `MaterialTheme.colorScheme.onSecondaryContainer`
  - Borda esquerda com `Modifier.border` em `MaterialTheme.colorScheme.primary`
- [ ] Estilizar labels de seção (GERAL, FERRAMENTAS, SOBRE):
  - `KronoType.labelSmall` + `FontWeight.Bold`
  - Cor `MaterialTheme.colorScheme.primary`
  - Padding superior via `KronoTokens.Spacing.lg`
- [ ] Adicionar chevron `KronoIcons.Navigation.ChevronRight` trailing em cada item
  - Visível apenas em mobile (ocultar em tablet)
- [ ] Aplicar `Modifier.clip(KronoTokens.Shape.Card)` no grupo inteiro de cada seção
- [ ] Espaçamento entre grupos via `KronoTokens.Spacing.md`

### Verificar antes do commit
- [ ] Estado ativo visualmente distinto
- [ ] Grupos separados com card sutil
- [ ] Chevron visível em mobile
- [ ] Nenhuma lógica de navegação alterada

**Aguardar aprovação → commit: `ui: improve settings menu panel visual hierarchy`**

---

## PASSO 2 — Painel direito: header e estrutura

### Investigar
- [ ] Abrir `SettingsPanelHost.kt` e `SettingsScreen.kt`
- [ ] Verificar se painel direito tem `TopAppBar` ou header próprio

### Implementar
- [ ] Adicionar `TopAppBar` no painel direito com:
  - Título = `stringResource(destination.titleRes)`
  - Tipografia `KronoType.titleLarge`
  - Back arrow apenas em mobile (`KronoIcons.Navigation.Back`)
  - Fundo transparente via `TopAppBarDefaults.transparentColors()`
- [ ] Envolver conteúdo do painel em `Column` com `verticalScroll`
- [ ] Aplicar padding horizontal via `KronoTokens.Spacing.lg`

### Verificar antes do commit
- [ ] Título aparece no painel direito
- [ ] Back arrow funciona em mobile
- [ ] Scroll funciona em painéis com muito conteúdo

**Aguardar aprovação → commit: `ui: add header and scroll to settings panel host`**

---

## PASSO 3 — Agrupamento de configs em cards

### Investigar
- [ ] Abrir `AppearancePanel.kt`, `BehaviorPanel.kt`, `OverlayPanel.kt`
- [ ] Mapear quais configs pertencem ao mesmo grupo lógico

### Implementar em todos os painéis
- [ ] Criar composable local `SettingsGroup(title, content)`:
  - Label de grupo: `KronoType.labelSmall` + `FontWeight.SemiBold` + `colorScheme.primary`
  - `Surface` envolvendo items: `tonalElevation = 1.dp`, shape `KronoTokens.Shape.Card`
  - `HorizontalDivider` entre itens dentro do grupo
  - Padding interno via `KronoTokens.Spacing.none` (divider separa visualmente)
- [ ] Agrupar configs por contexto:
  - `AppearancePanel`: grupo Tema (Tema + Fonte) | grupo Cores (Fundo + Texto)
  - `BehaviorPanel`: grupo Geral (autoLaunch + keepScreenOn + focusMode)
  - `OverlayPanel`: grupo Visual (scale + cornerRadius + showButtons)
- [ ] Espaçamento entre grupos: `KronoTokens.Spacing.lg`

### Verificar antes do commit
- [ ] Grupos visualmente separados
- [ ] Divisores internos corretos
- [ ] Visual consistente entre painéis

**Aguardar aprovação → commit: `ui: add settings group card layout to panels`**

---

## PASSO 4 — Rows de configuração consistentes

### Investigar
- [ ] Identificar todos os tipos de row usados: `ToggleRow`, `SliderRow`, `DropdownRow`, `ColorRow`
- [ ] Verificar se estão definidos em `SettingsComponents.kt` ou inline nos painéis

### Implementar composable `SettingsRow`:
- [ ] Layout: `Row` com `fillMaxWidth`, altura mínima `KronoTokens.Size.RowMin`
- [ ] Lado esquerdo: ícone opcional + label em `KronoType.bodyMedium`
- [ ] Lado direito: valor atual em `KronoType.bodyMedium` + `colorScheme.onSurfaceVariant`
- [ ] Padding vertical `KronoTokens.Spacing.md`, horizontal `KronoTokens.Spacing.lg`
- [ ] Ripple no click via `Modifier.clickable`

### Aplicar em:
- [ ] `ToggleRow` — trailing `Switch` alinhado à direita
- [ ] `DropdownRow` — trailing valor selecionado + `KronoIcons.Navigation.ChevronDown`
- [ ] `SliderRow` — valor numérico acima, slider abaixo com `min/max` labels
- [ ] `ColorRow` — trailing swatch circular + nome da cor (hex ou nome do tema)

### Verificar antes do commit
- [ ] Todos os tipos de row visualmente consistentes
- [ ] Ripple presente em rows clicáveis
- [ ] Alinhamento correto em todos os casos

**Aguardar aprovação → commit: `ui: unify settings row components`**

---

## PASSO 5 — Color row: preview melhorado

### Investigar
- [ ] Abrir `AppearancePanel.kt`
- [ ] Verificar como `ColorRow` exibe cor atual e abre picker

### Implementar
- [ ] Substituir retângulo preto por:
  - Swatch circular `size = KronoTokens.Size.ColorSwatch`
  - `background(Color(config.backgroundColor))` + `clip(CircleShape)`
  - Borda `1.dp` em `colorScheme.outline` para contraste em cores claras
  - Label de cor ao lado: hex em 6 dígitos maiúsculos `#RRGGBB`
- [ ] Aplicar mesmo padrão para cor do texto
- [ ] Manter abertura do `ColorPickerDialog` ao clicar

### Verificar antes do commit
- [ ] Swatch exibe cor correta
- [ ] Hex atualiza ao mudar cor
- [ ] Picker abre normalmente

**Aguardar aprovação → commit: `ui: improve color row with circular swatch and hex label`**

---

## PASSO 6 — Dropdowns de Tema e Fonte

### Investigar
- [ ] Verificar implementação atual de `ThemeSelector` e `FontSelector`
- [ ] Identificar se usam `DropdownMenu`, `ExposedDropdownMenuBox` ou componente custom

### Implementar
- [ ] Usar `ExposedDropdownMenuBox` do Material 3
- [ ] Aplicar `fillMaxWidth` no campo
- [ ] Trocar label acima por label inline (`labelText` do `OutlinedTextField`)
- [ ] Usar `KronoType.bodyMedium` para o valor selecionado
- [ ] Shape via `KronoTokens.Shape.Input`
- [ ] Cor de borda via `colorScheme.outline`

### Verificar antes do commit
- [ ] Dropdowns abrem e fecham corretamente
- [ ] Seleção persiste via DataStore
- [ ] Visual consistente com o restante do painel

**Aguardar aprovação → commit: `ui: improve theme and font dropdowns in AppearancePanel`**

---

## PASSO 7 — Painéis informativos (Sobre, Apoio, Changelog, Atualizações)

### Investigar
- [ ] Abrir `AboutPanel.kt`, `SupportPanel.kt`, `ChangelogPanel.kt`, `UpdatesPanel.kt`
- [ ] Identificar estrutura atual de cada painel

### Implementar padrão comum
- [ ] Usar `SettingsGroup` criado no Passo 3
- [ ] `AboutPanel`: card com versão + ícone do app + links como `SettingsRow` com `KronoIcons.Navigation.ExternalLink`
- [ ] `SupportPanel`: card com mensagem + botão de doação centralizado com `FilledTonalButton`
- [ ] `ChangelogPanel`: lista de versões com `SettingsGroup` por versão, texto em `KronoType.bodySmall`
- [ ] `UpdatesPanel`: card de status (atualizado / nova versão disponível) com badge de versão e botão de download

### Verificar antes do commit
- [ ] Cada painel renderiza corretamente
- [ ] Links e botões funcionam
- [ ] Changelog exibe conteúdo real

**Aguardar aprovação → commit: `ui: improve informational settings panels visual`**

---

## PASSO 8 — Tokens necessários (adicionar se ausentes)

### Investigar
- [ ] Abrir `KronoTokens.kt`
- [ ] Verificar se existem: `Shape.Card`, `Shape.Input`, `Size.RowMin`, `Size.ColorSwatch`, `Spacing.xxxl`

### Adicionar se ausentes
- [ ] `KronoTokens.Shape.Card` — `RoundedCornerShape(KronoTokens.Radius.md)`
- [ ] `KronoTokens.Shape.Input` — `RoundedCornerShape(KronoTokens.Radius.sm)`
- [ ] `KronoTokens.Size.RowMin` — `56.dp`
- [ ] `KronoTokens.Size.ColorSwatch` — `24.dp`
- [ ] Verificar que nenhum valor é hardcoded

### Verificar antes do commit
- [ ] Compilar sem erros
- [ ] Nenhum token duplicado

**Aguardar aprovação → commit: `chore: add missing KronoTokens for settings UI`**

---

## Checklist Final de Aceitação

```
□ Menu esquerdo: itens agrupados em cards, estado ativo claro
□ Labels de seção com hierarquia tipográfica correta
□ Painel direito: título visível, scroll funcional
□ Configs agrupadas logicamente com divisores
□ Todos os rows com altura, padding e alinhamento consistentes
□ Color row com swatch circular e hex label
□ Dropdowns com ExposedDropdownMenuBox
□ Painéis informativos com estrutura de card
□ Nenhum valor hardcoded — 100% via KronoTokens/KronoTheme/KronoType
□ Nenhuma lógica alterada — apenas visual
□ Visual consistente entre temas claro e escuro
```