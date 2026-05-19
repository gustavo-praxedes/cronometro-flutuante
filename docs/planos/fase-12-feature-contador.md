# Krono — Counter Tool Spec
> Versão 1.0

---

## 1. Visão Geral

Contador simples integrado ao Krono como nova aba na bottom bar. Tap na tela incrementa o valor. Valor persiste ao fechar o app.

---

## 2. Estrutura de Arquivos

```
feature/counter/
├── CounterDataStore.kt    # persistência do valor atual
└── CounterScreen.kt       # tela principal
```

Novo entry em `SettingsDestination`:
```kotlin
data object Counter : SettingsDestination(
    titleRes = R.string.settings_counter,
    icon = KronoIcons.Feature.Counter
)
```

---

## 3. Comportamento

| Ação | Resultado |
|------|-----------|
| Tap em qualquer área da tela | +1 |
| Botão `−1` | −1 (desabilitado quando valor = 0) |
| Botão reset | volta a 0 |

- Valor mínimo: `0`
- Valor máximo: ilimitado (`Long`)
- Sem configurações adicionais

---

## 4. CounterDataStore

```kotlin
data class CounterConfig(
    val count: Long = 0L
)
```

- DataStore **separado** do `OverlayDataStore` e `CalcDataStore`
- Persiste ao fechar o app

---

## 5. Layout

```
┌──────────────────────────────────────┐
│                                      │
│                                      │
│             [ 42 ]                   │  ← valor central, grande, negrito
│                                      │
│                                      │
├──────┬───────────────────────────────┤
│  −1  │          reset                │
└──────┴───────────────────────────────┘
```

- Tap em qualquer área acima dos botões → +1
- `−1` desabilitado (alpha reduzido) quando `count == 0`
- Fonte monospace, respeita `selectedFont` do `OverlayConfig`
- Tema visual segue `KronoTheme` ativo

---

## 6. Navegação

- Nova aba na **bottom bar** existente
- Ícone: `KronoIcons.Feature.Counter` (a definir em `KronoIcons.kt`)

---

## 7. Assumptions

- `[ASSUMPTION]` Sem limite máximo — valor armazenado como `Long`
- `[ASSUMPTION]` Sem histórico de incrementos
- `[ASSUMPTION]` Sem settings próprios
- `[ASSUMPTION]` Feedback visual/háptico no tap segue padrão do sistema

---

## 8. Plano de Implementação

| Passo | Entregável |
|---|---|
| 1 | `CounterDataStore.kt` |
| 2 | `CounterScreen.kt` — display + tap |
| 3 | Botões `−1` e reset |
| 4 | Integração na bottom bar |
