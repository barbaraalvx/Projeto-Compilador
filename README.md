# Compiladores – Atividade 09 – Comandos

### Alunos:
- Bárbara Geovanna Alves Cavalcante - 20220055021
- Eduardo Alves Braz de Medeiros - 20210024881
- Luiz Gustavo Paixão da Gama - 20220005834

--- 

## Descrição:

Este projeto implementa um compilador para uma linguagem de programação imperativa simples, desenvolvido na disciplina de Construção de Compiladores I.

A linguagem suporta:
- **Declaração de variáveis** com inicialização
- **Comandos de atribuição** (variável = expressão)
- **Estruturas de controle**:
  - `if-else` com condições
  - `while` para loops
- **Expressões aritméticas** com operadores: `+`, `-`, `*`, `/`
- **Operadores de comparação**: `==`, `<`, `>`
- **Comando `return`** para retornar valores

### Sintaxe da Linguagem:

```javascript
// Declarações de variáveis
variavel = expressao;

// Bloco de comandos
{
  // Comandos de atribuição
  variavel = expressao;
  
  // Estrutura condicional
  if condicao {
    comandos;
  } else {
    comandos;
  }
  
  // Loop
  while condicao {
    comandos;
  }
  
  // Retorno
  return expressao;
}
```

### Exemplo de programa:

```javascript
x = 2;
y = 2;
{
  if x == y {
    x = 10;
  } else {
    x = 5;
  }
  return x;
}
// Resultado: 10
```

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

- O projeto inclui scripts que executam todos os arquivos .txt da pasta src/inputs dentro do contêiner.

#### Executar no Linux/Git Bash/WSL:
```bash 
./test.sh                    # Testes básicos (sem asserções)
./test_with_assertions.sh    # Testes com validação de resultados
```

#### Executar no Windows PowerShell:
```powershell
.\test.ps1                   # Versão PowerShell para Windows
```

#### Os scripts:
- Constrói a imagem Docker (se necessário)
- Executa todos os testes .txt
- Monta, linka e executa cada .s gerado
- **test_with_assertions.sh**: Compara resultados com arquivos `.expected`
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

## Casos de Teste

Os arquivos de teste demonstram diferentes aspectos da linguagem:

### Testes de Estruturas de Controle:
- **igualdade.txt**: Testa estrutura `if-else` com comparação de igualdade
- **condicaofalsa.txt**: Testa loop `while` com condição falsa (nunca executa)
- **contagem.txt**: Testa loop `while` com decremento
- **somaenunciado.txt**: Testa loop `while` com soma acumulativa

### Testes de Algoritmos:
- **mdc.txt**: Implementa algoritmo de Euclides para máximo divisor comum
- **delta.txt**: Calcula discriminante de equação quadrática com estrutura condicional
- **parenteses.txt**: Testa expressões aritméticas complexas com parênteses

### Resultados Esperados:
Cada teste possui um arquivo `.expected` com o resultado correto:
- `igualdade.expected`: 10
- `condicaofalsa.expected`: 0  
- `contagem.expected`: 0
- `somaenunciado.expected`: 45
- `mdc.expected`: 6
- `delta.expected`: 8
- `parenteses.expected`: 17

## Requisitos:

- Docker Desktop instalado e rodando
- Terminal compatível com Bash e binutils (as/ld)
- No caso de execução pura em Java, JDK 17+

## Observações finais

### Pipeline de Compilação:
- **Análise Léxica**: Tokenização do código fonte
- **Análise Sintática**: Construção da AST (Abstract Syntax Tree)
- **Análise Semântica**: Verificação de tipos e declarações
- **Execução Semântica**: Avaliação de constantes e otimizações
- **Geração de Código**: Produção de assembly x86-64
- **Montagem e Linkagem**: Geração de executável
- **Execução**: Teste do programa final

### Sistema de Testes:
- **test.sh**: Testes básicos (smoke tests)
- **test_with_assertions.sh**: Testes com validação de resultados
- **test.ps1**: Versão PowerShell (com limitações no Windows)
- **Arquivos .expected**: Resultados esperados para cada teste

### Compatibilidade:
- **WSL (Recomendado)**: Máxima compatibilidade com ferramentas Unix
- **Git Bash**: Alternativa para Windows
- **PowerShell**: Limitado por problemas de execução de binários
- **Docker**: Ambiente isolado e portável

### Validação:
- O sistema valida tanto a **compilação bem-sucedida** quanto a **correção dos resultados**
- Cada teste possui resultado esperado documentado
- Falhas são categorizadas: compilação, montagem, linkagem ou resultado incorreto