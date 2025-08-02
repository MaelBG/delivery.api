#!/bin/bash

echo " Limpando o projeto e executando os testes..."
./mvnw clean verify

# A fase 'verify' já executa test, jacoco:report e jacoco:check, conforme configurado no pom.xml

echo ""
echo "----------------------------------------------------"
echo " Processo de teste e verificacao concluido!"
echo " Relatorio de cobertura disponivel em: target/site/jacoco/index.html"
echo "----------------------------------------------------"