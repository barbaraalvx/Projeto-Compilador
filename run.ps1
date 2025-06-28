# Para executar: .\run.ps1 no powershell

$inputFiles = @(
    "src/inputs/n.txt",
    "src/inputs/invalid.txt"
)

foreach ($file in $inputFiles) {
    Write-Host "`nCompilando: $file"
    docker run --rm -v "${PWD}/src:/app/src" ci-compiler-java $file
    Write-Host "Fim da execucao para: $file"
}

Write-Host "`nConcluido. Verifique a pasta src."
