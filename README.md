# Compiladores – Atividade 06 – Geração de Código Assembly

### Alunos:
- Bárbara Geovanna Alves Cavalcante - 20220055021
- Eduardo Alves Braz de Medeiros - 20210024881
- Luiz Gustavo Paixão da Gama - 20220005834

--- 

## Descrição:

Este projeto implementa um compilador completo para a linguagem **EC1 (Expressões Constantes 1)**, desenvolvido na disciplina de Construção de Compiladores I.

A linguagem EC1 aceita expressões aritméticas compostas por:
- Números inteiros
- Operadores `+`, `-`, `*`, `/`
- Parênteses obrigatórios para estrutura
- Comentários delimitados com `#comentários#` (ignorados na análise)

O compilador é dividido em 3 etapas:
1. Análise léxica: identifica tokens válidos e erros léxicos.
2. Análise sintática: constrói uma Árvore Sintática Abstrata (AST).
3. Geração de código: produz código Assembly x86-64 em sintaxe AT&T, que:
    - Coloca o resultado no registrador `%rax`
    - Chama funções `imprime_num` e `sair`(implementadas em `runtime.s`)


O projeto também inclui execução completa do Assembly:
- Montagem (as)
- Linkagem (ld)
- Execução do binário final

## Estrutura de pastas

```bash
src/
├── assembly/       # Arquivos .s gerados + runtime.s/.o
├── codegen/        # Gerador de códigos Assembly
├── inputs/         # Arquivos .txt de teste
├── syntacticanalysis/ # Lexer, Parser, AST, etc.
├── utils/          # Leitura e escrita de arquivos
└── Main.java       # Entrada principal do compilador
```
## Como executar:

### 1. Compilação e execução via Docker
#### 1.1 Construir a imagem
``` bash
docker build -t compilador-java .
```

#### 1.2. Executar um teste com arquivo .txt
```bash
docker run --rm -v "$(pwd -W)/src/inputs:/app/inputs" compilador-java inputs/{exemplo}.txt
```

### 2. Rodar todos os testes automaticamente:

- O projeto inclui um script que executa todos os arquivos .txt da pasta src/inputs dentro do contêiner.

#### Executar no Linux/Git Bash/WSL:
```bash 
./test.sh
```

#### O script:
- Constrói a imagem Docker
- Executa todos os testes .txt
- Monta, linka e executa cada .s gerado
- Exibe um resumo dos testes ao final (resultado e status)

### 3. Executar um `.s` específico

- O projeto também inclui um script que executa um arquivo .txt da pasta src/inputs.

#### Executar no Linux/Git Bash/WSL:
```bash 
./run.sh saida_{exemplo}.s
```

#### O script:
- Recebe o nome de um .s que já está em src/assembly
- Monta (as) e linka (ld) com runtime.o
- Executa o binário

### 4. Execução Java pura (sem Docker)

- Na raiz do projeto:
```bash 
javac -d out $(find src -name "*.java")
java -cp out Main src/inputs/{exemplo}.txt
```
- Depois, para montar e rodar o .s gerado:
```bash 
cd src/assembly
as saida_{exemplo}.s -o saida_{exemplo}.o
as runtime.s -o runtime.o
ld saida_{exemplo}.o runtime.o -o exec_{exemplo}
./exec_{exemplo}
```

## Requisitos:

- Docker Desktop instalado e rodando
- Terminal compatível com Bash e binutils (as/ld)
- No caso de execução pura em Java, JDK 17+

## Observações finais
- O pipeline via Docker é mais portável e evita problemas de ambiente.
- O test.sh faz teste completo de geração + execução de código Assembly.
- O run.sh é útil para depurar ou testar .s específico sem rodar todos os casos.
- Testes só usaram Cram na atividade 4, mas o método foi abandonado por incompatibilidade com codificações diferentes de ASCII puro na AST.
    - A partir da entrega 5, a validação passou a ser feita com scripts Bash que automatizam o pipeline. `test.sh` aplica o fluxo de execução a todos os casos de teste (src/inputs/*.txt) e marca como falhos os casos em que o `compilador retorna um erro`, o `.s não é gerado` ou a `montagem/linkagem falha`.