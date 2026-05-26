# Plano — Implementação Pomodoro

## Regras Gerais

- Manter aspecto visual consistente com o restante do app
- Nunca valores hardcoded. Adicionar em `KronoTokens`, `KronoType`, `KronoTheme`, `KronoIcons` etc
- Cada passo termina com checklist de verificação
- Aguardar verificação e aprovação do dev antes de sugerir commit msg

---

## Estrutura Final de Arquivos

```
feature/pomodoro/
├── PomodoroDataStore.kt
├── PomodoroOverlay.kt
├── PomodoroPhaseEngine.kt
├── PomodoroPreset.kt
├── PomodoroPresetEditorDialog.kt
├── PomodoroScreen.kt
├── PomodoroSettings.kt
├── PomodoroState.kt
├── PomodoroTask.kt
├── PomodoroTaskCard.kt
├── PomodoroTaskDialog.kt
├── PomodoroTaskSelectorDialog.kt
├── PomodoroTaskSession.kt
├── PomodoroTool.kt
└── PomodoroViewModel.kt

core/data/
└── KronoDatabase.kt           ← singleton compartilhado, registra todas as @Entity
```

---

## FASE 1 — Persistência

### Passo 1.1 — Adicionar dependência Room
- [ ] Adicionar `room-runtime`, `room-ktx`, `room-compiler` em `build.gradle.kts`
- [ ] Adicionar `ksp` ou `kapt` para o compilador Room
- [ ] Sincronizar Gradle

**Verificar antes do commit:**
- [ ] Sync sem erros
- [ ] Build sem erros

**Aguardar aprovação → commit: `chore: add Room dependency`**

---

### Passo 1.2 — Criar entidades e DAOs
- [ ] Criar `feature/pomodoro/PomodoroPreset.kt`
  - `@Entity(tableName = "pomodoro_presets")`
  - Campos: `id`, `name`, `phases` (JSON String), `repeatCount`, `isDefault`
  - `PomodoroPhaseItem` — data class serializada em JSON
  - `TypeConverter`: `List<PomodoroPhaseItem>` ↔ `String`
  - `PomodoroPresetDao`: `getAll(): Flow`, `insert`, `update`, `delete`
- [ ] Criar `feature/pomodoro/PomodoroTask.kt`
  - `@Entity(tableName = "pomodoro_tasks")`
  - Campos: `id`, `name`, `color`, `presetId`, `totalFocusMs`, `totalPauseMs`, `totalCycles`
  - `PomodoroTaskDao`: `getAll(): Flow`, `insert`, `update`, `delete`, `updateStats`
- [ ] Criar `feature/pomodoro/PomodoroTaskSession.kt`
  - `@Entity(tableName = "pomodoro_task_sessions")`
  - Campos: `id`, `taskId` (nullable), `date`, `focusMs`, `pauseMs`, `cycles`
  - `PomodoroTaskSessionDao`: `insert`, `getTotalStats(): Flow`, `getStatsByTask(taskId): Flow`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Nenhum arquivo core alterado

**Aguardar aprovação → commit: `feat: add Pomodoro Room entities and DAOs`**

---

### Passo 1.3 — Criar KronoDatabase
- [ ] Criar `core/data/KronoDatabase.kt`
  - `@Database(entities = [PomodoroPreset::class, PomodoroTask::class, PomodoroTaskSession::class])`
  - Singleton via `companion object`
  - Registrar `TypeConverter` de `PomodoroPreset`
  - `RoomDatabase.Callback` para inserir presets padrão no primeiro launch:
    - Clássico: Foco 25min / Pausa curta 5min / 4 repetições de foco até pausa longa / Pausa longa 15min / `isDefault = true`
    - Curto: Foco 15min / Pausa curta 5min / 4 repetições de foco até pausa longa / Pausa longa 15min / `isDefault = true`
    - Longo: Foco 50min / Pausa curta 10min / 4 repetições de foco até pausa longa / Pausa longa 15min / `isDefault = true`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Database criado no primeiro launch
- [ ] 3 presets padrão inseridos automaticamente

**Aguardar aprovação → commit: `feat: add KronoDatabase with default Pomodoro presets`**

---

### Passo 1.4 — Criar PomodoroDataStore
- [ ] Criar `feature/pomodoro/PomodoroDataStore.kt`
  - Chaves: `defaultPresetId`, `autoAdvance`, `autoRepeat`
  - Chaves de som: `soundFocusUri`, `soundBreakUri`, `soundCompleteUri`
  - Chaves de feedback: `ticTacEnabled`
  - Chave de ciclos: `cyclesDoneToday`

**Verificar antes do commit:**
- [ ] Compilar sem erros

**Aguardar aprovação → commit: `feat: add PomodoroDataStore`**

---

## FASE 2 — Lógica de Negócio

### Passo 2.1 — Criar PomodoroPhaseEngine
- [ ] Criar `feature/pomodoro/PomodoroPhaseEngine.kt`
  - Classe pura — sem dependências Android
  - Estado interno: `phases`, `repeatCount`, `currentPhase`, `currentRepeat`, `remainingMs`
  - `applyPreset(preset)` — reinicia ciclo imediatamente
  - `tick(deltaMs): PomodoroEngineEvent?` — decrementa `remainingMs`, avança fase quando zera
  - `advance(): PomodoroEngineEvent` — skip manual
  - `reset()` — volta ao início do preset
  - `currentState(): PomodoroEngineSnapshot`
  - `PomodoroEngineEvent`: `PHASE_CHANGED`, `REPEAT_COMPLETED`, `SESSION_COMPLETED`
  - `PomodoroEngineSnapshot`: snapshot imutável do estado atual

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Testar `tick()` manualmente: fase avança ao zerar
- [ ] Testar `advance()`: skip funciona
- [ ] Testar `applyPreset()`: reinicia corretamente

**Aguardar aprovação → commit: `feat: add PomodoroPhaseEngine`**

---

### Passo 2.2 — Criar PomodoroState
- [ ] Criar `feature/pomodoro/PomodoroState.kt`
  - Campos: `isRunning`, `currentPhaseItem`, `currentRepeat`, `totalRepeats`
  - Campos: `remainingMs`, `displayTime`, `phaseColor`, `cyclesDoneToday`, `activeTask`
  - Cor padrão via `KronoTheme` — não hardcoded

**Verificar antes do commit:**
- [ ] Compilar sem erros

**Aguardar aprovação → commit: `feat: add PomodoroState`**

---

### Passo 2.3 — Criar PomodoroViewModel
- [ ] Criar `feature/pomodoro/PomodoroViewModel.kt`
  - Estender `ToolViewModel`
  - Instanciar `PomodoroPhaseEngine`
  - Loop 250ms via coroutine — mesmo padrão do `StopwatchViewModel`
  - `playPause()`, `reset()`, `skip()`, `applyPreset()`, `setActiveTask()`, `resetCyclesToday()`
  - Ao `PHASE_CHANGED` → chamar `feedback.playPomodoroSound(event)`
  - Ao `SESSION_COMPLETED` → salvar `PomodoroTaskSession` via `sessionDao`
  - Atualizar `PomodoroTask.totalFocusMs/totalPauseMs/totalCycles` ao final de cada sessão
  - Persistir estado pós-morte via `SharedPreferences`:
    - `pomodoro_phase_index`, `pomodoro_repeat`, `pomodoro_remaining_ms`, `pomodoro_preset_id`
  - Restaurar estado no `init` a partir das `SharedPreferences`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Timer inicia e pausa
- [ ] Fases avançam automaticamente
- [ ] Skip funciona
- [ ] Estado persiste após fechar app

**Aguardar aprovação → commit: `feat: add PomodoroViewModel with phase loop`**

---

### Passo 2.4 — Estender FeedbackManager
- [ ] Adicionar `PomodoroSoundEvent` em `core/service/FeedbackManager.kt`:
  - `FOCUS_START`, `BREAK_START`, `SESSION_COMPLETE`
- [ ] Adicionar `playPomodoroSound(event, uris)` — toca URI via `RingtoneManager`
- [ ] Adicionar `startTicTac()` e `stopTicTac()` — loop de som durante foco

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Som toca na troca de fase (testar com URI de notificação padrão)
- [ ] Tic-tac ativa e desativa corretamente

**Aguardar aprovação → commit: `feat: extend FeedbackManager with Pomodoro sounds`**

---

## FASE 3 — Interface: Tela Principal

### Passo 3.1 — Criar PomodoroTaskCard
- [ ] Criar `feature/pomodoro/PomodoroTaskCard.kt`
  - Layout: nome da tarefa + [x] no topo
  - Controles: ▶/⏸, ⏹, display de tempo acumulado, ícone overlay
  - Indicador de ciclos `●●●` usando `KronoIcons`
  - Destaque visual no card ativo via `KronoTheme.colorScheme.secondaryContainer`
  - Espaçamentos e shapes via `KronoTokens`
  - [x] abre confirmação de exclusão inline

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Card renderiza corretamente
- [ ] Estado ativo destacado visualmente

**Aguardar aprovação → commit: `feat: add PomodoroTaskCard`**

---

### Passo 3.2 — Criar PomodoroScreen
- [ ] Criar `feature/pomodoro/PomodoroScreen.kt`
  - `TopBar` com ☰ hamburger → `onOpenSettings`
  - Display de tempo — usar `KronoTimerDisplay`
  - Cor do display reflete fase ativa (via `PomodoroState.phaseColor`)
  - Controles: Reset, Play/Pause, Abrir Overlay, Skip — usar `KronoControlButtons` e `KronoIcons`
  - Indicador de ciclos cumpridos hoje `●●` abaixo dos controles
  - Lista de `PomodoroTaskCard` (visível apenas se houver tarefas)
  - FAB `+` no canto inferior direito → `PomodoroTaskDialog`
  - `BottomBar` de navegação via `AppNavigation`
  - Espaçamentos via `KronoTokens`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Tela renderiza corretamente
- [ ] Timer exibe e atualiza
- [ ] FAB abre dialog

**Aguardar aprovação → commit: `feat: add PomodoroScreen`**

---

## FASE 4 — Interface: Dialogs

### Passo 4.1 — Criar PomodoroTaskDialog
- [ ] Criar `feature/pomodoro/PomodoroTaskDialog.kt`
  - Campos: nome (opcional), cor via `ColorPickerDialog`, preset associado (dropdown nullable)
  - Estatísticas visíveis apenas ao editar: foco total, pausa total, ciclos totais
  - Botão Salvar no fundo, X no topo fecha sem salvar
  - Shapes e espaçamentos via `KronoTokens`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Criar tarefa sem nome funciona
- [ ] Editar tarefa exibe estatísticas
- [ ] Salvar persiste no Room

**Aguardar aprovação → commit: `feat: add PomodoroTaskDialog`**

---

### Passo 4.2 — Criar PomodoroTaskSelectorDialog
- [ ] Criar `feature/pomodoro/PomodoroTaskSelectorDialog.kt`
  - Lista de tarefas existentes
  - Opção "Sem tarefa"
  - Seleção associa tarefa ao timer ativo
  - Espaçamentos e shapes via `KronoTokens`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Seleção associa tarefa corretamente ao estado

**Aguardar aprovação → commit: `feat: add PomodoroTaskSelectorDialog`**

---

### Passo 4.3 — Criar PomodoroPresetEditorDialog
- [ ] Criar `feature/pomodoro/PomodoroPresetEditorDialog.kt`
  - Campo: nome do preset
  - Lista de fases: nome livre + duração em minutos + cor própria via `ColorPickerDialog`
  - Drag handle para reordenar fases
  - Swipe ou ícone delete para remover fase
  - Botão `+ Adicionar fase`
  - Campo: repetir sequência N vezes (mínimo 1)
  - Botão Salvar no fundo, X no topo fecha sem salvar
  - Presets com `isDefault = true` não abrem este dialog
  - Validação: nome obrigatório, mínimo 1 fase, duração ≥ 1min
  - Shapes e espaçamentos via `KronoTokens`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Criar preset com múltiplas fases funciona
- [ ] Reordenar fases funciona
- [ ] Deletar fase funciona
- [ ] Validação impede salvar preset inválido
- [ ] Preset salvo aparece na lista

**Aguardar aprovação → commit: `feat: add PomodoroPresetEditorDialog`**

---

## FASE 5 — Interface: Overlay e Settings

### Passo 5.1 — Criar PomodoroOverlay
- [ ] Criar `feature/pomodoro/PomodoroOverlay.kt`
  - Layout: label da tarefa ativa (opcional) + [x]
  - Controles: ▶/⏸, ⏹, ⏭ + display de tempo
  - Cor de fundo reflete fase ativa via `PomodoroState.phaseColor`
  - Label da tarefa visível apenas se `activeTask != null`
  - Mesmos gestos do `StopwatchOverlay`: arrastar, snap nas bordas
  - Dimensões e espaçamentos via `KronoTokens`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Overlay abre sobre outros apps
- [ ] Cor muda com a fase
- [ ] Controles funcionam
- [ ] Arrastar e snap funcionam

**Aguardar aprovação → commit: `feat: add PomodoroOverlay`**

---

### Passo 5.2 — Criar PomodoroSettings
- [ ] Criar `feature/pomodoro/PomodoroSettings.kt`
  - Seção GERAL:
    - Dropdown preset padrão
    - Lista de presets com `PomodoroPresetCard`
    - Botão `+ Novo Preset` → `PomodoroPresetEditorDialog`
    - Toggle avançar automaticamente
    - Toggle repetir ao finalizar
  - Seção CORES:
    - Cor fase Foco via `ColorPickerDialog`
    - Cor fase Pausa via `ColorPickerDialog`
  - Seção SONS:
    - Som iniciar foco → `RingtoneManager` picker
    - Som iniciar pausa → `RingtoneManager` picker
    - Som sessão completa → `RingtoneManager` picker
    - Toggle tic-tac durante foco
  - Seção ESTATÍSTICAS GERAIS:
    - Foco total, pausa total, ciclos totais (lidos via `PomodoroTaskSessionDao`)
    - Botão resetar estatísticas
  - Espaçamentos, shapes e divisores via `KronoTokens`
  - Cores via `KronoTheme`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Todos os toggles persistem via `PomodoroDataStore`
- [ ] Pickers de som abrem `RingtoneManager`
- [ ] Estatísticas exibem valores reais do banco
- [ ] Reset de estatísticas funciona

**Aguardar aprovação → commit: `feat: add PomodoroSettings panel`**

---

## FASE 6 — Integração

### Passo 6.1 — Criar PomodoroTool e registrar
- [ ] Criar `feature/pomodoro/PomodoroTool.kt`
  - Implementar `KronoTool`
  - `id = "pomodoro"`
  - `nameRes = R.string.tool_pomodoro`
  - `iconRes = KronoIcons.Pomodoro`
  - `createViewModel()` instancia `PomodoroViewModel` com deps do `KronoDatabase`
  - `overlayContent()` retorna `PomodoroOverlay`
  - `settingsPanelContent()` retorna `PomodoroSettings`
  - `notificationContent()` retorna fase + tempo no formato `"Foco · 24:13"`
- [ ] Registrar `PomodoroTool` em `KronoApp.onCreate()`
- [ ] Adicionar `R.string.tool_pomodoro` em `strings.xml`
- [ ] Adicionar `KronoIcons.Pomodoro` em `KronoIcons.kt`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Pomodoro aparece no grid da `HomeScreen`
- [ ] Navegar para `PomodoroScreen` funciona
- [ ] Overlay do Pomodoro abre

**Aguardar aprovação → commit: `feat: add PomodoroTool and register in KronoApp`**

---

### Passo 6.2 — Integrar notificação
- [ ] Adaptar `NotificationHelper` para exibir `"Foco · 24:13"` / `"Pausa · 04:55"`
- [ ] Adicionar botões: Play/Pause, Stop, Skip — mesmo padrão do Stopwatch
- [ ] Usar mesmo canal de notificação do Stopwatch

**Verificar antes do commit:**
- [ ] Notificação aparece ao iniciar Pomodoro
- [ ] Botões da notificação funcionam
- [ ] Texto atualiza com fase e tempo

**Aguardar aprovação → commit: `feat: add Pomodoro notification support`**

---

### Passo 6.3 — Adicionar painel no SettingsPanelHost
- [ ] Adicionar `PomodoroSettings` como destino em `SettingsDestination.kt`
  - `object Pomodoro : SettingsDestination(R.string.tool_pomodoro, KronoIcons.Pomodoro)`
- [ ] Adicionar case em `SettingsPanelHost.kt`
- [ ] Adicionar item em `SettingsMenuPanel.kt` na seção FERRAMENTAS

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Menu de settings exibe Pomodoro na seção FERRAMENTAS
- [ ] Navegar para painel Pomodoro funciona

**Aguardar aprovação → commit: `feat: add Pomodoro to settings navigation`**

---

## FASE 7 — Limpeza e Auditoria

### Passo 7.1 — Auditoria de tokens
- [ ] Buscar `dp`, `sp`, `Color(`, `fontSize` hardcoded em todo `feature/pomodoro/`
- [ ] Substituir por `KronoTokens`, `KronoType`, `KronoTheme`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Visual consistente com restante do app

**Aguardar aprovação → commit: `refactor: replace hardcoded values in pomodoro feature`**

---

### Passo 7.2 — Auditoria de fluxos
- [ ] Verificar que `PomodoroViewModel` cancela corretamente o loop ao `onCleared()`
- [ ] Verificar que tic-tac para ao pausar e ao fechar overlay
- [ ] Verificar que `PomodoroTaskSession` é salva ao completar sessão com e sem tarefa
- [ ] Verificar que stats gerais refletem todas as sessões incluindo as sem tarefa
- [ ] Verificar que editar preset em uso reinicia ciclo imediatamente

**Verificar antes do commit:**
- [ ] Nenhum vazamento de coroutine
- [ ] Stats consistentes

**Aguardar aprovação → commit: `fix: pomodoro lifecycle and stats consistency`**

---

## Checklist Final de Aceitação

```
□ Timer inicia, pausa, reseta e faz skip
□ Fases avançam automaticamente com cor correta no display e overlay
□ Overlay abre, fecha, arrasta e faz snap
□ Preset padrão (Clássico) carregado no primeiro launch
□ Criar, editar e excluir presets funciona
□ Presets padrão não são editáveis nem excluíveis
□ Criar tarefa sem nome funciona
□ Associar tarefa ao timer funciona
□ Stats da tarefa acumulam corretamente
□ Stats gerais incluem sessões sem tarefa
□ Sons do sistema tocam na troca de fase
□ Tic-tac ativa e desativa corretamente
□ Notificação exibe fase + tempo e botões funcionam
□ Estado persiste após fechar o app
□ Pomodoro aparece no grid da HomeScreen
□ Painel de settings acessível via SettingsScreen
□ Nenhum valor hardcoded em feature/pomodoro/
```
