# Plano de Unificação de Overlays (Futura)

```
Passos futuros:
□ Criar core/ui/components/KronoOverlayShell.kt
  — shell genérico: drag, snap, gestos, dimensões via KronoTokens
□ Cada <Tool>Overlay renderiza apenas conteúdo interno
  — KronoOverlayShell recebe content: @Composable () -> Unit
□ OverlayManager usa KronoOverlayShell + activeTool.overlayContent()
□ Remover lógica de janela duplicada de cada overlay
□ Aguardar aprovação → commit: refactor: unify overlay shell into KronoOverlayShell
```

---

## Arquivos Criados