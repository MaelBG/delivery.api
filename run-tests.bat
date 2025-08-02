@echo off
ECHO Limpando o projeto e executando os testes...

call mvnw.cmd clean verify

ECHO.
ECHO ----------------------------------------------------
ECHO Processo de teste e verificacao concluido!
ECHO Relatorio de cobertura disponivel em: target/site/jacoco/index.html
ECHO ----------------------------------------------------
pause