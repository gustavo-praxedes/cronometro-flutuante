# Changelog

Histórico de mudanças do Krono.

## [3.4.1](https://github.com/gustavo-praxedes/krono/compare/v3.4.0...v3.4.1) (2026-05-28)


### 🐛 Correções

* Correção de textos cortados em preset editor ([2eb2e71](https://github.com/gustavo-praxedes/krono/commit/2eb2e71bef05e44c5356126b8a68e78e18865da6))

## [3.4.0](https://github.com/gustavo-praxedes/krono/compare/v3.3.1...v3.4.0) (2026-05-27)


### ✨ Novidades

* add AppearancePanel, BehaviorPanel, OverlayPanel ([184bbbd](https://github.com/gustavo-praxedes/krono/commit/184bbbd4f244a1124e99264268a9d4f7222c26b5))
* add CountdownTool ([a0788ed](https://github.com/gustavo-praxedes/krono/commit/a0788ed1b7026b6c079a08a409af49311f7efefa))
* add SettingsDestination sealed class ([4f7ca81](https://github.com/gustavo-praxedes/krono/commit/4f7ca8189236ec7a7daf94df80c77f362bd32d3c))
* add SettingsMenuPanel ([a2a60ec](https://github.com/gustavo-praxedes/krono/commit/a2a60eca253f25c1fa501d881f119fe3028e240f))
* add SettingsPanelHost dispatcher ([a28a795](https://github.com/gustavo-praxedes/krono/commit/a28a7958dd662c5c774ea3f1a5e969cd7977e7a4))
* add shared KronoTimerDisplay and KronoControlButtons ([0f50b4c](https://github.com/gustavo-praxedes/krono/commit/0f50b4c5eea8341df8c6df4068769d23e35de395))
* add StopwatchTool and StopwatchSettings ([43b23d6](https://github.com/gustavo-praxedes/krono/commit/43b23d697443b78aa4f15a2dad34ff1e92c9e265))
* integrate About, Support, Changelog, Updates as settings panels ([c82b018](https://github.com/gustavo-praxedes/krono/commit/c82b018ebd0d0e3f0959a1141bd89e1e1551d467))
* Pomodoro adicionado ([3dd0e37](https://github.com/gustavo-praxedes/krono/commit/3dd0e37bda3e5dcdd094840344f6b7e41f873a48))
* redesign de dialogo de criação de presets. ([aa595a2](https://github.com/gustavo-praxedes/krono/commit/aa595a2e19e5493ff62a1cc5e28ad0e8225fe6b8))
* settings two-panel navigation ([de582a8](https://github.com/gustavo-praxedes/krono/commit/de582a82cc5d06a888cef25e87bd22a48f073db6))


### 🐛 Correções

* Bug introduzido na refatoração corrigido ([d50752f](https://github.com/gustavo-praxedes/krono/commit/d50752f0cef43d1da8eb30c81bfec5ef36783f0a))


### ♻️ Refatorações

* decouple MainService from StopwatchViewModel via ToolRegistry ([c16ce1e](https://github.com/gustavo-praxedes/krono/commit/c16ce1ea7def0f5c3ceb69bb840b3cdb5f9e1dc8))
* migrate TimerScreen and FloatingTimerUi to stopwatch feature ([28a4f68](https://github.com/gustavo-praxedes/krono/commit/28a4f688831447934fc1daf5d0369024484677f2))
* migrate TimerViewModel to StopwatchViewModel ([7b2bfb6](https://github.com/gustavo-praxedes/krono/commit/7b2bfb6da479b718e0e691ac74b965343088cb24))
* migrate TimerViewModel to StopwatchViewModel ([7d5093d](https://github.com/gustavo-praxedes/krono/commit/7d5093db4222d75e8cfd6d826fb07e9ee668c75f))
* move activities and app shell to app ([8a2fe0f](https://github.com/gustavo-praxedes/krono/commit/8a2fe0f4fc978ac0913df43078646d3a2ad5d49e))
* move generic dialogs to core/ui/dialogs ([593f6a6](https://github.com/gustavo-praxedes/krono/commit/593f6a6e8a98e6f4f560bee83ea078ceddc743f5))
* move receiver to core/receiver ([8777396](https://github.com/gustavo-praxedes/krono/commit/8777396e5c0477dad379e3d73df60c37b52110b7))
* move settings to core/ui/settings ([a5ecf95](https://github.com/gustavo-praxedes/krono/commit/a5ecf95a0fb6d01e3138051d00e7933f97c61dea))
* move shared data files to core/data ([20e4f31](https://github.com/gustavo-praxedes/krono/commit/20e4f31461c656664cb308af641e0751df071bc7))
* move util to core/util ([221dfc6](https://github.com/gustavo-praxedes/krono/commit/221dfc64cde22e19d5bcfe75746b1efef6390633))


### 📝 Documentação

* planos fase 8.7 e 8.8 adicionados ([f531852](https://github.com/gustavo-praxedes/krono/commit/f53185201919033d1406938f0b1ef257c6432cc6))


### 🔧 Manutenção

* add settings strings and icons ([309cbbc](https://github.com/gustavo-praxedes/krono/commit/309cbbc4fd0dcb27ff94382243a21b39921e43df))
* ajustes em várias áreas ([05137ea](https://github.com/gustavo-praxedes/krono/commit/05137ea709b352d7bb67ed34debc4fb7a9fddc6f))
* fim da modularização ([8feb469](https://github.com/gustavo-praxedes/krono/commit/8feb4693cbb48a0c109caf0696e15ea64525ef7a))
* Mais refatoração. ([45c5524](https://github.com/gustavo-praxedes/krono/commit/45c552483f147ebebc166c7df533d4cbf8aab8f3))
* refatoração de settings ([5008004](https://github.com/gustavo-praxedes/krono/commit/5008004c1094c0f86a8f166abc3ef0e40f12013b))
* Unificação de overlays ([72f8273](https://github.com/gustavo-praxedes/krono/commit/72f82731fd3be1ae9e4708f3e4c9188935e56350))


### 💄 Estilo

* ajuste de alinhamento em settings ([b4f5876](https://github.com/gustavo-praxedes/krono/commit/b4f5876882256f8843bf1ebd8268001d662c8453))
* refine navigation icons and touch feedback ([bb2c2dc](https://github.com/gustavo-praxedes/krono/commit/bb2c2dc5caed593415f23ca02dddbc31662b69d7))

## [3.3.1](https://github.com/gustavo-praxedes/krono/compare/v3.3.0...v3.3.1) (2026-05-02)


### 🔧 Manutenção

* Fim da branch ([3e64cee](https://github.com/gustavo-praxedes/krono/commit/3e64cee833607869c74a153d61a35e45f3ef14ef))

## [3.3.0](https://github.com/gustavo-praxedes/krono/compare/v3.2.3...v3.3.0) (2026-05-02)


### ✨ Novidades

* Seleção de fontes para o overlay adicionada ([aece1e1](https://github.com/gustavo-praxedes/krono/commit/aece1e175c678080da3099c7e817c907c1913508))


### 🐛 Correções

* Correção de bugs no cronômetro regressivo ([fc04202](https://github.com/gustavo-praxedes/krono/commit/fc042020cb6f42ef61622a3bc025e57164614641))


### ♻️ Refatorações

* **ui:** adicionar largura adaptativa para dialogos no tema ([b96b5ba](https://github.com/gustavo-praxedes/krono/commit/b96b5ba47de56ebd71a1f7b76c0347d4a150c677))
* **ui:** aplicar largura adaptativa e rolagem no DonationDialog ([a4dcb55](https://github.com/gustavo-praxedes/krono/commit/a4dcb55ed1d73e2baf602d9ecf7acbea282f50f5))
* **ui:** aplicar largura adaptativa e rolagem no PermissionsDialog ([86f0ff5](https://github.com/gustavo-praxedes/krono/commit/86f0ff5db9aefc443dc1d85539f3ebba1e954c44))
* **ui:** aplicar largura adaptativa e rolagem no UpdateDialog ([5727854](https://github.com/gustavo-praxedes/krono/commit/5727854daeb2ffd9b6a4405f8487a4fdf3a75e16))
* **ui:** aplicar largura adaptativa no BugReportDialog ([d20629c](https://github.com/gustavo-praxedes/krono/commit/d20629cbbe71663c66e2bbf1834bb7ac085618c5))
* **ui:** aplicar largura adaptativa no ChangelogDialog ([5b7c6f3](https://github.com/gustavo-praxedes/krono/commit/5b7c6f3505d4fcef6b279ffb4c42ef618e9f0298))
* **ui:** aplicar largura adaptativa no ColorPickerDialog ([18db19e](https://github.com/gustavo-praxedes/krono/commit/18db19e822d8e3fd3c2fbea64eb43f7fec40a8a6))
* **ui:** tornar AboutDialog adaptativo e com rolagem ([9c0170b](https://github.com/gustavo-praxedes/krono/commit/9c0170b00e5e7113e9212d6a7c1c00e743e07442))


### 📝 Documentação

* atualizar changelog com as mudanças de largura adaptativa ([68016cf](https://github.com/gustavo-praxedes/krono/commit/68016cf5fd430d4163596fd140724ab39723e3b8))
* Documentação atualizada. ([175fb90](https://github.com/gustavo-praxedes/krono/commit/175fb90e35de9880470746ad57b60dfc5f44f288))


### 🔧 Manutenção

* Pastas de IA adicionadas ao gitignore ([a98f019](https://github.com/gustavo-praxedes/krono/commit/a98f0190da1bbeedf27a9814943ab08b45e54098))
* Refatoração de MainService para separar funções ([f419242](https://github.com/gustavo-praxedes/krono/commit/f4192427400649d79f503613342043d43aa3466d))


### 💄 Estilo

* Ajustes finais no countdown. ([95e5163](https://github.com/gustavo-praxedes/krono/commit/95e5163040487b37eb16e9e3f45027e7930b8f9d))
* Padronização de janelas ([765cfe3](https://github.com/gustavo-praxedes/krono/commit/765cfe3d085428c43ce64e0bd5f25f634debcfc0))
* Redefinição da UI. ([35b67e0](https://github.com/gustavo-praxedes/krono/commit/35b67e0c54fb4e3cf6cb197d03f856a4d3b3befd))
* Timer finalizado ([bf516ef](https://github.com/gustavo-praxedes/krono/commit/bf516efd53022d7f8c79d0c22f73a4f7e8e304d3))

## [3.2.3](https://github.com/gustavo-praxedes/krono/compare/v3.2.2...v3.2.3) (2026-04-24)

## [3.2.2](https://github.com/gustavo-praxedes/krono/compare/v3.2.1...v3.2.2) (2026-04-24)


### 🔧 Manutenção

* Adicionado o google firebase ([64937ed](https://github.com/gustavo-praxedes/krono/commit/64937ed3f5f63cbd9a0753baaee53aa0454f7ace))

## [3.2.1](https://github.com/gustavo-praxedes/krono/compare/v3.2.0...v3.2.1) (2026-04-24)


### 🐛 Correções

* Bug do voltar na SettingsScree ([de95e21](https://github.com/gustavo-praxedes/krono/commit/de95e21531b8ad36fb18c1a0ff98554e6408197f))


### 📝 Documentação

* Atualização dos documentos de contexto ([1288ef5](https://github.com/gustavo-praxedes/krono/commit/1288ef5230c1b3ef3ed0c44ee24b836b4dccceee))


### 🔧 Manutenção

* Adicionado campo de data interno no formulário de bug ([f521a90](https://github.com/gustavo-praxedes/krono/commit/f521a90e2b2446493ae3ac1d0832a1704d871d3c))
* Refatoração da chamada da janela de doação ([f452cca](https://github.com/gustavo-praxedes/krono/commit/f452cca9fb40fe4e427f662df7bc107281af797c))


### 💄 Estilo

* Animação overlay ([93f77fb](https://github.com/gustavo-praxedes/krono/commit/93f77fb71405b2bb2f696363ed254f236585923c))
* Melhoria na interface do overlay ([dfcd8cb](https://github.com/gustavo-praxedes/krono/commit/dfcd8cb06400f9de10af7e909b769ce5f2f06721))
* Mudança no ícone ([c4dfe81](https://github.com/gustavo-praxedes/krono/commit/c4dfe81ad7dddc0690774e4111d5554aa1bd35b5))

## [3.2.0](https://github.com/gustavo-praxedes/krono/compare/v3.1.2...v3.2.0) (2026-04-23)


### ✨ Novidades

* Adicionada a janela de BugReport. ([e610160](https://github.com/gustavo-praxedes/krono/commit/e6101603b758576c7381fa27d329343cd3c4863a))


### 🔧 Manutenção

* Ajuste do .versionc. ([9ba1cf8](https://github.com/gustavo-praxedes/krono/commit/9ba1cf8fea200a7c5e9bbf5070031ee1dddcf2ac))

## [3.1.2](https://github.com/gustavo-praxedes/krono/compare/v3.1.1...v3.1.2) (2026-04-23)


### 🐛 Correções

* Ajuste no changelog.md. ([006ea04](https://github.com/gustavo-praxedes/krono/commit/006ea04c1fdbfa133d9a5b6586b066f296a3fca8))
* Correção de loop no script. ([2598175](https://github.com/gustavo-praxedes/krono/commit/2598175e24cd45c3cf196df2fb3cbd5befa3d217))


### 💄 Estilo

* Pequeno ajuste. ([7902a46](https://github.com/gustavo-praxedes/krono/commit/7902a461b15fe2b01eb188ad68193d58348f3bd2))

## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Correção de loop no script.
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Correção de loop no script.
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Correção de loop no script.
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Correção de loop no script.
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Correção de loop no script.
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Correção de loop no script.
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Correção de loop no script.
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Correção de loop no script.
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Correção de loop no script.
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Correção de loop no script.
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Correção de loop no script.
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Correção de loop no script.
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Correção de loop no script.
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


## [3.1.1] (2026-04-23)

### 🐛 Correções
- fix: Ajuste no changelog.md.

---


Histórico de mudanças do Krono.

## [3.1.1](https://github.com/gustavo-praxedes/krono/compare/v3.1.0...v3.1.1) (2026-04-23)


### 🐛 Correções

* Ajuste simples. ([a1fd850](https://github.com/gustavo-praxedes/krono/commit/a1fd85091b881bde25457a6f43398a0d52330017))
* Corrigido o bug na atualização automática. ([09ae4f5](https://github.com/gustavo-praxedes/krono/commit/09ae4f5bfa908c5b08c9ac25325e453e59fb5a6a))
* Resolvido o bug na reabertura rápida do overlay. ([681acce](https://github.com/gustavo-praxedes/krono/commit/681acceee3e7317fb7a6a234895fbf9656430f52))

## [3.1.0] (2026-04-23)

### ✨ Novidades
- feat: Persistência do tempo adicionada. O tempo acumulado agora sobrevive ao fechamento do app.

### 🐛 Correções
- fix: Bug do overlay não encostar na borda inferior resolvido.
- fix: Correção do overlay fantasma no modo foco.
- fix: Correção da marcação instantânea das permissões no diálogo.

### ⚡ Performance
- perf: Overlay agora segue os temas escolhidos pelo usuário instantaneamente.
- perf: Ajuste estético e otimização da janela de permissão.
- perf: Padronização visual da janela de atualização (UpdateDialog).
- perf: Unificação e simplificação dos pedidos de permissão.

### 🔧 Manutenção
- chore: Refatoração do FloatingTimerUi usando Design Tokens.
- chore: Sincronização e padronização dos sistemas de Changelog (Local e Remoto).

---

## [3.0.0] (2026-04-22)

### ✨ Novidades
- feat: Persistência do tempo adicionada. O tempo acumulado agora sobrevive ao fechamento do app.

### 🐛 Correções
- fix: Correção do overlay fantasma que aparecia no Modo Foco.
- fix: Ajuste no arraste para permitir que o overlay encoste na borda inferior da tela.
- fix: Correção do check de permissão que não marcava instantaneamente.

### ⚡ Performance
- perf: Otimização do tick do cronômetro para 250ms visando economia de bateria.
- perf: Redução de recomposições desnecessárias na UI do overlay.
- perf: Unificação das chamadas de permissão no `AppNavigation`.

### 🔧 Manutenção
- chore: Refatoração do `FloatingTimerUi` para uso de tokens de design.
- chore: Implementação do sistema centralizado de tokens em `KronoTokens`.
- chore: Configuração do `keystore.properties` e ajustes no CI/CD (GitHub Actions).

---

## [2.5.12] (2026-04-22)

### 🐛 Correções
- fix: Bug do overlay no modo foco não sendo recriado corrigido.
- fix: Ajuste de ícones no `ChangelogDialog` e `AboutDialog`.

### ⚡ Performance
- perf: Padronização de caixas de diálogo e botões.
- perf: Refinamento da tipografia no campo de limite de tempo.

### 🔧 Manutenção
- chore: Limpeza de código e remoção de recursos não utilizados.
- chore: Aprimoramento da construção do APK no GitHub.

---
*Para ver o histórico anterior completo, consulte as Releases no GitHub.*
