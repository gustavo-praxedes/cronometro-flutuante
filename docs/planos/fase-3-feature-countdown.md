# FASE 3 — Feature Countdown

### Passo 3.1 — Organizar pasta feature/countdown/
- [ ] Mover todos os arquivos de countdown existentes → `feature/countdown/`
- [ ] Garantir nomenclatura: `CountdownViewModel`, `CountdownScreen`, `CountdownOverlay`, `CountdownConfig`, `CountdownConfigDialog`, `CountdownState`, `CountdownSettings`
- [ ] Substituir hardcoded values por `KronoTokens`, `KronoType`, `KronoTheme`
- [ ] Usar `KronoTimerDisplay` e `KronoControlButtons`
- [ ] Consumir `ToolState` e `ToolCallbacks`
- [ ] Atualizar imports em todos os arquivos afetados

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Visual idêntico ao atual

**Aguardar aprovação → commit: `refactor: organize countdown files into feature/countdown`**

---

### Passo 3.2 — Criar CountdownTool
- [ ] Criar `feature/countdown/CountdownTool.kt`
  - Implementar `KronoTool`
  - `createViewModel()` retorna `CountdownViewModel`
  - `overlayContent()` retorna `CountdownOverlay`
  - `settingsPanelContent()` retorna `CountdownSettings`
- [ ] Registrar `CountdownTool` em `KronoApp.onCreate()`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Countdown funcional

**Aguardar aprovação → commit: `feat: add CountdownTool`**