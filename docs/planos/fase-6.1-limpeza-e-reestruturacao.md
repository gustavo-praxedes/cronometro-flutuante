# Plano — Limpeza e Reorganização de Estrutura

## Regras
- App totalmente operacional após cada commit
- Ajustar imports em todos os arquivos afetados antes de cada commit
- Aguardar verificação e aprovação do dev antes de cada commit
- Nunca compilar — dev compila e verifica

---

## PASSO 1 — Investigar SettingsComponents.kt

**Investigar:**
- [ ] Abrir `ui/SettingsComponents.kt`
- [ ] Listar quais composables define
- [ ] Buscar todos os arquivos que importam `SettingsComponents`
- [ ] Verificar se algum composable já foi duplicado em `ui/settings/`

**Decidir:**
- Se tudo migrado → marcar para deletar no Passo 5
- Se ainda usado → marcar para mover para `ui/settings/` no Passo 5

---

## PASSO 2 — Investigar CountdownColorPickerDialog.kt

**Investigar:**
- [ ] Abrir `feature/countdown/CountdownColorPickerDialog.kt`
- [ ] Comparar com `ui/ColorPickerDialog.kt`
- [ ] Verificar se são idênticos ou têm diferenças relevantes
- [ ] Listar quais arquivos de countdown importam `CountdownColorPickerDialog`

**Decidir:**
- Se idênticos → substituir usos por `ColorPickerDialog`, deletar `CountdownColorPickerDialog`
- Se diferente → manter, sem ação

---

## PASSO 3 — Investigar KronoColors.kt

**Investigar:**
- [ ] Abrir `core/ui/theme/KronoColors.kt`
- [ ] Verificar se define algo não coberto por `KronoTheme.kt`
- [ ] Buscar importações de `KronoColors` no projeto

**Decidir:**
- Se conteúdo duplicado de `KronoTheme` → marcar para deletar no Passo 5
- Se conteúdo único → manter

---

## PASSO 4 — Mover arquivos mal posicionados

### 4.1 — Mover TimeWheelPicker.kt
- [ ] Verificar package atual de `ui/TimeWheelPicker.kt`
- [ ] Mover para `feature/countdown/TimeWheelPicker.kt`
- [ ] Atualizar package para `com.krono.app.feature.countdown`
- [ ] Buscar todos os imports de `TimeWheelPicker` e atualizar
- [ ] Verificar que `CountdownScreen.kt` continua importando corretamente

**Verificar antes do commit:**
- [ ] Nenhuma referência quebrada
- [ ] `CountdownScreen` compila e funciona

**Aguardar aprovação → commit: `refactor: move TimeWheelPicker to feature/countdown`**

---

### 4.2 — Mover TimeLimitField.kt
- [ ] Verificar package atual de `ui/TimeLimitField.kt`
- [ ] Identificar quem usa `TimeLimitField` (provavelmente `StopwatchSettings.kt`)
- [ ] Mover para `feature/stopwatch/TimeLimitField.kt`
- [ ] Atualizar package para `com.krono.app.feature.stopwatch`
- [ ] Atualizar imports em todos os arquivos afetados

**Verificar antes do commit:**
- [ ] Nenhuma referência quebrada
- [ ] Settings do Stopwatch funciona

**Aguardar aprovação → commit: `refactor: move TimeLimitField to feature/stopwatch`**

---

### 4.3 — Mover SkeletonLoader.kt
- [ ] Verificar package atual de `ui/SkeletonLoader.kt`
- [ ] Buscar todos os imports de `SkeletonLoader` no projeto
- [ ] Mover para `core/ui/components/SkeletonLoader.kt`
- [ ] Atualizar package para `com.krono.app.core.ui.components`
- [ ] Atualizar imports em todos os arquivos afetados

**Verificar antes do commit:**
- [ ] Nenhuma referência quebrada

**Aguardar aprovação → commit: `refactor: move SkeletonLoader to core/ui/components`**

---

## PASSO 5 — Deletar arquivos obsoletos

### 5.1 — Deletar DonationDialog.kt
- [ ] Confirmar que `SupportPanel.kt` cobre todo o conteúdo
- [ ] Buscar qualquer import restante de `DonationDialog` no projeto
- [ ] Remover todos os imports encontrados
- [ ] Deletar `ui/DonationDialog.kt`

**Verificar antes do commit:**
- [ ] Nenhuma referência quebrada
- [ ] Painel de Apoio em Settings funciona

**Aguardar aprovação → commit: `chore: remove DonationDialog replaced by SupportPanel`**

---

### 5.2 — Deletar UpdateDialog.kt
- [ ] Confirmar que `UpdatesPanel.kt` cobre todo o conteúdo
- [ ] Buscar qualquer import restante de `UpdateDialog` no projeto
- [ ] Remover todos os imports encontrados
- [ ] Deletar `ui/UpdateDialog.kt`

**Verificar antes do commit:**
- [ ] Nenhuma referência quebrada
- [ ] Painel de Atualizações em Settings funciona

**Aguardar aprovação → commit: `chore: remove UpdateDialog replaced by UpdatesPanel`**

---

### 5.3 — Deletar ou mover SettingsComponents.kt
*(executar conforme decisão do Passo 1)*

**Se deletar:**
- [ ] Confirmar que nenhum arquivo importa `SettingsComponents`
- [ ] Deletar `ui/SettingsComponents.kt`
- **Aguardar aprovação → commit: `chore: remove obsolete SettingsComponents`**

**Se mover:**
- [ ] Mover para `ui/settings/SettingsComponents.kt`
- [ ] Atualizar package para `com.krono.app.ui.settings`
- [ ] Atualizar imports em todos os arquivos afetados
- **Aguardar aprovação → commit: `refactor: move SettingsComponents to ui/settings`**

---

### 5.4 — Deletar CountdownColorPickerDialog.kt
*(executar somente se decisão do Passo 2 for deletar)*

- [ ] Substituir todos os imports de `CountdownColorPickerDialog` por `ColorPickerDialog`
- [ ] Ajustar chamadas se assinatura for diferente
- [ ] Deletar `feature/countdown/CountdownColorPickerDialog.kt`

**Verificar antes do commit:**
- [ ] Color picker do countdown continua funcional

**Aguardar aprovação → commit: `chore: remove CountdownColorPickerDialog, use ColorPickerDialog`**

---

### 5.5 — Deletar KronoColors.kt
*(executar somente se decisão do Passo 3 for deletar)*

- [ ] Mover qualquer definição única para `KronoTheme.kt`
- [ ] Substituir todos os imports de `KronoColors` pelo equivalente em `KronoTheme`
- [ ] Deletar `core/ui/theme/KronoColors.kt`

**Verificar antes do commit:**
- [ ] Nenhuma referência quebrada
- [ ] Cores do app intactas

**Aguardar aprovação → commit: `chore: remove KronoColors, consolidate into KronoTheme`**

---

## Estrutura Final Esperada

```
core/ui/components/
├── KronoControlButtons.kt
├── KronoTimerDisplay.kt
└── SkeletonLoader.kt

feature/countdown/
└── TimeWheelPicker.kt     ← movido de ui/

feature/stopwatch/
└── TimeLimitField.kt      ← movido de ui/

ui/
├── settings/              ← SettingsComponents.kt aqui se mantido
├── AppNavigation.kt
├── BugReportDialog.kt
├── ColorPickerDialog.kt
├── FocusActivity.kt
├── MainActivity.kt
├── PermissionsDialog.kt
└── TransparentProxyActivity.kt
```
