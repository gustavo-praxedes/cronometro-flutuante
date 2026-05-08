---
name: app-dev-pro/compose
description: >
  Expert Jetpack Compose + Material 3 UI development. Triggers on: Composable,
  LazyColumn, Scaffold, theming, animation, custom layout, recomposition, Modifier,
  state hoisting, side effects, Material You, dynamic color, typography, touch targets,
  haptics, dark mode, acessibilidade, safe area.
---

# Compose & Material 3

## Recomposição & Estabilidade

- Marque data classes com `@Stable` ou `@Immutable` quando contiverem `List<>`.
- Use `ImmutableList` (kotlinx-collections-immutable) em props de listas.
- Envolva cálculos caros em `remember`; use `derivedStateOf` para valores derivados.
- Passe lambdas e dados para filhos — nunca passe ViewModel como parâmetro.
- Eleve estado ao nível mais baixo que todos os consumidores compartilham (state hoisting).

```kotlin
// Padrão correto: Screen lê VM, Content é puro
@Composable fun Screen(vm: MyViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    Content(state = state, onAction = vm::onAction)
}
```

## Side Effects

- Use `LaunchedEffect(key)` para efeitos disparados por mudança de chave.
- Use `DisposableEffect` quando precisar de cleanup (`onDispose`).
- Use `rememberCoroutineScope` para ações do usuário que disparam coroutines.
- Nunca lance coroutines diretamente no corpo de um Composable.

## Material 3

- Use tokens semânticos (`MaterialTheme.colorScheme.*`) — nunca cores hardcoded.
- Respeite os papéis de cor M3: `primary`, `secondary`, `tertiary`, `surface`, `error`.
- Ative dynamic color (Android 12+) verificando `Build.VERSION.SDK_INT >= 31`.
- Defina `typography` e `shapes` no tema — não inline nos componentes.

## Toque & Acessibilidade

- Mínimo 48dp de touch target (Android) — 44dp (Apple HIG).
- Coloque CTAs primários na zona do polegar (parte inferior da tela).
- Afaste ações destrutivas da zona do polegar e exija confirmação.
- Adicione `contentDescription` em todos os elementos interativos sem texto.
- Adicione haptic feedback em ações importantes (`LocalHapticFeedback`).
- Teste com TalkBack ligado antes de considerar a UI pronta.

## Listas

- Use `LazyColumn`/`LazyRow` para listas dinâmicas — nunca `Column + verticalScroll`.
- Forneça `key = { it.id }` estável em todos os `items()`.
- Carregue imagens com Coil (`AsyncImage`) com tamanho explícito no Modifier.

## Animação

- Anime apenas `alpha`, `scale`, `translationX/Y` — propriedades de layout causam recomposição em cascata.
- Use `animateFloatAsState` para valores simples, `AnimatedVisibility` para mostrar/ocultar.
- Use `SharedTransitionLayout` para transições de elemento compartilhado (Compose 1.7+).

## Safe Area & Edge-to-Edge

- Chame `enableEdgeToEdge()` na Activity.
- Use `Scaffold` com `contentWindowInsets` — deixe o sistema calcular os insets.
- Aplique `windowInsetsPadding(WindowInsets.systemBars)` onde não houver Scaffold.

## Dark Mode

- Nunca use preto puro (`#000000`) como background — use `surface` do M3.
- Nunca use branco puro (`#FFFFFF`) como texto em dark mode — use `onSurface`.
- Detecte com `isSystemInDarkTheme()` e passe ao `MaterialTheme`.

## Anti-Padrões

| ❌ Errado | ✅ Correto |
|-----------|-----------|
| Cor hardcoded | `MaterialTheme.colorScheme.*` |
| `ScrollView` + lista dinâmica | `LazyColumn` |
| ViewModel como param em filho | Estado + lambda |
| Animar `width`/`height` | Animar `scale`/`alpha` |
| `LocalContext` fundo da árvore | Passe recursos como parâmetro |
| Lógica de negócio em Composable | Mova para ViewModel/UseCase |
