REM Para executar: run.bat no cmd

@echo off
setlocal enabledelayedexpansion

set FILES=src/inputs/n.txt src/inputs/invalid.txt

for %%F in (%FILES%) do (
    echo.
    echo Compilando: %%F
    docker run --rm -v "%cd%\src:/app/src" ci-compiler-java %%F
    echo Fim da execucao para: %%F
)

echo.
echo Concluido. Verifique a pasta src.
pause
