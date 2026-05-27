# Fase 17 - Ajustes pontuais de UI, estado, overlays e audio

> Status: especificacao. Nao implementar sem autorizacao do dev.
> Build: nao executar. Commit: nao executar.
> Objetivo: corrigir comportamentos pontuais preservando design atual, arquitetura existente e risco baixo por fase.

## Regras de execucao

- Alteracoes devem ser pequenas, rastreaveis e isoladas por fase.
- Nao refatorar legado junto da correcao, salvo autorizacao explicita.
- Codigo legado, acoplado ou sujo encontrado durante a implementacao deve ser relatado antes de corrigir.
- Nao compilar, nao rodar testes que disparem build, nao criar commit sem autorizacao.
- Validacao inicial deve ser por leitura, revisao local e checklist manual.
- Toda label editavel pelo usuario deve parar em 50 caracteres, sem contador visual.

## Fase 1 - Estado global, Aparencia e ajustes de configuracao

### Escopo

- Corrigir gatilhos fantasma de vibracao.
- Adicionar volume de notificacao.
- Ajustar fonte padrao Chivo Mono.
- Aplicar limite de 50 caracteres em labels editaveis.
- Mover opcoes condicionais de Pomodoro para Comportamento.
- Remover versao do card de atualizacoes e fixar versao no rodape esquerdo de Configuracoes.

### Arquivos provaveis

- `app/src/main/java/com/krono/app/core/data/OverlayConfig.kt`
- `app/src/main/java/com/krono/app/core/data/OverlayDataStore.kt`
- `app/src/main/java/com/krono/app/app/AppNavigation.kt`
- `app/src/main/java/com/krono/app/core/service/OverlayManager.kt`
- `app/src/main/java/com/krono/app/feature/pomodoro/PomodoroSettings.kt`
- `app/src/main/java/com/krono/app/core/ui/settings/AppearancePanel.kt`
- `app/src/main/java/com/krono/app/core/ui/settings/BehaviorPanel.kt`
- `app/src/main/java/com/krono/app/core/ui/settings/SettingsMenuPanel.kt`
- `app/src/main/java/com/krono/app/core/ui/settings/SettingsScreen.kt`
- `app/src/main/java/com/krono/app/core/ui/theme/KronoType.kt`
- `app/src/main/java/com/krono/app/core/ui/theme/KronoFontOption.kt`
- `app/src/main/java/com/krono/app/core/ui/components/FontSelector.kt`
- `app/src/main/res/font/chivo_mono_regular.ttf` ou familia equivalente
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-pt-rBR/strings.xml`

### Passos

1. Trocar `collectAsState(initial = OverlayConfig())` que pode escrever preferencia por estado hidratado nulo antes de qualquer `updateConfig`.
2. Auditar `LaunchedEffect` que grava DataStore ao abrir rota/tela:
   - `AppNavigation` atualiza `activeToolId`.
   - `PomodoroSettings` normaliza preset e semeia presets.
   - Overlays alteram configuracoes rapidas com config coletado.
3. Corrigir default fantasma:
   - `OverlayConfig.playPauseVibrationEnabled` deve refletir default de produto decidido.
   - Recomendado: default `false` para nao habilitar vibracao sem clique.
   - `preferencesToConfig` nao deve persistir default por navegacao.
4. Adicionar `appNotificationVolume: Float` em `OverlayConfig` e chave no DataStore.
5. Em Aparencia, abaixo de `Som notificacao`, adicionar `AppearanceSlider`:
   - Label: `Volume notificação`.
   - Range: `0f..1f`.
   - Enabled apenas quando `appNotificationSoundType != SOUND_NONE`.
   - Preview deve usar `config.appNotificationVolume`, nao `playPauseVolume`.
6. Embutir Chivo Mono:
   - Adicionar arquivo em `res/font`.
   - `CHIVO_MONO` usa `FontFamily(Font(R.font...))`.
   - `selectedFont` e `overlayFontFamily` seguem `CHIVO_MONO` como padrao.
7. Fontes remotas:
   - Manter outras fontes como baixaveis.
   - Nao listar fonte remota que nao esteja disponivel na sessao atual.
   - Confirmado: Chivo Mono embutida sempre aparece; outras fontes aparecem somente apos confirmacao de disponibilidade.
   - Criar detector de disponibilidade por fonte antes de montar `FontSelector`.
8. Aplicar limite 50 em labels editaveis:
   - Preset.
   - Card/preset phase.
   - Grupo.
   - Countdown card descricao.
   - Busca/settings e relato de bug nao entram, salvo decisao do dev.
9. Remover contador visual de label onde existir.
10. Mover `Não perturbe` do Pomodoro para `BehaviorPanel`:
    - Fica abaixo de `Modo Foco`.
    - Aparece somente quando `focusModeEnabled = true`.
    - Visual aninhado: recuo menor, sem novo quadro.
11. Aplicar mesmo padrao condicional em `Abrir overlay direto`:
    - Toggle principal em Comportamento.
    - Dropdown aninhado so aparece quando toggle ligado.
12. Adicionar versao no rodape fixo do painel esquerdo:
    - Texto discreto: `Versão x.x.x`.
    - Usar `BuildConfig.VERSION_NAME`.
    - Remover pill `Versão Atual - v...` dos cards de atualizacao.

### Validacao manual

- Abrir app limpo -> vibracoes continuam off se usuario nunca habilitou.
- Ir para Pomodoro -> nao altera vibracoes.
- Alternar Pomodoro/Timer/Stopwatch -> nenhuma preferencia muda so por navegar.
- Selecionar Som notificacao -> slider aparece/habilita corretamente.
- Testar preview notificacao em volumes 0%, 50%, 100%.
- Fonte padrao visivel como Chivo Mono sem rede.
- Fontes nao baixadas nao aparecem na lista.
- Labels param em 50 caracteres, sem contador.
- Modo Foco off -> Nao perturbe invisivel.
- Modo Foco on -> Nao perturbe aparece aninhado.
- Versao aparece somente no rodape do menu de Configuracoes.

### Legado/sujo a reportar

- `OverlayDataStore.writeConfig` grava o config inteiro em toda mudanca. Isso facilita sobrescrever preferencias reais com defaults de tela ainda nao hidratada.
- `PomodoroSettings` usa `collectAsState(initial = OverlayConfig())` e faz escrita em `LaunchedEffect`. Alto risco de gatilho fantasma.
- Varios updates usam `config.copy(...)` capturado por Composable. Para correcao pontual, preferir `updateConfig { it.copy(...) }`.

## Fase 2 - Ferramentas, overlays e acoes em tempo real

### Escopo

- Adicionar botao flag na `StopwatchScreen`.
- Corrigir `+1` no overlay de cards do Timer.
- Corrigir `+1` enquanto Timer esta rodando.
- Preservar visual: Stopwatch deve ficar como Timer/Pomodoro com 4 botoes.

### Arquivos provaveis

- `app/src/main/java/com/krono/app/feature/stopwatch/StopwatchScreen.kt`
- `app/src/main/java/com/krono/app/app/AppNavigation.kt`
- `app/src/main/java/com/krono/app/feature/stopwatch/StopwatchViewModel.kt`
- `app/src/main/java/com/krono/app/feature/countdown/CountdownOverlay.kt`
- `app/src/main/java/com/krono/app/feature/countdown/CountdownOverlayManager.kt`
- `app/src/main/java/com/krono/app/feature/countdown/CountdownManager.kt`
- `app/src/main/java/com/krono/app/feature/countdown/CountdownViewModel.kt`

### Passos

1. Stopwatch:
   - Adicionar quarto botao `Flag` (`KronoIcons.Action.Lap`) no fim da row.
   - Visual igual aos botoes pequenos de Pomodoro/Timer: 56dp tonal.
   - Confirmado: nao implementar voltas/laps nesta fase.
   - Botao fica como visual/gancho seguro, sem criar lista ou persistencia de laps.
   - Implementacao funcional do flag sera especificada em fase posterior.
2. Timer overlay:
   - Hoje `CountdownOverlayManager` mostra `+1` so quando `id == SCREEN_OVERLAY_ID`.
   - Alterar para cards criados tambem exibirem `+1`.
   - Manter `useToolColor` separado, para nao mudar cor visual de cards sem necessidade.
3. `+1` rodando:
   - `CountdownManager.play()` usa `deadlineElapsedMs` local imutavel.
   - `addOneMinute()` atualiza `remainingMsMap` e ViewModel, mas loop segue deadline antigo.
   - Mover deadline por id para estado mutavel, ou recalcular deadline ao receber `+1`.
   - Garantir que UI, overlay e notificacao usem o mesmo tempo.
4. Evitar regressao:
   - `+1` parado: soma no restante.
   - `+1` rodando: soma no deadline ativo.
   - Timer completo: `+1` limpa completed e reabre estado valido.
   - Multiplos cards: `+1` afeta somente id do overlay clicado.

### Validacao manual

- Stopwatch mostra 4 botoes alinhados.
- Timer screen overlay mostra `+1`.
- Timer card overlay mostra `+1`.
- Timer parado + `+1` soma 60s.
- Timer rodando + `+1` soma 60s sem voltar no proximo tick.
- Dois cards rodando: `+1` em um nao altera o outro.

### Legado/sujo a reportar

- Nome de rota `TIMER` aponta para stopwatch. Manter por compatibilidade nesta fase.
- `CountdownManager` mistura timer loop, overlay manager, notificacao e feedback. Refatorar exigiria autorizacao.

## Fase 3 - Sobre, atualizacoes e changelog

### Escopo

- Ajustar espacamentos e botoes dentro dos quadros.
- Reestruturar `O que há de novo`, `Apoio` e `Relatar bug` como cards recolhiveis.
- Consumir `res/raw/changelog.md` para mudancas da versao atual.
- Novo fluxo de verificar atualizacao.

### Arquivos provaveis

- `app/src/main/java/com/krono/app/core/ui/settings/AboutPanel.kt`
- `app/src/main/java/com/krono/app/core/ui/settings/BugReportPanel.kt`
- `app/src/main/java/com/krono/app/core/ui/settings/UpdatesPanel.kt`
- `app/src/main/java/com/krono/app/core/ui/settings/UpdatesChangelogParser.kt`
- `app/src/main/java/com/krono/app/core/ui/settings/SettingsGroup.kt`
- `app/src/main/res/raw/changelog.md`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-pt-rBR/strings.xml`

### Passos

1. Criar componente local para card expansivel do Sobre:
   - Estado recolhido mostra cabecalho/resumo.
   - Clique no card expande com animacao.
   - Acoes internas ficam escondidas quando recolhido.
   - Auto recolhe apos 5s sem foco/interacao.
   - Se usuario digitando, focado ou tocando no conteudo, mantem aberto.
2. Aplicar a:
   - `O que há de novo`.
   - `Apoio`.
   - `Relatar bug`.
3. `Apoio`:
   - Manter botao `Pagar um café` dentro do quadro.
   - Adicionar espacamento abaixo do botao para nao encostar na borda.
4. `Relatar bug`:
   - Botao `Enviar relatório` deve ficar dentro do quadro.
   - Estados sucesso/erro ficam dentro do quadro.
5. `O que há de novo`:
   - Remover pill `Versão Atual - v...`.
   - Botao dentro do quadro:
     - Inicial: `Verificar atualizações`.
     - Resultado atualizado: `App atualizado`, verde.
     - Resultado com update: `Atualizar`, laranja.
     - Download/instalacao mantem estados existentes se necessario.
6. Changelog local:
   - Ler `R.raw.changelog` via `resources.openRawResource`.
   - Parsear com `parseChangelog`.
   - Exibir mudancas da versao atual do app, nao release remoto.
   - Se arquivo vazio ou parse vazio, exibir:
     - `Aprimoramento no funcionamento.`
     - `Correção de pequenos bugs.`
7. Encoding:
   - Garantir `changelog.md` em UTF-8 real.
   - Evitar caracteres quebrados exibidos ao usuario.

### Validacao manual

- Cards do Sobre iniciam recolhidos.
- Clique expande e mostra botoes.
- Sem interacao por 5s -> recolhe.
- Campo de relato focado/digitando -> nao recolhe.
- Botao Pagar um cafe nao encosta na borda.
- Botao Enviar relatorio fica dentro do card.
- Verificar atualizacoes muda para verde/laranja conforme resultado.
- Sem changelog -> mostra duas mensagens fallback novas.

### Legado/sujo a reportar

- `UpdatesPanel` e `AboutPanel` duplicam bastante logica de update/download.
- Parser atual tem indicios de texto com encoding quebrado em secoes antigas. Corrigir dados e parser deve ser autorizado se passar de ajuste pontual.

## Fase 4 - Editor de presets Pomodoro e audio

### Escopo

- Rework pontual do editor de presets.
- Remover handles de drag e lixeira direta.
- Ajustar layout de cards/grupos.
- Corrigir sliders de ciclos.
- Ajustar audio ambiente, sobreposicao e sincronismo dos sons de segundo.

### Arquivos provaveis - Presets

- `app/src/main/java/com/krono/app/feature/pomodoro/PomodoroPresetEditorDialog.kt`
- `app/src/main/java/com/krono/app/feature/pomodoro/PomodoroPresetItemList.kt`
- `app/src/main/java/com/krono/app/feature/pomodoro/PomodoroPresetDragDrop.kt`
- `app/src/main/java/com/krono/app/feature/pomodoro/PomodoroPhaseCard.kt`
- `app/src/main/java/com/krono/app/feature/pomodoro/PomodoroGroupCard.kt`
- `app/src/main/java/com/krono/app/feature/pomodoro/PomodoroPresetEditorState.kt`
- `app/src/main/java/com/krono/app/feature/pomodoro/PomodoroPresetCatalog.kt`
- `app/src/main/java/com/krono/app/feature/pomodoro/PomodoroPresetSettingsSection.kt`
- `app/src/main/res/values/strings.xml`

### Arquivos provaveis - Audio

- `app/src/main/java/com/krono/app/core/audio/SoundTimingPolicy.kt`
- `app/src/main/java/com/krono/app/core/audio/EnvironmentSoundLoop.kt`
- `app/src/main/java/com/krono/app/core/audio/KronoSoundPool.kt`
- `app/src/main/java/com/krono/app/core/util/UserFeedback.kt`
- `app/src/main/java/com/krono/app/core/service/FeedbackManager.kt`
- `app/src/main/java/com/krono/app/feature/stopwatch/StopwatchViewModel.kt`
- `app/src/main/java/com/krono/app/feature/countdown/CountdownManager.kt`
- `app/src/main/java/com/krono/app/feature/pomodoro/PomodoroViewModel.kt`
- `app/src/main/res/raw/krono_env_ticking.mp3`
- `app/src/main/res/raw/krono_env_metronome.mp3`
- `app/src/main/res/raw/krono_env_fastticking.mp3`

### Passos - Presets

1. Drag/drop:
   - Remover icone drag de cards e grupos.
   - Card arrasta por clique longo no proprio card.
   - Grupo nao arrasta.
   - Card aparece durante drag com leve reducao de escala.
   - Usar deslocamento visual durante drag, nao apenas reordenacao instantanea.
   - Itens existentes deslocam para cima/baixo ao abrir espaco.
   - Card pode entrar e sair de grupo.
2. Menus:
   - Substituir lixeira por `more_vert`.
   - Clique abre menu com opcao `Excluir` e icone lixeira.
   - Grupo: `more_vert` dentro da label no fim.
   - Card: `more_vert` dentro do card no fim.
   - Alinhar visualmente os `more_vert` de grupo e card.
3. Layout cards/grupos:
   - Largura sempre guiada pela label.
   - Corrigir label de card dentro de grupo que hoje fica maior que a label de preset/grupo.
   - Card com mesma altura da label de preset/grupo.
   - Label e tempo na mesma linha.
   - Remover circulo de cor.
   - Card usa sua cor configurada como superficie interna.
   - Card novo nasce branco ou cor do tema, conforme tokens atuais.
4. Ciclos:
   - Remover texto dinamico acima do slider.
   - Acima fica apenas titulo `CICLOS`.
   - Ajustar tanto preset quanto grupo.
5. Validacao:
   - Remover mensagem `Adicione pelo menos um carde ou grupo para salvar`.
   - Botao salvar desabilitado basta.
6. Divisores:
   - Remover divisores horizontais do editor de preset.
   - Confirmado: escopo restrito ao editor de presets.
   - Nao remover divisores globais de Settings.
7. Novo preset:
   - `newUserPresetTemplate` deve nascer com label `Novo preset`.
   - Persistencia continua truncando em 50 caracteres.

### Passos - Audio

1. Ambient loops:
   - Crossfade padrao de sons ambientes: 5s.
   - Inicio imediato no play.
   - Final imperceptivel no loop.
   - Nao aplicar crossfade ao modo `SecondTick`.
2. Sobreposicao:
   - Play/pause, ambiente, alarmes e notificacao devem poder tocar juntos.
   - Nao parar um canal para iniciar outro.
   - Stop do ambiente deve afetar apenas ambiente/tick ativo.
   - Beep de pause deve disparar no mesmo instante em que ambiente pausa.
3. Som `Contador`/ticks de segundo:
   - Analisar onda dos audios de tick/metronomo/tic-tac.
   - Encontrar picos mais altos.
   - Definir ponto principal como batida do segundo.
   - Picos menores ficam entre segundos.
   - Se todos os picos forem iguais, escolher um como segundo e tratar os demais como subdivisoes.
4. Agendamento:
   - Manter audio em buffer via `SoundPool`.
   - Agendar execucao por tempo absoluto (`SystemClock.elapsedRealtime`).
   - Recalcular proximo disparo a cada segundo para eliminar lag acumulado.
   - Aplicar em Stopwatch, Timer e Pomodoro.
5. Parametros ignorados:
   - Revisar `startOffsetMs`, `alignmentOffsetMs`, `maxDurationMs`, `maxLifetimeMs`.
   - Remover `@Suppress("UNUSED_PARAMETER")` apenas se implementar uso real.

### Validacao manual

- Card arrasta por long press no card inteiro.
- Grupo nao arrasta.
- Card entra/sai de grupo sem perder label, cor, tempo e som.
- `more_vert` abre menu e exclui.
- Sem circulo de cor.
- Sliders mostram somente `CICLOS` acima.
- Novo preset nasce como `Novo preset`.
- Ambiente comeca no play sem atraso perceptivel.
- Loop ambiente nao tem corte perceptivel.
- Pause toca beep e interrompe ambiente no mesmo instante.
- Ticks seguem troca visual de segundo por 10 minutos sem drift perceptivel.

### Legado/sujo a reportar

- Drag atual e baseado em handle e indices, sem modelo visual de item arrastado.
- Grupos hoje sao arrastaveis, contrario ao novo requisito.
- `SoundTimingPolicy` ja tem `alignmentOffsetMs`, mas o fluxo atual nao usa esse dado.
- `playBundledSound` ignora `usage`, `maxLifetimeMs`, `startOffsetMs` e `endTrimMs`.
- `EnvironmentSoundLoop` usa `MediaPlayer`; mudancas profundas de audio podem exigir refatoracao maior.

## Ordem recomendada

1. Fase 1 primeiro, porque reduz risco de preferencias fantasmas antes das demais telas.
2. Fase 2 depois, porque corrige bugs visiveis de runtime com baixo impacto visual.
3. Fase 3 em seguida, isolada em Sobre/updates.
4. Fase 4 por ultimo, porque drag/drop e audio tem maior risco tecnico.

## Duvidas para validar antes da execucao

- Nenhuma duvida aberta.

## Checklist de aceite geral

- Nenhuma mudanca visual fora dos pontos pedidos.
- Nenhum build executado sem autorizacao.
- Nenhum commit criado sem autorizacao.
- Nenhuma preferencia muda so por navegar.
- Todas as labels editaveis param em 50 caracteres.
- Sem contadores visuais de caracteres em labels.
- Sobre usa changelog local da versao atual.
- Audio de segundo nao acumula atraso perceptivel.
