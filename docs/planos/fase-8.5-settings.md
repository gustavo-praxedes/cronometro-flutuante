# Especificação Detalhada — PASSO 5: Comportamento e Overlay

## Visão Geral
Este passo resolve a densidade cognitiva do painel de Comportamento. Vamos aplicar a "Lei da Proximidade" para agrupar funções semelhantes e mover configurações puramente visuais para o painel de Overlay, conforme sugerido na análise técnica.

---

## Commit 5.1 — Reorganização e Limpeza de Negócio
**Objetivo:** Mover configurações para os seus devidos lugares e limpar redundâncias.

### Especificações Técnicas:
- [ ] **Migração de Overlay**: Mover `showHours`, `showSeconds` e `showButtons` do painel de Comportamento para o painel de **Overlay**. Eles são estados visuais, não comportamentais.
- [ ] **Migração de Stopwatch**: Conforme o plano de reestruturação, mover o campo `timeLimitSeconds` para a nova seção de **Cronômetro (Tools)**.
- [ ] **Simplificação**: Renomear labels internas para maior clareza (ex: de "Habilitar Beep" para apenas "Sinal Sonoro").

**Msg Sugerida:** `refactor: move visual toggles to Overlay and time limit to Stopwatch`

---

## Commit 5.2 — Agrupamento de Comportamento (Sub-seções)
**Objetivo:** Organizar os toggles restantes em cards lógicos para facilitar a leitura rápida.

### Especificações Técnicas:
- [ ] **Grupo "SISTEMA"**:
    - `Auto-início`: Com descrição "Iniciar o Krono ao ligar o dispositivo".
    - `Manter Tela Ligada`: Com descrição "Evita que o dispositivo bloqueie com o overlay ativo".
- [ ] **Grupo "MODO FOCO"**:
    - `Habilitar Modo Foco`: Com descrição "Bloqueia notificações e apps distrativos".
- [ ] **Grupo "FEEDBACK"**:
    - `Vibração`: Toggle simples.
    - `Sinal Sonoro (Beep)`: Toggle simples.
- [ ] **Estilo**: Usar o componente `SettingsRow` (Passo 1) em todos, garantindo que o `Switch` esteja alinhado à direita.

**Msg Sugerida:** `ui: categorize behavior settings into System, Focus and Feedback groups`

---

## Commit 5.3 — Refinamento de Overlay (Sliders e Geometria)
**Objetivo:** Tornar os controles de escala e raio mais precisos e visuais.

### Especificações Técnicas:
- [ ] **Controles de Precisão**:
    - **Escala**: Implementar slider com steps de `0.1x`. Exibir o valor numérico (ex: `1.2x`) em tempo real no `trailing` da linha.
    - **Raio dos Cantos**: Slider de `0dp` a `50dp`. Exibir o valor (ex: `12dp`).
- [ ] **Integração Visual**:
    - Colocar os sliders dentro de um card `SettingsGroup` chamado "DIMENSÕES".
    - Adicionar um ícone de "Régua" ou "Monitor" no cabeçalho do grupo.
- [ ] **Toggle de Visibilidade**:
    - Agrupar os itens movidos no Commit 5.1 (`Horas`, `Segundos`, `Botões`) em um card chamado "EXIBIÇÃO".

**Msg Sugerida:** `ui: enhance overlay sliders with real-time value display`

---

## Commit 5.4 — Descrições Auxiliares (Subtitles)
**Objetivo:** Explicar funções complexas (como o Modo Foco) sem que o usuário precise testar para entender.

### Especificações Técnicas:
- [ ] **Implementação de Contexto**:
    - Adicionar o parâmetro `subtitle` em cada `SettingsRow`.
    - Garantir que a tipografia do subtítulo use `MaterialTheme.colorScheme.onSurfaceVariant` para manter a hierarquia.
- [ ] **Acessibilidade**:
    - Garantir que cada `Switch` e `Slider` tenha um `contentDescription` que descreva sua função atual (ex: "Desativar vibração").

**Msg Sugerida:** `ui: add explanatory subtitles to behavioral settings`

---

## Checklist de Validação do Passo 5:
- [ ] Os controles de "Segundos" e "Horas" agora aparecem no painel de Overlay?
- [ ] O usuário consegue entender o que o "Modo Foco" faz sem clicar nele?
- [ ] Os sliders de escala permitem um ajuste fino (0.1 em 0.1)?
- [ ] O espaçamento entre os grupos de "Sistema" e "Feedback" está consistente com o Passo 3?