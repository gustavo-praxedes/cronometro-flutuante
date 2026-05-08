---
name: app-dev-pro/architecture
description: >
  Expert Android app architecture: MVI/MVVM, Clean Architecture, Hilt DI, Room,
  Coroutines, Flow, StateFlow, SharedFlow, Repository pattern, UseCases, error handling,
  offline-first, ViewModel lifecycle, bug diagnosis, crash analysis, memory leaks, ANR.
  Triggers on: ViewModel, architecture, Room, Hilt, Flow, StateFlow, bug, crash, leak, ANR.
---

# Architecture

## Camadas

```
ui/          → Composables + ViewModels (zero lógica de negócio)
domain/      → UseCases + modelos de domínio + interfaces de repositório
data/        → Implementações: Room, Retrofit, DataStore
di/          → Módulos Hilt
```

- Direcione dependências: `ui → domain ← data`. Domain não conhece Android.
- Coloque lógica de negócio em UseCases, nunca em ViewModel ou Composable.
- Modele estado com `data class` + sealed classes para erros e efeitos.

## MVI — Estado, Ação, Efeito

- `UiState`: tudo que a tela precisa renderizar (um único `data class`).
- `Action`: intenções do usuário (sealed interface).
- `Effect`: eventos one-shot — navegação, snackbar (Channel → Flow).
- Exponha `StateFlow<UiState>` imutável — nunca MutableStateFlow público.

## ViewModel

- Lance coroutines em `viewModelScope` — cancela automaticamente no `onCleared`.
- Atualize estado com `_state.update { it.copy(...) }`.
- Envie efeitos via `Channel.BUFFERED` — nunca `SharedFlow` para navegação.
- Injete dispatchers — nunca use `Dispatchers.IO` hardcoded em prod sem injeção.

## Flow & Coroutines

- Exponha `Flow` para dados reativos; `suspend fun` para operações únicas.
- Use `collectAsStateWithLifecycle()` no Compose — não `collectAsState()`.
- Use `Dispatchers.IO` para I/O, `Dispatchers.Default` para CPU.
- Nunca use `GlobalScope` — sempre `viewModelScope` ou scope injetado.
- Prefira `runCatching` + `Result<T>` sobre try/catch solto.

## Hilt

- Anote repositórios com `@Singleton`, ViewModels com `@HiltViewModel`.
- Injete `CoroutineDispatcher` via módulo dedicado — permite substituição em testes.
- Nunca instancie dependências manualmente onde Hilt pode injetar.

## Room

- Use `Flow` em todas as queries reativas — nunca retorne `List` diretamente.
- Use `@Upsert` (Room 2.5+) em vez de `@Insert(onConflict = REPLACE)`.
- Adicione índices em colunas filtradas ou ordenadas frequentemente.
- Defina migrações explícitas — nunca dependa de `fallbackToDestructiveMigration` em prod.

## Tratamento de Erros

- Mapeie erros na camada `data` para tipos de domínio (sealed class `AppError`).
- Mapeie `AppError` para `UiError` na camada `ui` — nunca exiba stack traces.
- Nunca engula exceções silenciosamente — logue e propague.
- Sempre trate o estado de erro no `UiState` — nunca dependa de callbacks ad hoc.

## Offline-First

- Trate Room como fonte única da verdade (single source of truth).
- Atualize UI via Flow do Room — nunca retorne dado da API direto para UI.
- Faça refresh de rede em background; falhas não bloqueiam a UI (mostra cache).

## Prevenção de Memory Leaks

- Nunca armazene `Activity`, `View` ou `Context` de UI em ViewModel ou singleton.
- Cancele listeners e observadores no `onDestroy` ou via `DisposableEffect`.
- Use `LeakCanary` em debug builds — trate todos os leaks detectados.
- Passe `applicationContext` para dependências de longa vida.

## Diagnóstico de Bugs

| Sintoma | Onde olhar | Ação |
|---------|-----------|------|
| Crash | Logcat `Caused by` | Trace a cadeia completa |
| ANR | Profiler → main thread | Mova I/O para `Dispatchers.IO` |
| Memory leak | LeakCanary | Remova referência a contexto de UI |
| Recomposição infinita | Layout Inspector | Adicione `@Stable`, use `ImmutableList` |
| StateFlow não atualiza | Verifique `equals()` | Corrija data class ou use `copy()` |
| Crash de Room | Schema export | Adicione migração ou versão correta |
