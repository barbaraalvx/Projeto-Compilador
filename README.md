# Compiladores – Atividade 02 – Constantes Inteiras

### Alunos:
- Bárbara Geovanna Alves Cavalcante - 20220055021
- Eduardo Alves Braz de Medeiros - 20210024881
- Luiz Gustavo Paixão da Gama - 20220005834

## Descrição:

Este projeto é um compilador básico para a linguagem CI (Constantes Inteiras), construído na linguagem Java e conteinerizado via Docker.

O programa faz a leitura de um arquivo com uma constante inteira (42, 123, etc.) e verifica se está correto. Se estiver, é exibida uma mensagem de sucesso e o arquivo Assembly (.s) é gerado. Se não estiver, quando o texto contém letras, por exemplo, o compilador informa o erro.

## Classes da versão:

Na versão inicial, o compilador da linguagem CI possui 2 classes de utilidade: FileReaderUtil e AssemblyFileWriter. 

### FileReaderUtil

A classe FileReaderUtil é responsável pela leitura do arquivo .TXT (entre as opções presentes em inputs) passado como argumento na função principal.

### AssemblyFileWriter

A classe AssemblyFileWriter é responsável pela escrita do código assembly e possui apenas o método generate.

## Como executar:

### 1. Build da imagem Docker
> docker build --no-cache -t ci-compiler-java .

### 2. Executar compilação dos arquivos manualmente
> docker run --rm -v "${PWD}/src:/app/src" ci-compiler-java src/inputs/n.txt

## Scripts prontos:

- Ambos os scripts executam os dois arquivos:
    - n.txt (válido)
    - invalid.txt (com erro esperado)

### PowerShell:
.\run.ps1

### Windows CMD:
run.bat