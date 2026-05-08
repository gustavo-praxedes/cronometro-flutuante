---
name: app-dev-pro/security
description: >
  Expert Android mobile security: EncryptedSharedPreferences, Android Keystore,
  SSL/TLS certificate pinning, network security config, ProGuard obfuscation,
  root detection, WebView security, token storage, deep link validation,
  biometric auth, OWASP MASVS. Triggers on: security, token, keystore, pinning,
  encrypted, secure storage, auth, biometric, obfuscation, WebView, root.
---

# Security

## Armazenamento Seguro

- Use `EncryptedSharedPreferences` com `MasterKey.AES256_GCM` para tokens e preferências sensíveis.
- Use `Android Keystore` para chaves criptográficas — nunca armazene chaves em memória persistente.
- Nunca use `SharedPreferences` padrão para dados sensíveis.
- Exclua dados sensíveis do backup: configure `android:allowBackup="false"` ou use backup rules.
- Limpe credenciais do armazenamento no logout — nunca deixe tokens órfãos.

## Autenticação Biométrica

- Use `BiometricPrompt` com `BIOMETRIC_STRONG` — nunca implemente biometria custom.
- Sempre forneça fallback (PIN/senha) via `DEVICE_CREDENTIAL`.
- Verifique disponibilidade com `BiometricManager.canAuthenticate()` antes de exibir opção.
- Exija biometria para operações sensíveis: pagamento, exclusão de conta, visualização de dados privados.

## Certificate Pinning

- Adicione pin do certificado leaf + pin de backup (rotação sem downtime).
- Use `CertificatePinner` do OkHttp **ou** `network_security_config.xml` — nunca os dois.
- Defina data de expiração no pin-set — rotacione antes do vencimento.
- Planeje rotação: deploy do app com novo pin **antes** de trocar o certificado no servidor.

## Configuração de Rede

- Bloqueie cleartext em produção: `cleartextTrafficPermitted="false"`.
- Permita certificados de usuário apenas em debug builds — nunca em release.
- Nunca faça requisições HTTP em produção — use HTTPS obrigatório.

## WebView

- Desative JavaScript por padrão — habilite apenas se indispensável.
- Desative `allowFileAccess` e `allowContentAccess`.
- Implemente allowlist de URLs no `WebViewClient.shouldOverrideUrlLoading`.
- Limpe cache e histórico no `onDestroy`.
- Nunca exponha `JavascriptInterface` sem validação rigorosa de input.

## Deep Links

- Valide scheme e host antes de processar qualquer parâmetro.
- Sanitize todos os parâmetros de query com regex restritivo.
- Nunca execute ação sensível (login, pagamento) diretamente por deep link sem confirmação.

## Detecção de Root

- Verifique presença de binários su, props de debug e sistema RW.
- Degrade graciosamente — nunca trave o app; restrinja features sensíveis.
- Combine múltiplas verificações — checagem única é fácil de bypassar.

## Logs & Dados

- Nunca logue tokens, senhas, emails ou qualquer PII.
- Guarde logs sensíveis apenas atrás de `if (BuildConfig.DEBUG)`.
- Configure Timber com árvore de produção que exclui PII — nunca `DebugTree` em release.
- Remova chamadas `Log.*` do código de produção via ProGuard/R8.

## Obfuscação

- Ative R8 full mode em release — reduz superfície de ataque por engenharia reversa.
- Mantenha regras ProGuard mínimas e explícitas — nunca `-keep class * { *; }`.
- Nunca comite `mapping.txt` de build de produção no repositório.

## Checklist OWASP MASVS

```
Armazenamento:
□ Tokens em EncryptedSharedPreferences / Keystore
□ Nenhum dado sensível em logs
□ Backup desabilitado ou configurado com exclusões
□ Banco criptografado se contém dados sensíveis

Rede:
□ TLS obrigatório (network security config)
□ Certificate pinning no API de produção
□ Sem cleartext traffic

Autenticação:
□ Biometria em operações sensíveis
□ Token com expiração + refresh implementado
□ Logout limpa todas as credenciais armazenadas

Código:
□ R8 minification + obfuscation ativo
□ Sem chaves ou URLs hardcoded
□ BuildConfig para valores por ambiente
```
