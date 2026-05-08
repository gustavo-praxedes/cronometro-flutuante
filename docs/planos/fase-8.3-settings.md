# Especificação Detalhada — PASSO 3: Painel de Conteúdo e Hierarquia de Informação

## Visão Geral
Implementar o host de conteúdo que renderiza as configurações selecionadas. O foco é a legibilidade, o agrupamento lógico de opções através de cards internos e a fluidez na transição entre diferentes painéis.

---

## Commit 3.1 — Layout Host: Split View e Estrutura Base
**Objetivo:** Criar o container que divide a tela e gerencia o espaço entre o menu (esquerda) e o conteúdo (direita).

### Especificações Técnicas:
- [ ] **Adaptive Container**:
    - Usar uma `Row` como raiz para telas horizontais/tablets.
    - O menu lateral deve ocupar um `weight` fixo (ex: 0.3 ou 300.dp) e o painel de conteúdo o `weight` restante (1f).
- [ ] **Vertical Divider**:
    - Inserir uma `VerticalDivider` entre os dois painéis.
    - Cor: `MaterialTheme.colorScheme.outlineVariant`.
    - Espessura: `1.dp`.
- [ ] **Scaffold Interno**:
    - O painel direito deve ter seu próprio padding interno (`Spacing.lg`) para que o conteúdo não encoste nas bordas da tela ou na divisória.

**Msg Sugerida:** `ui: implement split-view layout host for settings`

---

## Commit 3.2 — Header Dinâmico e Título da Seção
**Objetivo:** Fornecer contexto imediato ao usuário sobre qual categoria ele está editando.

### Especificações Técnicas:
- [ ] **Sticky Header**:
    - O título da seção (ex: "Aparência") deve permanecer no topo enquanto o usuário rola as opções.
- [ ] **Tipografia**:
    - Título: `MaterialTheme.typography.headlineSmall` com `FontWeight.Bold`.
    - Cor: `MaterialTheme.colorScheme.onSurface`.
- [ ] **Sincronização**:
    - O título deve ser atualizado automaticamente com base no `SettingsDestination` selecionado no menu lateral.

**Msg Sugerida:** ``

---

## Commit 3.3 — Agrupamento por Cards Internos (`SettingsGroup`)
**Objetivo:** Evitar o efeito de "lista infinita" e organizar configurações relacionadas dentro de containers visuais.

### Especificações Técnicas:
- [ ] **Container de Grupo**:
    - Utilizar o `Surface` com `Shape.Card` (definido no Passo 1).
    - Background: `MaterialTheme.colorScheme.surface` ou um tom sutilmente diferente para criar profundidade.
- [ ] **Título do Grupo**:
    - Adicionar um label acima de cada card (ex: "CORES", "FONTE").
    - Estilo: `labelSmall`, em caixa alta, com `letterSpacing` sutil.
- [ ] **Divisores Internos**:
    - Dentro de cada card, as `SettingsRow` devem ser separadas por uma `HorizontalDivider` que não toca as bordas (com padding horizontal).

**Msg Sugerida:** `ui: implement card-based grouping for settings options`

---

## Commit 3.4 — Estado Vazio e Transições de Conteúdo
**Objetivo:** Refinar a experiência de troca entre menus e o estado inicial do app.

### Especificações Técnicas:
- [ ] **Empty State (Placeholder)**:
    - Se nenhuma configuração estiver selecionada (ou no carregamento inicial), exibir um ícone grande (ex: `KronoIcons.Settings.Logo`) com opacidade reduzida e o texto "Selecione uma categoria para configurar".
- [ ] **Animação de Transição**:
    - Usar `AnimatedContent` para alternar entre os painéis.
    - Transição sugerida: `fadeIn + slideInHorizontally` para uma sensação de "navegação para frente".
- [ ] **Independent Scroll State**:
    - Garantir que cada painel de conteúdo resete o seu `ScrollState` ao ser aberto, ou mantenha-o se for uma navegação lateral rápida.

**Msg Sugerida:** `ui: add empty state placeholder and panel transition animations`

---

## Checklist de Validação do Passo 3:
- [ ] O título no topo do painel direito muda corretamente ao clicar no menu?
- [ ] As configurações dentro dos cards têm espaçamento consistente (nem muito apertado, nem muito solto)?
- [ ] Ao rolar o conteúdo da direita, o menu da esquerda permanece estático?
- [ ] O "Empty State" está centralizado horizontal e verticalmente no painel de conteúdo?