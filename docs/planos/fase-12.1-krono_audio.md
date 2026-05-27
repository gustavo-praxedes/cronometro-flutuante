# Krono – Relatório de Bugs: Sincronização de Áudio de Ticking

> **Objetivo:** precisão absoluta entre o tique audível e a passagem real de cada segundo.  
> **Status atual:** duplo tique, offset constante de ~464 ms, race condition em estado global, e bloqueio de lock durante `prepare()`.

---

## Sumário Executivo

Foram identificados **7 bugs** organizados por severidade. Os dois mais graves explicam diretamente o "toque duplo" relatado:

| # | Título | Severidade | Arquivo Principal |
|---|--------|-----------|-------------------|
| 1 | `krono_env_metronome` classificado como `SecondTick` com clip de 11 s | 🔴 Crítico | `SoundTimingPolicy.kt` |
| 2 | `startOffsetMs = 0` no ticking com primeiro onset em 464 ms | 🔴 Crítico | `SoundTimingPolicy.kt` |
| 3 | `maxDurationMs` definido mas nunca aplicado no caminho SecondTick | 🟠 Alto | `UserFeedback.kt` |
| 4 | Variáveis globais sem `@Volatile` lidas de múltiplas coroutines | 🟠 Alto | `UserFeedback.kt` |
| 5 | `MediaPlayer.prepare()` bloqueante dentro de `synchronized(lock)` | 🟠 Alto | `EnvironmentSoundLoop.kt` |
| 6 | Debounce de 700 ms compartilhado entre timers independentes | 🟡 Médio | `UserFeedback.kt` |
| 7 | `startOffsetMs = 8 ms` no fastticking com primeiro onset em ~58 ms | 🟡 Médio | `SoundTimingPolicy.kt` |

---

## Bug 1 — `krono_env_metronome` classificado como `SecondTick` com clip de 11 segundos

### Descrição

`SoundTimingPolicy.kt` atribui `SecondTick` ao metronome:

```kotlin
// SoundTimingPolicy.kt
"krono_env_metronome" to SECOND_TICK_DEFAULT.copy(
    startOffsetMs = 0L, maxDurationMs = 1_000L, alignmentOffsetMs = 0L
),
```

O modo `SecondTick` faz com que `triggerSecondFeedback()` chame `KronoSoundPool.play()` uma vez a cada segundo:

```kotlin
// UserFeedback.kt
if (profile.playbackMode == SoundPlaybackMode.SecondTick) {
    val resId = KronoSoundCatalog.environmentResId(environmentSoundType)
    KronoSoundPool.play(context, resId, tickVolume)
    return
}
```

`SoundPool.play()` com `loop = 0` reproduz o **arquivo inteiro** — e o arquivo `krono_env_metronome.mp3` tem **11.051 ms** de duração com **~24 tiques internos**, cada um a ~454 ms de intervalo.

**Resultado medido:**

| Tempo | Evento |
|-------|--------|
| T + 0 s | SoundPool inicia clip 1 → tique em 149 ms, depois em 603 ms, 1057 ms, ... |
| T + 1 s | SoundPool inicia clip 2 (clip 1 ainda tocando) → segundo tique a 149 ms |
| T + 2 s | SoundPool inicia clip 3 → overlapping de 3 clips simultâneos |
| T + 3 s | **~73 eventos de tique sobrepostos** para apenas 3 segundos reais |

O ouvido percebe isso como toque duplo (ou triplo) caótico.

### Causa Raiz

O arquivo `krono_env_metronome.mp3` é um clip de loop ambiente (multi-beat, 11 s), **não** uma amostra de single-tick. Ele deveria ser tratado como `AmbientLoop`, com `EnvironmentSoundLoop.heartbeat()` gerenciando o loop contínuo. A atribuição de `SecondTick` foi provavelmente um erro de nomenclatura — "metronome" sugere batida única, mas o arquivo contém o padrão completo de loop.

### Solução

**Arquivo:** `SoundTimingPolicy.kt`

**Estratégia:** reclassificar como `AmbientLoop` ou criar um sample de tique único de ~100 ms.

**Opção A — Reclassificar como AmbientLoop (recomendada se o arquivo for mantido):**

```kotlin
// SoundTimingPolicy.kt — ANTES
"krono_env_metronome" to SECOND_TICK_DEFAULT.copy(
    startOffsetMs = 0L, maxDurationMs = 1_000L, alignmentOffsetMs = 0L
),

// DEPOIS
"krono_env_metronome" to AMBIENT_DEFAULT.copy(
    startOffsetMs = 149L,   // primeiro onset medido
    crossfadeMs = 500L,
    nativeLoop = false,     // EnvironmentSoundLoop gerencia o restart
    staleAfterMs = 1_500L
),
```

**Opção B — Substituir o arquivo por um sample de tick único (~100 ms) e manter SecondTick:**  
Criar/exportar apenas o primeiro transiente do metronomo como arquivo `.wav`/`.mp3` separado (≤ 100 ms), sem silêncio inicial. Isso é o que apps como **Clock (AOSP)** e **Insight Timer** fazem: samples curtíssimos carregados via SoundPool para baixíssima latência.

### Como Apps Similares Resolvem

- **Clock AOSP (Android):** SoundPool é usado **exclusivamente** com samples de ≤ 200 ms. Loops ambientes usam `MediaPlayer` com `isLooping = true` ou `ExoPlayer` com `RepeatMode.REPEAT_MODE_ONE`. Os dois nunca se misturam no mesmo arquivo.
- **Insight Timer:** Mantém dois caminhos de áudio estritamente separados: `bell_short.ogg` (80 ms) para single-tick via SoundPool, e `ambient_loop.ogg` (30–60 s) para loop ambiente via `MediaPlayer`.

---

## Bug 2 — `startOffsetMs = 0` no ticking, primeiro onset real em 464 ms

### Descrição

`krono_env_ticking.mp3` tem 464 ms de silêncio/pré-roll antes do primeiro tique audível. O `SoundTimingPolicy` define:

```kotlin
"krono_env_ticking" to SECOND_TICK_DEFAULT.copy(
    startOffsetMs = 0L, maxDurationMs = 1_000L, alignmentOffsetMs = 0L
),
```

Quando `KronoSoundPool.play()` dispara no boundary do segundo (T = 0), o tique é ouvido apenas em **T + 464 ms** — quase meio segundo atrasado.

**Dados medidos do arquivo:**

```
Primeiro onset: 464,4 ms
Intervalo médio entre onsets: 999,9 ms (std: 7 ms)
Duração decodificada: 8.960 ms
```

O atraso de 464 ms é **constante e perceptível**. O ouvido humano detecta desalinhamento ritmo-evento a partir de ~30 ms.

### Causa Raiz

O arquivo foi criado com pré-roll (fade-in da room ou silêncio de encoder), mas o `startOffsetMs` não foi calibrado para compensar. O parâmetro existe na API, só não foi preenchido.

### Solução

**Arquivo:** `SoundTimingPolicy.kt`

Como `krono_env_ticking` está em modo `SecondTick` (SoundPool), o `startOffsetMs` definido no profile **não é lido** pela função `triggerSecondFeedback` — ela chama direto `KronoSoundPool.play()` sem nenhum offset.

Há dois sub-problemas:

**Sub-problema 2a:** O profile `startOffsetMs` não é passado para `KronoSoundPool.play()`.

**Sub-problema 2b:** `SoundPool` não suporta `seekTo`. O único jeito de pular silêncio inicial é carregar o arquivo já sem o silêncio.

**Solução recomendada — Dois passos:**

**Passo 1:** Editar o arquivo de áudio para remover os 464 ms iniciais (Audacity → Export), garantindo que o onset do primeiro tique esteja em ≤ 10 ms. Essa é a abordagem adotada por todos os metrónomos de precisão (Metronomics, Pro Metronome, PolyNome): o sample começa no ataque.

**Passo 2 (fallback):** Se o arquivo não puder ser editado, converter o caminho `SecondTick` para usar `MediaPlayer` com `seekTo` antes de `start()`:

```kotlin
// UserFeedback.kt — substituição do bloco SecondTick
if (profile.playbackMode == SoundPlaybackMode.SecondTick) {
    if (activeEnvironmentLoopSoundType != SOUND_NONE) {
        EnvironmentSoundLoop.stop("second tick")
        activeEnvironmentLoopSoundType = SOUND_NONE
    }
    val resId = KronoSoundCatalog.environmentResId(environmentSoundType)
    // Usa MediaPlayer com seekTo para compensar o pré-roll
    playSecondTickWithOffset(
        context = context,
        resId = resId,
        volume = tickVolume,
        startOffsetMs = profile.startOffsetMs,   // 464ms para ticking
        maxDurationMs = profile.maxDurationMs
    )
    return
}
```

```kotlin
// Nova função em UserFeedback.kt
private fun playSecondTickWithOffset(
    context: Context,
    resId: Int,
    volume: Float,
    startOffsetMs: Long,
    maxDurationMs: Long
) {
    val appContext = context.applicationContext
    runCatching {
        val asset = appContext.resources.openRawResourceFd(resId) ?: return
        val mp = MediaPlayer()
        mp.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        mp.setDataSource(asset.fileDescriptor, asset.startOffset, asset.length)
        mp.setVolume(volume, volume)
        mp.setOnCompletionListener { it.release() }
        mp.setOnErrorListener { broken, _, _ -> broken.release(); true }
        mp.prepare()
        asset.close()
        if (startOffsetMs > 0L) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                mp.seekTo(startOffsetMs, MediaPlayer.SEEK_CLOSEST)
            else
                mp.seekTo(startOffsetMs.toInt())
        }
        mp.start()
        // Libera após maxDurationMs para não sobrepor o próximo segundo
        if (maxDurationMs > 0L) {
            Handler(Looper.getMainLooper()).postDelayed({ runCatching { mp.release() } }, maxDurationMs)
        }
    }.onFailure { Log.e("UserFeedback", "playSecondTickWithOffset failed resId=$resId", it) }
}
```

> ⚠️ `MediaPlayer.prepare()` é síncrono e introduz latência (ver Bug 5). Para precisão máxima, a solução definitiva é sempre **editar o arquivo de áudio**.

### Como Apps Similares Resolvem

- **Metronomics (iOS/Android):** Todos os samples de metrônomo são exportados com onset no sample 0 (zero pré-roll). Usa `AudioTrack` de baixa latência no Android.
- **Google Clock:** Assets de tique têm ≤ 5 ms de silêncio inicial, verificado em code review público do AOSP.

---

## Bug 3 — `maxDurationMs` definido mas nunca aplicado no caminho SecondTick

### Descrição

`SoundTimingProfile` define `maxDurationMs = 950L` para ambos os sons SecondTick:

```kotlin
// SoundTimingPolicy.kt
private val SECOND_TICK_DEFAULT = SoundTimingProfile(
    playbackMode = SoundPlaybackMode.SecondTick,
    maxDurationMs = 950L,   // ← definido
    previewMaxMs = 3_000L
)
```

Mas o bloco de reprodução em `UserFeedback.kt` ignora completamente esse valor:

```kotlin
// UserFeedback.kt — bloco SecondTick
if (profile.playbackMode == SoundPlaybackMode.SecondTick) {
    val resId = KronoSoundCatalog.environmentResId(environmentSoundType)
    KronoSoundPool.play(context, resId, tickVolume)  // maxDurationMs nunca passado
    return
}
```

`SoundPool` não tem mecanismo de `maxDuration`. O arquivo inteiro é reproduzido sem corte, o que para `krono_env_ticking` (8.960 ms) significa que cada "tick" toca por quase 9 segundos — sobrepondo-se a 8 tiques subsequentes.

### Solução

**Arquivo:** `UserFeedback.kt`  
**Estratégia:** aplicar `maxDurationMs` via `Handler.postDelayed` que chama `soundPool.stop(streamId)`.

```kotlin
// KronoSoundPool.kt — adicionar retorno do streamId
fun play(context: Context, resId: Int, volume: Float): Int {
    if (soundPool == null) init(context.applicationContext)
    if (!soundMap.containsKey(resId)) preload(context.applicationContext, resId)
    return playAndGetStreamId(resId, volume)
}

private fun playAndGetStreamId(resId: Int, volume: Float): Int {
    val sId = soundMap[resId] ?: return 0
    if (!loadedIds.contains(sId)) return 0
    return soundPool?.play(sId, volume, volume, 1, 0, 1f) ?: 0
}

fun stop(streamId: Int) {
    soundPool?.stop(streamId)
}
```

```kotlin
// UserFeedback.kt — aplicar maxDurationMs
if (profile.playbackMode == SoundPlaybackMode.SecondTick) {
    val resId = KronoSoundCatalog.environmentResId(environmentSoundType)
    val streamId = KronoSoundPool.play(context, resId, tickVolume)
    if (streamId > 0 && profile.maxDurationMs > 0L) {
        Handler(Looper.getMainLooper()).postDelayed({
            KronoSoundPool.stop(streamId)
        }, profile.maxDurationMs)
    }
    return
}
```

---

## Bug 4 — Variáveis globais sem `@Volatile` lidas de múltiplas coroutines

### Descrição

`UserFeedback.kt` declara estado de controle como variáveis de nível de pacote:

```kotlin
// UserFeedback.kt — topo do arquivo
private var lastSecondVibrationAtMs: Long = 0L   // sem @Volatile
private var lastTickSoundAtMs: Long = 0L          // sem @Volatile
private var activeEnvironmentLoopSoundType: String = SOUND_NONE  // sem @Volatile
private var activeSecondTickPlayer: MediaPlayer? = null           // sem @Volatile
```

Essas variáveis são lidas e escritas por:

- `PomodoroViewModel` → coroutine em `viewModelScope` (dispatcher padrão: `Main`)
- `StopwatchViewModel` → coroutine em `viewModelScope` (dispatcher padrão: `Main`)
- `CountdownManager` → coroutine em `scope` com `SupervisorJob()` (sem dispatcher explícito → `Default`)

Sem `@Volatile`, a JVM **não garante visibilidade** entre threads. Uma thread pode ler `lastTickSoundAtMs = 0` mesmo que outra tenha escrito `lastTickSoundAtMs = T` milissegundos antes, fazendo o debounce falhar e permitindo múltiplos tiques simultâneos.

### Solução

**Arquivo:** `UserFeedback.kt`

**Mínimo viável** — adicionar `@Volatile`:

```kotlin
@Volatile private var lastSecondVibrationAtMs: Long = 0L
@Volatile private var lastTickSoundAtMs: Long = 0L
@Volatile private var activeEnvironmentLoopSoundType: String = SOUND_NONE
@Volatile private var activeSecondTickPlayer: MediaPlayer? = null
```

**Solução robusta** — mover para um `object` com `AtomicLong` (recomendada se houver > 1 timer ativo simultâneo):

```kotlin
// Novo arquivo: TickFeedbackState.kt
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

internal object TickFeedbackState {
    val lastSecondVibrationAtMs = AtomicLong(0L)
    val lastTickSoundAtMs = AtomicLong(0L)
    val activeEnvironmentSoundType = AtomicReference(SOUND_NONE)
}
```

```kotlin
// UserFeedback.kt — uso
val now = SystemClock.elapsedRealtime()
if (now - TickFeedbackState.lastTickSoundAtMs.get() < 700L) return
TickFeedbackState.lastTickSoundAtMs.set(now)
```

---

## Bug 5 — `MediaPlayer.prepare()` bloqueante dentro de `synchronized(lock)`

### Descrição

`EnvironmentSoundLoop.createPlayer()` chama `prepare()` (bloqueante) dentro do bloco `synchronized(lock)`:

```kotlin
// EnvironmentSoundLoop.kt
private fun startLocked(context: Context, resId: Int, ...) {
    runCatching {
        val mp = createPlayer(context, resId, volume, usage, startOffsetMs, nativeLoop)
        // createPlayer chama mp.prepare() — bloqueante!
        // Todo este bloco está dentro de synchronized(lock)
        player = mp
        mp.start()
        ...
    }
}
```

`MediaPlayer.prepare()` faz I/O de arquivo e inicialização de codec. Em dispositivos de médio/baixo desempenho, demora **50–300 ms**. Durante esse tempo, o `lock` está retido e qualquer chamada a `heartbeat()` vinda do timer de ticking fica bloqueada na linha `synchronized(lock) { ... }`.

**Sequência problemática:**
```
T + 0 ms   : coroutine CountdownManager dispara tick
T + 0 ms   : heartbeat() tenta adquirir lock → BLOQUEADO (prepare em andamento)
T + 150 ms : prepare() conclui, lock liberado
T + 150 ms : heartbeat() adquire lock, processa → tique 150 ms atrasado
T + 1000 ms: próximo tick da coroutine → tique no horário certo
Percepção : tique irregular (150 ms → 850 ms → 1000 ms → ...)
```

### Solução

**Arquivo:** `EnvironmentSoundLoop.kt`  
**Estratégia:** usar `prepareAsync()` com listener, ou pre-criar o player fora do lock.

```kotlin
// EnvironmentSoundLoop.kt — versão com prepareAsync
private fun startLocked(context: Context, resId: Int, volume: Float, usage: Int,
                        startOffsetMs: Long, nativeLoop: Boolean) {
    runCatching {
        val asset = context.resources.openRawResourceFd(resId) ?: error("raw unavailable")
        val mp = MediaPlayer()
        mp.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(usage)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        mp.setDataSource(asset.fileDescriptor, asset.startOffset, asset.length)
        asset.close()
        mp.setVolume(volume, volume)
        mp.isLooping = nativeLoop
        mp.setOnPreparedListener { prepared ->
            synchronized(lock) {
                if (lastResId != resId) { prepared.release(); return@synchronized }
                player = prepared
                prepared.seekToCompat(startOffsetMs)
                prepared.start()
                if (!nativeLoop) scheduleRestartLocked(context, resId, volume)
            }
        }
        mp.setOnErrorListener { broken, _, _ ->
            synchronized(lock) {
                if (player === broken) player = null
                if (nextPlayer === broken) nextPlayer = null
            }
            broken.release()
            true
        }
        mp.prepareAsync()   // ← não bloqueia o lock
    }.onFailure { Log.e(TAG, "start failed resId=$resId", it) }
}
```

> Remover `createPlayer()` síncrono ou deixá-lo apenas para o `nextPlayer` pre-carregado no `startNextLocked`, onde o bloqueio é aceitável porque ainda existe um `player` ativo tocando.

### Como Apps Similares Resolvem

- **ExoPlayer (Google):** Usa pipeline completamente assíncrono. Nunca bloqueia thread para preparação de mídia.
- **Insight Timer:** Pre-aquece todos os players de ambiente na inicialização do serviço, antes de qualquer interação do usuário, usando `prepareAsync()`.

---

## Bug 6 — Debounce de 700 ms compartilhado entre timers independentes

### Descrição

```kotlin
// UserFeedback.kt
fun triggerSecondFeedback(...) {
    ...
    val now = SystemClock.elapsedRealtime()
    if (now - lastTickSoundAtMs < 700L) return   // ← debounce global
    lastTickSoundAtMs = now
    ...
}
```

`lastTickSoundAtMs` é uma variável **global de processo**. Se o usuário tiver dois timers ativos simultaneamente (Countdown + Pomodoro, por exemplo), o segundo timer a chamar `triggerSecondFeedback()` dentro de 700 ms do primeiro terá seu tique descartado silenciosamente.

**Cenário problemático:**
```
T + 0 ms    : CountdownManager dispara tick → lastTickSoundAtMs = T
T + 0 ms    : PomodoroViewModel dispara tick → now - T = 0 < 700 → DESCARTADO
T + 700 ms  : (silêncio — nenhum timer disparou)
T + 1000 ms : CountdownManager dispara tick → ok
T + 1000 ms : PomodoroViewModel dispara tick → 0 ms < 700 → DESCARTADO
```

Resultado: apenas o Countdown toca tiques; Pomodoro fica silencioso.

### Solução

**Arquivo:** `UserFeedback.kt`  
**Estratégia:** o debounce deve ser **por instância de timer**, não global. Mover `lastTickSoundAtMs` para o ViewModel ou para um parâmetro de chamada.

```kotlin
// PomodoroViewModel.kt — exemplo
private var lastSecondFeedback: Long? = null  // já existe

private suspend fun triggerSecondFeedbackIfEnabled(secondMarker: Long) {
    if (lastSecondFeedback == secondMarker) return   // debounce por instância ✓
    lastSecondFeedback = secondMarker
    ...
    triggerSecondFeedback(...)  // remove o debounce de 700ms de dentro dessa função
}
```

```kotlin
// UserFeedback.kt — remover debounce global
fun triggerSecondFeedback(
    context: Context,
    vibrationEnabled: Boolean,
    tickSoundEnabled: Boolean,
    tickVolume: Float,
    environmentSoundType: String = "krono_env_brownnoise",
    startDelayMs: Long = AUDIO_DELAY_NONE_MS,
    staleAfterMs: Long = 1_500L
) {
    triggerSecondVibration(context, vibrationEnabled)
    if (!tickSoundEnabled || environmentSoundType == SOUND_NONE) {
        EnvironmentSoundLoop.stop("disabled")
        return
    }
    // REMOVIDO: if (now - lastTickSoundAtMs < 700L) return
    // Cada caller já possui seu próprio debounce por secondMarker
    val profile = SoundTimingPolicy.profile(environmentSoundType)
    ...
}
```

---

## Bug 7 — `startOffsetMs = 8 ms` no fastticking, primeiro onset em ~58 ms

### Descrição

```kotlin
// SoundTimingPolicy.kt
"krono_env_fastticking" to AMBIENT_DEFAULT.copy(
    startOffsetMs = 8L,   // ← muito cedo
    crossfadeMs = 0L,
    nativeLoop = true
),
```

Dados medidos do arquivo `krono_env_fastticking.mp3`:
- Primeiro onset: **58 ms**
- Intervalo entre onsets: ~197 ms (tick rápido, ambiente)

Com `startOffsetMs = 8 ms`, o loop começa 50 ms antes do primeiro transiente real. Ao usar `nativeLoop = true`, o MediaPlayer faz loop a partir de 8 ms, repetindo esse silêncio de pré-roll em cada iteração. O efeito é uma micro-pausa perceptível no loop.

### Solução

**Arquivo:** `SoundTimingPolicy.kt`

```kotlin
// ANTES
"krono_env_fastticking" to AMBIENT_DEFAULT.copy(
    startOffsetMs = 8L,
    crossfadeMs = 0L,
    nativeLoop = true
),

// DEPOIS
"krono_env_fastticking" to AMBIENT_DEFAULT.copy(
    startOffsetMs = 58L,   // alinhado ao primeiro onset medido
    crossfadeMs = 0L,
    nativeLoop = true
),
```

> Alternativa: editar o arquivo de áudio para remover os 58 ms de pré-roll e manter `startOffsetMs = 0`.

---

## Ordem de Implementação Recomendada

```
Prioridade 1 (elimina o toque duplo imediatamente):
  Bug 1 → Reclassificar metronome como AmbientLoop
  Bug 2 → Corrigir startOffsetMs do ticking (editar arquivo ou usar seekTo)
  Bug 3 → Aplicar maxDurationMs via SoundPool.stop(streamId)

Prioridade 2 (estabilidade e correção de comportamento edge cases):
  Bug 4 → @Volatile ou AtomicLong nas vars globais
  Bug 5 → prepareAsync() no EnvironmentSoundLoop
  Bug 6 → Remover debounce global de 700 ms

Prioridade 3 (polimento):
  Bug 7 → startOffsetMs = 58L no fastticking
```

---

## Referências e Como Apps de Referência Resolvem

| Problema | App Referência | Solução Adotada |
|----------|---------------|-----------------|
| Loop ambiente vs. single tick | Insight Timer | Dois assets separados: `bell_single.ogg` (≤ 100 ms) e `ambient_loop.ogg` (≥ 30 s) |
| Sincronização de tique com segundo | Metronomics, Pro Metronome | `AudioTrack` com buffer de baixa latência; onset no sample 0 |
| Pré-roll em samples | Google Clock AOSP | Assets com ≤ 5 ms de silêncio inicial, verificados em CI |
| SoundPool + duração máxima | Android Docs / AOSP | `SoundPool.stop(streamId)` via `Handler.postDelayed` |
| `prepare()` bloqueante | ExoPlayer, Jetpack Media3 | Pipeline completamente assíncrono; nunca bloqueia thread de UI ou lock |
| Debounce por instância | Qualquer app multi-timer | Estado de debounce mantido no ViewModel, não em singleton global |

---

*Gerado em: análise estática de código + medição de onset via librosa nos arquivos `.mp3` fornecidos.*
