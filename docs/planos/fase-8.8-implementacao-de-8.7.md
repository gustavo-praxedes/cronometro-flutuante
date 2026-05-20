# Plano de Implementação — Krono Tools UX

> Baseado no relatório `krono-tools-ux-audit.md`  
> Ordenado por prioridade: crítico → alto → polimento

---

## Fase 1 — Navegação e Layout Responsivo

> Corrige os dois issues High de estrutura de navegação.

- [ ] Detectar tipo de tela (mobile / tablet) via `WindowSizeClass` do Compose
- [ ] **Mobile:** substituir split-panel por `LazyColumn` full-width com chevron por item
- [ ] **Mobile:** implementar navegação Settings como pilha de telas (push/pop)
- [ ] **Tablet:** manter split-panel; ajustar largura da sidebar para 40% da tela
- [ ] **Tablet:** reduzir `fontSize` dos itens da sidebar (~13sp) para acomodar nomes completos
- [ ] **Tablet:** adicionar `maxLines = 1` + `overflow = TextOverflow.Ellipsis` como fallback
- [ ] Testar nos breakpoints: 360px, 411px (mobile), 600px, 840px (tablet)

**Commit sugerido:**
```
feat(nav): layout responsivo — split-panel tablet, full-width mobile
```

---

## Fase 2 — Ícones e Identidade Visual da Sidebar

> Unifica a linguagem visual dos ícones com o tema do usuário.

- [ ] Ler cor primária do tema atual (Aparência) via token do sistema
- [ ] Aplicar cor única derivada do tema em todos os ícones da sidebar
- [ ] Remover cores hardcoded individuais por item (verde, azul, rosa, laranja, vermelho)
- [ ] Manter variação apenas na **forma** do ícone para distinguir itens
- [ ] Renomear label "FUNÇÕES GERAIS" → "GERAL" na sidebar
- [ ] Verificar contraste do ícone colorido sobre fundo de card em tema claro e escuro

**Commit sugerido:**
```
feat(sidebar): ícones com cor única do tema; renomear seção GERAL
```

---

## Fase 3 — Reestruturação de Sons do Pomodoro

> Maior refatoração do fluxo — separar configs globais das configs por preset.

### 3a — Settings globais (Pomodoro > Sons e Vibração)

- [ ] Remover entradas: "Alerta de foco", "Som alerta de foco", "Alerta de pausa", "Som alerta de pausa"
- [ ] Manter apenas:
  - Toggle: habilitar sons do Pomodoro (subordinado ao toggle global do sistema)
  - Toggle: habilitar tic-tac do relógio
  - Slider: volume geral do Pomodoro
  - Slider: volume do tic-tac
- [ ] Implementar disable condicional: toggle OFF → `opacity 0.4` + `pointer-events: none` nos controles dependentes
- [ ] Toggle de sons do Pomodoro: desabilitar e fixar em OFF se toggle global do sistema estiver OFF
- [ ] Garantir que "Nenhum" seja opção válida em todos os seletores de som

### 3b — Modal Editar Intervalo (por preset)

- [ ] Adicionar campo: seletor de som de alerta/notificação por intervalo
- [ ] Incluir opção "Nenhum" no seletor de som
- [ ] Remover restrição de labels — campo livre (não limitar a "Foco" / "Pausa")
- [ ] Garantir que pausa longa seja configurável como intervalo comum (label + duração + som)
- [ ] Migrar dados de som existentes de settings globais para o preset padrão na primeira abertura após update

**Commit sugerido:**
```
feat(pomodoro): sons por intervalo no preset; settings globais simplificadas
```

---

## Fase 4 — Seção COMPORTAMENTO e Auto-Ciclo

> Separa configurações de fluxo das configurações de áudio.

- [ ] Criar seção "COMPORTAMENTO" nas settings do Pomodoro, abaixo de Sons e Vibração
- [ ] Mover "Iniciar próximo ciclo automaticamente" para a nova seção
- [ ] Implementar lógica:
  - OFF → ao fim de cada intervalo, timer pausa e aguarda play manual do usuário
  - ON → timer avança automaticamente pelos intervalos, pela quantidade de ciclos do preset
  - Ao fim do último ciclo: timer para independentemente do toggle
- [ ] Atualizar texto descritivo do toggle para refletir o comportamento exato

**Commit sugerido:**
```
feat(pomodoro): seção COMPORTAMENTO; lógica de auto-ciclo revisada
```

---

## Fase 5 — Scroll Feedback Global

> Aplica indicador visual de scroll em todos os menus do app.

- [ ] Criar componente reutilizável `ScrollFadeContainer` com `fade gradient` no rodapé
- [ ] Gradient: transparente → cor de fundo, altura ~48dp
- [ ] Aplicar em: Pomodoro Settings, Timer Settings, Cronômetro Settings, Aparência, Comportamento, Sobre
- [ ] Ocultar gradient automaticamente quando scroll chegar ao fim do conteúdo
- [ ] Testar em telas com conteúdo curto (sem scroll) para garantir que gradient não aparece desnecessariamente

**Commit sugerido:**
```
feat(ui): ScrollFadeContainer com fade gradient em todos os menus
```

---

## Fase 6 — Modal Configurar Preset

> Melhora affordance e feedback do modal de edição de preset.

- [ ] Slider de ciclos: exibir valor atual como tooltip flutuante sobre o thumb, ou label `Ciclos: N` inline
- [ ] Itens de intervalo (Foco, Pausa, etc.): substituir ícone de engrenagem por chevron "›"
- [ ] Tornar o item inteiro tappable com ripple feedback (não apenas o ícone)
- [ ] Botão "+ Adicionar intervalo": substituir por `OutlinedButton` ou `AssistChip` do Material 3

**Commit sugerido:**
```
fix(preset): affordance de intervalos; slider de ciclos com valor inline
```

---

## Fase 7 — Modal Editar Intervalo

> Corrige feedback visual do picker e seletor de cor.

- [ ] Picker de horas/minutos/segundos: adicionar `fade gradient` vertical (topo e base) para indicar rolagem
- [ ] Campo "Cor da etapa": adicionar swatch circular (24dp) à esquerda do hex code
- [ ] Swatch atualiza em tempo real conforme usuário altera a cor
- [ ] Garantir que o swatch tenha borda sutil (1dp, cor neutra) para visibilidade em fundos claros e escuros

**Commit sugerido:**
```
fix(intervalo): fade gradient no picker; swatch de cor ao lado do hex
```

---

## Fase 8 — Telas Sobre e Atualizações

> Mescla telas, reposiciona Relatar Bug e ajusta CTA de apoio.

- [ ] Mesclar "Sobre" e "Atualizações" em tela única "Sobre o App"
- [ ] Incorporar changelog como seção "O que há de novo" dentro da tela Sobre
- [ ] Mover "Relatar Bug" para o **rodapé da tela Sobre** (link ou botão secundário)
- [ ] Remover "Relatar Bug" como item da sidebar
- [ ] Remover "Atualizações" como item separado da sidebar
- [ ] Bloco "Apoie o projeto": ocultar completamente até acúmulo de 5 minutos de uso
- [ ] Após 5 min: exibir contador + mensagem + botão
- [ ] Botão "Pagar um café": trocar de `FilledButton` para `OutlinedButton`

**Commit sugerido:**
```
feat(sobre): mesclar Sobre+Atualizações; Relatar Bug no rodapé; CTA outlined
```

---

## Fase 9 — Tela Relatar Bug

> Orienta o usuário sobre o campo obrigatório.

- [ ] Adicionar helper text abaixo do campo "Descrição do problema": `"Preencha a descrição para habilitar o envio"`
- [ ] Exibir helper text apenas quando o campo estiver vazio e o botão for o foco ou o usuário já tiver interagido com o form
- [ ] Manter botão desabilitado enquanto descrição estiver vazia
- [ ] Habilitar botão automaticamente ao detectar texto no campo de descrição

**Commit sugerido:**
```
fix(bug-report): helper text no campo obrigatório; botão habilita ao digitar
```

---

## Fase 10 — Novas Funcionalidades

> Implementar sugestões aprovadas na auditoria.

### Pomodoro

- [ ] Vibração independente: adicionar toggle "Vibração" em Sons e Vibração (separado de som)
- [ ] Modo Não Perturbe: adicionar toggle em COMPORTAMENTO; integrar com `NotificationManager.setInterruptionFilter` do Android
- [ ] Histórico de sessões: nova tela acessível pelo Pomodoro; registrar data, preset usado, ciclos completados
- [ ] Meta diária de ciclos: campo em COMPORTAMENTO; exibir progresso no widget e na tela principal

### Timer

- [ ] Presets rápidos: adicionar chips de tempo fixo (5, 10, 15, 30 min) na tela principal do Timer

### Global (Aparência)

- [ ] Configuração de widget: adicionar subseção "Widget" em Aparência
  - Tamanho: Compacto / Padrão / Expandido
  - Elementos visíveis: controles exibidos no widget flutuante

**Commit sugerido:**
```
feat(pomodoro/timer/widget): vibração, DND, histórico, meta, presets timer, config widget
```

---

## Resumo das Fases

| Fase | Escopo | Prioridade |
|---|---|---|
| 1 | Layout responsivo (mobile/tablet) | 🔴 Crítica |
| 2 | Ícones e identidade visual | 🟡 Alta |
| 3 | Reestruturação de sons do Pomodoro | 🔴 Crítica |
| 4 | Seção COMPORTAMENTO e auto-ciclo | 🟡 Alta |
| 5 | Scroll fade gradient global | 🟡 Alta |
| 6 | Modal Configurar Preset | 🟡 Alta |
| 7 | Modal Editar Intervalo | 🟡 Alta |
| 8 | Telas Sobre / Atualizações | 🟡 Alta |
| 9 | Tela Relatar Bug | 🟡 Alta |
| 10 | Novas funcionalidades | 🟢 Normal |

---

*Gerado a partir de `krono-tools-ux-audit.md` · Krono Tools v3.3.1*
