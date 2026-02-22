# 🌱 Sistema de Controle de Créditos de Carbono

Este é um sistema desenvolvido em **Java (console)** para controle, rastreabilidade e comercialização de créditos de carbono.

O projeto foi criado com base em modelagem estruturada e aplicação dos princípios da Programação Orientada a Objetos.

---

## 📐 Modelagem do Sistema

O desenvolvimento foi realizado em etapas:

### 🧩 1. Diagrama de Classes
O sistema foi inicialmente modelado através de um **Diagrama de Classes desenvolvido no LucidChart**, definindo:

- Classes do domínio
- Relacionamentos entre entidades
- Herança entre PF e PJ
- Regras de negócio

---


### 🏗 2. Modelo Lógico e Físico
Posteriormente foi desenvolvido o **Modelo Físico no diagram.io**, definindo:

- Entidades
- Atributos
- Relacionamentos
- Regras de cardinalidade
- Estrutura das tabelas
- Chaves primárias e estrangeiras

---

## 🎯 Objetivo do Sistema

Permitir o controle transparente de créditos de carbono, possibilitando:

- 🌳 Rastreabilidade ambiental
- 💰 Compra e venda de créditos
- 👥 Controle de copropriedade
- 📜 Histórico completo de transações

---

## 🚀 Funcionalidades

- ✅ Cadastro de Proprietários (Pessoa Física ou Pessoa Jurídica)
- 🌱 Registro de árvores geradoras com latitude e longitude
- 📦 Criação de lote fixo de 1000 créditos
- 👥 Até 3 proprietários simultâneos por lote
- 📊 Participação por quantidade (soma obrigatória = 1000)
- 💳 Registro completo de compra e venda
- 🧾 Histórico de participações e transações
- 📋 Relatório detalhado no console

---

## 📌 Regras de Negócio Implementadas

### 🌳 Rastreabilidade
Cada lote possui árvores geradoras com:
- Espécie
- Latitude
- Longitude

Permitindo identificar exatamente onde o crédito foi produzido.

---

### 📦 Comercialização
- O sistema comercializa apenas **lotes completos de 1000 créditos**
- Não existe venda fracionada de lote

---

### 👥 Copropriedade Simultânea
- Um lote pode ter entre **1 e 3 proprietários simultâneos**
- Cada proprietário possui uma quantidade de créditos
- A soma das participações deve ser exatamente **1000**

---

### 🔁 Histórico
Cada venda:
- Encerra as participações atuais
- Registra uma transação
- Cria nova participação para o comprador
- Mantém histórico completo com data de início e fim

---

## 🧱 Estrutura do Projeto

Classes principais:

- `SistemaCarbono`
- `LoteCreditoCarbono`
- `ParticipacaoLote`
- `TransacaoCompraVenda`
- `ArvoreGeradoraCredito`
- `Proprietario`
- `PessoaFisica`
- `PessoaJuridica`

---

## 🏛 Aplicação dos Pilares da POO

- 🔹 **Abstração** – Classe abstrata `Proprietario` e interface `Identificavel`
- 🔹 **Encapsulamento** – Atributos privados e validações internas
- 🔹 **Herança** – PF e PJ herdando de `Proprietario`
- 🔹 **Polimorfismo** – Tratamento genérico de proprietários

---

## ▶️ Como Executar o Projeto

### 🔍 Pré-requisitos
- Java JDK 17+ (ou compatível)

---

### ⚙️ Passos para rodar localmente

#### 1. Clone o repositório:

```bash
git clone [URL_DO_SEU_REPOSITORIO]
cd [NOME_DO_PROJETO]