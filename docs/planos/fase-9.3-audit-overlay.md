# Krono Overlay — Relatório de Melhorias Pontuais

> Contexto: widget em estado pré-final. As melhorias abaixo são ajustes cirúrgicos
> baseados na inspeção do código (`KronoTokens`, `OverlayDimensions`, `OverlayExpandableMenu`,
> `OverlayIconButton`, `UnifiedOverlay`) cruzada com as capturas de tela enviadas.

---

## 1. Crítico / Funcional

### 1.1 Touch target do handle é inutilizável — `handleTouchHeight = 8.dp`

**Arquivo:** `KronoTokens.kt` → `Overlay.handleTouchHeight`  
**Problema:** 8 dp é menos que metade do mínimo recomendado pelo Material Design (48 dp) e pelo iOS HIG (44 pt). Em uso real com o polegar, a área de toque é praticamente impossível de acertar na primeira tentativa, especialmente com o overlay em movimento ou em posição periférica da tela.  
**Fix:**

Expandir a área sem mudar a aparência usando um `Box` com padding interno em vez de alterar a altura do componente diretamente.
24.dp  // mínimo confortável para um tap deliberado

---

### 1.2 Auto-dismiss do menu em 4 s é curto — `menuTimeoutMs = 4000L`

**Arquivo:** `KronoTokens.kt` → `Overlay.menuTimeoutMs`  
**Problema:** 4 s é tempo insuficiente para usuários que precisam encontrar e acionar um ícone específico no menu expandido, especialmente em telas menores ou com escala reduzida. O timer não é resetado ao interagir com o handle (só com as opções do menu).  
**Evidência no código (`OverlayExpandableMenu.kt`):**

```kotlin
onTap = {
    expanded = !expanded
    if (expanded) resetTimer()  // timer reseta só ao abrir, não ao fechar e reabrir
}
```

**Fix:**

```kotlin
val menuTimeoutMs = 5000L
```

E garantir que `resetTimer()` também seja chamado no `onTap` para o caso de reabrir o menu:

```kotlin
onTap = {
    expanded = !expanded
    resetTimer() // sempre, independente da direção
}
```

### 2.2 Botões inativos sem background — `buttonContainerAlpha = 0f`

**Arquivo:** `KronoTokens.kt` → `Overlay.buttonContainerAlpha`  
**Problema:** Os botões inativos são renderizados com fundo transparente (`alpha = 0f`), ficando visíveis apenas pela borda (`buttonBorderAlpha = 0.18f`). Em fundos claros (image 2), os botões perdem presença e parecem mais "wireframe" do que componentes concretos. A borda sozinha não é suficiente para comunicar interatividade.  
**Fix:**

```kotlin
const val buttonContainerAlpha = 0.05f  // presença mínima sem poluir o design
```

Isso mantém a leveza do overlay mas dá substância suficiente para os botões serem percebidos como elementos táteis.

---

### 2.3 Divider do menu expandido com alpha muito baixo — `dividerAlpha = 0.12f`

**Arquivo:** `KronoTokens.kt` → `Overlay.dividerAlpha`  
**Problema:** O divider entre a fileira principal e o menu expandido está no limiar da invisibilidade. Em fundos claros, a separação entre as duas fileiras de botões fica ambígua, reduzindo a leitura da hierarquia do componente.  
**Fix:**

```kotlin
const val dividerAlpha = 0.18f  // ainda sutil, mas perceptível
```

---

### 2.4 Gap assimétrico ao redor do handle

**Arquivo:** `KronoTokens.kt` → `Overlay.handleTopGap` / `handleBottomGap`  
**Problema:** `handleTopGap = 2.dp` vs `handleBottomGap = 7.dp` cria assimetria visual perceptível, com o handle "colado" nos botões acima e com mais espaço abaixo. O handle fica posicionado visivelmente deslocado para cima dentro da sua zona.  
**Fix:**

```kotlin
val handleTopGap    = 4.dp  // era 2
val handleBottomGap = 4.dp  // era 7 — quase igual, mas simétrico
```

---

## 3. Visual — Média Prioridade

### 3.1 Ausência de indicador visual de que o handle é expansível

**Arquivo:** `OverlayExpandableMenu.kt`  
**Problema:** O handle é um pill estático — não há nenhuma pista visual de que ele responde a toque e expande um menu oculto. Primeira interação com o componente depende de descoberta acidental.  
**Opção A (mínima):** Animar sutilmente o `alpha` do pill quando o menu está expandido:

```kotlin
val pillAlpha by animateFloatAsState(
    targetValue = if (expanded) 0.6f else KronoTokens.Alpha.disabled,
    label = "handleAlpha"
)
```

**Opção B (mais explícita):** Adicionar um ícone de chevron (`KeyboardArrowDown`) centralizado e diminuto (8–10 dp), acima ou abaixo do pill, com animação de rotação ao expandir. Seria adicionado somente na zona do `Box` do handle.

---

### 3.2 Ícones do menu expandido com alpha reduzido quando inativos — `Alpha.disabled = 0.38f`

**Arquivo:** `OverlayExpandableMenu.kt`

```kotlin
tint = if (option.isActive) {
    MaterialTheme.colorScheme.primary
} else {
    textColor.copy(alpha = KronoTokens.Alpha.disabled)  // 0.38f
}
```

**Problema:** 0.38f é o alpha de componentes *desabilitados* pelo Material Design — não de componentes *opcionais/inativos*. Ícones inativos do menu (ex.: beep desligado) parecem desabilitados/não-clicáveis, quando na verdade são totalmente interativos.  
**Fix:**

```kotlin
// Usar alpha semântico para "opção disponível, mas não ativa"
textColor.copy(alpha = KronoTokens.Alpha.medium)  // 0.75f — presente, mas diferenciado do ativo
```

---

### 3.3 `labelBottomGap` muito apertado entre label e timer — `2.dp`

**Arquivo:** `KronoTokens.kt` → `Overlay.labelBottomGap`  
**Problema:** O gap de 2 dp entre o label "Foco" e o timer `00:25:00` é mínimo. Com `includeFontPadding = false`, o descende do label e a ascendente do timer ficam muito próximos, especialmente em escala aumentada.  
**Fix:**

```kotlin
val labelBottomGap = 4.dp
```

---

### 3.4 `timeButtonGap` muito apertado — `3.dp`

**Arquivo:** `KronoTokens.kt` → `Overlay.timeButtonGap`  
**Problema:** O padding de 3 dp entre o timer e a fileira de botões é muito pequeno. Visualmente os botões ficam "grudados" no número, sem respiração suficiente para separar a zona de informação (timer) da zona de ação (botões).  
**Fix:**

```kotlin
val timeButtonGap = 6.dp  // dobrar — ainda compacto, mas com respiração
```

---

## 4. Funcional — Média Prioridade

### 4.1 Sem feedback háptico nos botões e no handle

**Arquivo:** `OverlayIconButton.kt`, `OverlayExpandableMenu.kt`  
**Problema:** O overlay opera sobre outras apps (contexto de foco ativo). O usuário está com atenção dividida. Háptico no tap do handle (expand) e nos botões de ação (play/pause/stop) melhora significativamente a confiança na interação sem exigir confirmação visual.  
**Fix:** Adicionar `HapticFeedback` no `onClick` do `OverlayIconButton` e no `onTap` do handle:

```kotlin
// Em OverlayIconButton.kt
val haptic = LocalHapticFeedback.current
// No onClick:
haptic.performHapticFeedback(HapticFeedbackType.LongPress) // ou TextHandleMove para mais leve
```

---

### 4.2 Conflito potencial entre tap no handle e drag do container

**Arquivo:** `OverlayExpandableMenu.kt` / `OverlayContainer.kt`  
**Problema:** O `detectTapGestures` no handle e o `detectDragGestures` no container estão em camadas diferentes do modifier chain, sem consumo explícito de eventos. Em gestos muito curtos (quasi-tap), o drag handler do container pode competir com o tap do handle, causando falsos drags ou taps ignorados.  
**Fix recomendado:** No `pointerInput` do handle, consumir o evento no `onTap`:

```kotlin
detectTapGestures(
    onTap = { offset ->
        it.consume()  // garantir que o drag do container não intercepte
        expanded = !expanded
        resetTimer()
    }
)
```

---

## 5. Baixa Prioridade / Polimento

| # | Observação | Arquivo | Fix |
|---|-----------|---------|-----|
| 5.1 | `timerLetterSpacing = (-2).sp` — tracking negativo forte; pode cortar glifos em fontes não-Krono | `KronoTokens.kt` | Testar com todas as `overlayFontFamily` disponíveis; ajustar por família se necessário |
| 5.2 | `menuBorderColor.animationSpec = tween(600ms)` — transição da borda ao mudar `isRunning` é lenta comparado aos outros timings do app (`durationSlow = 400ms`) | `OverlayBorderColor.kt` | `tween(KronoTokens.Motion.durationSlow)` — remover `+ 200` |
| 5.3 | `bottomOptions` recria a lista em todo recompose de `onToggleBeep` — o `remember` na linha deveria incluir `config.playPauseSoundEnabled` como chave | `UnifiedOverlay.kt` | Já está na key do `remember` — OK. Verificar se `onToggleBeep` lambda está estabilizado no caller |
| 5.4 | `noLabelTopInset = 8.dp` só aplicado quando `bottomOptions` não está vazio — se o menu estiver vazio, o timer não tem inset superior | `UnifiedOverlay.kt` | Considerar aplicar `noLabelTopInset` sempre que `cleanLabel.isEmpty()`, independente do menu |
| 5.5 | `maxCornerRadiusFloat = 80f` com `defaultCornerRadius = 32.dp` — se o usuário configurar a escala máxima, o corner pode saturar no máximo e criar inconsistência com o border radius do handle visual | `KronoTokens.kt` | Documentar o ceiling no token e validar visualmente nos extremos de `scale` |

---

## Resumo Executivo

| Prioridade | Quantidade | Impacto |
|-----------|-----------|---------|
| Crítico/Funcional | 2 | Touch target do handle + timeout do menu |
| Visual Alta | 3 | Handle pill, bg dos botões inativos, divider alpha |
| Visual Média | 4 | Chevron handle, alpha menu inativo, gaps timer |
| Funcional Média | 2 | Háptico, conflito tap/drag |
| Polimento | 5 | Micro-ajustes de token e lambda stability |

**Veredicto:** `PASS WITH NOTES` — pronto para ship após os 2 itens críticos e os 3 visuais de alta prioridade.
