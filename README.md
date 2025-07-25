# Compiladores – Atividade 05 – Análise Sintática

### Alunos:
- Bárbara Geovanna Alves Cavalcante - 20220055021
- Eduardo Alves Braz de Medeiros - 20210024881
- Luiz Gustavo Paixão da Gama - 20220005834

--- 

## Descrição:

Este projeto implementa um analisador léxico e sintático para a linguagem **EC1 (Expressões Constantes 1)**, desenvolvido na disciplina de Construção de Compiladores I.

A linguagem EC1 aceita expressões aritméticas compostas por:
- Números inteiros
- Operadores `+`, `-`, `*`, `/`
- Parênteses obrigatórios para estrutura
- Comentários delimitados com `#comentários#` (ignorados na análise)

O analisador léxico:
- Lê um arquivo .txt com a expressão
- Faz a análise léxica: identifica tokens válidos e erros léxicos
- Faz a análise sintática: constrói uma árvore de derivação (AST)
- Imprime a árvore sintática
- Avalia o resultado da expressão final

## Como executar com Docker:

### 1. Build da imagem
``` bash
docker build -t compilador-java .
```

### 2. Executar um teste com arquivo .txt
```bash
docker run --rm -v "$(pwd -W)/src/inputs:/app/inputs" compilador-java inputs/aninhado.txt
```


## Rodar todos os testes automaticamente:

- O projeto inclui um script que executa todos os arquivos .txt da pasta src/inputs dentro do contêiner.

### Executar no Linux/Git Bash/WSL:
```bash 
./test.sh
```

#### O script:
- Compila a imagem Docker
- Executa todos os testes .txt
- Verifica se houve falhas
- Exibe um resumo dos testes ao final

## Requisitos:

- Docker Desktop instalado e rodando
- Terminal compatível com Bash

## Observações finais
Os testes não dependem mais do cram, por causa da incompatibilidade com codificações de arquivo diferentes de ASCII puro. A execução via Docker é mais confiável e portável.