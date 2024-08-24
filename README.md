# Trabalho de Banco de Dados

Repositório destinado ao Desenvolvimento de um Marketplace de venda e aluguel de filmes e séries

## Descrição do Projeto

Este projeto tem como objetivo desenvolver o backend de um marketplace voltado para a venda e aluguel de filmes. A loja virtual permitirá que os usuários explorem uma vasta coleção de filmes, divididos em categorias e gêneros, podendo optar por comprar ou alugar títulos de interesse. O sistema será desenvolvido seguindo o padrão de arquitetura MVC (Model-View-Controller), utilizando uma API REST para comunicação e o banco de dados PostgreSQL para armazenamento dos dados.

## Funcionalidades Principais

- Cadastro de Filmes: Os administradores poderão cadastrar novos filmes, incluindo informações como título, diretor, ano de lançamento, gênero, descrição, preço de venda e aluguel.

- Cadastro de Clientes: Os clientes poderão se registrar na plataforma, fornecendo informações pessoais como nome, e-mail e endereço.

- Gerenciamento de Compras e Aluguéis: O sistema permitirá que os clientes comprem ou aluguem filmes, gerando registros de transações no banco de dados.

- Buscas e Filtragens: Os clientes poderão buscar filmes por palavra-chave, gênero, ou por diretor, e também visualizar detalhes de um filme específico.

- Carrinho de Compras: Implementação de um carrinho de compras, onde os clientes podem adicionar itens antes de finalizar a compra ou aluguel.

## Relatórios
### 1. Relatório de Vendas por Período
- Descrição: Este relatório mostrará o total de vendas realizadas em um determinado período, permitindo analisar o desempenho da loja.
- Dados Incluídos: Data da venda, título do filme, quantidade vendida, receita gerada.
- Consultas SQL Utilizadas: Agregações (SUM), agrupamentos (GROUP BY), ordenações (ORDER BY).

### 2. Relatório de Aluguéis por Período
- Descrição: Semelhante ao relatório de vendas, este relatório focará nos aluguéis de filmes, permitindo identificar os filmes mais alugados e as tendências de aluguel ao longo do tempo.
- Dados Incluídos: Data do aluguel, título do filme, quantidade de vezes alugado.
- Consultas SQL Utilizadas: Agregações (COUNT), agrupamentos (GROUP BY), ordenações (ORDER BY).

### 3. Relatório de Filmes Mais Vendidos/Alugados
- Descrição: Relatório que exibirá os filmes mais vendidos e mais alugados em um determinado período.
- Dados Incluídos: Título do filme, total de vendas, total de aluguéis.
- Consultas SQL Utilizadas: Junções (JOIN), agregações (SUM, COUNT), ordenações (ORDER BY).

### 4. Relatório de Receitas
- Descrição: Relatório de receitas da loja, detalhando a receita total gerada por vendas e aluguéis em diferentes períodos.
- Dados Incluídos: Receita por venda, receita por aluguel, receita total.
- Consultas SQL Utilizadas: Agregações (SUM), agrupamentos (GROUP BY), ordenações (ORDER BY).

## Tecnologias Utilizadas
- Java EE: Stack principal para o desenvolvimento do backend.
- PostgreSQL: Banco de dados relacional para armazenar as informações da loja, filmes, clientes, e transações.
- Spring Framework: Para facilitar a criação de APIs REST e organização do código.
- JSON: Formato de troca de dados entre frontend e backend

## Documentação adicional

- API Utilizada: [OMDB](https://www.omdbapi.com/)
