# FASE 1 — Infraestrutura Core

### Passo 1.1 — Criar contratos core/tool/
- [ ] Criar `core/tool/ToolState.kt`
- [ ] Criar `core/tool/ToolCallbacks.kt`
- [ ] Criar `core/tool/ToolViewModel.kt`
- [ ] Criar `core/tool/NotificationContent.kt`
- [ ] Criar `core/tool/KronoTool.kt`
- [ ] Criar `core/tool/ToolRegistry.kt`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Nenhum arquivo existente alterado

**Aguardar aprovação → commit: `feat: add core tool contracts and ToolRegistry`**

---

### Passo 1.2 — Criar componentes UI compartilhados
- [ ] Criar `core/ui/components/KronoTimerDisplay.kt`
  - Usar `KronoTokens` para tamanhos
  - Usar `KronoType` para tipografia
  - Usar `KronoTheme` para cores
- [ ] Criar `core/ui/components/KronoControlButtons.kt`
  - Usar `KronoTokens` para espaçamentos e tamanhos
  - Usar `KronoIcons` para ícones

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Visual idêntico ao atual

**Aguardar aprovação → commit: `feat: add shared KronoTimerDisplay and KronoControlButtons`**

---

### Passo 1.3 — Mover arquivos para core/data/
- [ ] Mover `OverlayConfig.kt` → `core/data/`
- [ ] Mover `OverlayDataStore.kt` → `core/data/`
- [ ] Mover `TimeUtils.kt` → `core/data/`
- [ ] Mover `TimerPreferences.kt` → `core/data/`
- [ ] Atualizar imports em todos os arquivos afetados

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] App funcional, cronômetro e overlay operacionais

**Aguardar aprovação → commit: `refactor: move shared data files to core/data`**

---

### Passo 1.4 — Mover arquivos para core/service/
- [ ] Mover `MainService.kt` → `core/service/`
- [ ] Mover `OverlayManager.kt` → `core/service/`
- [ ] Mover `NotificationHelper.kt` → `core/service/`
- [ ] Mover `FeedbackManager.kt` → `core/service/`
- [ ] Mover `WakeLockManager.kt` → `core/service/`
- [ ] Atualizar imports em todos os arquivos afetados

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] App funcional, notificação e overlay operacionais

**Aguardar aprovação → commit: `refactor: move service files to core/service`**

---

### Passo 1.5 — Mover theme para core/ui/theme/
- [ ] Mover `KronoTheme.kt` → `core/ui/theme/`
- [ ] Mover `KronoIcons.kt` → `core/ui/theme/`
- [ ] Mover `KronoTokens.kt` → `core/ui/theme/`
- [ ] Mover `KronoType.kt` → `core/ui/theme/`
- [ ] Atualizar imports em todos os arquivos afetados

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Visual idêntico ao atual

**Aguardar aprovação → commit: `refactor: move theme files to core/ui/theme`**