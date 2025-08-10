#!/bin/bash

set -euo pipefail

ASM_DIR="src/assembly"

if [ "$#" -ne 1 ]; then
  echo "Uso: $0 nome_arquivo.s"
  exit 1
fi

FILE="$1"
BASENAME=$(basename "$FILE" .s)
ASM_PATH="$ASM_DIR/$FILE"
OBJ_PATH="$ASM_DIR/$BASENAME.o"
EXEC_PATH="$ASM_DIR/${BASENAME}_exec"
RUNTIME_OBJ="$ASM_DIR/runtime.o"

# Monta runtime.o se não existir
if [ ! -f "$RUNTIME_OBJ" ]; then
  echo "Montando runtime.s"
  as "$ASM_DIR/runtime.s" -o "$RUNTIME_OBJ"
fi

echo "Montando $FILE"
as "$ASM_PATH" -o "$OBJ_PATH"

echo "Linkando $OBJ_PATH + runtime.o"
ld "$OBJ_PATH" "$RUNTIME_OBJ" -o "$EXEC_PATH"

echo "Executando $EXEC_PATH"
"$EXEC_PATH"
