# Specs — Pomodoro Preset Editor (Refactor)

## Contexto

Refatoração completa do `PomodoroPresetEditorDialog` para suportar estrutura hierárquica de itens:
cards soltos e grupos (que contêm cards + ciclo próprio), com drag & drop em dois níveis.

---

## Modelo de Dados

```
PomodoroPresetConfig
  ├── id: String
  ├── name: String
  ├── cycles: Int                        // ciclo global (1–12)
  └── items: List<PomodoroPresetItem>    // ordem importa

PomodoroPresetItem (sealed)
  ├── Card(phase: PomodoroPhaseConfig)   // card solto
  └── Group(
        id: String,
        label: String,
        cycles: Int,                     // ciclo do grupo (1–12)
        phases: List<PomodoroPhaseConfig>
      )
```

**Execução:** top-down. Grupos repetem seus cards `group.cycles` vezes. A sequência completa de itens repete `preset.cycles` vezes.

---

## Arquivos a Criar / Modificar

| Arquivo | Ação |
|---|---|
| `PomodoroPresetItem.kt` | Novo — sealed class do modelo |
| `PomodoroPresetConfig.kt` | Modificar — trocar `phases` por `items` |
| `PomodoroPresetCatalog.kt` | Modificar — migrar fábricas para novo modelo |
| `PomodoroPresetEditorDialog.kt` | Refatorar — orquestrador da dialog |
| `PomodoroPresetEditorState.kt` | Novo — state holder da dialog |
| `PomodoroPresetItemList.kt` | Novo — lista raiz com drag & drop |
| `PomodoroGroupCard.kt` | Novo — card de grupo expansível |
| `PomodoroPhaseCard.kt` | Novo — card de fase (solto ou dentro de grupo) |
| `PomodoroPresetDragDrop.kt` | Novo — lógica de drag & drop reutilizável |

---

## Fase 1 — Modelo de Dados

### Objetivo
Definir e expor o novo schema sem quebrar nada ainda.

### Passos

**1.1 — Criar `PomodoroPresetItem.kt`**
- Sealed class com dois subtipos: `Card` e `Group`
- `Group` tem `id`, `label`, `cycles`, `phases: List<PomodoroPhaseConfig>`
- Ambos anotados com `@Stable`
- Adicionar extensão `PomodoroPresetItem.id: String` para drag & drop

**1.2 — Atualizar `PomodoroPresetConfig`**
- Remover campo `phases: List<PomodoroPhaseConfig>`
- Adicionar `items: List<PomodoroPresetItem>`
- Manter todos os outros campos intactos
- Garantir que `copy()` funciona corretamente

**1.3 — Atualizar `PomodoroPresetCatalog`**
- Migrar todos os presets de fábrica (Classic, Custom, etc.) para usar `items`
- Remover `ensureRequiredPhases()` — não existe mais restrição mínima
- Adicionar fábrica `defaultGroup(index: Int): PomodoroPresetItem.Group`
- Adicionar fábrica `defaultCard(index: Int): PomodoroPresetItem.Card`

**1.4 — Verificação ✅**
- Build sem erros
- Nenhum crash em runtime
- Presets de fábrica carregam corretamente na tela principal

---

## Fase 2 — State Holder da Dialog

### Objetivo
Centralizar toda a lógica mutável da dialog fora do Composable.

### Passos

**2.1 — Criar `PomodoroPresetEditorState.kt`**

```kotlin
class PomodoroPresetEditorState(initialPreset: PomodoroPresetConfig) {
    var name by mutableStateOf(initialPreset.name)
    var cycles by mutableStateOf(initialPreset.cycles.coerceIn(1, 12))
    val items = mutableStateListOf<PomodoroPresetItem>()
        .apply { addAll(initialPreset.items) }

    fun addCard() { ... }
    fun addGroup() { ... }
    fun removeItem(id: String) { ... }
    fun moveItem(fromIndex: Int, toIndex: Int) { ... }       // nível raiz
    fun addPhaseToGroup(groupId: String) { ... }
    fun removePhaseFromGroup(groupId: String, phaseId: String) { ... }
    fun movePhaseInGroup(groupId: String, from: Int, to: Int) { ... }
    fun moveCardToGroup(cardId: String, groupId: String, atIndex: Int) { ... }
    fun moveCardOutOfGroup(groupId: String, phaseId: String, atRootIndex: Int) { ... }
    fun updateGroup(updated: PomodoroPresetItem.Group) { ... }
    fun updatePhase(groupId: String?, updated: PomodoroPhaseConfig) { ... }

    fun toPresetConfig(original: PomodoroPresetConfig): PomodoroPresetConfig { ... }
}
```

- Instanciado com `remember { PomodoroPresetEditorState(initialPreset) }`
- Nunca passar para filhos — passar apenas dados + lambdas

**2.2 — Verificação ✅**
- Todos os métodos cobertos por testes unitários (JUnit + sem Android deps)
- `moveItem`, `moveCardToGroup`, `moveCardOutOfGroup` testados com edge cases (primeiro, último, único item)

---

## Fase 3 — Componentes Visuais (sem drag)

### Objetivo
Construir os blocos visuais reutilizáveis da dialog.

### Passos

**3.1 — `PomodoroPhaseCard.kt`**
- Composable que recebe `PomodoroPhaseConfig`, callbacks `onEdit`, `onDelete`
- Usa `SettingsRow` existente como base
- Exibe label, duração formatada (`formatAsHhMmSs`), ícone de cor
- Botão delete só aparece quando `showDelete: Boolean = true`
- Botão edit → `KronoIcons.Navigation.ChevronRight`

**3.2 — `PomodoroGroupCard.kt`**
- Composable que recebe `PomodoroPresetItem.Group`, callbacks
- Header: label do grupo + ícone drag handle + botão delete
- Conteúdo interno: lista de `PomodoroPhaseCard` filhos
- `AppearanceSlider` para `group.cycles` (1–12) dentro do grupo
- Botão `+ Adicionar card` dentro do grupo
- Visualmente indentado/destacado em relação ao nível raiz

**3.3 — `PomodoroPresetItemList.kt`** (sem drag ainda)
- `Column` com scroll que renderiza `items` da raiz
- Alterna entre `PomodoroPhaseCard` e `PomodoroGroupCard` via `when`
- Botões ao final: `+ Adicionar card` e `+ Adicionar grupo`
- `AppearanceSlider` global de ciclos ao final

**3.4 — Integrar em `PomodoroPresetEditorDialog.kt`**
- Instanciar `PomodoroPresetEditorState`
- Passar estado + lambdas para `PomodoroPresetItemList`
- Manter `PomodoroPhaseEditorDialog` para edição de fases individuais
- Dialog usa `AlertDialog` com `text` scrolável

**3.5 — Verificação ✅**
- Dialog abre com preset existente e exibe itens corretamente
- Adicionar card solto funciona
- Adicionar grupo funciona
- Editar fase (dialog aninhada) abre e salva corretamente
- Deletar card/grupo funciona
- Slider de ciclo global atualiza corretamente
- Slider de ciclo de grupo atualiza corretamente
- Salvar persiste todas as alterações

---

## Fase 4 — Drag & Drop

### Objetivo
Adicionar reordenação via drag em dois níveis + migração cross-level.

### Passos

**4.1 — Criar `PomodoroPresetDragDrop.kt`**
- Implementar `rememberDragDropState` usando `Modifier.pointerInput` + `LazyListState`
- Expor `DragDropState` com:
  - `draggingItemIndex: Int?`
  - `draggedOverIndex: Int?`
  - `onDragStart(index: Int)`
  - `onDrag(offset: Float)`
  - `onDragEnd()`
- Extensão `Modifier.dragHandle(state, index)` para o ícone de drag
- Suportar callback `onMove(from: Int, to: Int)` e `onDrop(itemId: String, targetGroupId: String?, atIndex: Int)` para cross-level

**4.2 — Drag no nível raiz**
- Converter `PomodoroPresetItemList` para usar `LazyColumn` com `key = { item.id }`
- Integrar `DragDropState` para reordenar `items` raiz
- Drag handle visível em cada item raiz (card solto e header de grupo)
- Ao arrastar card solto sobre grupo → indicar drop zone visual (ex: highlight da borda do grupo)
- Ao soltar card sobre grupo → `moveCardToGroup()`

**4.3 — Drag dentro de grupo**
- Cada `PomodoroGroupCard` tem seu próprio `DragDropState` interno
- Drag handle em cada `PomodoroPhaseCard` filho
- Ao arrastar fase para fora do grupo (threshold de posição Y) → `moveCardOutOfGroup()`

**4.4 — Animações**
- `animateItemPlacement()` nos itens do `LazyColumn`
- `AnimatedVisibility` para highlight de drop zone
- Haptic feedback em `onDragStart` e `onDragEnd`

**4.5 — Verificação ✅**
- Reordenar cards soltos na raiz funciona
- Reordenar grupos na raiz funciona
- Reordenar cards dentro de grupo funciona
- Arrastar card solto para dentro de grupo funciona
- Arrastar card de dentro de grupo para raiz funciona
- Drag não quebra ao ter apenas 1 item
- Drag não quebra com grupos vazios
- Animações fluidas (sem jank)
- Haptic feedback dispara corretamente

---

## Regras Gerais

- Nenhum `ViewModel` como parâmetro de Composable filho
- Nenhuma cor hardcoded — usar `MaterialTheme.colorScheme.*` e tokens `KronoTokens`
- Todo elemento interativo com `contentDescription`
- Touch targets mínimo 48dp
- `key = { it.id }` em toda lista
- Lógica de negócio fora de Composables → `PomodoroPresetEditorState`
- Cada fase concluída só após verificação ✅ completa

---

## Ordem de Execução

```
Fase 1 (Modelo) ──► Fase 2 (State) ──► Fase 3 (UI estática) ──► Fase 4 (Drag)
```

Não avançar para a próxima fase enquanto a verificação ✅ da fase atual não estiver completa.
