# Simplificação da Reprodução de Sons

## Escopo

Apenas código. Nenhuma alteração de UI.

---

## 1. Novo `SoundTimingProfile`

```kotlin
enum class SoundPlaybackMode {
    AmbientLoop,
    SecondTick
}

data class SoundTimingProfile(
    val playbackMode: SoundPlaybackMode = SoundPlaybackMode.AmbientLoop,

    // Compartilhado
    val startOffsetMs: Long = 0L,
    val endTrimMs: Long = 0L,

    // AmbientLoop
    val crossfadeMs: Long = 1_500L,
    val staleAfterMs: Long = 1_500L,

    // SecondTick
    val maxDurationMs: Long = 950L,
    val alignmentOffsetMs: Long = 0L,

    // Preview
    val previewMaxMs: Long = 5_000L,

    // Beep/Alarm (legado)
    val startDelayMs: Long = 0L,
    val maxLifetimeMs: Long = 0L
)
```

### Variáveis novas

| Variável | Tipo | Default | Descrição |
|---|---|---|---|
| `crossfadeMs` | Long | 1500 | Customizável por som no mapa PROFILES |
| `maxDurationMs` | Long | 950 | Duração máx SecondTick. Configurável por som |
| `alignmentOffsetMs` | Long | 0 | Compensação de latência MediaPlayer. Negativo=antecipa |
| `previewMaxMs` | Long | 5000 | Corta preview após X ms. Evita tocar arquivo inteiro |

### Variáveis removidas

Nenhuma variável removida — manter retrocompatibilidade com beep/alarm.

---

## 2. Novo `SoundTimingPolicy`

```kotlin
object SoundTimingPolicy {
    private val DEFAULT = SoundTimingProfile()

    private val AMBIENT_DEFAULT = SoundTimingProfile(
        playbackMode = SoundPlaybackMode.AmbientLoop,
        crossfadeMs = 1_500L,
        staleAfterMs = 1_500L,
        previewMaxMs = 5_000L
    )

    private val SECOND_TICK_DEFAULT = SoundTimingProfile(
        playbackMode = SoundPlaybackMode.SecondTick,
        maxDurationMs = 950L,
        previewMaxMs = 3_000L
    )

    private val PROFILES = mapOf(
        // Ambient — apenas os que divergem do AMBIENT_DEFAULT
        "ENVIRONMENT_04" to AMBIENT_DEFAULT.copy(crossfadeMs = 2_000L),
        "ENVIRONMENT_08" to AMBIENT_DEFAULT.copy(crossfadeMs = 1_000L),

        // SecondTick
        "ENVIRONMENT_13" to SECOND_TICK_DEFAULT.copy(
            startOffsetMs = 0L,
            maxDurationMs = 950L,
            alignmentOffsetMs = 0L
        )
    )

    fun profile(soundType: String): SoundTimingProfile = when {
        soundType.isBlank() || soundType == SOUND_NONE -> DEFAULT
        soundType in PROFILES -> PROFILES[soundType]!!
        soundType.startsWith("ENVIRONMENT_") -> AMBIENT_DEFAULT
        else -> DEFAULT
    }
}
```

**Diferenças do atual:**
- `crossfadeMs` customizável por mapeamento (não fixo)
- `maxDurationMs` configurável por som SecondTick
- Perfis simplificados — sem `startOffsetMs`/`endTrimMs` redundantes
- Valores de ENVIRONMENT_04/08 são exemplos — ajustar após testes auditivos

---

## 3. Preview (SoundPreviewPlayer)

**Comportamento:** Toca 1x sem loop. Corta após `previewMaxMs`.

**Alterações em `SoundPreviewPlayer.kt`:**
- Remover parâmetro `looping` → sempre `false`
- Renomear `maxLifetimeMs` interno para usar `previewMaxMs` do profile
- Manter `startOffsetMs` (pode ser útil para pular silêncio)
- `stopLocked("restart")` já garante que som anterior para ao clicar outro

**Alterações em `UserFeedback.kt`:**
- `previewEnvironmentSound()` → passar `previewMaxMs` do profile como limite
- `previewPlayPauseSound()` → sem mudanças (beep é curto)
- `previewPomodoroNotificationSound()` → sem mudanças (alarm é curto)
- `previewAppNotificationSound()` → sem mudanças

---

## 4. AmbientLoop (EnvironmentSoundLoop)

**Comportamento:** Loop contínuo. Crossfade customizado por som via PROFILES.

**Alterações em `EnvironmentSoundLoop.kt`:**
- Sem mudanças estruturais — já suporta crossfade variável
- Valores de `crossfadeMs` vêm do profile (já é assim)

**Alterações em `UserFeedback.kt` → `triggerSecondFeedback()`:**
- `startOffsetMs`, `endTrimMs`, `crossfadeMs` continuam vindo do profile
- Sem mudanças funcionais — crossfade agora default 1500 ao invés de 0

---

## 5. SecondTick

**Comportamento:** Reproduz sincronizado com segundo. Sem loop. Sem crossfade.

**Alterações em `UserFeedback.kt` → `playEnvironmentSecondTick()`:**
- Usar `maxDurationMs` do profile (substituir `maxLifetimeMs`)
- Aplicar `endTrimMs` para cortar cauda
- Usar `alignmentOffsetMs` para ajustar momento de disparo:
  ```kotlin
  val fireDelayMs = profile.alignmentOffsetMs.coerceAtLeast(0L)
  // Se negativo, antecipar via Handler scheduling
  ```

**Alterações em `triggerSecondFeedback()`:**
- Quando `SecondTick`, usar `alignmentOffsetMs` e `maxDurationMs` do profile

---

## 6. Beep/Alarm

**Sem alterações.** `startDelayMs` e `maxLifetimeMs` mantidos.

---

## Ordem de Implementação

1. `SoundTimingPolicy.kt` — novo profile + policy
2. `SoundPreviewPlayer.kt` — remover `looping`, usar `previewMaxMs`
3. `UserFeedback.kt` — ajustar funções de preview, SecondTick, ambiente
4. `FeedbackManager.kt` — ajustar nomes de campos se necessário
5. Verificar compilação nos consumers: `AppearancePanel.kt`, `PomodoroSettings.kt`, `PomodoroScreen.kt`, `CountdownScreen.kt`, `StopwatchViewModel.kt`, `AppNavigation.kt`

---

## Arquivos Impactados

| Arquivo | Impacto |
|---|---|
| `SoundTimingPolicy.kt` | ALTO — reescrever |
| `SoundPreviewPlayer.kt` | MÉDIO — remover looping, usar previewMaxMs |
| `UserFeedback.kt` | MÉDIO — ajustar SecondTick e preview |
| `FeedbackManager.kt` | BAIXO — ajustar campos |
| `EnvironmentSoundLoop.kt` | NENHUM |
| `AppearancePanel.kt` | BAIXO — ajustar chamadas preview |
| `PomodoroSettings.kt` | BAIXO — verificar |
| `PomodoroScreen.kt` | BAIXO — verificar |
| `CountdownScreen.kt` | BAIXO — verificar |
| `StopwatchViewModel.kt` | BAIXO — verificar |
| `AppNavigation.kt` | BAIXO — verificar |
