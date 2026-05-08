# Especificação Detalhada — PASSO 1: Fundação de UI e Sistema de Design

## Visão Geral
Estabelecer a infraestrutura atômica necessária para o redesenho. O foco não é a lógica de negócio, mas a criação de componentes "puros" e tokens que respeitem a arquitetura de design do Krono e os padrões do Material 3.

---

## Commit 1.1 — Expansão de Design Tokens (KronoTokens)
**Objetivo:** Garantir que não existam valores "hardcoded" nos componentes. Toda a geometria e espaçamento deve vir do sistema de tokens.

### Especificações Técnicas:
- [ ] **Shapes**:
    - `Shape.Card`: Definir como `RoundedCornerShape(16.dp)` para os grupos de configurações.
    - `Shape.IconBox`: Definir como `RoundedCornerShape(10.dp)` para os receptáculos de ícones.
- [ ] **Dimensions**:
    - `Size.IconBox`: Padronizar em `40.dp` ou `44.dp`.
    - `Size.IconInner`: Padronizar em `20.dp` ou `22.dp` para ícones dentro do box.
    - `Spacing.RowInner`: Espaçamento interno de `12.dp` entre ícone e texto.
- [ ] **Colors**:
    - Garantir que `SurfaceVariant` e `SecondaryContainer` existam para estados ativos e backgrounds de ícones.

**Msg Sugerida:** `style: expand KronoTokens with card and iconbox geometry`

---

## Commit 1.2 — Componente Atômico: `SettingsRow`
**Objetivo:** Criar o "bloco de construção" universal para todos os itens de menu.

### Especificações Técnicas:
- [ ] **Estrutura de Slots**:
    - `leadingIcon`: Um `ImageVector?` opcional envolto em um `Surface` com `Shape.IconBox`.
    - `content`: Coluna contendo `title` (Material 3 `bodyLarge`) e `subtitle` (Material 3 `bodySmall` com cor `onSurfaceVariant`).
    - `trailing`: Slot genérico `@Composable` para aceitar `Switch`, `Chevron`, ou `Text` (valor da config).
- [ ] **Interação**:
    - Aplicar `Modifier.clickable` apenas se um `onClick` for fornecido.
    - Altura mínima de `56.dp` para garantir a zona de toque (Touch Target Psychology).
- [ ] **Visual**:
    - Alinhamento vertical centralizado de todos os elementos.

**Msg Sugerida:** `ui: implement universal SettingsRow component with slot API`

---

## Commit 1.3 — Componentes de Seleção: `KronoToggle` & `DropdownRow`
**Objetivo:** Padronizar os controles de entrada de dados.

### Especificações Técnicas:
- [ ] **KronoToggle**:
    - Estilizar o `Switch` nativo do Compose para ter um visual mais retangular/moderno.
    - Garantir que a área de toque cubra a linha inteira do `SettingsRow` onde ele estiver inserido.
- [ ] **KronoDropdown**:
    - Implementar utilizando `ExposedDropdownMenuBox`.
    - Design "Clean": O menu deve abrir como um card flutuante com elevação sutil.
    - Utilizar para seletores de Idioma, Fonte e Temas.

**Msg Sugerida:** `ui: add stylized KronoToggle and ExposedDropdownRow`

---

## Commit 1.4 — Sistema de Diálogos: `KronoDialog`
**Objetivo:** Criar a base para as janelas flutuantes de confirmação (limpeza de dados) e diálogos informativos.

### Especificações Técnicas:
- [ ] **Comportamento de Fechamento**:
    - Configurar `DialogProperties(usePlatformDefaultWidth = true)`.
    - `onDismissRequest`: Acionar o fechamento ao clicar fora da área do card (sem botão "X").
- [ ] **Layout do Diálogo**:
    - **Backdrop (Scrim)**: Escurecimento configurado para focar o conteúdo central.
    - **Container**: `Surface` com `Shape.Card` e `padding(24.dp)`.
    - **Botões**: Alinhados à direita, utilizando `TextButton` para ações secundárias e `Button` (ou `ButtonError` para ações sensíveis) para primárias.
- [ ] **Animação**:
    - Entrada suave com `fadeIn` e `scaleIn` (opcional, conforme `KronoTokens.Animation`).

**Msg Sugerida:** `ui: implement modern KronoDialog with scrim and click-outside dismiss`

---

## Checklist de Validação do Passo 1:
- [ ] Os ícones estão centralizados dentro dos seus boxes de cor?
- [ ] O texto do subtítulo está legível em Light e Dark mode?
- [ ] O `SettingsRow` responde visualmente ao clique (Ripple effect)?
- [ ] O diálogo é dispensado corretamente ao tocar fora dele?