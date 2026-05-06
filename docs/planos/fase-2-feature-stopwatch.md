# FASE 2 — Feature Stopwatch

### Passo 2.1 — Migrar ViewModel e State
- [ ] Mover `TimerViewModel.kt` → `feature/stopwatch/StopwatchViewModel.kt`
  - Renomear classe para `StopwatchViewModel`
  - Estender `ToolViewModel`
  - Expor `ToolState` no `StateFlow`
- [ ] Mover `TimeState.kt` → `feature/stopwatch/StopwatchState.kt`
  - Renomear para `StopwatchState`
- [ ] Atualizar imports em todos os arquivos afetados

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Cronômetro funcional

**Aguardar aprovação → commit: `refactor: migrate TimerViewModel to StopwatchViewModel`**

---

### Passo 2.2 — Migrar tela e overlay
- [ ] Mover `TimerScreen.kt` → `feature/stopwatch/StopwatchScreen.kt`
  - Renomear composable para `StopwatchScreen`
  - Substituir hardcoded values por `KronoTokens`, `KronoType`, `KronoTheme`
  - Usar `KronoTimerDisplay` e `KronoControlButtons`
  - Consumir `ToolState` e `ToolCallbacks`
- [ ] Mover `FloatingTimerUi.kt` → `feature/stopwatch/StopwatchOverlay.kt`
  - Renomear composable para `StopwatchOverlay`
  - Substituir hardcoded values por `KronoTokens`, `KronoType`, `KronoTheme`
  - Consumir `ToolState` e `ToolCallbacks`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Visual idêntico ao atual
- [ ] Overlay funcional

**Aguardar aprovação → commit: `refactor: migrate TimerScreen and FloatingTimerUi to stopwatch feature`**

---

### Passo 2.3 — Criar StopwatchSettings e StopwatchTool
- [ ] Criar `feature/stopwatch/StopwatchSettings.kt`
  - Mover configs de stopwatch do `SettingsScreen.kt` atual
  - Usar `KronoTokens`
- [ ] Criar `feature/stopwatch/StopwatchTool.kt`
  - Implementar `KronoTool`
  - `createViewModel()` retorna `StopwatchViewModel`
  - `overlayContent()` retorna `StopwatchOverlay`
  - `settingsPanelContent()` retorna `StopwatchSettings`
- [ ] Registrar `StopwatchTool` em `KronoApp.onCreate()`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] App funcional

**Aguardar aprovação → commit: `feat: add StopwatchTool and StopwatchSettings`**

---

### Passo 2.4 — Adaptar MainService para ToolRegistry
- [ ] Remover referência direta a `StopwatchViewModel` de `MainService`
- [ ] Usar `ToolRegistry.find(activeToolId)` para obter ferramenta ativa
- [ ] Usar `activeTool.createViewModel()` para instanciar ViewModel
- [ ] Usar `activeTool.notificationContent()` em `NotificationHelper`
- [ ] Persistir `activeToolId` em `OverlayDataStore`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Cronômetro funcional
- [ ] Notificação funcional
- [ ] Overlay funcional

**Aguardar aprovação → commit: `refactor: decouple MainService from StopwatchViewModel via ToolRegistry`**
Quando screenWidthDp >= 600:
□ Renderizar Row { SettingsMenuPanel(weight=0.35f) | SettingsPanelHost(weight=0.65f) }
□ selectedDestination inicia com Appearance
□ Back arrow some do painel esquerdo
□ Menu permanece visível
□ Item selecionado destacado via KronoTheme.colorScheme.secondaryContainer
```