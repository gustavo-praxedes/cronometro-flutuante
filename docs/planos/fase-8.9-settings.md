# Fix: Animação dos painéis em `SettingsScreen.kt`

## Diagnóstico

Dois bugs independentes fazem as animações ficarem inconsistentes.

---

## Bug 1 — `clipToBounds()` ausente no `AnimatedContent` (crítico)

**Arquivo:** `SettingsScreen.kt` — `WideScreenLayout`, linha 237  
**Efeito:** sem clipping, o painel entrante (que começa em `offsetX = -larguraPainel`) transborda para a esquerda e sobrepõe visualmente o painel esquerdo **em cima** dele, em vez de "emergir por baixo".

### Por que acontece

`slideInHorizontally { -it }` posiciona o conteúdo em `x = -width` do próprio `AnimatedContent`. O `AnimatedContent` ocupa `weight(0.60f)` dentro do `Row`, mas **não tem clipping ativo**. O Compose, por padrão, não corta filhos que transbordam os limites do pai em `Row`/`Column`. Resultado: o painel animado invade a área do painel esquerdo durante os primeiros frames.

Com `clipToBounds()`, qualquer pixel à esquerda da borda do painel direito fica invisível — o conteúdo só aparece a partir do divisor, criando a ilusão de "deslizar por baixo" do painel fixo.

### Correção

```kotlin
// ANTES (linha 237):
modifier = Modifier.fillMaxSize()

// DEPOIS:
modifier = Modifier
    .fillMaxSize()
    .clipToBounds()
```

Aplicar o mesmo em `NarrowScreenLayout` (linha 348):

```kotlin
// ANTES:
modifier = modifier
    .fillMaxSize()
    .padding(paddingValues)

// DEPOIS:
modifier = modifier
    .fillMaxSize()
    .padding(paddingValues)
    .clipToBounds()
```

---

## Bug 2 — Sticky header fora do `AnimatedContent` (widescreen)

**Arquivo:** `SettingsScreen.kt` — `WideScreenLayout`, linhas 205–229  
**Efeito:** o título do painel (ex.: "Aparência", "Notificações") está renderizado **fora** do `AnimatedContent`, protegido apenas por um `if (selectedDestination != null)`. Quando o destino muda, o título substitui instantaneamente (recomposição comum) enquanto o corpo abaixo anima. Na abertura inicial (`null → Appearance` via `LaunchedEffect`), o header aparece um frame antes/depois do corpo, criando descontinuidade visual.

### Correção

Mover o sticky header para **dentro** do `AnimatedContent`, fazendo-o parte de cada destino renderizado:

```kotlin
// ANTES — header fora, body dentro:
Column(modifier = Modifier.weight(0.60f).fillMaxHeight()) {

    // Header FORA do AnimatedContent (linha 206–229)
    if (selectedDestination != null) {
        Spacer(Modifier.height(KronoTokens.Settings.stickyHeaderTop))
        Row(...) { Text(selectedDestination.titleRes) }
    }

    AnimatedContent(
        targetState = selectedDestination,
        ...
        modifier = Modifier.fillMaxSize()
    ) { destination ->
        if (destination == null) { EmptyState() }
        else { SettingsPanelHost(...) }
    }
}

// DEPOIS — header e body juntos dentro do AnimatedContent:
Column(modifier = Modifier.weight(0.60f).fillMaxHeight()) {

    AnimatedContent(
        targetState = selectedDestination,
        transitionSpec = { settingsPanelContentTransition(forward = targetState != null) },
        label = "panel-transition",
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()           // Bug 1 também resolvido aqui
    ) { destination ->
        if (destination == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState()
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                // Header agora DENTRO — anima junto com o conteúdo
                Spacer(Modifier.height(KronoTokens.Settings.stickyHeaderTop))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(SettingsHeaderControlSize)
                        .padding(
                            start = KronoTokens.Settings.panelHorizontalInset,
                            end = KronoTokens.Settings.panelHorizontalInset
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(destination.titleRes),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = KronoTokens.Typography.dialogTitle
                        ),
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                SettingsPanelHost(
                    destination = destination,
                    config = config,
                    dataStore = dataStore,
                    scope = scope,
                    totalLifetimeMs = config.totalLifetimeMs,
                    pendingUpdateInfo = pendingUpdateInfo,
                    isServiceRunning = isServiceRunning,
                    isAnyToolRunning = isAnyToolRunning,
                    onStartFocusMode = onStartFocusMode,
                    onSupportClick = {},
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
            }
        }
    }
}
```

---

## Resumo das alterações

| Localização | Mudança | Bug corrigido |
|---|---|---|
| `WideScreenLayout` — `AnimatedContent` modifier | Adicionar `.clipToBounds()` | #1 |
| `NarrowScreenLayout` — `AnimatedContent` modifier | Adicionar `.clipToBounds()` | #1 |
| `WideScreenLayout` — sticky header | Mover para dentro do `AnimatedContent` | #2 |

---

## Como funciona após a correção

```
┌──────────────────┬──────────────────────────────┐
│  Painel esquerdo │  Painel direito (clipToBounds)│
│  (fixo, z baixo) │  ┌────────────────────────┐  │
│                  │  │← conteúdo entrando      │  │
│                  │  │   (oculto à esquerda    │  │
│                  │  │    do clip boundary)    │  │
│                  │  └────────────────────────┘  │
└──────────────────┴──────────────────────────────┘
```

Com `clipToBounds()`, o conteúdo em `x = -width` é invisível (está "atrás" do painel esquerdo). Conforme desliza para a direita, emerge a partir da borda do divisor — exatamente o efeito "passando por baixo do painel esquerdo".
