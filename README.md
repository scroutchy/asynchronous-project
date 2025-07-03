# Desafio BTG - Processamento Assíncrono

Este projeto é uma aplicação Kotlin/Spring Boot que demonstra o processamento assíncrono de pedidos utilizando Apache Kafka como sistema de
mensageria.

## Funcionamento Geral

A aplicação trata requisições de criação de pedidos via API REST (cada requisição contem um identificador de cliente e uma lista de itens).
Os pedidos criados são armazenados numa memória interna sob um status PENDING e um identificador único. Na sequencia uma mensagem kafka é
enviada num tópico para processamento assíncrono (com um delay artificial) que atualiza o status do pedido para PROCESSED. O status do
pedido pode ser consultado posteriormente por um endpoint de consultação.

Principais componentes:

- **Producer**: Envia mensagens identificando os pedidos para o Kafka.
- **Consumer**: Consome as mensagens do tópico e processa os pedidos (atualização do status).
- **API REST**: Permite criar pedidos e consultar o status.

## Como rodar localmente

### Pré-requisitos

- Java 17+
- Docker e Docker Compose
- Gradle (ou utilize o wrapper `./gradlew`)

### Configuração do delay artificial

É possível configurar um delay artificial para o processamento dos pedidos. Para isso, edite o arquivo `src/main/resources/application.yaml`
e ajuste a propriedade `kafka.processing.delay` com o valor desejado em milissegundos. A ausência dessa propriedade implica em um delay de 0
segundos (sem delay).

### 1. Subir o Kafka com Docker Compose

No diretório `misc/`, execute:

```bash
cd misc
docker-compose up -d
```

Isso irá iniciar um broker Apache Kafka para o funcionamento da aplicação.

### 2. Subir a aplicação

Na raiz do projeto, execute:

```bash
./gradlew bootRun
```

A aplicação estará disponível em `http://localhost:8080`.

### 3. Testar a API

#### via Postman

Pode-se utilizar o arquivo `misc/postman_collection.json` para importar no Postman e testar os endpoints disponíveis.

#### via cURL

Para criar um pedido, execute o seguinte comando cURL:

```bash
curl --request POST \
  --url http://localhost:8080/api/pedidos \
  --header 'content-type: application/json' \
  --data '{
  "clientId": "C003",
  "items": [
    {
      "description": "item1"
    },
    {
      "description": "item2"
    }
  ]
}'
```

Para resgatar um pedido, utilize:

```bash
curl --request GET \
  --url http://localhost:8080/api/pedidos/f8a04f00-4574-4674-acbb-23c7a0182300
```

usando o identificador único do seu pedido.

### 4. Parar os serviços

Para parar o Kafka:

```bash
cd misc
docker-compose down
```

---



