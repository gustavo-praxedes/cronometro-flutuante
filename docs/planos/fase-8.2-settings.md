# Especificação Detalhada — PASSO 2: Menu de Navegação e Arquitetura Lateral

## Visão Geral
Migrar a navegação simples para um sistema de "Navigation Sidebar" baseado em grupos. O menu deve fornecer feedback visual imediato sobre a posição do usuário (Estado Ativo) e separar as ferramentas das informações do sistema.

---

## Commit 2.1 — Agrupamento Lógico e Labels de Seção
**Objetivo:** Organizar os destinos existentes em três categorias: Geral, Ferramentas (Tools) e Info.

### Especificações Técnicas:
- [ ] **Mapeamento de Categorias**:
    - **GERAL**: Aparência, Comportamento, Overlay.
    - **TOOLS**: Cronômetro, Contagem Regressiva.
    - **INFO**: Sobre, Apoiar, Novidades, Atualizações.
- [ ] **Section Labels**:
    - Criar componente `SettingsSectionLabel`.
    - Estilo: Texto em `Uppercase`, fonte `labelSmall` ou `labelMedium`.
    - Cor: `MaterialTheme.colorScheme.primary`.
    - Espaçamento: `8.dp` de padding inferior antes do card.
- [ ] **Estrutura de Card**:
    - Envolver cada grupo de itens em um `Surface` ou `Box` usando o `Shape.Card` criado no Passo 1.

**Msg Sugerida:** `ui: group settings destinations into logical sections with labels`

---

## Commit 2.2 — Estado Ativo e Indicadores de Seleção
**Objetivo:** Implementar a sinalização visual do item selecionado seguindo o padrão Material 3 para Navigation Rail/Drawer.

### Especificações Técnicas:
- [ ] **Container de Seleção**:
    - O item ativo deve ter o background alterado para `MaterialTheme.colorScheme.secondaryContainer`.
    - Aplicar `clip(Shape.Card)` para que o fundo acompanhe o arredondamento do menu.
- [ ] **Indicador Lateral**:
    - Adicionar uma barra vertical de `4.dp` de largura na extremidade esquerda do item ativo.
    - Cor do indicador: `MaterialTheme.colorScheme.primary`.
- [ ] **Hierarquia de Texto**:
    - Item Ativo: `FontWeight.SemiBold` + Cor `onSecondaryContainer`.
    - Item Inativo: `FontWeight.Normal` + Cor `onSurface`.

**Msg Sugerida:** `ui: implement active state indicators and container styling`

---

## Commit 2.3 — Refinamento de Ícones e Reações de Toque (Ripple)
**Objetivo:** Melhorar a interatividade e a clareza visual dos ícones de navegação.

### Especificações Técnicas:
- [ ] **Comportamento do Ícone**:
    - Se o item estiver ativo, o ícone deve usar a cor `primary` ou `onSecondaryContainer`.
    - Se inativo, usar `onSurfaceVariant` com opacidade de 60%.
- [ ] **Configuração do Ripple**:
    - Garantir que o efeito de clique (`Indication`) respeite os limites do card arredondado.
- [ ] **Remoção de Chevrons (Navegação Interna)**:
    - No menu esquerdo, remover todos os chevrons (`>`). O estado ativo (background colorido) já é o sinalizador de navegação.

**Msg Sugerida:** `style: refine navigation icons and touch feedback`

---

## Commit 2.4 — Layout Adaptativo e Scroll Independente
**Objetivo:** Garantir que o menu se comporte corretamente em diferentes alturas de tela.

### Especificações Técnicas:
- [ ] **Scroll Isolado**:
    - Aplicar `Modifier.verticalScroll(rememberScrollState())` apenas à coluna do menu esquerdo.
    - O menu não deve "empurrar" o conteúdo da direita para cima ao rolar.
- [ ] **Divisor Vertical**:
    - Implementar uma `VerticalDivider` sutil (0.5.dp) com cor `outlineVariant` entre a navegação e o painel de conteúdo.
- [ ] **Largura Fixa/Flexível**:
    - Definir largura fixa (ex: `280.dp` a `320.dp`) para o menu em telas horizontais para evitar estiramento.

**Msg Sugerida:** `layout: implement independent vertical scroll and sidebar divider`

---

## Checklist de Validação do Passo 2:
- [ ] O usuário consegue identificar em qual seção está apenas olhando para a esquerda?
- [ ] O scroll do menu de categorias é fluido e não interfere no painel da direita?
- [ ] Os títulos das seções (GERAL, TOOLS) estão alinhados com o início dos cards?
- [ ] A transição de cor entre um item e outro ao clicar é instantânea e sem "glitch" visual?