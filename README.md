<div align="center">

<img src="app/src/main/ic_launcher-playstore.png" width="96" alt="Krono Logo"/>

<h1>Krono</h1>

<p><em>O cronômetro minimalista que flutua sobre sua produtividade.</em></p>

<p>
  <a href="https://github.com/gustavo-praxedes/krono/releases/latest">
    <img src="https://img.shields.io/github/v/release/gustavo-praxedes/krono?style=flat-square&logo=github&color=4f46e5&label=Vers%C3%A3o" alt="Versão"/>
  </a>
  <img src="https://img.shields.io/badge/Android-8.0%2B-3ddc84?style=flat-square&logo=android&logoColor=white" alt="Android 8.0+"/>
  <a href="LICENSE">
    <img src="https://img.shields.io/github/license/gustavo-praxedes/krono?style=flat-square&color=6b7280" alt="Licença"/>
  </a>
  <img src="https://img.shields.io/badge/offline--first-6366f1?style=flat-square" alt="Offline First"/>
  <img src="https://img.shields.io/badge/sem%20an%C3%BAncios-10b981?style=flat-square" alt="Sem Anúncios"/>
</p>

<p>
  <a href="https://github.com/gustavo-praxedes/krono/releases/latest">
    <img src="https://img.shields.io/badge/↓ Baixar APK-4f46e5?style=for-the-badge" alt="Baixar APK"/>
  </a>
  &nbsp;
  <a href="https://github.com/gustavo-praxedes/krono/issues">
    <img src="https://img.shields.io/badge/Reportar Bug-ef4444?style=for-the-badge" alt="Reportar Bug"/>
  </a>
  &nbsp;
  <a href="https://ko-fi.com/gustavo-praxedes">
    <img src="https://img.shields.io/badge/Apoiar o Projeto-f59e0b?style=for-the-badge" alt="Apoiar Projeto"/>
  </a>
</p>

</div>

---

## O que é o Krono

O **Krono** é um cronômetro Android de código aberto com widget flutuante. Ele roda sobre qualquer aplicativo, é controlado por gestos e não coleta nenhum dado do usuário.

**O Krono faz:**
- Iniciar, pausar e resetar o tempo com um toque
- Flutuar sobre qualquer app sem bloquear a interação com ele
- Persistir o estado do cronômetro após reinicialização do dispositivo
- Permitir personalização completa de cor, opacidade e tamanho

**O Krono não faz:**
- Coletar dados, exibir anúncios ou requerer internet
- Funcionar como agenda, lembrete ou temporizador regressivo
- Suportar múltiplos cronômetros simultâneos (por enquanto)

---

## Pré-requisitos

Antes de instalar, verifique se o seu dispositivo atende aos requisitos:

| Requisito | Mínimo | Recomendado |
|:---|:---|:---|
| **Versão do Android** | 8.0 (Oreo, API 26) | 11+ |
| **Permissão de sobreposição** | Obrigatória | — |
| **Conexão com internet** | Não necessária | — |
| **Espaço em disco** | ~5 MB | — |

---

## Instalação

1. Acesse **[Releases](https://github.com/gustavo-praxedes/krono/releases)** e baixe o `.apk` mais recente.
2. No Android, vá em **Configurações → Aplicativos → Instalar apps desconhecidos** e habilite para o seu gerenciador de arquivos.
3. Abra o arquivo `.apk` baixado e confirme a instalação.
4. Na primeira execução, o Krono solicitará a permissão de **"Sobrepor a outros apps"** — conceda-a para que o widget funcione.

---

## Funcionalidades

<table>
  <tr>
    <td valign="top" width="50%">
      <h4>Widget Flutuante</h4>
      <ul>
        <li><strong>WindowManager</strong> — Flutua sobre qualquer app</li>
        <li><strong>Física de Borda</strong> — Gruda suavemente nas laterais</li>
        <li><strong>Persistência</strong> — Lembra a última posição ao reabrir</li>
        <li><strong>Passivo</strong> — Não bloqueia toques no app de fundo</li>
      </ul>
    </td>
    <td valign="top" width="50%">
      <h4>Personalização</h4>
      <ul>
        <li><strong>Cores HSB</strong> — Ajuste de tom e saturação</li>
        <li><strong>Opacidade</strong> — Transparência de 0% a 100%</li>
        <li><strong>Tamanho</strong> — Escala de 0.5× a 1.5×</li>
        <li><strong>Bordas</strong> — Arredondamento customizável</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td valign="top" width="50%">
      <h4>Controle por Gestos</h4>
      <ul>
        <li><strong>Toque simples</strong> — Play / Pause</li>
        <li><strong>Toque duplo</strong> — Resetar</li>
        <li><strong>Notificação</strong> — Controles na barra de status</li>
        <li><strong>Pós-reboot</strong> — Retoma o estado automaticamente</li>
      </ul>
    </td>
    <td valign="top" width="50%">
      <h4>Confiabilidade</h4>
      <ul>
        <li><strong>Código aberto</strong> — Auditável e transparente</li>
        <li><strong>Sem rastreadores</strong> — Zero coleta de dados</li>
        <li><strong>Offline</strong> — Sem dependência de rede</li>
        <li><strong>Leve</strong> — Consumo mínimo de CPU e bateria</li>
      </ul>
    </td>
  </tr>
</table>

---

## Changelog Recente

[//]: # (CHANGELOG_LATEST_START)

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

[//]: # (CHANGELOG_LATEST_END)

---

<div align="center">
  <sub>Se o Krono foi útil para você, considere deixar uma ⭐ no repositório.</sub>
</div>
