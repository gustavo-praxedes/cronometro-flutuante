# Relatório de Auditoria UX — Krono Tools Settings

> **App:** Widget flutuante de cronômetro (Android) · **Versão:** v3.3.1  
> **Telas avaliadas:** Pomodoro, Sobre, Atualizações, Relatar Bug, Modal Preset, Modal Intervalo  
> **Método:** Nielsen's 10 Heurísticas + Mobile UX (Material Design 3) + Validação Visual  
> **Veredito Final:** ❌ FAIL — corrigir antes da próxima release

---

## Índice

1. [Problemas Globais](#1-problemas-globais)
2. [Pomodoro Settings — Auditoria Rigorosa](#2-pomodoro-settings--auditoria-rigorosa)
3. [Modal "Configurar Preset"](#3-modal-configurar-preset)
4. [Modal "Editar Intervalo"](#4-modal-editar-intervalo)
5. [Tela "Sobre"](#5-tela-sobre)
6. [Tela "Relatar Bug"](#6-tela-relatar-bug)
7. [Sugestões de Novas Configurações](#7-sugestões-de-novas-configurações)
8. [Itens a Remover / Mesclar / Agrupar](#8-itens-a-remover--mesclar--agrupar)
9. [Resumo de Severidades](#9-resumo-de-severidades)

---

## 1. Problemas Globais

### 1.1 Layout split-panel inadequado para mobile

| Campo | Detalhe |
|---|---|
| **Severidade** | 🔴 High |
| **Heurística** | Reconhecimento > recordação; Consistência |
| **Localização** | Todas as telas — sidebar permanente |

**Problema:** Layout split-panel em tela mobile (~380px) comprime demais a sidebar. Itens como "Cro…", "Tim…", "Pom…", "Atu…" e "Rela…" ficam truncados e ilegíveis.

**Decisão:** Manter split-panel em **tablet** com sidebar em 40%. Em **mobile**, substituir por navegação full-width — `LazyColumn` com chevron por item, sem sidebar permanente.

---

### 1.2 Itens de menu truncados (tablet)

| Campo | Detalhe |
|---|---|
| **Severidade** | 🔴 High |
| **Heurística** | Consistência; Estética minimalista |
| **Localização** | Sidebar tablet — todos os itens |

**Problema:** Sidebar em ~35% da largura corta nomes longos sem reticências explícitas.

**Decisão:** Em tablet, aumentar sidebar para **40%** da largura. Reduzir levemente o tamanho da fonte dos itens para acomodar nomes completos. Truncar com reticências (`…`) apenas como último recurso em telas muito estreitas.

---

### 1.3 Sistema de cores dos ícones sem semântica

| Campo | Detalhe |
|---|---|
| **Severidade** | 🟡 Medium |
| **Heurística** | Consistência |
| **Localização** | Sidebar — todos os ícones |

**Problema:** Ícones em verde, azul, rosa, laranja e vermelho sem critério semântico aparente.

**Decisão:** Usar **cor única derivada do tema escolhido pelo usuário** em Aparência. Todos os ícones da sidebar seguem a mesma cor base, com variação apenas na forma do ícone para distinguir itens.

---

### 1.4 Seção "PROJETO" misturada com ferramentas funcionais

| Campo | Detalhe |
|---|---|
| **Severidade** | 🟡 Medium |
| **Heurística** | Estética minimalista; Agrupamento lógico |
| **Localização** | Sidebar — seção Projeto |

**Decisão:** Manter como está. A separação visual por seção (FERRAMENTAS / PROJETO) já resolve o agrupamento sem necessidade de reorganização estrutural.

---

## 2. Pomodoro Settings — Auditoria Rigorosa

### 2.1 Seção "Sons e Vibração" sem agrupamento visual

| Campo | Detalhe |
|---|---|
| **Severidade** | 🔴 High |
| **Heurística** | Estética minimalista; Agrupamento |
| **Localização** | Pomodoro > Sons e Vibração |

**Problema:** 8 controles empilhados numa lista linear — tic-tac e alertas de intervalo misturados sem divisão visual clara.

**Decisão:** Reestruturar a seção Sons e Vibração para conter apenas **configurações globais de áudio**:

- Toggle global habilitar/desabilitar sons do Pomodoro
- Toggle global habilitar/desabilitar tic-tac do relógio
- Slider de volume global do Pomodoro
- Slider de volume do tic-tac

Configurações específicas de som por intervalo (qual som tocar ao iniciar foco, pausa, pausa longa) devem ser **movidas para dentro do preset**, na edição de cada intervalo. Ver item 2.6.

---

### 2.2 Controles dependentes ativos com toggle pai desligado

| Campo | Detalhe |
|---|---|
| **Severidade** | 🔴 High |
| **Heurística** | Prevenção de erros; Status do sistema |
| **Localização** | Pomodoro > Sons e Vibração |

**Problema:** Toggle desligado mas sliders e dropdowns dependentes permanecem ativos e editáveis.

**Correção:** Quando toggle pai = OFF → desabilitar controles dependentes com `opacity: 0.4` e `pointer-events: none`. Aplicar para: toggle global de sons (desabilita volume global) e toggle de tic-tac (desabilita volume do tic-tac). Padrão Material 3 para configurações condicionais.

**Regra adicional:** Toggle de sons do Pomodoro fica desabilitado e fixo em OFF se o **toggle global de sons do sistema** estiver desligado.

---

### 2.3 Sliders de volume sem vínculo visual ao contexto

| Campo | Detalhe |
|---|---|
| **Severidade** | 🟡 Medium |
| **Heurística** | Reconhecimento > recordação |
| **Localização** | Pomodoro > Sons e Vibração > sliders |

**Problema:** Sliders com aparência idêntica sem associação visual imediata ao seu contexto.

**Correção:** Com a reestruturação do item 2.1, cada slider ficará imediatamente abaixo do toggle ao qual pertence, dentro do mesmo grupo visual — eliminando a ambiguidade sem necessidade de ícones adicionais.

---

### 2.4 "Iniciar próximo ciclo automaticamente" na seção errada

| Campo | Detalhe |
|---|---|
| **Severidade** | 🟡 Medium |
| **Heurística** | Agrupamento lógico |
| **Localização** | Pomodoro > final da seção Sons e Vibração |

**Decisão:** Mover para nova seção **"COMPORTAMENTO"** nas settings do Pomodoro, abaixo de Sons e Vibração.

**Comportamento definido:**
- OFF → ao fim de cada intervalo, o timer pausa e aguarda o usuário pressionar play manualmente.
- ON → o timer avança automaticamente para o próximo intervalo, repetindo pela quantidade de ciclos configurada no preset. Ao fim do último ciclo, o timer para.

---

### 2.5 Conteúdo oculto sem indicador de scroll

| Campo | Detalhe |
|---|---|
| **Severidade** | 🟢 Low |
| **Heurística** | Status do sistema |
| **Localização** | Todos os menus com scroll |

**Decisão:** Aplicar em **todos os menus com conteúdo scrollável** do app — não apenas Pomodoro. Adicionar `fade gradient` ou `elevation shadow` no rodapé do container de scroll sempre que houver conteúdo abaixo da área visível.

---

### 2.6 Reestruturação de sons: configurações específicas no preset

| Campo | Detalhe |
|---|---|
| **Severidade** | 🟡 Medium |
| **Heurística** | Flexibilidade; Controle do usuário |
| **Localização** | Pomodoro Settings — geral + Modal Editar Intervalo |

**Problema:** Sons de alerta de foco e pausa ficavam nas settings globais como categorias fixas, limitando usuários que queiram presets com sons diferentes.

**Decisão — nova arquitetura de sons do Pomodoro:**

**Settings globais (Pomodoro > Sons e Vibração):**
- Toggle: habilitar sons do Pomodoro (subordinado ao toggle global do sistema)
- Toggle: habilitar tic-tac do relógio
- Slider: volume geral do Pomodoro
- Slider: volume do tic-tac
- Opção "Nenhum som" disponível em todos os seletores de som

**Dentro do preset (Modal Editar Intervalo, por intervalo):**
- Seletor: som de alerta/notificação deste intervalo
- O usuário define o label do intervalo livremente (ex: "Foco", "Pausa", "Pausa Longa", "Alongamento")
- Pausa longa configurável como intervalo comum dentro do preset — sem categoria especial fixada pelo app

**Resultado:** dois presets com tempos iguais podem ter sons completamente diferentes. Labels "Foco" e "Pausa" são apenas sugestões — não há restrição de nomenclatura.

**Remover das settings globais:** "Alerta de foco", "Alerta de pausa", "Som alerta de foco", "Som alerta de pausa" como entradas fixas.

---

## 3. Modal "Configurar Preset"

### 3.1 Valor do slider de ciclos mal posicionado

| Campo | Detalhe |
|---|---|
| **Severidade** | 🟡 Medium |
| **Heurística** | Reconhecimento > recordação |
| **Localização** | Modal Configurar Preset > slider Ciclos |

**Problema:** Valor atual "4" aparece no canto superior direito do label, distante do thumb do slider.

**Correção:** Mostrar valor como tooltip flutuante sobre o thumb do slider, ou exibir inline no formato `Ciclos: 4` diretamente ao lado do controle.

---

### 3.2 Affordance fraca nos itens de intervalo

| Campo | Detalhe |
|---|---|
| **Severidade** | 🟡 Medium |
| **Heurística** | Reconhecimento > recordação; Controle do usuário |
| **Localização** | Modal Configurar Preset > lista de intervalos |

**Problema:** Itens mostram apenas ícone de engrenagem, sem indicação clara de que são tappable para editar.

**Correção:** Tornar o item inteiro tappable com ripple feedback. Substituir engrenagem por chevron "›" — padrão Android para "toca para editar". Engrenagem remete a configurações globais, não a edição de item individual.

---

### 3.3 Botão "+ Adicionar intervalo" com affordance insuficiente

| Campo | Detalhe |
|---|---|
| **Severidade** | 🟢 Low |
| **Heurística** | Consistência; Reconhecimento |
| **Localização** | Modal Configurar Preset > rodapé |

**Problema:** "+ Adicionar intervalo" usa apenas cor como affordance — sem borda, sem elevação. Parece link, não ação.

**Correção:** Usar `OutlinedButton` ou `AssistChip` do Material 3 para deixar explícito que é uma ação que cria novo elemento.

---

## 4. Modal "Editar Intervalo"

### 4.1 Scroll picker sem indicador visual de rolagem

| Campo | Detalhe |
|---|---|
| **Severidade** | 🟡 Medium |
| **Heurística** | Reconhecimento > recordação |
| **Localização** | Modal Editar Intervalo > picker de horas/minutos/segundos |

**Problema:** Picker mostra valores adjacentes mas sem gradiente fade — usuário pode não perceber que é rolável.

**Correção:** Adicionar `fade gradient` (transparente → opaco → transparente) no eixo vertical do picker. Comportamento padrão do `WheelPicker` nativo Android.

---

### 4.2 "Cor da etapa" exibe apenas hex sem swatch visual

| Campo | Detalhe |
|---|---|
| **Severidade** | 🟡 Medium |
| **Heurística** | Reconhecimento > recordação; Linguagem do mundo real |
| **Localização** | Modal Editar Intervalo > Cor da etapa |

**Problema:** Exibe apenas `#EF4444` — usuário precisa decodificar hex para saber a cor selecionada.

**Correção:** Exibir swatch colorido circular (24dp) ao lado do hex code. O código hex é informação secundária — a cor visual é o que o usuário precisa ver primeiro.

---

## 5. Tela "Sobre"

### 5.1 Contador de uso zerado gera desconfiança

| Campo | Detalhe |
|---|---|
| **Severidade** | 🟢 Low |
| **Heurística** | Status do sistema; Confiança |
| **Localização** | Sobre > Apoie o projeto |

**Problema:** Contador "0h 00m 00s" visível desde o primeiro acesso enfraquece a mensagem de apelo.

**Decisão:** Manter o contador, mas **exibir o bloco de apoio (mensagem + botão) somente após acúmulo de 5 minutos de uso**. Antes disso, o bloco fica oculto.

---

### 5.2 CTA "Pagar um café" com hierarquia visual excessiva

| Campo | Detalhe |
|---|---|
| **Severidade** | 🟢 Low |
| **Heurística** | Estética minimalista |
| **Localização** | Sobre > botão Pagar um café |

**Decisão:** Substituir botão sólido full-width por **`OutlinedButton`** — mantendo visibilidade sem dominar a hierarquia visual da tela.

---

## 6. Tela "Relatar Bug"

### 6.1 Botão desabilitado sem explicação

| Campo | Detalhe |
|---|---|
| **Severidade** | 🟡 Medium |
| **Heurística** | Prevenção de erros; Recuperação de erros |
| **Localização** | Relatar Bug > botão Enviar relatório |

**Problema:** Botão cinza/desabilitado sem mensagem de orientação. Campo obrigatório com asterisco mas sem validação inline visível.

**Correção:** Manter botão desabilitado e adicionar helper text `"Preencha a descrição para habilitar o envio"` imediatamente abaixo do campo de descrição quando estiver vazio.

---

## 7. Sugestões de Novas Configurações

### Pomodoro

| Configuração | Decisão | Observação |
|---|---|---|
| Pausa longa configurável | ✅ Implementar | Configurada dentro do preset como intervalo comum com label livre — não como categoria fixa |
| Vibração separada de som | ✅ Implementar | Toggle independente de vibração nas settings globais de Sons |
| Modo "Não perturbe" durante foco | ✅ Implementar | Integração com DND do Android |
| Histórico de sessões / estatísticas | ✅ Implementar | Motivação e acompanhamento de produtividade |
| Meta diária de ciclos | ✅ Implementar | Usuário define a meta; gamificação leve |

### Timer

| Configuração | Decisão | Observação |
|---|---|---|
| Presets de tempo rápido (5, 10, 15, 30 min) | ✅ Implementar | Reduz fricção para uso frequente |
| Repetição automática com N repetições | ❌ Não implementar | Pomodoro já cobre esse caso de uso |

### Global

| Configuração | Decisão | Observação |
|---|---|---|
| Tema escuro / claro / automático | ✅ Já existe | Disponível em Aparência |
| Exportar / importar configurações | ❌ Não por enquanto | Avaliar em versão futura |
| Aparência e tamanho do widget | ✅ Implementar | Usuário configura aparência (compacto / padrão / expandido) e elementos visíveis no widget flutuante. Disponível em Aparência |
| Atalho de widget para ferramenta específica | ❌ Não implementar | Fora do escopo atual |

---

## 8. Itens a Remover / Mesclar / Agrupar

| Item atual | Problema | Decisão |
|---|---|---|
| "Sobre" + "Atualizações" como itens separados | Raramente acessados, ocupam espaço premium | ✅ Mesclar em tela única "Sobre o App" com seção de changelog incorporada |
| "Relatar Bug" na sidebar com peso de ferramenta | Acesso de emergência — não rotina | ✅ Mover para rodapé da tela "Sobre" |
| Sliders de volume sem agrupamento por toggle pai | Usuário configura volume de feature desligada | ✅ Agrupar em grupos visuais com disable condicional (ver 2.1 e 2.2) |
| "Iniciar próximo ciclo automaticamente" em Sons | Configuração de comportamento, não de áudio | ✅ Mover para seção "COMPORTAMENTO" — manter apenas em Pomodoro Settings |
| Seções "FUNÇÕES GERAIS" e "FERRAMENTAS" na sidebar | Nomenclatura técnica, não orientada ao usuário | ✅ Renomear: "FERRAMENTAS" permanece; "FUNÇÕES GERAIS" → "GERAL" |

---

## 9. Resumo de Severidades

| Severidade | Qtd | Itens |
|---|---|---|
| 🔴 **High** | 4 | Split-panel mobile, itens truncados no tablet, controles dependentes ativos com toggle OFF, sons sem agrupamento/reestruturação |
| 🟡 **Medium** | 9 | Cor única por tema (ícones), sliders sem contexto, auto-ciclo na seção errada, reestruturação de sons no preset, valor de ciclos mal posicionado, affordance fraca nos intervalos, picker sem fade gradient, hex sem swatch, botão desabilitado sem helper text |
| 🟢 **Low** | 4 | Scroll indicator em todos os menus, contador de uso (exibir após 5 min), CTA outlined no Sobre, affordance do "+ Adicionar intervalo" |

---

## Veredito Final

```
QUALITY CHECK RESULT: FAIL — corrigir antes da próxima release

Prioridade máxima:
1. Layout mobile full-width (split-panel apenas em tablet)
2. Sidebar tablet 40% com fonte reduzida e ellipsis como fallback
3. Disable de controles dependentes de toggle OFF
4. Reestruturação de Sons: globais nas settings, específicos no preset
5. Swatch de cor no editor de intervalo

Alta prioridade:
6. Seção COMPORTAMENTO com "Iniciar próximo ciclo automaticamente"
7. Scroll fade gradient em todos os menus scrolláveis
8. Affordance dos intervalos no modal de preset (chevron + ripple)
9. Mesclar Sobre + Atualizações; Relatar Bug no rodapé de Sobre
10. Helper text no botão desabilitado de Relatar Bug

Melhorias de polimento:
11. Scroll picker com fade gradient (modal Editar Intervalo)
12. Contador de uso oculto até 5 min acumulados
13. Botão "Pagar um café" como OutlinedButton
14. OutlinedButton para "+ Adicionar intervalo"
15. Renomear "FUNÇÕES GERAIS" → "GERAL"
```

---

*Auditoria gerada com ui-ux-pro · audit + mobile specialist · Nielsen's 10 Heurísticas · Material Design 3*
