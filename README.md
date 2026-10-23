# 📱 Documentação do Projeto Mobile - Segunda Entrega

Esta documentação tem como objetivo registrar a evolução do aplicativo, detalhando a história das decisões do trio, a arquitetura adotada, a evolução das telas e como superamos os desafios de desenvolvimento nesta segunda etapa.

---

## 1. Evolução do Projeto: Do Trabalho 1 ao Estado Atual

### 🔄 Como estava o projeto no Trabalho 1 e o que mudou?
No **Trabalho 1**, tínhamos um protótipo inicial composto por apenas **3 telas estáticas/isoladas**:
1. `HomeScreen`
2. `DetalhesFilme`
3. `Review`

A navegação era básica, os dados eram voláteis (sem sincronização real entre fluxos) e o layout ainda carecia de um padrão visual consolidado.

Para esta **segunda entrega**, o aplicativo passou por um processo completo de refatoração e expansão:
- **Novas Telas:** Desenvolvemos 4 novas telas (`Lista de Comentários`, `Comentário Detalhado`, `Perfil` e `Lista de Filmes Pessoais / Minha Lista`).
- **Navegação Global e Scaffold Padronizado:** Criamos uma estrutura global utilizando `TopBar` e `BottomBar` persistentes nas telas principais, garantindo usabilidade e consistência de UI/UX.
- **Gerenciamento de Estado:** Implementamos um `MainViewModel` compartilhado para centralizar a regra de negócio e permitir a persistência e reatividade dos dados entre todas as telas.
- **Padronização Visual:** Harmonizamos componentes (botões, cards, cores e tipografia) para dar um aspecto visual coeso ao aplicativo.

---

## 2. Novas Telas e Decisões de Design

Abaixo apresentamos o motivo de criação de cada uma das novas telas e o papel que desempenham na experiência do usuário.

### 🎬 A. Lista de Comentários
* **O que faz:** Exibe a lista completa de avaliações e comentários deixados por outros usuários a respeito de um filme específico.
* **Por que escolhemos:** Filmes e mídias são consumidos de forma social. Permitir que o usuário leia opiniões de terceiros adiciona um valor indispensável para a tomada de decisão sobre assistir ou não a uma obra.

> 🖼️ **[PRINT DA TELA: Lista de Comentários]**  
<img width="480" alt="Captura de tela 10-08" src="https://github.com/user-attachments/assets/42678a19-f675-4514-a074-747f609c1ad2" />



---

### 💬 B. Comentário Detalhado
* **O que faz:** Ao clicar em um comentário específico na lista, o usuário é direcionado a esta tela para visualizar o texto na íntegra, além de informações detalhadas do autor, data e nota atribuída.
* **Por que escolhemos:** Evita que a tela de lista de comentários fique poluída com blocos enormes de texto, oferecendo uma navegação fluida (*Master-Detail pattern*).

> 🖼️ **[PRINT DA TELA: Comentário Detalhado]**  
<img width="480" alt="Captura de tela 2026-10-08 204630" src="https://github.com/user-attachments/assets/75b4e29b-e5e0-459b-9128-f34535c9ffe6" />


---

### 👤 C. Perfil do Usuário
* **O que faz:** Exibe informações do usuário logado (foto de perfil, nome, bio e métricas/estatísticas simples de filmes assistidos ou avaliados).
* **Por que escolhemos:** Traz o sentimento de personalização e identidade ao aplicativo, servindo também como ponto de ancoragem central dentro do menu inferior (`BottomBar`).

> 🖼️ **[PRINT DA TELA: Perfil]**  
<img width="480" alt="image" src="https://github.com/user-attachments/assets/daafd006-0cb6-4def-b898-8e52e8f7a861" />


---

### 📌 D. Lista de Filmes Pessoais ("Minha Lista")
* **O que faz:** Apresenta um **Grid** visualmente atrativo com todos os filmes salvos pelo usuário para assistir mais tarde ou marcados como favoritos.
* **Por que escolhemos:** É um recurso indispensável em apps do gênero (como Netflix ou Letterboxd). A exibição em formato de *Grid* otimiza o uso da tela em dispositivos móveis.

> 🖼️ **[PRINT DA TELA: Minha Lista / Grid de Filmes]**  
<img width="480" alt="image" src="https://github.com/user-attachments/assets/05d0c599-6f47-41f8-876f-3aa7be44e2df" />


---

## 3. Arquitetura, Configuração e Organização do Código

Para garantir sustentabilidade e facilidade de manutenção no código, adotamos a seguinte organização:

### 🛣️ Estrutura do NavHost e Rotas
* **Roteamento Centralizado:** Definimos uma classe/enum selada `Screen` com todas as rotas de navegação bem declaradas (passando argumentos como `filmeId` e `comentarioId` quando necessário).
* **Estrutura de Scaffold:** O `NavHost` foi inserido dentro do container principal envolvido pela `TopBar` e `BottomBar`. Condicionamos a exibição dessas barras para que apareçam somente nas telas principais do aplicativo, ocultando-as em fluxos secundários/de detalhe para dar mais espaço à leitura.

### 💾 Persistência de Dados e Estado (`MainViewModel`)
* Criamos uma instância única de **`MainViewModel`** associada ao contexto da Activity/NavHost.
* Toda a lista de filmes, status de "Minha Lista" e comentários cadastrados residem no `ViewModel` através de `StateFlow` / `mutableStateOf`. Dessa forma, se o usuário adiciona um filme à sua lista na tela de detalhes, a alteração reflete instantaneamente na tela de "Minha Lista" sem necessidade de re-fetch manual.

---

## 4. Complexidade Extra na Tela de Detalhes do Filme (Seção 3.2)

A tela de **Detalhes do Filme** recebeu uma reformulação significativa para atender aos requisitos de complexidade extra.

### 🚀 O que implementamos de complexidade extra?
1. **Integração Dinâmica de Estado:** Ações na tela de detalhes (como favoritar/salvar na "Minha Lista" ou adicionar uma nova avaliação) reagem imediatamente ao `MainViewModel` e atualizam a interface do usuário em tempo real.
2. **Seção de Mídia e Preview de Comentários:** Adicionamos um carrossel visual e um componente dinâmico de resumo dos últimos comentários deixados no filme, com atalho direto para a nova tela de `Lista de Comentários`.
3. **UI Responsiva e Efeitos Visuais:** Uso de `Coil` para carregamento de imagens com estados de skeleton/loading, gradiente dinâmico de sobreposição no banner principal do filme e formatação de dados dinâmicos.

* **Por que escolhemos essa solução?**  
  A tela de detalhes é a "vitrine principal" de qualquer aplicativo de entretenimento. Adicionar interatividade em tempo real e integração com o ViewModel central provou a maturidade da nossa arquitetura de dados.

> 🖼️ **[PRINT DA TELA: Detalhes do Filme (Melhorada)]**  
<img width="480" alt="image" src="https://github.com/user-attachments/assets/56b0c136-4b97-4705-9881-760e44b15429" />


---

## 5. Desafios Enfrentados e Soluções Encontradas

Durante esta etapa, o trio se deparou com alguns obstáculos técnicos:

* **Desafio 1: Compartilhamento de estado entre telas separadas pelo NavHost**
  * *Problema:* Inicialmente, ao criar instâncias de ViewModel dentro de cada tela individualmente, os dados de comentários e filmes salvos eram resetados ao navegar.
  * *Solução:* Passamos a injetar/compartilhar o mesmo `MainViewModel` escopado no `NavHost` raiz do aplicativo.

* **Desafio 2: Exibição condicional de TopBar e BottomBar**
  * *Problema:* A `BottomBar` aparecia em telas de detalhe profundo (ex: `ComentarioDetalhado`), onde ela prejudicava a experiência do usuário.
  * *Solução:* Mapeamos a rota atual via `currentBackStackEntryAsState()` no Jetpack Compose para esconder barras navegacionais em rotas específicas.

* **Desafio 3: Renderização do Grid e layout responsivo**
  * *Problema:* Ajustar o espaçamento e a proporção de aspecto dos cartazes de filmes no Grid da "Minha Lista".
  * *Solução:* Utilização de `LazyVerticalGrid` combinada com dimensões relativas e componentes de card padrão para adequação em diferentes tamanhos de tela.

