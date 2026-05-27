# Krono — Calculator Tool Spec
> Versão 1.0 | Brainstorm validado

---

## 1. Visão Geral

Calculadora de horas integrada ao Krono como nova aba na bottom bar. Permite somar, subtrair, multiplicar e dividir blocos de tempo com entrada estilo calculadora numérica e formato `HH:MM:SS` (com milissegundos opcionais).

**Problema resolvido:** usuário do Krono precisa operar blocos de tempo sem sair do app.

**MVP:** ferramenta independente — sem integração com cronômetro ou countdown nesta versão.

---

## 2. Estrutura de Arquivos

```
feature/calculator/
├── CalcEngine.kt          # lógica pura, zero Compose, testável isolado
├── CalcState.kt           # data class do estado completo da UI
├── CalcDataStore.kt       # persistência: histórico + configurações
├── CalcViewModel.kt       # bridge CalcEngine ↔ UI
├── CalculatorScreen.kt    # tela principal + visor + teclado
└── CalcHistorySheet.kt    # ModalBottomSheet do histórico completo
```

Novo entry em `SettingsDestination`:
```kotlin
data object Calculator : SettingsDestination(
    titleRes = R.string.settings_calculator,
    icon = KronoIcons.Feature.Calculator
)
```

---

## 3. Entrada e Formatação

### 3.1 Regra de digitação
- Dígitos empurram da direita, máximo 6 dígitos (sem ms) ou 6+3 (com ms)
- 7º dígito é ignorado silenciosamente
- `00` empurra dois zeros à direita de uma vez

| Digitado | Exibido |
|----------|---------|
| `1` | `00:00:01` |
| `10` | `00:00:10` |
| `2025` | `00:20:25` |
| `442255` | `44:22:55` |

### 3.2 Milissegundos
- Ativados via `.` no teclado — sempre visível e funcional
- Exibição: `HH:MM:SS.mmm` (3 casas)
- Habilitado/desabilitado em Settings da calc (`showMillis: Boolean = false` por padrão)
- Quando desabilitado: `.` é ignorado silenciosamente

### 3.3 Overflow e Normalização
- MM e SS aceitam valores 0–59 durante digitação (sem bloqueio)
- Normalização ocorre ao pressionar qualquer operador (`+` `−` `×` `÷`) ou `=`
- Exemplo: `99:75:80` → normaliza → `100:16:20`

---

## 4. Operações

| Operação | Comportamento |
|----------|---------------|
| `+` | soma dois tempos |
| `−` | subtrai; resultado pode ser negativo |
| `×` | tempo × fator numérico |
| `÷` | tempo ÷ fator numérico |

### 4.1 Multiplicação e Divisão
- Operando direito é sempre um **fator numérico**, não tempo
- Tipo do fator configurável em Settings: `Inteiro` ou `Decimal`
- Padrão: `Decimal`

### 4.2 Resultado Negativo
- Permitido e exibido com prefixo `−`
- Exemplo: `00:10:00 − 00:30:00 = −00:20:00`

### 4.3 Encadeamento
- Apenas um operador pendente por vez (sem precedência de operadores no MVP)
- Pressionar novo operador antes de `=` aplica o pendente primeiro

---

## 5. CalcEngine

```kotlin
object CalcEngine {
    fun pushDigit(raw: String, digit: Char): String
    fun pushDoubleZero(raw: String): String
    fun popDigit(raw: String): String          // ⌫ backspace
    fun format(raw: String, showMillis: Boolean): String
    fun normalize(raw: String): Long           // retorna total em ms
    fun applyOp(acc: Long, op: Op, operand: Long, factorMode: FactorMode): Long
    fun toDisplay(ms: Long, showMillis: Boolean): String
    fun toggleSign(ms: Long): Long             // +/−
}

enum class Op { PLUS, MINUS, TIMES, DIVIDE }
enum class FactorMode { INTEGER, DECIMAL }
```

Engine é **Kotlin puro** — sem dependências Android ou Compose. Totalmente testável via unit tests.

---

## 6. CalcState

```kotlin
data class CalcState(
    val inputRaw: String = "0",
    val displayValue: String = "00:00:00",
    val pendingOp: Op? = null,
    val accumulator: Long = 0L,       // ms
    val expression: String = "",      // ex: "01:30:00 +"
    val result: String? = null,       // último resultado formatado
    val isNegative: Boolean = false,
    val showMillis: Boolean = false,
    val factorMode: FactorMode = FactorMode.DECIMAL
)
```

---

## 7. CalcDataStore

```kotlin
// Configurações
data class CalcConfig(
    val showMillis: Boolean = false,
    val factorMode: FactorMode = FactorMode.DECIMAL,
    val historyLimit: Int = 50        // configurável, padrão 50
)

// Entrada do histórico
data class CalcHistoryEntry(
    val expression: String,           // "01:30:00 + 00:45:00"
    val result: String,               // "02:15:00"
    val timestamp: Long               // epoch ms — para ordenação interna
)
```

- Serialização via `kotlinx.serialization`
- DataStore **separado** do `OverlayDataStore`
- Histórico mantém as últimas N entradas (N = `historyLimit`)
- Timestamp não exibido na UI — apenas para ordenação

---

## 8. Layout do Teclado

```
┌──────────────────────────────────────┐
│  expressão atual          01:30:00 + │  ← cinza médio, alinhado à direita
│  = 02:15:00                          │  ← resultado, grande, negrito
├──────────────────────────────────────┤
│  C      ⌫      +/−      ÷           │
│  7      8       9        ×           │
│  4      5       6        −           │
│  1      2       3        +           │
│  00     0       .       [=]          │  ← = é FAB primário (cor primary)
└──────────────────────────────────────┘
```

### Mapeamento dos botões especiais

| Botão | Ação |
|-------|------|
| `C` | Limpa histórico completo |
| `⌫` | Apaga último dígito digitado |
| `+/−` | Inverte sinal do valor atual |
| `00` | Empurra dois zeros à direita |
| `.` | Ativa entrada de milissegundos |
| `=` | Aplica operação + normaliza + salva no histórico |

### Estilo visual (referência imagem)
- Botões de operador: cor `primary` (laranja no tema padrão)
- Botões numéricos: cor `surface` / `onSurface`
- `=` : FAB circular, cor `primary`, maior que os demais
- Sem espaços vazios — botões ocupam 100% da largura disponível
- Fonte monospace (respeita `selectedFont` do `OverlayConfig`)

---

## 9. Visor

- Parte superior da tela, rolável verticalmente
- Histórico inline: entradas anteriores empilhadas, alinhadas à direita, cor `onSurfaceVariant` (cinza claro)
- Cada entrada inline: expressão + resultado, separados por `HorizontalDivider` suave
- Expressão atual: cinza médio
- Resultado atual: grande, negrito, cor `onSurface`
- Clique no visor: **sem ação** (histórico abre via `⋮`)

---

## 10. Histórico Completo

- Acessado via `⋮` (três pontos verticais) no topo da tela
- Menu exibe: "Exibir histórico" e "Configurações da Calculadora"
- Histórico abre como `ModalBottomSheet`
- Cada entry: expressão em cima, resultado embaixo, separados por `HorizontalDivider` suave
- Swipe individual → deleta entry
- Botão `C` no sheet → limpa histórico completo

---

## 11. Settings da Calculadora

Entry em `SettingsMenuPanel` na seção **Ferramentas**. Painel `CalcSettingsPanel` com:

| Configuração | Tipo | Padrão |
|---|---|---|
| Exibir milissegundos | Toggle | Off |
| Tipo do fator (× ÷) | Seletor: Inteiro / Decimal | Decimal |
| Limite do histórico | Campo numérico | 50 |

---

## 12. Navegação

- Nova aba na **bottom bar** existente
- Ícone: `KronoIcons.Feature.Calculator` (a definir em `KronoIcons.kt`)
- `SettingsDestination.Calculator` adicionado ao menu de settings, seção Ferramentas

---

## 13. Decision Log

| Decisão | Alternativas | Rationale |
|---|---|---|
| Engine puro separado da UI | Lógica no ViewModel | Testável, extensível |
| Overflow normaliza em operador ou `=` | Normalizar por dígito, bloquear | Não interrompe digitação |
| Milissegundos opcionais, 3 casas, default off | Sempre on, 4 casas | YAGNI |
| Histórico inline no visor (rolável, cinza) | ModalBottomSheet exclusivo | Visibilidade imediata |
| `⋮` abre histórico completo + config | Clique no visor | Pedido explícito |
| `C` limpa histórico, `⌫` apaga dígito | C único | Pedido explícito |
| `00` substitui ícone de histórico no teclado | Ícone dedicado | Pedido explícito |
| `.` sempre visível e funcional | Oculto quando ms desabilitado | Pedido explícito |
| Resultado negativo permitido | Bloquear, travar em zero | Pedido explícito |
| MVP independente do cronômetro | Integração bidirecional | YAGNI |
| DataStore separado `CalcDataStore` | Mesmo `OverlayDataStore` | Separação de responsabilidade |

---

## 14. Assumptions

- `[ASSUMPTION]` 100% offline, sem chamadas externas
- `[ASSUMPTION]` Fonte do visor respeita `selectedFont` do `OverlayConfig`
- `[ASSUMPTION]` Tema visual segue `KronoTheme` ativo (sem tema próprio)
- `[ASSUMPTION]` Timestamp salvo internamente para ordenação mas nunca exibido
- `[ASSUMPTION]` Integração com cronômetro/countdown fora do escopo desta versão

---

## 15. Plano de Implementação Incremental

| Passo | Entregável |
|---|---|
| 1 | `CalcEngine.kt` + unit tests completos |
| 2 | `CalcState.kt` + `CalcDataStore.kt` |
| 3 | `CalcViewModel.kt` |
| 4 | Visor (`CalculatorScreen` — só display) |
| 5 | Teclado completo + interações |
| 6 | Histórico inline no visor |
| 7 | `CalcHistorySheet.kt` (ModalBottomSheet) |
| 8 | `CalcSettingsPanel.kt` + entry em Settings |
| 9 | Integração na bottom bar |
| 10 | Testes de integração + edge cases |
