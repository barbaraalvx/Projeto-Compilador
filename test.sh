#!/bin/bash

INPUT_DIR="src/inputs"
IMAGE_NAME="compilador-java"

# Caminho do host compatível com Docker no Windows
HOST_INPUT_DIR="$(cd $INPUT_DIR && pwd -W)"

echo "Construindo imagem Docker..."
docker build -t $IMAGE_NAME .

echo
echo "Rodando testes:"
echo "-----------------------------------"

FAILED=()

for file in "$INPUT_DIR"/*.txt; do
    name=$(basename "$file" .txt)
    echo
    echo
    echo
    echo "Teste: $name"
    echo "-----------------------------------"
    
    if docker run --rm -v "$HOST_INPUT_DIR:/app/inputs" $IMAGE_NAME "inputs/$name.txt"; then
        echo "✅ Teste '$name' passou"
    else
        echo "❌ Teste '$name' falhou"
        FAILED+=("$name")
    fi

    echo "-----------------------------------"
done

echo
echo "===================="
if [ ${#FAILED[@]} -eq 0 ]; then
    echo "Todos os testes passaram!"
else
    echo "❌ ${#FAILED[@]} teste(s) falharam:"
    for name in "${FAILED[@]}"; do
        echo "   - $name"
    done
fi
echo "===================="
