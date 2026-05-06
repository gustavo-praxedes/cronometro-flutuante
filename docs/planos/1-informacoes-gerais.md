# Plano Mestre — Modularização + Settings Redesign

## Regras Gerais

- Manter aspecto visual exatamente como está
- Substituir todos os valores hardcoded por `KronoTokens`, `KronoType`, `KronoTheme`, `KronoIcons` etc...
- Aguardar verificação e aprovação do dev antes de cada commit
- Cada passo termina com checklist de verificação
- Nunca compile. Deixe para o dev. 
- Todos os erros devem ser corrigidos antes de avançar para próxima fase. Se não conseguir corrigir, peça ajuda ao dev.

---

## Estrutura Final de Pacotes (podem ser feitas mudanças na estrutura ao longo do desenvolvimento)

```
com.krono.app/
├── core/
│   ├── data/
│   │   ├── KronoDatabase.kt
│   │   ├── OverlayConfig.kt
│   │   ├── OverlayDataStore.kt
│   │   └── TimeUtils.kt
│   │   └── TimerPreferences.kt
│   ├── service/
│   │   ├── FeedbackManager.kt
│   │   ├── MainService.kt
│   │   ├── NotificationHelper.kt
│   │   ├── OverlayManager.kt
│   │   └── WakeLockManager.kt
│   ├── tool/
│   │   ├── KronoTool.kt
│   │   ├── NotificationContent.kt
│   │   ├── ToolCallbacks.kt
│   │   ├── ToolRegistry.kt
│   │   ├── ToolState.kt
│   │   └── ToolViewModel.kt
│   └── ui/
│       ├── components/
│       │   ├── KronoControlButtons.kt
│       │   └── KronoTimerDisplay.kt
│       └── theme/
│           ├── KronoIcons.kt
│           ├── KronoTheme.kt
│           ├── KronoTokens.kt
│           └── KronoType.kt
│           └── ...
├── feature/
│   ├── countdown/
│   │   ├── CountdownConfig.kt
│   │   ├── CountdownConfigDialog.kt
│   │   ├── CountdownOverlay.kt
│   │   ├── CountdownScreen.kt
│   │   ├── CountdownSettings.kt
│   │   ├── CountdownState.kt
│   │   ├── CountdownTool.kt
│   │   └── CountdownViewModel.kt
│   └── stopwatch/
│       ├── StopwatchOverlay.kt
│       ├── StopwatchScreen.kt
│       ├── StopwatchSettings.kt
│       ├── StopwatchState.kt
│       ├── StopwatchTool.kt
│       └── StopwatchViewModel.kt
├── receiver/
│   ├── BootReceiver.kt
│   └── NotificationActionReceiver.kt
├── ui/
│   ├── settings/
│   │   ├── AppearancePanel.kt
│   │   ├── BehaviorPanel.kt
│   │   ├── AboutPanel.kt
│   │   ├── ChangelogPanel.kt
│   │   ├── OverlayPanel.kt
│   │   ├── SupportPanel.kt
│   │   ├── UpdatesPanel.kt
│   │   ├── SettingsDestination.kt
│   │   ├── SettingsMenuPanel.kt
│   │   ├── SettingsPanelHost.kt
│   │   └── SettingsScreen.kt
│   ├── AppNavigation.kt
│   ├── ColorPickerDialog.kt
│   ├── HomeScreen.kt
│   ├── MainActivity.kt
│   └── TransparentProxyActivity.kt
├── util/
│   ├── ApkInstaller.kt
│   ├── KronoNavigator.kt
│   ├── PermissionUtils.kt
│   └── UpdateChecker.kt
└── KronoApp.kt
```

---

## Overlays por Enquanto

Cada ferramenta mantém overlay próprio (`StopwatchOverlay.kt`, `CountdownOverlay.kt`).
Unificação planejada em etapa futura — ver seção ao fim.

---

## Arquivos Criados

Este plano foi dividido nos seguintes arquivos:

1. `1-informacoes-gerais.md` — Regras gerais e estrutura final de pacotes
2. `fase-1-infraestrutura-core.md` — FASE 1
3. `fase-2-feature-stopwatch.md` — FASE 2
4. `fase-3-feature-countdown.md` — FASE 3
5. `fase-4-settings-redesign.md` — FASE 4
6. `fase-5-shell-do-app.md` — FASE 5
7. `fase-6-limpeza-final.md` — FASE 6
8. `checklist-nova-ferramenta.md` — Checklist para adicionar nova ferramenta
9. `plano-unificacao-overlays.md` — Plano de unificação de overlays (futura)

---

## Compatibilidade com Tablet (Implementada desde já)

```
Quando screenWidthDp >= 600:
□ Renderizar Row { SettingsMenuPanel(weight=0.35f) | SettingsPanelHost(weight=0.65f) }
□ selectedDestination inicia com Appearance
□ Back arrow some do painel esquerdo
□ Menu permanece visível
□ Item selecionado destacado via KronoTheme.colorScheme.secondaryContainer
```