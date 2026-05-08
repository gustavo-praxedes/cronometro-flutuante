---
name: app-dev-pro/testing
description: >
  Expert Android testing: unit tests com JUnit5/MockK/Turbine, UI tests com Compose
  testing, Hilt test setup, ViewModel testing, Flow testing, coroutine testing,
  test doubles, pirâmide de testes, screenshot testing, accessibility testing.
  Triggers on: test, spec, unit test, mock, UI test, instrumented, assert, cobertura.
---

# Testing

## Pirâmide

```
       [E2E / UI]       → Espresso, Maestro (poucos, lentos, alta confiança)
    [Integração]        → Room in-memory, Hilt test, fake API (alguns)
  [Unitário / VM]       → JUnit5, MockK, Turbine (muitos, rápidos)
```

- Siga 70% unitário → 20% integração → 10% UI.
- Nunca teste implementação — teste comportamento observável.

## Dependências Essenciais

```
testImplementation: junit-jupiter, mockk, turbine, kotlinx-coroutines-test
androidTestImplementation: androidx.compose.ui:ui-test-junit4, hilt-android-testing
debugImplementation: ui-test-manifest, leakcanary-android
```

## Unitários — ViewModel & UseCase

- Crie `MainDispatcherRule` com `UnconfinedTestDispatcher` — aplique em todo teste de VM.
- Use `runTest { }` para código suspense.
- Use Turbine: `flow.test { awaitItem(); cancelAndConsumeRemainingEvents() }`.
- Prefira fakes a mocks para repositórios — mocks para colaboradores simples.
- Nomeie: `` `dado [contexto] quando [ação] então [resultado]` ``.

```kotlin
// Fake: implementação real com dados controlados
class FakeItemRepo : ItemRepository {
    private val _items = MutableStateFlow<List<Item>>(emptyList())
    fun emit(items: List<Item>) { _items.value = items }
    override fun observeItems() = _items.asStateFlow()
}

// Mock: quando comportamento importa mais que dados
val repo: ItemRepository = mockk {
    coEvery { refresh() } returns Result.success(Unit)
}
```

## Compose UI Tests

- Teste Composables isolados — passe estado fake, não ViewModel real.
- Use `createComposeRule()` para componentes, `createAndroidComposeRule<Activity>()` para telas completas.
- Prefira finders semânticos: `onNodeWithText`, `onNodeWithContentDescription`, `onNodeWithTag`.
- Nunca teste coordenadas de pixel — teste semântica.
- Verifique touch targets: nenhum nó clicável deve ter bounds menor que 48dp.

## Hilt em Testes

- Use `@HiltAndroidTest` + `HiltAndroidRule` em testes instrumentados.
- Substitua módulos reais por fakes via `@UninstallModules` + `@Module @TestInstallIn`.
- Nunca use instâncias reais de API ou banco de dados em testes unitários.

## Room — Testes de Integração

- Use `Room.inMemoryDatabaseBuilder` — nunca banco real em testes.
- Ative `allowMainThreadQueries()` apenas em testes.
- Feche o banco no `@After` — nunca deixe estado vazar entre testes.

## Screenshot Testing (Paparazzi)

- Use Paparazzi para snapshots sem emulador.
- Capture dark mode + light mode separadamente.
- Capture variações de acessibilidade (fonte grande).
- Trate falhas de snapshot como bugs — nunca atualize sem revisar o diff.

## Acessibilidade

- Verifique `contentDescription` em todos os nós interativos.
- Verifique `hasClickAction()` nos botões.
- Execute `AccessibilityChecks.enable()` em testes instrumentados.

## Scripts

- `scripts/mobile_audit.py` — auditoria estática de UX/performance mobile (RN/Flutter, adaptável).
- `scripts/verify_ui.sh` — dump de hierarquia UI + screenshot via ADB.

## Anti-Padrões

| ❌ Errado | ✅ Correto |
|-----------|-----------|
| Testar implementação interna | Testar comportamento público |
| Mock para repositório inteiro | Fake com estado controlado |
| `Thread.sleep()` em coroutine test | `advanceUntilIdle()` / Turbine |
| Banco real em unitário | Room in-memory |
| Ignorar flakiness | Isole causa raiz, corrija |
