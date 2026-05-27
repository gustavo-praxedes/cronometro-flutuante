# Plano de Refatoração Visual — PomodoroPresetEditorDialog

> Objetivo: elevar o dialog a nível premium Material 3, com hierarquia clara, cards visíveis, sliders com título e alinhamento consistente dentro e fora de grupos.

---

## Diagnóstico Atual (Problemas Identificados)

| # | Problema | Arquivo | Impacto |
|---|----------|---------|---------|
| 1 | Cards de fase sem container visível — parecem itens soltos | `PomodoroPhaseCard.kt` | Hierarquia visual fraca |
| 2 | Slider sem label de título acima — só valor inline | `PomodoroPresetItemList.kt`, `PomodoroGroupCard.kt` | Affordance baixa |
| 3 | Ícone drag handle e color dot sem padding/alinhamento consistente no `leading` | `PomodoroPhaseCard.kt` | Desalinhamento visual |
| 4 | Dentro do grupo: `PomodoroPhaseCard` usa `padding(start = sm)` mas sem estrutura de container próprio | `PomodoroGroupCard.kt` | Cards filhos invisíveis |
| 5 | Botão colapso/expansão do grupo (`-`/`+`) como `offset` negativo — gambiarra de posicionamento | `PomodoroGroupCard.kt` | Frágil, desalinhado |
| 6 | `SettingsDivider` antes de cada card filho dentro do grupo — congestionamento visual | `PomodoroGroupCard.kt` | Poluição visual |
| 7 | Botão "Adicionar card" dentro do grupo sem espaçamento superior adequado | `PomodoroGroupCard.kt` | Apertado |
| 8 | Falta de seção visual separando "ciclos" do restante no corpo do dialog | `PomodoroPresetItemList.kt` | Sem hierarquia de seção |
| 9 | Error text sem espaçamento superior | `PomodoroPresetEditorDialog.kt` | Colado no conteúdo acima |
| 10 | `SettingsRow` no `PomodoroPhaseCard` sem shape/fundo — invisível como card | `PomodoroPhaseCard.kt` | Card sem identidade visual |

---

## Passo a Passo de Refatoração

---

### PASSO 1 — `PomodoroPhaseCard.kt`: Tornar o card visível com Surface

**Objetivo:** Cada fase deve parecer um card independente, com fundo elevado, borda e shape.

**O que fazer:**

Envolva o `SettingsRow` em um `Surface` com:
- `shape = KronoTokens.Shape.card`
- `color = MaterialTheme.colorScheme.surface`
- `tonalElevation = 1.dp` (ou `2.dp` para mais destaque)
- `border` via `Modifier.border(KronoTokens.Stroke.divider, MaterialTheme.colorScheme.outlineVariant, KronoTokens.Shape.card)` — opcional se tonalElevation já diferencia

```kotlin
// ANTES
SettingsRow(
    title = ...,
    ...
    modifier = modifier,
    ...
)

// DEPOIS
Surface(
    shape = KronoTokens.Shape.card,
    tonalElevation = 1.dp,
    modifier = modifier
        .fillMaxWidth()
        .border(
            width = KronoTokens.Stroke.divider,
            color = MaterialTheme.colorScheme.outlineVariant,
            shape = KronoTokens.Shape.card
        )
) {
    SettingsRow(
        title = ...,
        ...
        modifier = Modifier, // sem modifier externo aqui
        ...
    )
}
```

**Alinhamento do `leading`:**

O `Row` do leading deve ter `Alignment.CenterVertically` e `horizontalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.xs)`. O color dot deve ter tamanho fixo `KronoTokens.Icon.button` com `size()` explícito. Não altere tamanhos — apenas garanta que o espaçamento entre drag handle e dot use `spacedBy`.

---

### PASSO 2 — `PomodoroGroupCard.kt`: Corrigir botão de colapso

**Objetivo:** Remover o `offset` negativo frágil. Integrar o botão expand/collapse dentro do header do grupo, como um ícone real no início da linha.

**O que fazer:**

1. Remova o `Box` externo que usa `Alignment.TopStart` com `offset(x = (-8).dp, y = 18.dp)`.
2. Adicione o ícone de expand/collapse **diretamente no `Row` do header**, **antes** do drag handle, como primeiro filho:

```kotlin
Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.xs)
) {
    // 1. Expand/collapse — primeiro
    IconButton(
        onClick = { expanded = !expanded },
        modifier = Modifier.size(KronoTokens.Size.iconBox)
    ) {
        Icon(
            imageVector = if (expanded) KronoIcons.Action.ExpandLess else KronoIcons.Action.ExpandMore,
            contentDescription = if (expanded) "Recolher" else "Expandir"
        )
    }
    
    // 2. Drag handle (quando presente)
    if (rootDragDropState != null) { ... }
    
    // 3. Label ou TextField
    ...
    
    // 4. Delete — no final
    IconButton(onClick = onDeleteGroup) { ... }
}
```

> **Ícones sugeridos:** Substituir texto `-`/`+` por `KronoIcons.Action.ExpandLess` / `KronoIcons.Action.ExpandMore` (ou equivalente no KronoIcons). Isso elimina o `Text` com estilo `titleMedium` usado como botão, que não tem semantics corretos.

---

### PASSO 3 — `PomodoroGroupCard.kt`: Cards filhos dentro do grupo com container

**Objetivo:** Fases dentro do grupo devem ter o mesmo aspecto de card do Passo 1 — sem `SettingsDivider` entre elas.

**O que fazer:**

1. **Remova** o `SettingsDivider()` que precede cada `PomodoroPhaseCard` dentro do `forEachIndexed`.
2. Use `Arrangement.spacedBy(KronoTokens.Spacing.xs)` no `Column` pai (já existe, confirme que o valor é `xs` ou `sm`).
3. O `PomodoroPhaseCard` com `Surface` (Passo 1) já entregará a separação visual via elevation/borda.
4. Adicione `padding(horizontal = KronoTokens.Spacing.sm)` no `modifier` do card filho — mantendo o indentamento visual que sinaliza hierarquia.

```kotlin
// ANTES
group.phases.forEachIndexed { index, phase ->
    SettingsDivider()
    PomodoroPhaseCard(
        ...
        modifier = Modifier.padding(start = KronoTokens.Spacing.sm)
    )
}

// DEPOIS
group.phases.forEachIndexed { index, phase ->
    PomodoroPhaseCard(
        ...
        modifier = Modifier.padding(horizontal = KronoTokens.Spacing.sm)
    )
}
```

---

### PASSO 4 — Sliders com título de seção (`AppearanceSlider`)

**Objetivo:** Sliders devem ser precedidos por um label de seção, não depender apenas do texto inline do próprio slider.

**Onde ocorre:**
- `PomodoroPresetItemList.kt` → slider de ciclos globais
- `PomodoroGroupCard.kt` → slider de ciclos do grupo

**O que fazer:**

Antes de cada `AppearanceSlider`, adicione um `Text` de seção usando `labelMedium` ou `bodySmall` com cor `onSurfaceVariant`:

```kotlin
// Em PomodoroPresetItemList.kt
SettingsDivider()
Text(
    text = stringResource(R.string.pomodoro_section_cycles), // "Ciclos de repetição"
    style = MaterialTheme.typography.labelMedium,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier = Modifier.padding(horizontal = KronoTokens.Settings.panelHorizontalInset)
)
AppearanceSlider(
    label = stringResource(R.string.pomodoro_custom_cycles_inline, cycles),
    ...
)
```

```kotlin
// Em PomodoroGroupCard.kt — dentro do Column expandido
Text(
    text = stringResource(R.string.pomodoro_section_group_cycles), // "Ciclos do grupo"
    style = MaterialTheme.typography.labelMedium,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier = Modifier.padding(horizontal = KronoTokens.Settings.panelHorizontalInset)
)
AppearanceSlider(
    label = stringResource(R.string.pomodoro_group_cycles_inline, group.cycles),
    ...
)
```

> **Nota strings:** Adicione `pomodoro_section_cycles` e `pomodoro_section_group_cycles` em `strings.xml`. Ex: `"Ciclos de repetição"` e `"Ciclos do grupo"`.

---

### PASSO 5 — `PomodoroGroupCard.kt`: Espaçamento do botão "Adicionar card" interno

**Objetivo:** O botão "Adicionar card" dentro do grupo deve ter espaço visual adequado acima e abaixo — não colado nos cards.

**O que fazer:**

Remova o `padding(top = 4.dp)` hardcoded e use token:

```kotlin
// ANTES
OutlinedButton(
    onClick = onAddPhase,
    modifier = Modifier
        .fillMaxWidth()
        .padding(top = 4.dp)
)

// DEPOIS
OutlinedButton(
    onClick = onAddPhase,
    modifier = Modifier
        .fillMaxWidth()
        .padding(top = KronoTokens.Spacing.xs)
)
```

---

### PASSO 6 — `PomodoroPresetEditorDialog.kt`: Espaçamento do error text

**Objetivo:** A mensagem de erro não deve aparecer colada ao conteúdo acima — precisa de separação clara.

**O que fazer:**

O `Column` já usa `Arrangement.spacedBy(KronoTokens.Spacing.sm)`, então o espaçamento é automático. Confirme que o `Text` de erro está dentro do mesmo `Column` raiz (não dentro de um sub-Column). Se estiver, o espaçamento já será respeitado.

Adicione `animateContentSize()` no `Column` raiz do `text` do AlertDialog para suavizar a entrada do error:

```kotlin
Column(
    modifier = Modifier.animateContentSize(),
    verticalArrangement = Arrangement.spacedBy(KronoTokens.Spacing.sm)
) {
    ...
    if (!state.canSave) {
        Text(
            text = stringResource(R.string.pomodoro_preset_empty_error),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}
```

---

### PASSO 7 — `PomodoroPresetItemList.kt`: Hierarquia de seção para os botões de ação

**Objetivo:** Os botões "Adicionar card" e "Adicionar grupo" devem ter uma separação visual clara do conteúdo da lista acima — sem uso de `SettingsDivider` entre lista e botões.

**O que fazer:**

Substitua o `SettingsDivider()` existente (entre a lista e os botões) por um `Spacer` com token, mantendo a separação visual pelos próprios containers:

```kotlin
// ANTES
...lista...
Row(botões) { ... }
SettingsDivider()
AppearanceSlider(...)

// DEPOIS
...lista...
Spacer(modifier = Modifier.height(KronoTokens.Spacing.xs))
Row(botões) { ... }
SettingsDivider()
// label de seção (Passo 4)
AppearanceSlider(...)
```

---

### PASSO 8 — Alinhamento geral: ícones no trailing do `PomodoroPhaseCard`

**Objetivo:** O `trailing` atualmente tem um `Row` com apenas o botão delete. Simplifique removendo o `Row` desnecessário quando há só um filho.

**O que fazer:**

```kotlin
// ANTES
trailing = {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (showDelete) {
            IconButton(...) { Icon(...) }
        }
    }
}

// DEPOIS
trailing = if (showDelete) ({
    IconButton(
        onClick = onDelete,
        modifier = Modifier.size(KronoTokens.Size.iconBox)
    ) {
        Icon(
            imageVector = KronoIcons.Action.Delete,
            contentDescription = stringResource(R.string.action_delete)
        )
    }
}) else null
```

---

## Resumo da Ordem de Execução

```
1. PomodoroPhaseCard.kt       → Surface wrapper nos cards
2. PomodoroGroupCard.kt       → Botão expand/collapse no header (remover offset)
3. PomodoroGroupCard.kt       → Remover SettingsDivider entre cards filhos
4. PomodoroGroupCard.kt       → padding(horizontal) nos cards filhos (era só start)
5. PomodoroGroupCard.kt       → Padding do botão interno com token
6. PomodoroGroupCard.kt       → Label de seção antes do slider do grupo
7. PomodoroPresetItemList.kt  → Label de seção antes do slider global
8. PomodoroPresetItemList.kt  → Spacer entre lista e botões de ação
9. PomodoroPresetEditorDialog.kt → animateContentSize no Column + erro espaçado
10. PomodoroPhaseCard.kt      → Simplificar trailing (remover Row desnecessário)
```

---

## Tokens Referenciados (não alterar valores)

| Token | Uso |
|-------|-----|
| `KronoTokens.Spacing.xs` | Espaçamento mínimo entre elementos |
| `KronoTokens.Spacing.sm` | Espaçamento padrão entre cards |
| `KronoTokens.Shape.card` | Shape dos cards de fase |
| `KronoTokens.Stroke.divider` | Espessura de borda |
| `KronoTokens.Size.iconBox` | Tamanho dos IconButtons |
| `KronoTokens.Settings.panelHorizontalInset` | Padding horizontal padrão de painel |

---

## Strings Novas Necessárias (`strings.xml`)

```xml
<string name="pomodoro_section_cycles">Ciclos de repetição</string>
<string name="pomodoro_section_group_cycles">Ciclos do grupo</string>
```

---

## Quality Check Final

| Critério | Status após refatoração |
|----------|------------------------|
| Cards visíveis com container | ✅ Surface + borda em cada fase |
| Hierarquia grupo > fase | ✅ Indentação + elevation diferenciada |
| Sliders com título | ✅ Label de seção adicionado |
| Botão expand sem gambiarra | ✅ Integrado no header Row |
| Tokens M3 usados (sem hardcode) | ✅ Apenas 1 hardcode removido (`4.dp` → token) |
| Touch targets ≥ 48dp | ✅ `KronoTokens.Size.iconBox` mantido |
| Sem dividers redundantes | ✅ Removidos dentro dos grupos |
| animateContentSize no grupo | ✅ Já existia — mantido |
| Erro com espaçamento correto | ✅ Dentro do Column raiz com spacedBy |
