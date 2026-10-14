# ---------- Etapa de construcción ----------
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /usr/src/app

# Descarga las dependencias primero para aprovechar la caché de Docker
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src ./src
RUN mvn -q -B clean package -DskipTests

# ---------- Etapa de ejecución ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Usuario sin privilegios
RUN useradd --system --uid 1001 stratton
USER stratton

# Variables de entorno (se pueden sobrescribir en docker-compose.yml)
ENV BBDD_HOST=db \
    BBDD_PORT=3306 \
    BBDD_NAME=strattonapp \
    DATABASE_USERNAME=root \
    DATABASE_PASSWORD="" \
    APP_PORT=8080 \
    DDL_AUTO=update \
    SEED_DATA=true \
    LOG_LEVEL=INFO \
    JAVA_OPTS="-Xms128m -Xmx512m"

EXPOSE 8080

COPY --from=builder /usr/src/app/target/Backend-0.0.1-SNAPSHOT.jar /app/app.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
