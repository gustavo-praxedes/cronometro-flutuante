# Checklist: Adicionar Nova Ferramenta

```
□ Criar feature/<tool>/
□ Criar <Tool>State.kt
□ Criar <Tool>ViewModel.kt       — estende ToolViewModel
□ Criar <Tool>Screen.kt          — usa KronoTimerDisplay, KronoControlButtons, KronoTokens
□ Criar <Tool>Overlay.kt         — usa KronoTokens
□ Criar <Tool>Settings.kt        — usa KronoTokens
□ Criar <Tool>Tool.kt            — implementa KronoTool
□ Registrar em KronoApp.onCreate()
□ Adicionar string e ícone em recursos
□ Painel de settings registrado automaticamente via ToolRegistry
```