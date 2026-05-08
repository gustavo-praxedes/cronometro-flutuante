---
name: app-dev-pro
description: >
  Master orchestrator for senior-level Android/KMP Kotlin development.
  Triggers on: Android app, Kotlin, Jetpack Compose, Material 3, KMP, Kotlin Multiplatform,
  ViewModel, Coroutines, Flow, Room, Hilt, Gradle, app architecture, Android bug, crash fix,
  Play Store, app release, mobile security, Android testing. Routes to focused sub-skills.
  Use proactively whenever Kotlin/Android development is involved.
---

# app-dev-pro

Orquestrador Android/KMP sênior. Roteia para sub-skills especialistas.

## Sub-Skills

| Área | Path | Triggers |
|------|------|----------|
| **Compose & UI** | `compose/compose.md` | Compose, Material 3, animação, layout, tema |
| **Architecture** | `architecture/architecture.md` | ViewModel, MVI, Hilt, Room, Coroutines, Flow, bugs |
| **Testing** | `testing/testing.md` | Testes unitários, UI test, mock, cobertura |
| **Release** | `release/release.md` | Play Store, changelog, signing, CI/CD |
| **Security** | `security/security.md` | Keystore, pinning, biometria, ProGuard |
| **KMP** | `kmp/kmp.md` | Kotlin Multiplatform, iOS interop, shared code |

## Roteamento

- Leia a tarefa → selecione sub-skill → carregue o SKILL.md.
- Múltiplas áreas → carregue todos os relevantes.
- Bug fix → carregue `architecture/` + `testing/`.
- Tarefa KMP → carregue `kmp/` + skill da área.

## Princípios Globais

- Use Kotlin idiomático: coroutines, sealed classes, extension fns, data classes.
- Prefira Compose — use XML só em legado forçado.
- Siga UDF (fluxo unidirecional de dados) em toda a UI.
- Falhe rápido: valide entradas, exponha erros cedo, nunca engula exceções.
- Nunca vaze contexto: escopize coroutines, cancele jobs, evite Activity em singletons.
- Aplique Apple HIG onde relevante: safe areas, haptics, touch targets, dark mode correto.
