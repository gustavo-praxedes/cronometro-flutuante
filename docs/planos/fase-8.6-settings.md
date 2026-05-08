# Especificação Detalhada — PASSO 6: Informações, Sobre e Ações

## Visão Geral
Este passo finaliza a experiência de configurações, transformando o painel "Sobre" em uma central de transparência e suporte. Vamos focar em branding, integração com GitHub, logs de alteração e opções de apoio ao desenvolvimento.

---

## Commit 6.1 — Branding Header e Identidade do App
**Objetivo:** Criar um cabeçalho de impacto que identifique claramente a versão e o propósito do app.

### Especificações Técnicas:
- [ ] **Hero Section**:
    - Centralizar o logo do Krono (`KronoIcons.Settings.Logo`) com um tamanho maior (`80.dp`).
    - Exibir o nome do app em `headlineMedium`.
    - Exibir a versão (`BuildConfig.VERSION_NAME`) e o build em um `Badge` ou texto secundário sutil.
- [ ] **Estatísticas de Uso (Opcional/Contextual)**:
    - Se disponível, adicionar uma linha discreta com o "Tempo Total de Foco" ou "Lifetime" do app para gerar conexão com o usuário.

**Msg Sugerida:** `ui: implement branding header in About panel`

---

## Commit 6.2 — Links da Comunidade e Transparência
**Objetivo:** Facilitar o acesso ao repositório e documentos legais.

### Especificações Técnicas:
- [ ] **Grupo "PROJETO"**:
    - `Código Fonte (GitHub)`: Usar `TrailingType.External`.
    - `Documentação`: Link para o Wiki ou site oficial.
    - `Política de Privacidade`: Acesso rápido via navegador.
- [ ] **Componente de Linha**:
    - Garantir que cada item use o componente padronizado com ícones coloridos (ex: azul para GitHub, roxo para Docs) para facilitar o reconhecimento visual.

**Msg Sugerida:** `ui: add community and project links to About section`

---

## Commit 6.3 — Central de Atualizações e Changelog
**Objetivo:** Unificar a verificação de novas versões e o histórico de melhorias.

### Especificações Técnicas:
- [ ] **Integração de Check**:
    - Adicionar um botão "Verificar Atualizações" que dispare a lógica do `UpdatesPanel`.
    - Exibir um estado de "App atualizado" com um ícone de check verde.
- [ ] **Histórico de Versões**:
    - Criar um ponto de entrada para o `ChangelogPanel`.
    - Mostrar um resumo da última grande funcionalidade (ex: "Novidade: Modo Foco 2.0").

**Msg Sugerida:** `ui: integrate update checker and changelog entry point`

---

## Commit 6.4 — Fluxo de Suporte e Doação
**Objetivo:** Oferecer formas claras do usuário contribuir com o projeto.

### Especificações Técnicas:
- [ ] **Card de Apoio**:
    - Usar uma cor de destaque (ex: `Tertiary` ou um tom de rosa/ouro) para o card de suporte.
    - Título: "Apoie o Desenvolvimento".
    - Descrição: Texto curto explicando que o projeto é open-source.
- [ ] **Ações de Feedback**:
    - Link direto para "Reportar Bug" (abre o `BugReportDialog`).
    - Link para "Sugerir Funcionalidade".

**Msg Sugerida:** `ui: implement support card and feedback actions`

---

## Checklist de Validação do Passo 6:
- [ ] O logo no topo do painel "Sobre" está devidamente centralizado e nítido?
- [ ] Todos os links externos (GitHub, Privacidade) abrem corretamente no navegador?
- [ ] A versão exibida corresponde exatamente ao `versionName` definido no `build.gradle`?
- [ ] O card de suporte se destaca visualmente dos demais sem quebrar a harmonia do Material 3?
- [ ] O botão de verificar atualizações exibe um feedback visual (spinner ou texto) durante o processo?