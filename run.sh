#!/usr/bin/env bash
set -euo pipefail

ASM_DIR="src/assembly"

if [ "$#" -ne 1 ]; then
  echo "Uso: $0 caminho_ou_nome_do_arquivo.s"
  exit 1
fi

FILE="$1"
BASENAME="$(basename "$FILE" .s)"

# Se o usuário passou um caminho existente, usa-o.
# Senão, procura em src/assembly/
if [ -f "$FILE" ]; then
  ASM_PATH="$FILE"
else
  ASM_PATH="$ASM_DIR/$FILE"
fi

OBJ_PATH="$ASM_DIR/$BASENAME.o"
EXEC_PATH="$ASM_DIR/${BASENAME}_exec"
RUNTIME_SRC="$ASM_DIR/runtime.s"
RUNTIME_OBJ="$ASM_DIR/runtime.o"

echo "[INFO] ASM_PATH.......: $ASM_PATH"
echo "[INFO] OBJ_PATH.......: $OBJ_PATH"
echo "[INFO] EXEC_PATH......: $EXEC_PATH"
echo "[INFO] RUNTIME_SRC....: $RUNTIME_SRC"
echo "[INFO] RUNTIME_OBJ....: $RUNTIME_OBJ"
echo

# Verificações
if ! command -v as >/dev/null 2>&1; then
  echo "[ERRO] 'as' (GNU assembler) não encontrado. Instale com: sudo apt update && sudo apt install build-essential binutils"
  exit 1
fi
if ! command -v ld >/dev/null 2>&1; then
  echo "[ERRO] 'ld' (linker) não encontrado. Instale com: sudo apt update && sudo apt install binutils"
  exit 1
fi
if [ ! -f "$ASM_PATH" ]; then
  echo "[ERRO] Arquivo ASM não encontrado em: $ASM_PATH"
  exit 1
fi

# Monta runtime.o se necessário
if [ ! -f "$RUNTIME_OBJ" ]; then
  if [ ! -f "$RUNTIME_SRC" ]; then
    echo "[ERRO] runtime.s não encontrado em: $RUNTIME_SRC"
    exit 1
  fi
  echo "[BUILD] Montando runtime.s -> runtime.o"
  as "$RUNTIME_SRC" -o "$RUNTIME_OBJ"
fi

echo "[BUILD] Montando $ASM_PATH -> $OBJ_PATH"
as "$ASM_PATH" -o "$OBJ_PATH"

echo "[LINK ] Linkando $OBJ_PATH + $RUNTIME_OBJ -> $EXEC_PATH"
ld "$OBJ_PATH" "$RUNTIME_OBJ" -o "$EXEC_PATH"

echo "[RUN  ] Executando $EXEC_PATH"
"$EXEC_PATH"
