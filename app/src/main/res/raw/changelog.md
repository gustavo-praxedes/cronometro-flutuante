# ✨ Novidades
- feat: settings two-panel navigation
- feat: add SettingsPanelHost dispatcher
- feat: add SettingsMenuPanel
- feat: integrate About, Support, Changelog, Updates as settings panels
- feat: add AppearancePanel, BehaviorPanel, OverlayPanel
- feat: add SettingsDestination sealed class
- feat: add CountdownTool
- feat: add StopwatchTool and StopwatchSettings
- feat: add shared KronoTimerDisplay and KronoControlButtons

# 🐛 Correções
- fix: Bug introduzido na refatoração corrigido

# 🔧 Manutenção
- refactor: move generic dialogs to core/ui/dialogs
- refactor: move util to core/util
- refactor: move receiver to core/receiver
- Limpeza de arquivos obsoletos
- chore: add settings strings and icons
- refactor: decouple MainService from StopwatchViewModel via ToolRegistry
- refactor: migrate TimerScreen and FloatingTimerUi to stopwatch feature
- refactor: migrate TimerViewModel to StopwatchViewModel
- refactor: migrate TimerViewModel to StopwatchViewModel
- refactor: move shared data files to core/data