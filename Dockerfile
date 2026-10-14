# ---------- Etapa 1: compilación ----------
# Compila el proyecto dentro de la imagen, así basta con "docker build" (no hay que compilar antes).
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Primero solo el pom, para que las dependencias queden en caché entre builds
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests && cp target/*.jar app.jar

# JRE reducido con solo los módulos de Java que usa Spring Boot, para que la imagen final pese poco
RUN jlink \
      --add-modules java.base,java.compiler,java.desktop,java.instrument,java.logging,java.management,java.naming,java.net.http,java.prefs,java.rmi,java.scripting,java.security.jgss,java.sql,java.xml,jdk.crypto.ec,jdk.management,jdk.unsupported,jdk.zipfs \
      --strip-debug --no-header-files --no-man-pages --compress=zip-6 \
      --output /jre

# ---------- Etapa 2: ejecución ----------
FROM alpine:3.22
WORKDIR /app

ENV JAVA_HOME=/opt/java \
    PATH=/opt/java/bin:$PATH \
    APP_ENV=production \
    APP_PORT=3001 \
    CSV_PATH=/data/compras.csv

COPY --from=build /jre $JAVA_HOME

# Usuario sin privilegios: el contenedor no se ejecuta como root
RUN addgroup -S app && adduser -S -G app app

COPY --from=build --chown=app:app /app/app.jar app.jar

# compras.csv NO se copia a la imagen: se monta en /data al ejecutar el contenedor
EXPOSE ${APP_PORT}

USER app

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
