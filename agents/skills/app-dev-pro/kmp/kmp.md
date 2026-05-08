---
name: app-dev-pro/kmp
description: >
  Expert Kotlin Multiplatform (KMP) e Compose Multiplatform: shared business logic,
  expect/actual, setup de projeto KMP, networking compartilhado com Ktor, storage com
  SQLDelight, ViewModel compartilhado, iOS interop com Swift, SKIE, Compose Multiplatform UI.
  Triggers on: KMP, Kotlin Multiplatform, shared code, iOS interop, expect/actual,
  Compose Multiplatform, cross-platform Kotlin.
---

# Kotlin Multiplatform

## Estrutura de Projeto

```
composeApp/
  commonMain/   → UI compartilhada (Compose Multiplatform)
  androidMain/  → entry point Android
  iosMain/      → entry point iOS (ComposeUIViewController)
shared/
  commonMain/   → domínio + dados (Ktor, SQLDelight, ViewModels)
  androidMain/  → actuals Android
  iosMain/      → actuals iOS
iosApp/         → projeto Xcode
```

## O Que Compartilhar

| Camada | Compartilha? | Tecnologia |
|--------|-------------|-----------|
| Modelos de domínio | ✅ Sempre | data classes Kotlin |
| Lógica de negócio | ✅ Sempre | UseCases em commonMain |
| API client | ✅ | Ktor |
| Banco de dados | ✅ | SQLDelight |
| ViewModel/Presenter | ✅ majoritariamente | Presenter compartilhado + wrapper de plataforma |
| UI | ⚖️ Opcional | Compose Multiplatform ou nativa |
| APIs de plataforma | ❌ | expect/actual |
| Acessibilidade | ❌ | Sempre nativa |

## expect/actual

- Declare `expect` em `commonMain` para qualquer dependência de plataforma.
- Implemente `actual` em `androidMain` e `iosMain` separadamente.
- Use para: UUID, drivers de banco, info de dispositivo, permissões, criptografia nativa.
- Nunca importe tipos Android (`android.*`) em `commonMain` — erro de compilação.

## Ktor (Rede Compartilhada)

- Configure engine por plataforma: `OkHttp` no Android, `Darwin` no iOS.
- Centralize configuração (timeout, base URL, serialização) em função comum em `commonMain`.
- Injete o `HttpClient` — nunca instancie inline.
- Use `kotlinx.serialization` — compatível com todas as plataformas KMP.

## SQLDelight (Banco Compartilhado)

- Escreva queries em `.sq` em `commonMain` — SQLDelight gera Kotlin typesafe.
- Injete `SqlDriver` via expect/actual: `AndroidSqliteDriver` / `NativeSqliteDriver`.
- Exponha dados como `Flow` — use `.asFlow().mapToList(Dispatchers.IO)`.
- Trate migrações explicitamente — nunca destructive em produção.

## ViewModel Compartilhado

- Crie Presenter em `commonMain` com `CoroutineScope` próprio.
- Envolva no Android com `AndroidX ViewModel` — cancel no `onCleared`.
- No iOS: colete `StateFlow` via `AsyncSequence` (SKIE) ou helper manual.
- Nunca use `viewModelScope` em `commonMain` — não existe no iOS.

## iOS Interop (Swift)

- Use **SKIE** (Touchlab) para APIs Kotlin mais naturais no Swift: `StateFlow → AsyncSequence`, sealed classes → Swift enums.
- Exponha API pública limpa do módulo shared — evite classes internas no framework.
- Nunca bloqueie a main thread do iOS: todo acesso a coroutines deve ser suspend ou Flow.
- Evite `runBlocking` em `iosMain` — congela a UI do iOS.

## Compose Multiplatform

- Compartilhe UI em `composeApp/commonMain` para Android + iOS + Desktop.
- Use entry points de plataforma: `setContent { }` no Android, `ComposeUIViewController { }` no iOS.
- Use Decompose ou Voyager para navegação multiplatform — `Navigation-Compose` ainda é experimental.
- Aplique temas M3 compartilhados — ajuste apenas tokens específicos de plataforma.

## Armadilhas Comuns

| ❌ Errado | ✅ Correto |
|-----------|-----------|
| Importar `android.*` em commonMain | expect/actual |
| `runBlocking` no iOS | suspend fun ou Flow |
| `Dispatchers.IO` hardcoded em commonMain | Injete dispatcher |
| `viewModelScope` em commonMain | Scope próprio no Presenter |
| Frameworks estáticos sem SKIE | SKIE para API Swift natural |
| UI de acessibilidade compartilhada | Nativa por plataforma |

## Gradle — Pontos de Atenção

- Declare targets explicitamente: `androidTarget()`, `iosX64()`, `iosArm64()`, `iosSimulatorArm64()`.
- Configure framework como `isStatic = true` para iOS.
- Use `libs.versions.toml` (version catalog) — evite strings de versão duplicadas.
- Separe `commonTest`, `androidTest` e `iosTest` — não misture dependências de plataforma em commonTest.
