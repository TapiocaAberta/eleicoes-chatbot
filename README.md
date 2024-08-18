# eleicoes-chatbot

Este é um chatbot feito em [Quarkus](https://quarkus.io/), usando [Langchain4J](https://docs.langchain4j.dev/), [Ollama](https://ollama.com/) com [llama 3.1](https://ollama.com/library/llama3.1). Usamos [RAG](https://arxiv.org/abs/2005.11401) (Retrieval Augmented Generation) como técnica.

OS dados foram todos retirados do site do [TSE](https://dadosabertos.tse.jus.br/dataset/candidatos-2024), tratados com o [ETL](/etl/) e armazenados na pasta [/data](/data/) deste repositório.

Para executar o projeto, é necessário ter os dados do ETL, Olla com Llama 3.1 e um ambiente Java para executar o projeto Quarkus.

Acessando localhost:8080 você verá um chat bot, onde poderá fazer as perguntas que quiser.

![exemplo 1](/images/exemplo.png "Exemplo 1")

![exemplo 2](/images/exemplo_2.png.png "Exemplo 2")

## Contribuição

Claro que este é apenas um exemplo de chatbot muitas melhorias podem ser feitas. Não hesite em contribuir, seja escrevendo uma [Issue](https://github.com/TapiocaAberta/eleicoes-chatbot/issues) ou nos enviando um Pull Request. Ficaremos contentes em receber sua ajuda!

## License

* [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)