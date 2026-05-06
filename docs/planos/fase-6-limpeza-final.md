# FASE 6 — Limpeza Final

### Passo 6.1 — Remover arquivos obsoletos
- [ ] Deletar `TimerViewModel.kt`
- [ ] Deletar `TimerScreen.kt`
- [ ] Deletar `FloatingTimerUi.kt`
- [ ] Deletar `TimeState.kt`
- [ ] Deletar `AboutDialog.kt`
- [ ] Deletar `DonationDialog.kt`
- [ ] Deletar `ChangelogDialog.kt`
- [ ] Deletar `UpdateDialog.kt`
- [ ] Verificar nenhuma referência quebrada

**Aguardar aprovação → commit: `chore: remove obsolete files after modularization`**

---

### Passo 6.2 — Auditoria de tokens
- [ ] Buscar `dp`, `sp`, `Color(`, `fontSize` hardcoded em todo `feature/` e `ui/`
- [ ] Substituir por `KronoTokens`, `KronoType`, `KronoTheme`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Visual idêntico ao atual

**Aguardar aprovação → commit: `refactor: replace hardcoded values with design tokens`**

---

### Passo 6.3 — Organizar receiver/ e util/
- [ ] Mover `BootReceiver.kt` → `receiver/`
- [ ] Mover `NotificationActionReceiver.kt` → `receiver/`
- [ ] Confirmar `util/`: `ApkInstaller`, `KronoNavigator`, `PermissionUtils`, `UpdateChecker`
- [ ] Atualizar imports

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] App funcional

**Aguardar aprovação → commit: `refactor: organize receiver and util packages`**
Quando screenWidthDp >= 600:
□ Renderizar Row { SettingsMenuPanel(weight=0.35f) | SettingsPanelHost(weight=0.65f) }
□ selectedDestination inicia com Appearance
□ Back arrow some do painel esquerdo
□ Menu permanece visível
□ Item selecionado destacado via KronoTheme.colorScheme.secondaryContainer
```