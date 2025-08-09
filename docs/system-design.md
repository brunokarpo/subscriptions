# System Design

## Descrição
Esse System Design descorre sobre uma proposta de solução para o seguinte desafio:
Você trabalha para uma empresa que fornece um Software As A Service (SaaS) e os clientes desse sistema precisam pagar uma mensalidade para usufruir das funcionalidades do sistema. Toda operação que o cliente envia para o serviço contém um token. Esse token guarda as seguintes propriedades do cliente: identificador do cliente, serviços autorizados para o cliente, data de vencimento.

{
	"client-id": "uuid",
	"enabled-services": ["service1", "service2", "service3"],
	"endAt": "2025-08-09T12:00:00Z"
}

Esse token é enviado como um objeto chamado "Chave de Ativação". A Chave de Ativação é basicamente o conteúdo do Token, porém encriptado usando a Chave Privada da Companhia que fornece o SaaS. Isso porque, quando o SaaS recebe uma requisição com a Chave de Ativação, o sistema tem a chave pública relativa a Chave Privada que encriptou o Token, de forma que garanta que alguém mal intencionado não consigará gerar um token válido para acessar indevidamente um serviço se esse token não for fornecido pela empresa.


## Requisitos Funcionais

### *Cadastrar um novo serviço*
O sistema deve possibilitar a criação de um novo serviço que será oferecido para os clientes. O serviço deve possuir, inicialmente, um nome que deve ser único no sistema.

### *Cadastrar um novo cliente*
O sistema deve possibilitar o cadastro de novos clientes. Os clientes deve possuir um nome e um email. O email deve ser único no sistema. Não pode haver dois clientes com o mesmo email cadastrado.

### *Assinatura de serviço*
Deve ser possível que o cliente assine um ou vários serviços. Essa assinatura deve ser "aprovada" por algum processo interno e quando essa aprovação acontecer deve ser gerado uma chave de ativação com validade de 40 dias. Caso o cliente já tenha uma assinatura prévia ativa, a assinatura antiga deve ser "invalidada" e a nova assinatura deve ser gerada com a data de validade da assinatura anterior.

### *Renovação de chaves de ativação*
O sistema deve, diariamente, validar quais são as assinaturas cujo a chave de ativação vencerão nas próximas 48 horas. As assinaturaos ativas (clientes adimplentes) devem ser renovadas por mais 30 dias. As assinaturas inativas (clientes inadimplentes) não devem gerar chave de ativação.

### *Ativar / desativar clientes*
O sistema deve fornecer uma API administrativa que marca um cliente como adimplente/inadimplente de forma que bloqueie a geração de novas chaves de ativação.

### *Buscar chave de ativação ativa*
O sistema deve forncer uma API para que os clientes busquem sua chave de ativação atual.

## Requisitos não funcionais
### Atributos de qualidades

#### Consistência
O sistema deve garantir que suas respostas, principalmente a busca pela chave de ativação, retorne um valor consistente.

#### Segurança
O sistema deve garantir formas de nunca deixar a chave privada da companhia exposta. Ninguém deve ser capaz de acessar indevidamente essa chave, mesmo os desenvolvedores da aplicação.

### Dados importantes

| Descrição 									| Dados									|
|-----------------------------------------------|---------------------------------------|
| Quantidade de serviços possíveis 				| 100 serviços 							|
| Quantidade esperada de clientes 				| 100.000 clientes ativos 				|
| Daily Active Users 							| 4000 									|
| Quantidade de requests por cliente por dia 	| 10 									|
| Ratio Read:Write 								| 9:1 				 					|
| Payload médio									| 150 caracteres; 1200 bits ; 150 bytes	|


## Estimativa de Capacidade

### *Throughtput*
**Daily Active Users * Quantidade de requests por cliente por dia / 24 / 60**
Aproximadamente 30 RPM (Requests Por Minuto).

**Ratio Read:Write**
Aproximadamente 27 requests de leitura e 3 de escrita.

### *Taxa de crescimento da base de dados*
Write per minute: 3
Payload médio: 150 bytes

Taxa de crescimento por minuto: 450 bytes
Taxa de crescimento por dia: 	648000 bytes ; ~ 650 Megabytes
Taxa de crescimento por ano:	237250 Megabytes ; ~ 240 Gigabytes
Taxa de crescimento em 5 anos: 	1,2 Terabytes


## Modelagem de Dados
![SistemaAtivação-ERM.jpg](SistemaAtiva%C3%A7%C3%A3o-ERM.jpg)

## Modelagem de APIs


## System Design


## Alternativas consideradas


## Preocupações transversais

