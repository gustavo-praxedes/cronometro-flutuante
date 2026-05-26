# FASE — Sincronização de Áudio e Precisão do Timer

### Problema
1. **Latência de Áudio:** O `MediaPlayer` recria o buffer a cada segundo, gerando atrasos variáveis (50ms - 200ms) dependendo da carga do sistema.
2. **Drift do Timer:** O `delay(1000L)` nas Coroutines não é compensado. Se o código de processamento levar 20ms, o segundo real dura 1020ms, acumulando erro ao longo do tempo.

---

### Passo 1 — Criar KronoSoundPool (Baixa Latência)
- [ ] Criar `core/audio/KronoSoundPool.kt`
  - Usar `SoundPool` para manter áudios decodificados em PCM na memória.
  - Implementar `init(context)` para pré-carregar sons de tick e metrônomo.
  - Implementar `play(resId, volume)` para disparo instantâneo.
- [ ] Inicializar `KronoSoundPool` no `KronoApp` ou `MainService`.

---

### Passo 2 — Migrar UserFeedback para SoundPool
- [ ] Alterar `UserFeedback.kt` -> `playEnvironmentSecondTick`
  - Substituir a criação de `MediaPlayer` por chamadas ao `KronoSoundPool.play()`.
  - Manter suporte a `startOffsetMs` (mesmo que SoundPool lide melhor com isso, o corte de silêncio inicial é útil).

---

### Passo 3 — Implementar Loop de Precisão (Compensação de Drift)
- [ ] Alterar `CountdownManager.kt` -> `play()`
  - Substituir `delay(1000L)` por um cálculo de "Próximo Tick Absoluto".
  - Exemplo:
    ```kotlin
    var nextTickTime = SystemClock.elapsedRealtime() + 1000L
    while (isRunning) {
        val delayTime = nextTickTime - SystemClock.elapsedRealtime()
        if (delayTime > 0) delay(delayTime)
        
        // Executar tick...
        
        nextTickTime += 1000L
    }
    ```

---

### Passo 4 — Otimização de Ordem de Execução
- [ ] Garantir que o áudio seja disparado **antes** da atualização da UI/ViewModel no loop, para que a percepção do usuário seja de um "clique" instantâneo na mudança do número.

**Verificar antes do commit:**
- [ ] Compilar sem erros.
- [ ] Sincronia perfeita entre som e troca de dígito em testes de longa duração (10+ min).

**Aguardar aprovação → commit: `perf: improve audio sync and timer precision using SoundPool and drift compensation`**
