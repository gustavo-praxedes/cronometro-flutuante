# Plano — Reorganização de Pacotes

## Regras
- App totalmente operacional após cada commit
- Ajustar imports em todos os arquivos afetados antes de cada commit
- Aguardar verificação e aprovação do dev antes de cada commit
- Nunca compilar — dev compila e verifica
- Usar imperativo

---

## Estrutura Final

```
com.krono.app/
├── core/
│   ├── data/
│   ├── receiver/          ← de receiver/
│   ├── service/
│   ├── tool/
│   ├── util/              ← de util/
│   └── ui/
│       ├── components/
│       ├── dialogs/       ← de ui/ (dialogs genéricos)
│       ├── settings/      ← de ui/settings/
│       └── theme/
├── feature/
│   ├── countdown/
│   ├── pomodoro/
│   └── stopwatch/
└── app/                   ← atividades e raiz
    ├── AppNavigation.kt
    ├── FocusActivity.kt
    ├── KronoApp.kt
    ├── MainActivity.kt
    └── TransparentProxyActivity.kt
```

---

## PASSO 1 — Mover receiver/ → core/receiver/

**Mover:**
- [ ] Mover `receiver/BootReceiver.kt` → `core/receiver/`
- [ ] Mover `receiver/NotificationActionReceiver.kt` → `core/receiver/`
- [ ] Atualizar package para `com.krono.app.core.receiver` em ambos
- [ ] Atualizar imports em todos os arquivos afetados
- [ ] Atualizar referências no `AndroidManifest.xml`

**Verificar antes do commit:**
- [ ] Nenhuma referência quebrada
- [ ] Boot receiver funciona após reiniciar dispositivo
- [ ] Ações de notificação funcionam

**Aguardar aprovação → commit: `refactor: move receiver to core/receiver`**

---

## PASSO 2 — Mover util/ → core/util/

**Mover:**
- [ ] Mover `util/ApkInstaller.kt` → `core/util/`
- [ ] Mover `util/KronoNavigator.kt` → `core/util/`
- [ ] Mover `util/PermissionUtils.kt` → `core/util/`
- [ ] Mover `util/UpdateChecker.kt` → `core/util/`
- [ ] Atualizar package para `com.krono.app.core.util` em todos
- [ ] Atualizar imports em todos os arquivos afetados

**Verificar antes do commit:**
- [ ] Nenhuma referência quebrada
- [ ] Verificação de updates funciona
- [ ] Instalação de APK funciona

**Aguardar aprovação → commit: `refactor: move util to core/util`**

---

## PASSO 3 — Mover dialogs genéricos → core/ui/dialogs/

**Mover:**
- [ ] Mover `ui/BugReportDialog.kt` → `core/ui/dialogs/`
- [ ] Mover `ui/ColorPickerDialog.kt` → `core/ui/dialogs/`
- [ ] Mover `ui/PermissionsDialog.kt` → `core/ui/dialogs/`
- [ ] Atualizar package para `com.krono.app.core.ui.dialogs` em todos
- [ ] Atualizar imports em todos os arquivos afetados

**Verificar antes do commit:**
- [ ] Nenhuma referência quebrada
- [ ] Color picker funciona em AppearancePanel e CountdownSettings
- [ ] Dialog de permissões abre no primeiro launch

**Aguardar aprovação → commit: `refactor: move generic dialogs to core/ui/dialogs`**

---

## PASSO 4 — Mover ui/settings/ → core/ui/settings/

**Mover:**
- [ ] Mover todos os arquivos de `ui/settings/` → `core/ui/settings/`
  - `AboutPanel.kt`
  - `AppearancePanel.kt`
  - `BehaviorPanel.kt`
  - `ChangelogPanel.kt`
  - `OverlayPanel.kt`
  - `SettingsDestination.kt`
  - `SettingsMenuPanel.kt`
  - `SettingsPanelHost.kt`
  - `SettingsScreen.kt`
  - `SupportPanel.kt`
  - `UpdatesPanel.kt`
- [ ] Atualizar package para `com.krono.app.core.ui.settings` em todos
- [ ] Atualizar imports em todos os arquivos afetados

**Verificar antes do commit:**
- [ ] Nenhuma referência quebrada
- [ ] Settings abre e navega corretamente
- [ ] Todos os painéis acessíveis

**Aguardar aprovação → commit: `refactor: move settings to core/ui/settings`**

---

## PASSO 5 — Mover atividades e raiz → app/

**Mover:**
- [ ] Mover `ui/AppNavigation.kt` → `app/`
- [ ] Mover `ui/FocusActivity.kt` → `app/`
- [ ] Mover `ui/MainActivity.kt` → `app/`
- [ ] Mover `ui/TransparentProxyActivity.kt` → `app/`
- [ ] Mover `KronoApp.kt` (raiz) → `app/` se ainda não estiver
- [ ] Atualizar package para `com.krono.app` ou `com.krono.app.app` — manter package original das atividades para não quebrar `AndroidManifest.xml`
- [ ] Atualizar imports em todos os arquivos afetados
- [ ] Verificar referências no `AndroidManifest.xml`

**Verificar antes do commit:**
- [ ] App inicia normalmente
- [ ] Navegação entre telas funciona
- [ ] FocusActivity abre no modo foco

**Aguardar aprovação → commit: `refactor: move activities and app shell to app/`**

---

## PASSO 6 — Deletar pastas vazias

**Verificar e deletar:**
- [ ] Confirmar que `receiver/` está vazia → deletar
- [ ] Confirmar que `util/` está vazia → deletar
- [ ] Confirmar que `ui/settings/` está vazia → deletar
- [ ] Confirmar que `ui/` está vazia → deletar

**Aguardar aprovação → commit: `chore: remove empty packages after reorganization`**
