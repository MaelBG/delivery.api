# Stage 1: Build
# Usa uma imagem com Maven e Java 21 (Temurin) para compilar a aplicação
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
# Usa uma imagem base leve apenas com Java 21 para rodar a aplicação
FROM openjdk:21-jdk-slim
WORKDIR /app
# Copia o arquivo .jar gerado no primeiro estágio
COPY --from=build /app/target/*.jar app.jar
# Expõe a porta que a aplicação Spring Boot usa
EXPOSE 8080
# Comando para iniciar a aplicação quando o contêiner subir
ENTRYPOINT ["java", "-jar", "app.jar"]