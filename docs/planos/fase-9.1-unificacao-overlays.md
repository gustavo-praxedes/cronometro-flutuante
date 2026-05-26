# Krono — Unificação de Overlays
> Versão 1.0

---

## 1. Diagnóstico

Três overlays existentes (`StopwatchOverlay`, `CountdownOverlayUi`, `PomodoroOverlay`) compartilham código duplicado em:

| Bloco duplicado | Onde aparece |
|---|---|
| `entranceScale` + `entranceAlpha` animatables + `LaunchedEffect` | Stopwatch, Countdown |
| `isDragging` + `dragScale` + `animateFloatAsState` | Stopwatch, Countdown |
| `borderColor` animado (`isRunning → primary`, idle → `textColor`) | Stopwatch, Countdown |
| `graphicsLayer { scaleX/Y, alpha, shape, clip }` | Stopwatch, Countdown |
| `background + border + pointerInput detectDragGestures` | Stopwatch, Countdown |
| `paddingH/V/cornerRadius` escalados por `scale × currentScale` | Stopwatch, Countdown |
| `compactFactor` por visibilidade de horas/segundos/botões | Stopwatch, Countdown |
| `minWidth/maxWidth` calculados com scale | Stopwatch, Countdown |
| `QuickOptionIcon` / `AnimatedIconButton` | Stopwatch, Countdown (privado) |

`PomodoroOverlay` é wrapper fino sobre `CountdownOverlayUi` — já bem fatorado, mas depende de `CountdownState` como proxy, acoplamento desnecessário.

---

## 2. Arquitetura Alvo

```
core/ui/overlay/
├── OverlayContainer.kt        # Box unificado: entrada, drag, border, bg, shape
├── OverlayScaleState.kt       # entranceScale + entranceAlpha + dragScale encapsulados
├── OverlayDimensions.kt       # paddingH/V, cornerRadius, minWidth, maxWidth, compactFactor
├── OverlayBorderColor.kt      # animateColorAsState do border (isRunning → primary)
└── AnimatedIconButton.kt      # movido de privado → público compartilhado
```

Cada overlay passa a ser apenas **conteúdo** dentro de `OverlayContainer`. Zero lógica de animação/drag nos overlays filhos.

OverlayContainer recebe label: String? = null. Null ou vazio → sem renderização de label. Cada overlay decide se passa ou não.

### OverlayContainer — assinatura

```kotlin
@Composable
fun OverlayContainer(
    isRunning     : Boolean,
    scale         : Float,
    cornerRadius  : Float,
    bgColor       : Color,
    textColor     : Color,
    onDrag        : (dx: Float, dy: Float) -> Unit,
    onDragEnd     : () -> Unit,
    modifier      : Modifier = Modifier,
    content       : @Composable ColumnScope.(currentScale: Float, txtColor: Color) -> Unit
)
```

### OverlayScaleState — uso

```kotlin
@Composable
fun rememberOverlayScaleState(): OverlayScaleState {
    // encapsula entranceScale, entranceAlpha, dragScale, isDragging
    // expõe: currentScale, alpha, dragScale, onDragStart, onDragEnd
}
```

### OverlayDimensions — uso

```kotlin
@Stable
data class OverlayDimensions(
    val paddingH      : Dp,
    val paddingV      : Dp,
    val cornerRadius  : Dp,
    val minWidth      : Dp,
    val maxWidth      : Dp,
    val iconSize      : Dp,
    val btnSize       : Dp,
    val controlGap    : Dp,
)

@Composable
fun rememberOverlayDimensions(
    scale         : Float,
    currentScale  : Float,
    showHours     : Boolean,
    showSeconds   : Boolean,
    showButtons   : Boolean,
    widthScale    : Float = 1f,
): OverlayDimensions
```

---

## 3. Resultado por Overlay

| Overlay | Antes | Depois |
|---|---|---|
| `StopwatchOverlay` | ~180 linhas lógica infra | ~60 linhas só conteúdo |
| `CountdownOverlayUi` | ~160 linhas lógica infra | ~60 linhas só conteúdo |
| `PomodoroOverlay` | wrapper + proxy CountdownState | wrapper limpo sem proxy |
| `AnimatedIconButton` | 2× privado duplicado | 1× público em `core/ui` |

Nota: unificação é refactor puro de infra. Cada overlay mantém layout interno independente (horizontal/vertical decidido por feature). Múltiplos overlays abertos simultaneamente continuam funcionando — posição, cor, escala e opacidade permanecem configuráveis individualmente por overlay via OverlayConfig.

---

## 4. Decisões

| Decisão | Alternativas | Rationale |
|---|---|---|
| `OverlayContainer` como `@Composable` Box | Modifier extension, base class | Composable é idiomático Compose |
| `rememberOverlayScaleState()` encapsula animatables | Passar animatables como params | Estado encapsulado = testável |
| `OverlayDimensions` como `@Stable data class` | Calcular inline | Evita recalc a cada recomposição |
| `AnimatedIconButton` público em `core/ui` | Manter privado em cada feature | DRY, já usado em 2 features |
| `PomodoroOverlay` sem proxy `CountdownState` | Manter proxy | Desacopla Pomodoro de Countdown |

---

## 5. Plano de Implementação

---

### Fase 1 — Extrair AnimatedIconButton
**Objetivo:** eliminar duplicação mais simples e óbvia primeiro.

**Passos:**
1. Mover `AnimatedIconButton` de `CountdownOverlay.kt` para `core/ui/components/AnimatedIconButton.kt`
2. Verificar se `StopwatchOverlay` já importa de `core` ou tem cópia local — unificar import
3. Remover declaração privada de `CountdownOverlay.kt`
4. Verificar outros usos no projeto — migrar todos

> **Commit sugerido:** `refactor: promote AnimatedIconButton to shared core component`

---

### Fase 2 — OverlayScaleState
**Objetivo:** encapsular lógica de animação de entrada e drag.

**Passos:**
1. Criar `core/ui/overlay/OverlayScaleState.kt` com `rememberOverlayScaleState()`
2. Encapsular: `entranceScale`, `entranceAlpha`, `LaunchedEffect(Unit)`, `isDragging`, `dragScale`
3. Expor: `currentScale: Float`, `alpha: Float`, `combinedScale: Float`, `onDragStart()`, `onDragEnd()`
4. Substituir bloco de animação em `StopwatchOverlay` pelo novo `rememberOverlayScaleState()`
5. Substituir bloco de animação em `CountdownOverlayUi` pelo novo `rememberOverlayScaleState()`
6. Verificar paridade visual — smoke test nos dois overlays

> **Commit sugerido:** `refactor: extract overlay entrance and drag animation into OverlayScaleState`

---

### Fase 3 — OverlayDimensions
**Objetivo:** encapsular cálculo de dimensões escaladas.

**Passos:**
1. Criar `core/ui/overlay/OverlayDimensions.kt` com `rememberOverlayDimensions()`
2. Encapsular: `paddingH`, `paddingV`, `cornerRadius`, `minWidth`, `maxWidth`, `iconSize`, `btnSize`, `controlGap`, `compactFactor`
3. Substituir cálculos inline em `StopwatchOverlay`
4. Substituir cálculos inline em `CountdownOverlayUi`
5. Verificar que `overlayWidthScale` e `bottomExtraButtonScale` do Countdown são passados corretamente

> **Commit sugerido:** `refactor: extract overlay scaled dimensions into OverlayDimensions`

---

### Fase 4 — OverlayBorderColor
**Objetivo:** encapsular animação de cor do border.

**Passos:**
1. Criar `core/ui/overlay/OverlayBorderColor.kt` com função `@Composable overlayBorderColor(isRunning, textColor): Color`
2. Substituir `animateColorAsState` de border em `StopwatchOverlay`
3. Substituir `animateColorAsState` de border em `CountdownOverlayUi`

> **Commit sugerido:** `refactor: extract overlay border color animation into shared util`

---

### Fase 5 — OverlayContainer
**Objetivo:** unificar Box de infra (bg, border, shape, graphicsLayer, drag).

**Passos:**
1. Criar `core/ui/overlay/OverlayContainer.kt`
2. Implementar: `graphicsLayer`, `background`, `border`, `pointerInput detectDragGestures` unificados
3. Usar `OverlayScaleState`, `OverlayDimensions`, `overlayBorderColor` internamente
4. Refatorar `StopwatchOverlay`: substituir Box infra por `OverlayContainer`, manter só conteúdo
5. Refatorar `CountdownOverlayUi`: idem
6. Smoke test completo: drag, animação de entrada, border running/idle, tema claro/escuro

> **Commit sugerido:** `refactor: unify overlay infrastructure into OverlayContainer`

---

### Fase 6 — Desacoplar PomodoroOverlay
**Objetivo:** remover dependência de `CountdownState` como proxy.

**Passos:**
1. Criar `PomodoroOverlayState` próprio (ou usar `PomodoroState` diretamente) sem depender de `CountdownState`
2. Refatorar `PomodoroOverlay` para usar `OverlayContainer` diretamente
3. Remover construção do proxy `CountdownState` dentro de `PomodoroOverlay`
4. Manter lógica de flash de cor de fase (já isolada em `LaunchedEffect`)
5. Verificar que botão "Próximo" (next) e demais controles funcionam igual

> **Commit sugerido:** `refactor: decouple PomodoroOverlay from CountdownState proxy`

---

### Fase 7 — Novos Overlays usam OverlayContainer
**Objetivo:** garantir que Clock, Calculator, Counter e Alarms nunca dupliquem infra.

**Passos:**
1. Documentar `OverlayContainer` com KDoc exemplificando uso mínimo
2. Criar template/snippet interno de referência para novos overlays
3. Implementar `ClockOverlay` usando `OverlayContainer` (Fase 1 do Clock)
4. Definir padrão: PR review verifica que novos overlays não reimplementam infra

> **Commit sugerido:** `docs: document OverlayContainer usage pattern for new features`

---

## 6. Assumptions

- `[ASSUMPTION]` `KronoTokens.Overlay.*` permanece como fonte de verdade de valores base
- `[ASSUMPTION]` `overlayColorsForTheme` permanece em `core/ui/theme` sem alteração
- `[ASSUMPTION]` Sem alteração visual em nenhuma fase — refactor puro
- `[ASSUMPTION]` Smoke test manual suficiente por fase; sem novo teste automatizado no escopo
- `[ASSUMPTION]` `PomodoroOverlay` mantém lógica de flash de cor — só remove proxy de state
- `[ASSUMPTION]` Layout interno (horizontal vs vertical) não é unificado — cada overlay decide
- `[ASSUMPTION]` Cor, escala, opacidade e posição permanecem configuráveis individualmente por overlay
- `[ASSUMPTION]` Múltiplos overlays simultâneos não são afetados pela unificação