# Krono — Clock Tool Spec
> Versão 1.0 | Brainstorm validado

---

## 1. Visão Geral

Ferramenta de relógio integrada ao Krono como nova aba na bottom bar. Exibe a hora local atual em formato 24h e permite adicionar cards de fusos horários do mundo.

---

## 2. Estrutura de Arquivos

```
feature/clock/
├── ClockDataStore.kt      # persistência dos fusos adicionados
├── ClockModel.kt          # modelos de dados
├── ClockViewModel.kt      # atualização de tempo + estado
└── ClockScreen.kt         # tela principal
```

---

## 3. Modelos de Dados

### 3.1 WorldClockEntry

```kotlin
data class WorldClockEntry(
    val id: String,                  // UUID
    val zoneId: String,              // ex: "America/New_York"
    val displayName: String,         // ex: "Nova York"
    val addedAt: Long                // epoch ms — para ordenação
)
```

### 3.2 Lista Pré-definida

Lista curada de cidades com `zoneId` e nome localizado em PT-BR. Exemplos:

| Cidade | ZoneId |
|--------|--------|
| Londres | Europe/London |
| Lisboa | Europe/Lisbon |
| Nova York | America/New_York |
| Los Angeles | America/Los_Angeles |
| São Paulo | America/Sao_Paulo |
| Tokyo | Asia/Tokyo |
| Sydney | Australia/Sydney |
| Dubai | Asia/Dubai |
| Paris | Europe/Paris |
| Berlim | Europe/Berlin |
| Moscou | Europe/Moscow |
| Pequim | Asia/Shanghai |
| Mumbai | Asia/Kolkata |
| Toronto | America/Toronto |
| Chicago | America/Chicago |
| Buenos Aires | America/Argentina/Buenos_Aires |
| Cidade do México | America/Mexico_City |
| Cairo | Africa/Cairo |
| Joanesburgo | Africa/Johannesburg |
| Singapura | Asia/Singapore |

> Lista completa a ser definida na implementação com cobertura global adequada.

---

## 4. Tela Principal (ClockScreen)

### 4.1 Hora Local

```
┌─────────────────────────────────────────┐
│                                         │
│              14:32:07                   │  ← hora local, grande, negrito
│           Brasília · BRT               │  ← cidade/fuso local, menor
│                                         │
├─────────────────────────────────────────┤
│  [ Card fuso 1 ]                        │
│  [ Card fuso 2 ]                        │
│  ...                                    │
│                              [ + FAB ]  │
└─────────────────────────────────────────┘
```

- Hora local centralizada no topo
- Atualiza a cada segundo via `Flow` no `ClockViewModel`
- Formato: `HH:mm:ss` (24h)
- Subtítulo: nome da cidade local + sigla do fuso (detectado automaticamente)
- `LazyColumn` rolável para os cards abaixo
- `FloatingActionButton` `+` fixo no canto inferior direito

### 4.2 Estado sem cards
- Apenas o relógio local é exibido
- Sem watermark ou instrução — relógio já é o conteúdo principal

---

## 5. WorldClockCard

```
┌─────────────────────────────────────────┐
│ Nova York                         [ ⋮ ] │
│ 09:32:07                    −5h / −4h* │
└─────────────────────────────────────────┘
```

- **Cidade:** `displayName` do `WorldClockEntry`
- **Hora:** hora atual no fuso, formato `HH:mm:ss`, atualiza a cada segundo
- **Diferença:** offset em relação ao horário local (ex: `−5h`, `+3h`)
- `*` indica horário de verão quando aplicável
- **⋮:** menu com opção "Remover"
- Cores seguem `KronoTheme` ativo
- Ordenação: ordem de adição (`addedAt`)

---

## 6. Diálogo de Adição de Fuso

- Abre ao clicar no FAB `+`
- Lista rolável de cidades pré-definidas
- Cidades já adicionadas aparecem desabilitadas (sem duplicatas)
- Toque em uma cidade → adiciona card imediatamente → fecha diálogo
- Sem botão "Salvar" — ação imediata ao selecionar

---

## 7. Overlay

- Exibe **apenas a hora local** em formato `HH:mm:ss`
- Segue configuração de `OverlayConfig` existente (posição, tamanho, fonte)
- Atualiza a cada segundo junto com a tela principal

---

## 8. ClockDataStore

```kotlin
data class ClockConfig(
    val worldClocks: List<WorldClockEntry> = emptyList()
)
```

- DataStore **separado** de todos os outros (`OverlayDataStore`, `CalcDataStore`, etc.)
- Serialização via `kotlinx.serialization`
- Persiste ao fechar o app

---

## 9. ClockViewModel

```kotlin
class ClockViewModel : ViewModel() {
    val localTime: StateFlow<LocalTime>           // atualiza a cada segundo
    val worldClocks: StateFlow<List<WorldClockEntry>>
    val worldClockTimes: StateFlow<Map<String, LocalTime>>  // zoneId → hora atual

    fun addWorldClock(entry: WorldClockEntry)
    fun removeWorldClock(id: String)
}
```

- `ticker` via `flow { while(true) { emit(Unit); delay(1000) } }`
- Calcula hora de cada fuso a cada tick

---

## 10. Navegação

- Nova aba na **bottom bar** existente
- Ícone: `KronoIcons.Feature.Clock` (a definir em `KronoIcons.kt`)
- Sem `SettingsDestination` próprio no MVP

---

## 11. Assumptions

- `[ASSUMPTION]` Hora local detectada via `ZoneId.systemDefault()`
- `[ASSUMPTION]` Diferença de fuso calculada em runtime (respeita DST automaticamente)
- `[ASSUMPTION]` Lista de cidades pré-definida hardcoded em um objeto `WorldZones.kt`
- `[ASSUMPTION]` Sem suporte a busca por cidade no MVP
- `[ASSUMPTION]` Fonte do relógio respeita `selectedFont` do `OverlayConfig`
- `[ASSUMPTION]` Sem configuração de formato (sempre 24h)

---

## 12. Decision Log

| Decisão | Alternativas | Rationale |
|---|---|---|
| Sempre 24h | Seguir sistema, 12h | Pedido explícito |
| Overlay só hora local | Todos os fusos | Pedido explícito |
| Lista pré-definida | Busca livre | Pedido explícito |
| Ordenação por adição | Por offset, drag | Pedido explícito |
| Ação imediata no diálogo | Botão salvar | UX mais simples |
| Sem estado vazio instrucional | Watermark + texto | Relógio já é conteúdo |
| DataStore separado | Mesmo OverlayDataStore | Separação de responsabilidade |

---

## 13. Plano de Implementação

---

### Fase 1 — Dados e ViewModel
**Passos:**
1. Criar `ClockModel.kt` (`WorldClockEntry`, `ClockConfig`)
2. Criar `WorldZones.kt` — lista curada de cidades com `ZoneId`
3. Criar `ClockDataStore.kt` com serialização
4. Criar `ClockViewModel.kt` com ticker a cada segundo e operações CRUD

> **Commit sugerido:** `feat: add clock data layer and viewmodel with ticker`

---

### Fase 2 — Tela Principal
**Passos:**
1. Criar `ClockScreen.kt` com hora local centralizada (`HH:mm:ss`)
2. Subtítulo com cidade local + sigla do fuso
3. `LazyColumn` para cards de fusos
4. FAB `+` fixo no canto inferior direito
5. Aplicar `KronoTheme`

> **Commit sugerido:** `feat: add clock main screen with local time display`

---

### Fase 3 — Cards de Fuso e Diálogo
**Passos:**
1. Criar `WorldClockCard.kt` — cidade + hora + offset + menu `⋮`
2. Indicação de DST (`*`) quando aplicável
3. Diálogo de adição: lista rolável, cidades já adicionadas desabilitadas
4. Ação imediata ao selecionar cidade
5. Remover card via menu `⋮`

> **Commit sugerido:** `feat: add world clock cards and add/remove flow`

---

### Fase 4 — Overlay e Integração
**Passos:**
1. Integrar hora local no overlay existente (`HH:mm:ss`, atualiza a cada segundo)
2. Adicionar aba na bottom bar com `KronoIcons.Feature.Clock`
3. Smoke test: adicionar fusos → horas corretas → remover → overlay atualiza

> **Commit sugerido:** `feat: integrate clock into overlay and bottom bar navigation`
