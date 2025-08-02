# Usa uma imagem base oficial do Java 21
FROM openjdk:21-jdk-slim

# Argumento para o caminho do arquivo JAR
ARG JAR_FILE=target/*.jar

# Copia o arquivo JAR da aplicação para o contêiner
COPY ${JAR_FILE} app.jar

# Expõe a porta que a aplicação vai rodar
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java","-jar","/app.jar"]