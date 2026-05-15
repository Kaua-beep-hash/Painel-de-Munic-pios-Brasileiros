# =============================================
# Dockerfile — Municípios BR (Spring Boot)
# Multi-stage build para imagem enxuta
# =============================================

# --- Estágio 1: Build com Maven ---
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copia apenas pom.xml primeiro (aproveita cache do Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -B 2>/dev/null || true

# Copia código-fonte e faz o build
COPY . .
RUN ./mvnw package -DskipTests --no-transfer-progress 2>/dev/null || \
    mvn package -DskipTests --no-transfer-progress

# --- Estágio 2: Imagem de execução (JRE apenas) ---
FROM eclipse-temurin:17-jre-alpine AS runtime

# Metadados
LABEL maintainer="projeto-academico"
LABEL description="Painel de Municípios Brasileiros — API IBGE"

# Usuário não-root por segurança
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

# Copia o JAR gerado no estágio anterior
COPY --from=build /app/target/municipios-br-*.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Variáveis de ambiente configuráveis
ENV JAVA_OPTS="-Xmx256m -Xms128m"
ENV SPRING_PROFILES_ACTIVE=prod

# Inicia a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
