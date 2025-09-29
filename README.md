# Compiladores – Atividade 10 - Linguagem Imperativa com Funções

### Alunos:
- Bárbara Geovanna Alves Cavalcante - 20220055021
- Eduardo Alves Braz de Medeiros - 20210024881
- Luiz Gustavo Paixão da Gama - 20220005834

--- 

## Descrição

Este projeto implementa um compilador para uma linguagem imperativa simples com suporte a funções, desenvolvido na disciplina de Construção de Compiladores I.

Linguagem – recursos suportados:
- **Programa principal em `main { ... }`** com declarações via `var` e um `return` ao final
- **Funções** com parâmetros: `fun nome(p1, p2, ...) { ... }` e retorno via `return`
- **Declarações de variáveis** com inicialização: `var x = expressão;`
- **Atribuição**: `x = expressão;`
- **Estruturas de controle**: `if ... else ...`, `while ... { ... }`
- **Expressões aritméticas**: `+`, `-`, `*`, `/` (inteiros)
- **Comparações**: `==`, `<`, `>` (retornam 0/1)
- **Chamadas de função** com até 6 argumentos (convenção interna adotada)
- **Recursão** suportada (pilha de chamadas gerenciada corretamente)

### Sintaxe da Linguagem

```javascript
// Declaração de função (opcional)
fun soma(a, b) {
  return a + b;
}

// Programa principal (obrigatório)
main {
  var x = 10;
  var y = 32;
  return soma(x, y);
}
```

### Exemplos rápidos

```javascript
// If/else
main {
  var x = 2;
  var y = 2;
  if x == y {
    x = 10;
  } else {
    x = 5;
  }
  return x; // 10
}
```

```javascript
// While e chamada de função
fun dobro(n) { return n * 2; }
main {
  var x = 7;
  var y = dobro(x);
  return y; // 14
}
```

```javascript
// Recursão
fun fact(n) {
  if n == 0 { return 1; } else { return n * fact(n - 1); }
}
main { var x = 5; return fact(x); } // 120
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
## Como executar

### 1. Compilação e execução via Docker
#### 1.1 Construir a imagem
``` bash
docker build -t compilador-java .
```

#### 1.2 Executar um teste com arquivo .txt
```bash
docker run --rm -v "$(pwd -W)/src/inputs:/app/inputs" compilador-java inputs/{exemplo}.txt
```

### 2. Rodar todos os testes automaticamente

- O projeto inclui scripts que executam todos os arquivos .txt da pasta src/inputs dentro do contêiner.

#### Executar no Linux/Git Bash/WSL
```bash 
./test.sh                    # Testes básicos (sem asserções)
./test_with_assertions.sh    # Testes com validação de resultados
```

#### Executar no Windows PowerShell
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

#### Executar no Linux/Git Bash/WSL
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

### Testes de Estruturas de Controle
- **igualdade.txt**: Testa estrutura `if-else` com comparação de igualdade
- **condicaofalsa.txt**: Testa loop `while` com condição falsa (nunca executa)
- **contagem.txt**: Testa loop `while` com decremento
- **somaenunciado.txt**: Testa loop `while` com soma acumulativa

### Testes de Algoritmos
- **mdc.txt**: Implementa algoritmo de Euclides para máximo divisor comum
- **delta.txt**: Calcula discriminante de equação quadrática com estrutura condicional
- **parenteses.txt**: Testa expressões aritméticas complexas com parênteses

### Resultados Esperados
Cada teste possui um arquivo `.expected` com o resultado correto:
- `igualdade.expected`: 10
- `condicaofalsa.expected`: 0  
- `contagem.expected`: 0
- `somaenunciado.expected`: 45
- `mdc.expected`: 6
- `delta.expected`: 8
- `parenteses.expected`: 17

### Testes de Funções
- `fun1.txt` / `fun1.expected`: Função simples `dobro`
- `fun_if.txt` / `fun_if.expected`: Função com `if-else`
- `fun_while.txt` / `fun_while.expected`: Função com `while`
- `fun2.txt` / `fun2.expected`: Função com variáveis locais e soma
- `fun_add2.txt` / `fun_add2.expected`: Função que incrementa
- `fun_nested.txt` / `fun_nested.expected`: Composição de chamadas
- `fun_rec_fact.txt`: Recursão (fatorial) – esperado 120

Obs.: arquivos `.expected` são usados no script `test_with_assertions.sh`.

## Requisitos

- Docker Desktop instalado e rodando
- Terminal compatível com Bash e binutils (as/ld)
- No caso de execução pura em Java, JDK 17+

## Observações finais

### Pipeline de Compilação
- **Análise Léxica**: Tokenização do código fonte
- **Análise Sintática**: Construção da AST (Abstract Syntax Tree)
- **Análise Semântica**: Verificação de tipos e declarações
- **Execução Semântica**: Avaliação de constantes e otimizações
- **Geração de Código**: Produção de assembly x86-64
- **Montagem e Linkagem**: Geração de executável
- **Execução**: Teste do programa final

### Sistema de Testes
- **test.sh**: Testes básicos (smoke tests)
- **test_with_assertions.sh**: Testes com validação de resultados
- **test.ps1**: Versão PowerShell (com limitações no Windows)
- **Arquivos .expected**: Resultados esperados para cada teste

### Compatibilidade
- **WSL (Recomendado)**: Máxima compatibilidade com ferramentas Unix
- **Git Bash**: Alternativa para Windows
- **PowerShell**: Limitado por problemas de execução de binários
- **Docker**: Ambiente isolado e portável

### Validação
- O sistema valida tanto a **compilação bem-sucedida** quanto a **correção dos resultados**
- Cada teste possui resultado esperado documentado
- Falhas são categorizadas: compilação, montagem, linkagem ou resultado incorreto