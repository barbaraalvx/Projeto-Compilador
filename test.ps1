# PowerShell version of test.sh
param(
    [switch]$Help
)

if ($Help) {
    Write-Host "Uso: .\test.ps1"
    Write-Host "Executa os testes do compilador"
    exit 0
}

$ErrorActionPreference = "Stop"

$INPUT_DIR = "src\inputs"
$ASM_DIR = "src\assembly"
$IMAGE_NAME = "compilador-java"

# Get absolute paths
$HOST_INPUT_DIR = (Resolve-Path $INPUT_DIR).Path
$HOST_ASM_DIR = (Resolve-Path $ASM_DIR).Path

Write-Host "Construindo imagem Docker..."
docker build -t $IMAGE_NAME .

Write-Host ""
Write-Host "Preparando runtime..."
# Check if we have as (assembler) available
try {
    & as "$ASM_DIR\runtime.s" -o "$ASM_DIR\runtime.o"
} catch {
    Write-Host "Erro: Assembler 'as' não encontrado. Instale o WSL ou use Git Bash."
    Write-Host "Alternativa: Execute no WSL com: wsl ./test.sh"
    exit 1
}

Write-Host ""
Write-Host "Rodando testes:"
Write-Host "-----------------------------------"

$FAILED = @()

$inputFiles = Get-ChildItem "$INPUT_DIR\*.txt"
foreach ($file in $inputFiles) {
    $name = [System.IO.Path]::GetFileNameWithoutExtension($file.Name)
    
    Write-Host ""
    Write-Host ""
    Write-Host ""
    Write-Host "Teste: $name"
    Write-Host "-----------------------------------"

    # Run Docker container
    try {
        docker run --rm `
            -v "${HOST_INPUT_DIR}:/app/inputs" `
            -v "${HOST_ASM_DIR}:/app/src/assembly" `
            $IMAGE_NAME "inputs/$name.txt"
        
        Write-Host "✅ Compilação/execução Java OK"
    } catch {
        Write-Host "❌ Execução do compilador falhou"
        $FAILED += "$name (compilador)"
        Write-Host "-----------------------------------"
        continue
    }

    $ASM_FILE = "$ASM_DIR\saida_${name}.s"
    $OBJ_FILE = "$ASM_DIR\saida_${name}.o"
    $EXEC_FILE = "$ASM_DIR\${name}_exec"

    if (Test-Path $ASM_FILE) {
        Write-Host "→ Montando $ASM_FILE"
        & as $ASM_FILE -o $OBJ_FILE
        
        Write-Host "→ Linkando $OBJ_FILE + runtime.o"
        & ld $OBJ_FILE "$ASM_DIR\runtime.o" -o $EXEC_FILE
        
        Write-Host "→ Executando $EXEC_FILE"
        try {
            $OUTPUT = & $EXEC_FILE
            Write-Host "Resultado: $OUTPUT"
            Write-Host "✅ Teste '$name' passou"
        } catch {
            Write-Host "Erro na execução: $_"
            $FAILED += "$name (execução)"
        }
    } else {
        Write-Host "⚠️  Arquivo $ASM_FILE não foi gerado. Pulando execução."
        $FAILED += "$name (assembly ausente)"
    }

    Write-Host "-----------------------------------"
}

Write-Host ""
Write-Host "===================="
if ($FAILED.Count -eq 0) {
    Write-Host "Todos os testes passaram!"
} else {
    Write-Host "❌ $($FAILED.Count) teste(s) falharam:"
    foreach ($t in $FAILED) {
        Write-Host "   - $t"
    }
}
Write-Host "===================="
