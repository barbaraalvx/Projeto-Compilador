#!/bin/bash

set -euo pipefail

INPUT_DIR="src/inputs"
ASM_DIR="src/assembly"
IMAGE_NAME="compilador-java"

# Caminhos host (Windows-friendly via -W; se falhar, usa pwd normal)
HOST_INPUT_DIR="$(cd "$INPUT_DIR" && (pwd -W 2>/dev/null || pwd))"
HOST_ASM_DIR="$(cd "$ASM_DIR" && (pwd -W 2>/dev/null || pwd))"

echo "Construindo imagem Docker..."
docker build -t "$IMAGE_NAME" .   # 

echo
echo "Preparando runtime..."
as "$ASM_DIR/runtime.s" -o "$ASM_DIR/runtime.o"

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

  if docker run --rm \
      -v "$HOST_INPUT_DIR:/app/inputs" \
      -v "$HOST_ASM_DIR:/app/src/assembly" \
      "$IMAGE_NAME" "inputs/$name.txt"; then
    echo "✅ Compilação/execução Java OK"
  else
    echo "❌ Execução do compilador falhou"
    FAILED+=("$name (compilador)")
    echo "-----------------------------------"
    continue
  fi

  ASM_FILE="$ASM_DIR/saida_${name}.s"
  OBJ_FILE="$ASM_DIR/saida_${name}.o"
  EXEC_FILE="$ASM_DIR/${name}_exec"

  if [ -f "$ASM_FILE" ]; then
    echo "→ Montando $ASM_FILE"
    as "$ASM_FILE" -o "$OBJ_FILE"
    echo "→ Linkando $OBJ_FILE + runtime.o"
    ld "$OBJ_FILE" "$ASM_DIR/runtime.o" -o "$EXEC_FILE"
    echo "→ Executando $EXEC_FILE"
    OUTPUT=$("$EXEC_FILE" || true)
    echo "Resultado: $OUTPUT"
    echo "✅ Teste '$name' passou"
  else
    echo "⚠️  Arquivo $ASM_FILE não foi gerado. Pulando execução."
    FAILED+=("$name (assembly ausente)")
  fi

  echo "-----------------------------------"
done

echo
echo "===================="
if [ ${#FAILED[@]} -eq 0 ]; then
  echo "Todos os testes passaram!"
else
  echo "❌ ${#FAILED[@]} teste(s) falharam:"
  for t in "${FAILED[@]}"; do
    echo "   - $t"
  done
fi
echo "===================="
