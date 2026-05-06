# FASE 5 — Shell do App

### Passo 5.1 — Reorganizar ui/
- [ ] Mover `MainActivity.kt` → `ui/`
- [ ] Mover `TransparentProxyActivity.kt` → `ui/`
- [ ] Mover `ColorPickerDialog.kt` → `ui/`
- [ ] Mover `PermissionsDialog.kt` → `ui/`
- [ ] Mover `BugReportDialog.kt` → `ui/`
- [ ] Atualizar imports em todos os arquivos afetados

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] App funcional

**Aguardar aprovação → commit: `refactor: reorganize ui shell`**

---

### Passo 5.2 — Criar HomeScreen
- [ ] Criar `ui/HomeScreen.kt`
  - Grid via `LazyVerticalGrid`
  - Popular via `ToolRegistry.all()`
  - Destacar ferramenta ativa com `KronoTheme.colorScheme.secondaryContainer`
  - Espaçamentos via `KronoTokens`
  - Shapes via `KronoTokens`

**Verificar antes do commit:**
- [ ] Compilar sem erros
- [ ] Grid exibe Stopwatch e Countdown

**Aguardar aprovação → commit: `feat: add HomeScreen with tool grid`**

---

### Passo 5.3 — Atualizar AppNavigation
- [ ] Definir rotas: `home`, `settings`, `tool/{toolId}`
- [ ] `ToolScreen` resolve `ToolRegistry.find(toolId).timerScreenContent()`
- [ ] Substituir `TimerScreen` como root por `HomeScreen`

**Verificar antes do commit:**
- [ ] Navegação completa funcional
- [ ] Nenhuma rota quebrada

**Aguardar aprovação → commit: `feat: update AppNavigation with tool routing`**
Quando screenWidthDp >= 600:
□ Renderizar Row { SettingsMenuPanel(weight=0.35f) | SettingsPanelHost(weight=0.65f) }
□ selectedDestination inicia com Appearance
□ Back arrow some do painel esquerdo
□ Menu permanece visível
□ Item selecionado destacado via KronoTheme.colorScheme.secondaryContainer
```