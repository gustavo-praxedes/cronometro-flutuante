# Krono — Alarms Feature Spec
> Versão 1.0 | Brainstorm validado

---

## 1. Visão Geral

Sistema de alarmes totalmente configuráveis integrado ao Krono como nova aba na bottom bar. Suporta múltiplos tipos de recorrência combinados, múltiplos horários por alarme, som personalizado, snooze configurável e tela fullscreen ao disparar.

**Problema resolvido:** usuário do Krono precisa de alarmes flexíveis e configuráveis sem sair do app.

---

## 2. Estrutura de Arquivos

```
feature/alarms/
├── data/
│   ├── AlarmDataStore.kt          # persistência de alarmes e config global
│   ├── AlarmModel.kt              # modelos de dados
│   └── AlarmRepository.kt        # bridge DataStore ↔ ViewModel
├── domain/
│   ├── AlarmScheduler.kt          # agenda alarmes via AlarmManager
│   ├── AlarmDeduplicator.kt       # deduplica conflitos de recorrência
│   └── NextAlarmCalculator.kt     # calcula próximo disparo
├── receiver/
│   └── AlarmBroadcastReceiver.kt  # recebe broadcast do AlarmManager
├── service/
│   └── AlarmForegroundService.kt  # toca alarme em foreground
├── ui/
│   ├── AlarmsScreen.kt            # tela principal (lista de cards)
│   ├── AlarmCard.kt               # card individual
│   ├── AlarmConfigDialog.kt       # diálogo de criação/edição
│   ├── AlarmFullscreenActivity.kt # tela fullscreen ao disparar
│   └── AlarmViewModel.kt          # ViewModel principal
```

---

## 3. Modelos de Dados

### 3.1 AlarmModel

```kotlin
data class AlarmModel(
    val id: String,                          // UUID
    val label: String,                       // nome; auto "Alarme N" se vazio
    val times: List<LocalTime>,              // lista de horários (mín. 1)
    val recurrences: List<AlarmRecurrence>,  // lista de recorrências (mín. 1)
    val monthFilter: Set<Month>,             // vazio = todos os meses
    val sound: Uri?,                         // null = som padrão do sistema
    val snoozeMinutes: Int,                  // duração da soneca em minutos
    val snoozeMaxCount: Int,                 // máximo de sonecas permitidas
    val isEnabled: Boolean,                  // ativo/inativo permanente
    val skipNext: Boolean,                   // pular próximo disparo uma vez
    val createdAt: Long,                     // epoch ms
    val lastFiredAt: Long?,                  // epoch ms — para recorrência XDays
)
```

### 3.2 AlarmRecurrence (sealed)

```kotlin
sealed class AlarmRecurrence {
    data class Weekly(val days: Set<DayOfWeek>) : AlarmRecurrence()
    data class MonthlyByDay(val day: Int) : AlarmRecurrence()   // 1–28
    data object LastDayOfMonth : AlarmRecurrence()
    data class XDaysAfterLast(
        val days: Int,
        val startDate: LocalDate                                  // definido pelo usuário na criação
    ) : AlarmRecurrence()
}
```

### 3.3 GlobalAlarmConfig

```kotlin
data class GlobalAlarmConfig(
    val defaultSnoozeMinutes: Int = 5,
    val defaultSnoozeMaxCount: Int = 3
)
```

---

## 4. Regras de Negócio

### 4.1 Recorrências
- Um alarme pode ter **múltiplas recorrências combinadas** (ex: toda segunda + dia 15)
- Mínimo: 1 recorrência por alarme
- Se duas recorrências resultam no mesmo dia, o alarme dispara **uma única vez** (deduplica)
- Filtro de meses é **opcional** — se configurado, alarme só dispara nos meses selecionados

### 4.2 Cobertura de Dias
| Tipo | Cobertura |
|------|-----------|
| Dia da semana | SEG TER QUA QUI SEX SAB DOM |
| Nº do mês | 1 a 28 (existe em qualquer mês) |
| Último dia do mês | cobre 28, 29, 30, 31 conforme o mês |
| X dias do último disparo | contagem a partir de `startDate` ou `lastFiredAt` |

### 4.3 Múltiplos Horários
- Um alarme pode ter **múltiplos horários** no mesmo dia (ex: 08:00 e 18:00)
- Mínimo: 1 horário por alarme
- Cada horário dispara de forma independente

### 4.4 Snooze
- Duração e quantidade máxima configuráveis por alarme
- Controle de `snoozeCount` em runtime (não persistido — reinicia a cada disparo)
- Na última soneca permitida: tela fullscreen exibe **apenas "Dispensar"** (sem botão de soneca)
- Após soneca: alarme vai para notificação do sistema aguardando

### 4.5 Toggle / Desativar
- Clicar no toggle do card abre dialog:
  - **"Pular próximo disparo"** → seta `skipNext = true` (volta a disparar depois)
  - **"Desativar permanentemente"** → seta `isEnabled = false`
- Quando `skipNext = true`, o card exibe indicação visual (ex: ícone ou texto "Próximo pulado")

### 4.6 Ordenação
- Cards ordenados por **próximo disparo** (mais próximo primeiro)
- Alarmes desativados vão para o fim da lista

### 4.7 Label Automático
- Se usuário não preencher label: gerado automaticamente como "Alarme N" onde N é sequencial

---

## 5. Tela Principal (AlarmsScreen)

### 5.1 Estado vazio
- Ícone watermark centralizado
- Texto instrucional: "Nenhum alarme. Toque em + para adicionar."

### 5.2 Lista de cards
- `LazyColumn` rolável
- `FloatingActionButton` `+` fixo no canto inferior direito
- Cards ordenados por próximo disparo

### 5.3 AlarmCard
```
┌─────────────────────────────────────────┐
│ Nome do Alarme                    [ ⋮ ] │
│ 08:00 · 18:00                  [ toggle]│
└─────────────────────────────────────────┘
```
- **Nome:** label do alarme (ou "Alarme N")
- **Horários:** lista de horários formatados
- **Toggle:** ativa/desativa com dialog de confirmação
- **⋮:** menu com opção "Excluir"
- Cores seguem `KronoTheme` ativo
- Card com `skipNext = true`: badge ou subtítulo "Próximo disparo pulado"

---

## 6. Diálogo de Configuração (AlarmConfigDialog)

Aberto ao clicar em `+` (criação) ou no card (edição futura).

### 6.1 Seções do diálogo

**Label**
- Campo de texto opcional
- Placeholder: "Nome do alarme"

**Horários**
- Lista de `TimePicker` com botão `+` para adicionar
- Mínimo 1 horário
- Botão `−` em cada item para remover (desabilitado se só restar 1)

**Recorrências**
- Lista de recorrências com botão `+` para adicionar
- Mínimo 1 recorrência
- Cada recorrência tem seletor de tipo + configuração específica:

| Tipo | UI |
|------|----|
| Dia da semana | chips SEG TER QUA QUI SEX SAB DOM (multi-select) |
| Nº do mês | número 1–28 (scroll picker ou campo numérico) |
| Último dia do mês | sem configuração extra |
| X dias do último disparo | campo numérico (dias) + date picker (data inicial) |

**Filtro de Meses** (opcional, colapsável)
- Chips JAN FEV MAR ABR MAI JUN JUL AGO SET OUT NOV DEZ
- Vazio = todos os meses

**Som**
- Botão que abre seletor de sons do dispositivo (`RingtoneManager`)
- Exibe nome do som selecionado
- Opção "Padrão do sistema"

**Snooze**
- Campo: duração (minutos)
- Campo: quantidade máxima de sonecas

**Botões**
- `Cancelar` — fecha sem salvar
- `Salvar` — valida e persiste

---

## 7. Tela Fullscreen (AlarmFullscreenActivity)

Exibida quando alarme dispara e app está em foreground (ou usuário toca na notificação).

```
┌─────────────────────────────────────────┐
│                                         │
│           Nome do Alarme                │
│            08:00                        │
│                                         │
│                                         │
│    [ Soneca (N restantes) ]             │
│    [ Dispensar            ]             │
│                                         │
└─────────────────────────────────────────┘
```

- Exibe nome + horário do disparo
- Botão **Soneca** visível apenas se `snoozeCount < snoozeMaxCount`
- Botão **Dispensar** sempre visível
- Após última soneca: apenas **Dispensar**
- Após soneca: vai para notificação do sistema
- Cores seguem `KronoTheme` ativo

---

## 8. Notificação

- Disparada quando app está em background
- Ações na notificação: **Soneca** e **Dispensar**
- Toque na notificação → abre `AlarmFullscreenActivity`
- Permissão necessária: `USE_EXACT_ALARM` (Android 12+) e `POST_NOTIFICATIONS` (Android 13+)

---

## 9. AlarmScheduler

```kotlin
object AlarmScheduler {
    fun schedule(alarm: AlarmModel)
    fun cancel(alarmId: String)
    fun reschedule(alarm: AlarmModel)   // cancela e reagenda
    fun cancelAll()
}
```

- Usa `AlarmManager.setExactAndAllowWhileIdle()` para precisão
- Após cada disparo, `AlarmBroadcastReceiver` reagenda o próximo automaticamente
- `NextAlarmCalculator` computa o próximo `Instant` com base em todas as recorrências + filtro de meses + deduplicação

---

## 10. AlarmDataStore

- DataStore **separado** de `OverlayDataStore`, `CalcDataStore` e `CounterDataStore`
- Armazena: lista de `AlarmModel` + `GlobalAlarmConfig`
- Serialização via `kotlinx.serialization`

---

## 11. Permissões (AndroidManifest)

```xml
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.USE_EXACT_ALARM" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

- `RECEIVE_BOOT_COMPLETED`: reagenda alarmes após reinício do dispositivo

---

## 12. Navegação

- Nova aba na **bottom bar** existente
- Ícone: `KronoIcons.Feature.Alarm` (a definir em `KronoIcons.kt`)
- `SettingsDestination` não necessário no MVP (sem settings próprios de alarme — snooze configurado por alarme)

---

## 13. Decision Log

| Decisão | Alternativas | Rationale |
|---|---|---|
| Recorrências combinadas livres | Um tipo por alarme | Pedido explícito |
| Deduplicação de conflitos | Disparar duas vezes | Pedido explícito |
| Dias 1–28 + último dia do mês | Range 1–31 com ajuste global | Cobre todos os casos sem edge cases |
| X dias: data inicial definida pelo usuário | Data de criação, disparo imediato | Pedido explícito |
| Múltiplos horários por alarme | Um horário por alarme | Pedido explícito |
| Son personalizável do dispositivo | Sons fixos do Krono | Pedido explícito |
| Snooze configurável por alarme | Fixo global | Pedido explícito |
| Última soneca só tem "Dispensar" | Bloqueia depois do limite | Pedido explícito |
| Toggle → dialog (pular/desativar) | Toggle direto | Pedido explícito |
| "Pular" = só próximo disparo | Pular dia inteiro, N disparos | Pedido explícito |
| Cards ordenados por próximo disparo | Ordem de criação, drag | Pedido explícito |
| Label opcional com auto-nome | Obrigatório | Pedido explícito |
| USE_EXACT_ALARM | WorkManager | Precisão total, pedido explícito |
| DataStore separado | Mesmo OverlayDataStore | Separação de responsabilidade |
| Fullscreen ao disparar | Só notificação | Pedido explícito |

---

## 14. Assumptions

- `[ASSUMPTION]` Alarmes são reagendados após reboot via `RECEIVE_BOOT_COMPLETED`
- `[ASSUMPTION]` `snoozeCount` é estado de runtime — não persiste entre disparos
- `[ASSUMPTION]` Som padrão = `RingtoneManager.TYPE_ALARM` do sistema
- `[ASSUMPTION]` Edição de alarme existente: toque no card abre `AlarmConfigDialog` preenchido
- `[ASSUMPTION]` Vibração sempre acompanha o som (sem config separada no MVP)
- `[ASSUMPTION]` App solicita permissão `USE_EXACT_ALARM` no primeiro acesso à aba de alarmes
- `[ASSUMPTION]` Filtro de meses vazio = todos os meses ativos

---

## 15. Plano de Implementação

---

### Fase 1 — Fundação de Dados
**Objetivo:** modelos, persistência e nenhuma UI.

**Passos:**
1. Criar `AlarmModel.kt` com todos os tipos (`AlarmRecurrence` sealed, `GlobalAlarmConfig`)
2. Criar `AlarmDataStore.kt` com serialização via `kotlinx.serialization`
3. Criar `AlarmRepository.kt` com operações CRUD básicas (insert, update, delete, getAll)
4. Escrever unit tests para serialização e CRUD

> **Commit sugerido:** `feat: add alarm data layer (models, datastore, repository)`

---

### Fase 2 — Engine de Cálculo
**Objetivo:** lógica de negócio pura, testável isolada.

**Passos:**
1. Criar `NextAlarmCalculator.kt` — calcula próximo `Instant` de disparo para cada `AlarmModel`
2. Criar `AlarmDeduplicator.kt` — remove disparos duplicados quando recorrências colidem
3. Cobrir edge cases: último dia do mês em fevereiro, filtro de meses, X dias sem `lastFiredAt`
4. Unit tests completos para todos os tipos de recorrência e combinações

> **Commit sugerido:** `feat: add alarm scheduling engine (calculator, deduplicator)`

---

### Fase 3 — Agendamento do Sistema
**Objetivo:** integração com `AlarmManager` e reboot receiver.

**Passos:**
1. Criar `AlarmScheduler.kt` com `setExactAndAllowWhileIdle()`
2. Criar `AlarmBroadcastReceiver.kt` — recebe disparo, aciona serviço, reagenda próximo
3. Criar `BootReceiver.kt` — reagenda todos alarmes ativos após reboot
4. Registrar receivers e permissões no `AndroidManifest`
5. Criar `AlarmForegroundService.kt` — toca som + vibração em foreground

> **Commit sugerido:** `feat: add alarm system integration (scheduler, receivers, foreground service)`

---

### Fase 4 — Tela Fullscreen
**Objetivo:** UI de alarme disparando.

**Passos:**
1. Criar `AlarmFullscreenActivity.kt` com nome + horário do alarme
2. Implementar botão **Dispensar** — para serviço + cancela notificação
3. Implementar botão **Soneca** — para serviço, posta notificação, agenda próximo snooze
4. Controle de `snoozeCount`: ocultar botão Soneca na última soneca
5. Aplicar `KronoTheme` e estilo visual consistente com o app

> **Commit sugerido:** `feat: add alarm fullscreen activity with snooze control`

---

### Fase 5 — Notificação
**Objetivo:** notificação do sistema com ações.

**Passos:**
1. Criar canal de notificação de alarme (`NotificationChannel`)
2. Notificação com ações **Soneca** e **Dispensar** via `PendingIntent`
3. Toque na notificação → abre `AlarmFullscreenActivity`
4. Solicitar permissão `POST_NOTIFICATIONS` em Android 13+
5. Solicitar permissão `USE_EXACT_ALARM` / `SCHEDULE_EXACT_ALARM` em Android 12+

> **Commit sugerido:** `feat: add alarm notification with snooze and dismiss actions`

---

### Fase 6 — Tela Principal
**Objetivo:** lista de alarmes com estado vazio e FAB.

**Passos:**
1. Criar `AlarmsScreen.kt` com `LazyColumn` e estado vazio (watermark + instrução)
2. Criar `AlarmCard.kt` — nome + horários + toggle + menu `⋮`
3. Implementar ordenação por próximo disparo
4. Implementar menu `⋮` com opção "Excluir" (com dialog de confirmação)
5. Implementar badge "Próximo disparo pulado" quando `skipNext = true`
6. `FloatingActionButton` `+` fixo no canto inferior direito
7. Criar `AlarmViewModel.kt` conectando repository + scheduler

> **Commit sugerido:** `feat: add alarms main screen (list, card, empty state, FAB)`

---

### Fase 7 — Diálogo de Configuração
**Objetivo:** criação e edição de alarmes.

**Passos:**
1. Criar `AlarmConfigDialog.kt` com campo de label
2. Seção de horários com lista + botão `+` e remoção
3. Seção de recorrências com lista + botão `+` e seletor de tipo:
   - Chips de dia da semana
   - Picker numérico de dia do mês (1–28)
   - Toggle "Último dia do mês"
   - Campo de dias + date picker para X dias
4. Seção de filtro de meses (chips colapsáveis)
5. Seletor de som via `RingtoneManager`
6. Campos de snooze (duração + quantidade)
7. Validação: mínimo 1 horário, mínimo 1 recorrência
8. Salvar → persiste + agenda no `AlarmScheduler`

> **Commit sugerido:** `feat: add alarm config dialog with full recurrence and snooze options`

---

### Fase 8 — Toggle e Fluxo de Desativação
**Objetivo:** comportamento do toggle e skip.

**Passos:**
1. Dialog ao clicar no toggle: "Pular próximo disparo" / "Desativar permanentemente"
2. "Pular próximo" → `skipNext = true` + reagenda pulando o próximo `Instant`
3. "Desativar" → `isEnabled = false` + cancela agendamento
4. Reativar toggle quando desativado → `isEnabled = true` + reagenda
5. Reset de `skipNext` após o próximo disparo ser pulado

> **Commit sugerido:** `feat: add alarm toggle flow (skip once, disable permanently)`

---

### Fase 9 — Integração na Bottom Bar
**Objetivo:** aba de alarmes visível e navegável.

**Passos:**
1. Adicionar `KronoIcons.Feature.Alarm` em `KronoIcons.kt`
2. Adicionar aba na bottom bar existente
3. Verificar e solicitar permissões ao primeiro acesso à aba
4. Smoke test de navegação completa: criar alarme → card aparece → dispara → fullscreen → soneca → notificação → dispensar

> **Commit sugerido:** `feat: integrate alarms into bottom bar navigation`

---

### Fase 10 — Polimento e Edge Cases
**Objetivo:** robustez e experiência final.

**Passos:**
1. Testar reboot: alarmes reagendados corretamente
2. Testar troca de tema: fullscreen e cards seguem `KronoTheme`
3. Testar filtro de meses + combinações de recorrência complexas
4. Testar snooze no limite máximo
5. Testar `skipNext` + reagendamento correto
6. Testar alarmes com múltiplos horários no mesmo dia
7. Resolver edge cases de `X dias` sem `lastFiredAt` (primeiro disparo)
8. Acessibilidade: content descriptions nos botões da fullscreen e cards

> **Commit sugerido:** `fix: alarm edge cases, reboot persistence and theme consistency`
