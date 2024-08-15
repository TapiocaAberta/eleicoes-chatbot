# Eleições ETL

Este é um [ETL (Extract - Transform - Load)](https://www.ibm.com/br-pt/topics/etl) simples desenvolvido para unir alguns dados retirados do site do [TSE](https://dadosabertos.tse.jus.br/dataset/candidatos-2024) para os candidatos às eleições Municipais de 2024. A princípio, este ETL apenas trabalha com os dados de SP, para o município de São José dos Campos, mas isso pode ser facilmente editável.

## Como Funciona?

Você precisar fazer o download dos zips presentes em [TSE](https://dadosabertos.tse.jus.br/dataset/candidatos-2024), hoje fazemos o ETL dos seguintes arquivos:

* Candidatos
* Bens de candidatos
* Redes sociais de candidatos

Após o download de cada item faça o unzip dos mesmos. 

O coódigo irá gerar um texto para cada candidado com o conteúdo abaixo, e irá salvar o mesmo em uma pasta definida no formato `cd_candidato.md`, onde `cd_candidato` é um código único gerado pelo proprio TSE.

```{md}
# {CARGO}
## {NOME CANDIDATP} - {PARTIDO NOME} ({PARTIDO SIGLA})
---
**{NOME CANDIDATO}**, é candidato a **{CARGO}** pelo município de **{NOME MUNICÍPIO} - {UF}** na eleição **{TIPO ELEIÇÃO}** de **{ANO}** pelo **{PARTIDO NOME} ({PARTIDO SIGLA})**.
**{NOME CANDIDATO}** é **{ESTADO CIVIL}**, nasceu em **{ANO NASCIMENTO}**, se declara do gênero **{GENERO}** da cor/raça **{RAÇA/COR}**, sua ocupação é **{OCUPAÇÃO}**, e seu grau de instrução é **{GRAU DE INSTRUÇÃO}**.
Seu nome de urna é **{NOME URNA}**, e seu número de urna é **{NUMERO URNA}**.

Os bens declarados do candidato(a) {NOME CANDIDATO} são:
- {LISTA COM OS BENS}

As redes sociais de {NOME CANDIDATO} são:
- {LISTA COM AS REDES}
```

Caso o candidato não tenha declarado bens, veremos: `O Candidato(a) {NOME CANDIDATO} não declarou bens.` e,canso não tenha declarado redes sociais, veremos: `O Candidato(a) MARIA {NOME CANDIDATO} não forneceu dados das redes sociais.`

Você pode ver esses arquivos neste mesmo repositório, em [data](https://github.com/TapiocaAberta/eleicoes-chatbot/tree/main/data).
## Como exacutar?

Você precisa ter os basicamente:

- Os dados baixados do TSE
- JDK 17+ instalado
- [JBang](https://www.jbang.dev/) instalado

Algumas modificações precisam ser feitas no código, para apontar para a pasta onde você fez o download dos dados. Abra o arquivo `EleicoesETL.java` em qualquer editor e modifique:

```{java}
private static final String DATA_MD_PATH = ""; (1)

private static final String ELEICOES_PATH = ""; (2)

private static final String CAND_INFO_FILE = ELEICOES_PATH + ""; (3)

private static final String BENS_CAND_FILE = ELEICOES_PATH + ""; (4)

private static final String REDE_SOCIAL_CAND_FILE = ELEICOES_PATH + ""; (5)
```

1. Path onde os dados gerados serão salvos;
2. Path onde estão as pastas com dados;
3. Path onde está o `csv` com os dados dos candidatos. Ex. `consulta_cand_2024_SP.csv`;
4. Path onde está o `csv` com os dados dos bens dos candidatos. Ex. `bem_candidato_2024_SP.csv`;
5. Path onde está o `csv` com os dados das redes sociais dos candidatos. Ex. `rede_social_candidato_2024_SP.csv`;

Ainda existe a possibilidade de mudar os seguintes parametros:

```{java}
private static final String SEPARATOR = ";"; (1)
private static final String MUNICIPIO = "SÃO JOSÉ DOS CAMPOS"; (2)
private static final String CHARSET = "ISO-8859-1"; (3)
```

Os arquivos `csv` são separados por `;` (1) e o charset dos arquivos são `ISO-8859-1` (3), mas podem ser alterados, caso necessário.

Você pode escolher outro municipio de SP para baixar, mas certifique de mudar o campo (2) com o mesmo nome que estão nos arquivos CSV. Caso queira mudar de estado, altere o caminho dos arquivos CSV para o correspondente estado, e então altere o nome do município em 2.

Para executar, basta rodar o comando `jbang`:

```
jbang EleicoesETL.java
```

## Contribuição

Claro que este é um ETL bem simples e muitas melhorias podem ser feitas. Não hesite em contribuir, seja escrevendo uma [Issue](https://github.com/TapiocaAberta/eleicoes-chatbot/issues) ou nos enviando um Pull Request. Ficaremos contentes em receber sua ajuda!

## License

* [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)