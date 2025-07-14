@echo off
setlocal enabledelayedexpansion

set TEST_FOLDER=src/tests
set TEST_FILES=test_soma.t test_simples.t test_aninhado.t test_comentarios.t test_espbrancos.t test_errolexico.t test_misturado.t

echo Iniciando execucao dos testes com Cram...
echo -------------------------------------------

for %%F in (%TEST_FILES%) do (
    echo.
    echo Rodando teste: %%F
    docker run --rm -v "%cd%\%TEST_FOLDER%:/app/tests" --entrypoint cram compilador-cram tests/%%F
)

echo.
echo Todos os testes foram executados.
pause
