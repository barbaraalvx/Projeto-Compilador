# Compiladores – Atividade 04 – Análise Léxica (EC1)

### Alunos:
- Bárbara Geovanna Alves Cavalcante - 20220055021
- Eduardo Alves Braz de Medeiros - 20210024881
- Luiz Gustavo Paixão da Gama - 20220005834

--- 

## Descrição:

Este projeto é um analisador léxico para a linguagem **EC1 (Expressões Constantes 1)**, desenvolvida na disciplina de Construção de Compiladores I.

A linguagem EC1 aceita expressões aritméticas compostas por:
- Números inteiros
- Operadores `+`, `-`, `*`, `/`
- Parênteses obrigatórios para estrutura

O analisador léxico:
- Lê um arquivo .txt com a expressão
- Remove comentários (delimitados com #)
- Gera uma sequência de tokens com tipo, lexema e posição
- Reporta erros léxicos (caracteres inválidos)

## Classes da versão:

Na versão inicial, o compilador da linguagem CI possui as seguintes classes de utilidade:

### FileReaderUtil

A classe FileReaderUtil é responsável pela leitura do arquivo .TXT (entre as opções presentes em inputs) passado como argumento na função principal.

### Token.java

A classe Token define um token léxico da linguagem EC1, que é composto por um tipo, um lexema e uma posição. Armazena os resultados da análise léxica.

### TypeEnum.java

A classe TypeEnum corresponde à enumeração que define todos os tipos possíveis de token reconhecidos pela linguagem EC1.

### Position.java

A classe Position representa a posição de um token no texto de entrada, armazenando o índice absoluto, a linha e a coluna. 

## Como executar com Docker:

### 1. Build da imagem
> docker build --no-cache -t compilador-cram .

### 2. Executar compilação dos arquivos manualmente
> docker run --rm -v "%cd%\src\inputs:/app/inputs" compilador-cram inputs/aninhado.txt


## Rodar todos os testes com ``cram``:

- O projeto inclui testes automatizados usando a ferramenta ``cram``

### Executar no CMD do linux:
> test.sh

### Executar no CMD do Windows:
> test.bat

## Requisitos:

- Docker Desktop instalado e rodando
- Cram incluído no contêiner