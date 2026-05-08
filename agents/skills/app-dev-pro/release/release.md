---
name: app-dev-pro/release
description: >
  Expert Android release engineering: Play Store, signing, keystore, ProGuard/R8,
  release notes do git, changelog, CI/CD com GitHub Actions, Fastlane, versionamento,
  AAB, otimização de tamanho, Play Console, staged rollout, tracks de beta.
  Triggers on: release, deploy, Play Store, signing, changelog, CI/CD, build, versão.
---

# Release Engineering

## Versionamento

- Use `versionCode` auto-incrementado via `git rev-list --count HEAD`.
- Use `versionName` semântico: `MAJOR.MINOR.PATCH`.
- MAJOR → redesign ou breaking change; MINOR → nova feature; PATCH → bug fix.
- Nunca incremente `versionCode` manualmente — automatize no CI.

## Signing

- Armazene keystore e senhas **fora** do repositório — use variáveis de ambiente.
- Adicione `*.jks`, `*.keystore` ao `.gitignore` imediatamente.
- No CI: encode keystore em base64 como secret, decode no build step.
- Mantenha backup criptografado do keystore em local separado do código.
- Rotacione chaves comprometidas imediatamente via Play Console.

## ProGuard / R8

- Ative `isMinifyEnabled = true` e `isShrinkResources = true` em release.
- Ative full mode R8: `android.enableR8.fullMode=true` em `gradle.properties`.
- Adicione regras explícitas para: entidades Room, interfaces Retrofit, classes serializadas.
- Nunca suprima todos os warnings globalmente — corrija por biblioteca.
- Teste build obfuscado antes de subir para Play Store.

## AAB & Tamanho

- Sempre publique `.aab`, nunca `.apk` no Play Store.
- Ative splits: `language`, `density`, `abi` no bundle config.
- Use WebP para imagens raster, vetores para ícones.
- Remova recursos não utilizados: `lint --check UnusedResources`.
- Gere Baseline Profile para melhorar startup time.

## Changelog do Git

- Use `scripts/collect_release_changes.sh` para coletar commits desde a última tag.
- Filtre internos: refactor, chore, build, ci, bump deps — nenhum vai para o usuário.
- Inclua: feat, fix, perf, a11y — apenas o que o usuário percebe.
- Escreva em benefício do usuário, não em detalhe técnico.
- Máximo 500 caracteres por locale no Play Store.

```
# Mapeamento commit → Play Store
feat(search): add voice input   →  • Pesquise com a voz diretamente na busca
fix(auth): token refresh crash  →  • Corrigida falha ao renovar sessão
perf(feed): lazy load images    →  • Feed mais rápido ao rolar
```

## CI/CD (GitHub Actions)

- Dispare release em push de tag `v*`.
- Ordem: checkout → Java 17 → Gradle cache → decode keystore → build AAB → upload Play.
- Use `r0adkll/upload-google-play@v1` para upload automatizado.
- Separe tracks: `internal` → `alpha` → `beta` → `production`.
- Nunca suba direto para produção sem passar por internal track.

## Rollout

- Staged rollout: 10% → 50% → 100%.
- Monitore crash-free rate — pare rollout abaixo de 99.5%.
- Responda reviews negativos em 24h após release.
- Mantenha versão anterior disponível para rollback via Play Console.

## Checklist Pre-Release

```
□ versionCode incrementado
□ versionName atualizado
□ Build obfuscado testado (sem crashes)
□ Baseline Profile regenerado
□ Release notes escritas (todos os locales)
□ Screenshots atualizados (se UI mudou)
□ Política de privacidade atualizada (se coleta mudou)
□ Testado no internal track antes de promover
```

## Scripts

- `scripts/collect_release_changes.sh` — coleta commits desde a última tag git.
