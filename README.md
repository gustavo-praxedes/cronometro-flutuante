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

[//]: # (CHANGELOG_LATEST_END)

---

<div align="center">
  <sub>Se o Krono foi útil para você, considere deixar uma ⭐ no repositório.</sub>
</div>
