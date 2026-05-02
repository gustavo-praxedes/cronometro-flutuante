# Relatório de Incidente: Falha de Build no CountdownOverlay

**Data:** 30 de Abril de 2026  
**Status:** Resolvido  
**Componentes Afetados:** `CountdownOverlayManager.kt`, `CountdownOverlayUi.kt`

## 1. Descrição do Erro
O build do aplicativo falhou com os seguintes erros de compilação:

1.  **CountdownOverlayManager.kt (Linha 196):** `No value passed for parameter 'selectedTheme'`.
    *   O componente `KronoTheme` foi atualizado para exigir obrigatoriamente o parâmetro `selectedTheme`, mas a chamada no gerenciador de overlay de contagem regressiva não passava esse valor.
2.  **CountdownOverlayUi.kt (Linhas 52 e 58):** `Unresolved reference 'launch'` e `Suspension functions can only be called within coroutine body`.
    *   O código tentava utilizar a função `launch` dentro de um `LaunchedEffect` para disparar animações de entrada em paralelo, porém o import necessário (`kotlinx.coroutines.launch`) não estava presente no arquivo. Isso causava o erro secundário de função de suspensão (`animateTo`) ser chamada fora de um escopo de corrotina reconhecido pelo compilador.

## 2. Causa Raiz
*   **Mudança de Contrato de API:** A atualização do sistema de temas (`KronoTheme`) não foi propagada para todos os locais onde o componente era instanciado.
*   **Import Faltante:** Copiar padrões de animação de outros arquivos (`FloatingTimerUi.kt`) sem incluir as dependências de corrotinas necessárias.

## 3. Solução Adotada

### 3.1. Correção do Tema
No arquivo `CountdownOverlayManager.kt`, a chamada ao tema foi corrigida para incluir um tema padrão:
```kotlin
// Antes
KronoTheme { ... }

// Depois
KronoTheme(selectedTheme = KronoThemeOption.AUTO.name) { ... }
```
*Também foi adicionado o import `com.krono.app.ui.theme.KronoThemeOption`.*

### 3.2. Correção das Corrotinas
No arquivo `CountdownOverlayUi.kt`, foi adicionado o import faltante:
```kotlin
import kotlinx.coroutines.launch
```
Isso permitiu que o compilador resolvesse a função `launch` dentro do `LaunchedEffect`, criando o escopo necessário para as chamadas de `animateTo`.

## 4. Lições Aprendidas
*   Sempre verificar todas as referências ao atualizar componentes de alto nível como `KronoTheme`.
*   Atenção redobrada a imports ao replicar padrões de UI complexos que envolvam animações e corrotinas.
