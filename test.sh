#!/bin/bash
set -e

TEST_FOLDER="src/tests"
TEST_FILES="test_soma.t test_simples.t test_aninhado.t test_comentarios.t test_espbrancos.t test_errolexico.t test_misturado.t"

echo "Iniciando execução dos testes com Cram..."
echo "-------------------------------------------"

for F in $TEST_FILES; do
    echo
    echo "Rodando teste: $F"
    docker run --rm -v "$(pwd)/$TEST_FOLDER:/app/tests" --entrypoint cram compilador-cram tests/$F
done

echo
echo "Todos os testes foram executados."
