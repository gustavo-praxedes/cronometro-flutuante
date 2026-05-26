# Krono — Unificação Visual de Overlays
> Versão 2.0 | Substitui plano anterior de unificação de infra

---

## 1. Visão Geral

Todos os overlays do Krono passam a compartilhar o mesmo design, estrutura e comportamento. Base visual: `StopwatchOverlay`. Diferença entre features: apenas conteúdo das linhas 3 (botões fixos) e 4 (quick options).

---

## 2. Anatomia do Overlay Unificado

```
┌─────────────────────────────────────────┐
│ Label opcional                      [ ⋮ ]  │  ← Linha 1 (some se sem label, MoreVert permanece)
├─────────────────────────────────────────┤
│ HH:MM:SS.mmm                            │  ← Linha 2
├─────────────────────────────────────────┤
│  ▶        ■        +1                   │  ← Linha 3 (SpaceBetween)
├ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┤  ← Divisor (só quando menu expandido)
│  ☯        ☀        ⬡                   │  ← Linha 4 (menu expansível, empurra)
│                [────────────]           │  ← ícone de barra para expandir/recolher
└─────────────────────────────────────────┘
```

### Linha 1 — Label + Menu Topo
- Label opcional alinhado à esquerda
- `⋮` (`MoreVert`) alinhado à direita — abre menu dropdown:
  - `✕ Fechar` — encerra overlay
  - `↩ Voltar` — fecha overlay e abre app na tela da feature
- **Se label vazio:** linha 1 some completamente, overlay encolhe

### Linha 2 — Tempo
- Formato: `HH:MM:SS` ou `HH:MM:SS.mmm` se ms habilitado
- Fonte monospace (`overlayFontFamily`)
- Largura mínima do overlay = largura desta linha quando ms ativo (linha mais larga)

### Linha 3 — Botões Fixos por Feature
- Distribuição: `SpaceBetween` horizontal
- Botões por feature:

| Feature | Botões |
|---------|--------|
| Stopwatch | play/pause · stop · lap *(placeholder)* |
| Countdown | play/pause · stop · +1 min |
| Pomodoro | play/pause · stop · next |
| Clock | *(sem botões — linha 3 ausente)* |
| Calculator | *(sem botões — linha 3 ausente)* |
| Counter | −1 · reset · +1 |
| Alarms | *(sem botões — linha 3 ausente)* |

### Linha 4 — Menu Expansível
- Acionado pelo ícone de barra horizontal (`MoreHoriz`) na base do overlay
- Abre para **baixo** — overlay cresce verticalmente (`expandVertically + fadeIn`)
- Fecha automaticamente após timeout sem interação (mesmo padrão do Stopwatch)
- `HorizontalDivider` aparece **apenas quando expandido**
- Quick options (ícones horizontais):

| Opção | Todas as features |
|-------|-------------------|
| Focus | ✓ |
| KeepScreen | ✓ |
| AutoLaunch | ✓ |

### Largura
- `wrapContentWidth` com `widthIn(min = larguraDaLinhaMaisLarga)`
- Linha 2 com ms ativo (`HH:MM:SS.mmm`) tende a ser a mais larga
- Linha 3 com 3 botões pode ser mais larga que linha 2 sem ms

---

## 3. Componentes a Criar/Refatorar

```
core/ui/overlay/
├── UnifiedOverlay.kt          # composable principal unificado
├── OverlayContainer.kt        # infra: drag, animação, border, bg, shape
├── OverlayScaleState.kt       # entranceScale + entranceAlpha + dragScale
├── OverlayDimensions.kt       # paddingH/V, cornerRadius, min/maxWidth
├── OverlayBorderColor.kt      # animação de cor do border
├── OverlayTopMenu.kt          # ⋮ dropdown (Fechar / Voltar)
├── OverlayExpandableMenu.kt   # linha 4: quick options expansíveis
└── AnimatedIconButton.kt      # promovido de privado → público
```

### UnifiedOverlay — assinatura

```kotlin
@Composable
fun UnifiedOverlay(
    timeDisplay       : String,              // já formatado: "01:23:45" ou "01:23:45.123"
    label             : String?,             // null ou vazio → linha 1 some
    isRunning         : Boolean,
    config            : OverlayConfig,
    scale             : Float,
    buttons           : List<OverlayButton>, // linha 3
    quickOptions      : List<OverlayQuickOption>, // linha 4 (fixo + específicos)
    onDrag            : (dx: Float, dy: Float) -> Unit,
    onDragEnd         : () -> Unit,
    onClose           : () -> Unit,
    onNavigateToApp   : () -> Unit,
    modifier          : Modifier = Modifier
)

data class OverlayButton(
    val icon        : ImageVector,
    val description : String,
    val isActive    : Boolean = false,
    val onClick     : () -> Unit
)

data class OverlayQuickOption(
    val icon        : ImageVector,
    val description : String,
    val isActive    : Boolean,
    val onClick     : () -> Unit
)
```

---

## 4. Refatoração por Feature

| Feature | Mudança |
|---------|---------|
| `StopwatchOverlay` | Migra para `UnifiedOverlay`. Adiciona placeholder de Lap. Remove X dos botões → vai para menu `⋮`. |
| `CountdownOverlayUi` | Migra para `UnifiedOverlay`. Label = `config.description`. Remove close inline. |
| `PomodoroOverlay` | Migra para `UnifiedOverlay` direto. Remove proxy `CountdownState`. Label = `phaseLabel`. |
| `ClockOverlay` *(novo)* | Usa `UnifiedOverlay` desde o início. Sem linha 3. |
| `CalculatorOverlay` *(novo)* | Usa `UnifiedOverlay` desde o início. Sem linha 3. |
| `CounterOverlay` *(novo)* | Usa `UnifiedOverlay` desde o início. Botões: −1, reset, +1. |
| `AlarmOverlay` *(novo)* | Usa `UnifiedOverlay` desde o início. Sem linha 3. |

---

## 5. Decisões

| Decisão | Alternativas | Rationale |
|---|---|---|
| Base visual = StopwatchOverlay | Countdown, design novo | Pedido explícito |
| Linha 1 some quando sem label | Placeholder vazio | Overlay mais compacto |
| ⋮ substitui X inline | Manter X nos botões | Consistência entre features |
| Linha 4 empurra overlay | Sobrepõe | Pedido explícito |
| Divisor só quando expandido | Sempre visível | Pedido explícito |
| SpaceBetween nos botões | Left, Center | Pedido explícito |
| Largura = linha mais larga | Largura fixa | Adaptável ao conteúdo |
| Quick options fixos: Focus, KeepScreen, AutoLaunch | Beep incluído | Pedido explícito |
| Lap como placeholder | Implementar agora | Fora do escopo atual |
| +1 Countdown = fixo 1 min | Configurável, picker | Pedido explícito |

---

## 6. Assumptions

- `[ASSUMPTION]` `timeDisplay` formatado pelo ViewModel de cada feature antes de passar ao overlay
- `[ASSUMPTION]` Features sem linha 3 passam `buttons = emptyList()`
- `[ASSUMPTION]` Timeout do menu expansível: mesmo valor de `KronoTokens.Overlay.menuTimeoutMs`
- `[ASSUMPTION]` Flash de cor do Pomodoro continua sendo responsabilidade do `PomodoroOverlay` wrapper
- `[ASSUMPTION]` Cor, escala, opacidade e posição continuam configuráveis individualmente por feature via `OverlayConfig`
- `[ASSUMPTION]` Múltiplos overlays simultâneos não são afetados
- `[ASSUMPTION]` Layout interno (linhas) não é configurável pelo usuário — fixo por design
- `[ASSUMPTION]` Lap button recebe `onClick = {}` e alpha reduzido até implementação

---

## 7. Plano de Implementação

---

### Fase 1 — Infraestrutura Base
**Objetivo:** criar componentes de infra sem tocar nos overlays existentes.

**Passos:**
1. Criar `AnimatedIconButton.kt` em `core/ui/components/` — mover de privado para público
2. Criar `OverlayScaleState.kt` — encapsular `entranceScale`, `entranceAlpha`, `isDragging`, `dragScale`, `rememberOverlayScaleState()`
3. Criar `OverlayDimensions.kt` — encapsular `paddingH/V`, `cornerRadius`, `minWidth`, `iconSize`, `btnSize`, `controlGap`, `rememberOverlayDimensions()`
4. Criar `OverlayBorderColor.kt` — `@Composable fun overlayBorderColor(isRunning, textColor): Color`
5. Criar `OverlayContainer.kt` — Box com `graphicsLayer`, `background`, `border`, `pointerInput`, usando os componentes acima

**Verificações antes do commit:**
- [ ] Projeto compila sem erros
- [ ] Nenhum overlay existente foi modificado
- [ ] `AnimatedIconButton` importável de `core/ui/components`
- [ ] `OverlayContainer` renderiza corretamente isolado (teste visual com preview)

> **Commit:** `refactor: adiciona infraestrutura base de overlay unificado (container, scale, dimensions)`

---

### Fase 2 — OverlayTopMenu e OverlayExpandableMenu
**Objetivo:** criar os dois menus reutilizáveis.

**Passos:**
1. Criar `OverlayTopMenu.kt` — dropdown `⋮` com itens "Fechar" e "Voltar", usando `DropdownMenu` do Material 3
2. Criar `OverlayExpandableMenu.kt` — linha 4 com `AnimatedVisibility` (`expandVertically + fadeIn`), `HorizontalDivider` condicional, row de quick options, ícone de barra (`MoreHoriz`), lógica de timeout
3. Criar `OverlayButton` e `OverlayQuickOption` data classes em `core/ui/overlay/OverlayModels.kt`

**Verificações antes do commit:**
- [ ] Projeto compila sem erros
- [ ] Dropdown `⋮` abre e fecha corretamente em preview
- [ ] Menu expansível: abre para baixo, empurra conteúdo, fecha por timeout
- [ ] Divisor aparece apenas quando menu expandido
- [ ] Nenhum overlay existente modificado

> **Commit:** `feat: adiciona OverlayTopMenu e OverlayExpandableMenu reutilizáveis`

---

### Fase 3 — UnifiedOverlay
**Objetivo:** compor o overlay unificado completo.

**Passos:**
1. Criar `UnifiedOverlay.kt` compondo: `OverlayContainer`, linha 1 (label + `OverlayTopMenu`), linha 2 (tempo), linha 3 (botões com `SpaceBetween`), `OverlayExpandableMenu`
2. Linha 1: `AnimatedVisibility` — some completamente quando `label` null ou vazio
3. Linha 3: `AnimatedVisibility` — some quando `buttons` vazio
4. Largura mínima: calculada via `SubcomposeLayout` ou `IntrinsicSize.Max` sobre todas as linhas
5. Criar preview com label, sem label, com ms, sem ms, com 3 botões, sem botões

**Verificações antes do commit:**
- [ ] Projeto compila sem erros
- [ ] Preview com label: linha 1 visível
- [ ] Preview sem label: linha 1 ausente, overlay mais compacto
- [ ] Preview com ms: linha 2 exibe `HH:MM:SS.mmm`
- [ ] Preview sem botões: linha 3 ausente
- [ ] Largura se adapta à linha mais larga
- [ ] Menu `⋮` funciona dentro do `UnifiedOverlay`
- [ ] Menu expansível funciona dentro do `UnifiedOverlay`
- [ ] Nenhum overlay existente modificado

> **Commit:** `feat: adiciona UnifiedOverlay — composable unificado para todos os overlays`

---

### Fase 4 — Migrar StopwatchOverlay
**Objetivo:** primeira feature real usando `UnifiedOverlay`.

**Passos:**
1. Refatorar `StopwatchOverlay` para usar `UnifiedOverlay`
2. Definir `buttons`: play/pause, stop, lap (placeholder: `onClick = {}`, alpha reduzido)
3. Definir `quickOptions`: Focus, KeepScreen, AutoLaunch
4. Remover: toda infra de animação, drag, border, menu inline — substituída pelo `UnifiedOverlay`
5. Manter: lógica de `menuVisible`, `resetMenuTimer` (agora dentro de `OverlayExpandableMenu`)
6. Remover `QuickOptionIcon` privado — usa `OverlayQuickOption` via `UnifiedOverlay`

**Verificações antes do commit:**
- [ ] Stopwatch inicia, pausa, reseta normalmente
- [ ] Drag funciona
- [ ] Animação de entrada idêntica ao anterior
- [ ] Border muda de cor ao iniciar/pausar
- [ ] Menu expansível abre, exibe 3 quick options, fecha por timeout
- [ ] `⋮` abre dropdown Fechar / Voltar
- [ ] Fechar encerra overlay
- [ ] Voltar fecha overlay e abre app no Stopwatch
- [ ] Lap aparece desabilitado (placeholder)
- [ ] Overlay sem label (Stopwatch não tem) — linha 1 só com `⋮`
- [ ] Outros overlays não afetados

> **Commit:** `refactor: migra StopwatchOverlay para UnifiedOverlay`

---

### Fase 5 — Migrar CountdownOverlay
**Objetivo:** segunda feature migrada.

**Passos:**
1. Refatorar `CountdownOverlayUi` para usar `UnifiedOverlay`
2. `label` = `state.config.description` (vazio → linha 1 reduz ao `⋮`)
3. `buttons`: play/pause, stop, +1 min
4. `+1 min`: incrementa `remainingSeconds + 60` via ViewModel
5. Remover: infra de animação, drag, border, close inline, label Row manual
6. Verificar que `overlayWidthScale` e `bottomExtraButtonScale` legados são removidos (não mais necessários)

**Verificações antes do commit:**
- [ ] Countdown inicia, pausa, reseta normalmente
- [ ] +1 min adiciona 60 segundos ao timer
- [ ] Label visível quando configurado, linha 1 compacta quando vazio
- [ ] Drag, animação, border funcionam
- [ ] Menu expansível: Focus, KeepScreen, AutoLaunch
- [ ] `⋮` Fechar / Voltar funcionam
- [ ] Pomodoro não afetado (ainda usa proxy antigo)
- [ ] Stopwatch não afetado

> **Commit:** `refactor: migra CountdownOverlay para UnifiedOverlay`

---

### Fase 6 — Migrar PomodoroOverlay
**Objetivo:** remover proxy `CountdownState` e migrar para `UnifiedOverlay`.

**Passos:**
1. Refatorar `PomodoroOverlay` para usar `UnifiedOverlay` diretamente
2. `label` = `state.phaseLabel`
3. `buttons`: play/pause, stop, next
4. Manter lógica de flash de cor de fase (`LaunchedEffect` com repeat de cor) — passa cor resultante como `bgColor` ao `UnifiedOverlay`
5. Remover: construção do proxy `CountdownState`, dependência de `CountdownOverlayUi`

**Verificações antes do commit:**
- [ ] Pomodoro inicia, pausa, reseta normalmente
- [ ] Botão Next avança fase
- [ ] Flash de cor ao trocar de fase funciona
- [ ] Label exibe fase atual (ex: "Foco", "Pausa Curta")
- [ ] Menu expansível: Focus, KeepScreen, AutoLaunch
- [ ] `⋮` Fechar / Voltar funcionam
- [ ] Stopwatch e Countdown não afetados

> **Commit:** `refactor: migra PomodoroOverlay para UnifiedOverlay, remove proxy CountdownState`

---

### Fase 7 — Padronizar Novos Overlays
**Objetivo:** garantir que Clock, Calculator, Counter e Alarms nascem no padrão.

**Passos:**
1. Adicionar KDoc em `UnifiedOverlay.kt` com exemplo mínimo de uso
2. Criar `ClockOverlay.kt` usando `UnifiedOverlay` — sem linha 3 (`buttons = emptyList()`), sem label
3. Criar `CounterOverlay.kt` usando `UnifiedOverlay` — `buttons`: −1, reset, +1
4. Criar `CalculatorOverlay.kt` usando `UnifiedOverlay` — sem linha 3, sem label
5. Criar `AlarmOverlay.kt` usando `UnifiedOverlay` — sem linha 3, sem label
6. Definir regra de PR: novos overlays obrigatoriamente usam `UnifiedOverlay`

**Verificações antes do commit:**
- [ ] Todos os 4 novos overlays renderizam corretamente em preview
- [ ] Counter: −1 desabilitado quando count = 0
- [ ] Todos os overlays podem ser abertos simultaneamente sem conflito
- [ ] Tema claro e escuro corretos em todos
- [ ] KDoc claro e exemplificado

> **Commit:** `feat: adiciona overlays de Clock, Counter, Calculator e Alarm no padrão UnifiedOverlay`

---

### Fase 8 — Smoke Test Final e Limpeza
**Objetivo:** validar sistema completo e remover código morto.

**Passos:**
1. Abrir todos os overlays simultaneamente — verificar independência de posição, cor e escala
2. Trocar tema do app com overlays abertos — verificar atualização em todos
3. Remover arquivos/funções privadas órfãs (`QuickOptionIcon` em Stopwatch, `AnimatedIconButton` privado em Countdown, proxy `CountdownState` do Pomodoro)
4. Verificar que `CountdownOverlayUi` não tem mais assinatura com parâmetros legados (`overlayWidthScale`, `bottomExtraButtonScale`, etc.)
5. Rodar lint e verificar zero warnings em `feature/*/overlay`

**Verificações antes do commit:**
- [ ] Todos os overlays simultâneos: funcionando e independentes
- [ ] Troca de tema: atualiza todos os overlays em tempo real
- [ ] Zero código morto em overlay files
- [ ] Lint limpo
- [ ] Builds release sem warnings

> **Commit:** `chore: remove código legado de overlays e valida sistema unificado completo`
