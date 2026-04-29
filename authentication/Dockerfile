# Build stage - Compilar la aplicación
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copiar archivos de proyecto
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn/ .mvn/
COPY src/ src/

# Compilar aplicación
RUN chmod +x mvnw && \
    ./mvnw clean package -DskipTests -q

# Runtime stage - Ejecutar la aplicación
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear usuario no-root para mayor seguridad
RUN addgroup -g 1001 appgroup && \
    adduser -u 1001 -G appgroup -s /sbin/nologin -D appuser

# Copiar JAR compilado desde el stage de build
COPY --from=builder --chown=appuser:appgroup /build/target/*.jar app.jar

# Cambiar al usuario no-root
USER appuser

# Información del puerto (por defecto Spring Boot usa 8080)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
