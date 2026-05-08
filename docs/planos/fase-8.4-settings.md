# Especificação Detalhada — PASSO 4: Refinação de Aparência e Seletores

## Visão Geral
Transformar o painel de "Aparência" em uma vitrine visual do app. Este passo foca em tornar a escolha de temas, fontes e cores uma experiência tátil e visualmente rica, eliminando componentes "soltos" e padronizando a largura dos seletores.

---

## Commit 4.1 — Padronização de Theme & Font Selector
**Objetivo:** Integrar os seletores de tema e fonte para que não pareçam componentes genéricos jogados na tela.

### Especificações Técnicas:
- [ ] **Enclausuramento**:
    - Colocar o `ThemeSelector` e o `FontSelector` dentro de um `SettingsGroup` chamado "ESTILO VISUAL".
- [ ] **Largura e Alinhamento**:
    - Forçar `fillMaxWidth()` em ambos os seletores.
    - Garantir que o texto interno dos dropdowns/segmentos use `KronoType.bodyMedium`.
- [ ] **Visual**:
    - Usar `MaterialTheme.colorScheme.surfaceVariant` para o background do seletor inativo.
    - Adicionar um ícone sutil de "Pincel" (`KronoIcons.Settings.Appearance`) ao lado do título do grupo.

**Msg Sugerida:** `ui: standardize Theme and Font selector width and grouping`

---

## Commit 4.2 — Redesign do Color Swatch (Preview de Cores)
**Objetivo:** Corrigir o "retângulo preto" e transformar o seletor de cores em uma linha informativa e interativa.

### Especificações Técnicas:
- [ ] **Componente `ColorRow`**:
    - Substituir o seletor atual por uma `SettingsRow` customizada.
    - **Leading**: Um círculo de cor (`Surface` com `Shape.CircleShape`) de `24.dp`.
    - **Content**: Título (ex: "Cor de Fundo") e Subtítulo com o código Hex (ex: "#121212").
    - **Trailing**: Botão de "Editar" ou o próprio chevron que abre o `ColorPickerDialog`.
- [ ] **Feedback Visual**:
    - Adicionar uma borda fina (`0.5.dp`) ao redor do círculo de cor caso a cor selecionada seja muito próxima à cor de fundo da UI (evitando que o preview "desapareça").

**Msg Sugerida:** `ui: replace color rectangles with labeled circular swatches`

---

## Commit 4.3 — Refino de Padding e Respiro Visual
**Objetivo:** Ajustar o espaçamento interno do painel de aparência para evitar fadiga visual.

### Especificações Técnicas:
- [ ] **Hierarquia de Spacing**:
    - Distância entre o Header da seção e o primeiro card: `KronoTokens.Spacing.lg`.
    - Distância interna entre itens do mesmo card: `0.dp` (separados apenas pela `HorizontalDivider`).
    - Distância entre cards de grupos diferentes: `KronoTokens.Spacing.xl`.
- [ ] **Margens Laterais**:
    - Aplicar `padding(horizontal = KronoTokens.Spacing.lg)` em todo o painel de conteúdo.

**Msg Sugerida:** `style: adjust appearance panel spacing and vertical margins`

---

## Commit 4.4 — Preview em Tempo Real (Live Polish)
**Objetivo:** Garantir que as mudanças de cor e tema reflitam instantaneamente no painel de configurações sem causar saltos na UI.

### Especificações Técnicas:
- [ ] **Animação de Cor**:
    - Usar `animateColorAsState` para a transição do círculo de preview e dos backgrounds dos cards quando o tema (Light/Dark) for trocado.
- [ ] **Estado de Seleção**:
    - No `FontSelector`, destacar a fonte ativa com uma cor de acento (`Primary`) e um ícone de "check" sutil.

**Msg Sugerida:** `ui: implement smooth color transitions and font selection states`

---

## Checklist de Validação do Passo 4:
- [ ] O círculo de preview de cor mostra o código Hex correto abaixo ou ao lado?
- [ ] O `ThemeSelector` ocupa toda a largura disponível dentro do card?
- [ ] Ao trocar de fonte, o preview do texto no painel de configurações atualiza instantaneamente?
- [ ] O espaçamento entre o grupo "Temas" e "Cores" permite distinguir claramente as seções?