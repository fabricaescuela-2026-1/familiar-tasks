# Authentication Service 🔐

Servicio de autenticación y gestión de usuarios para la plataforma Familiar Task, construido con Spring Boot 4.0.5 y arquitectura hexagonal.

## Descripción

Authentication Service es un microservicio REST que proporciona funcionalidades de autenticación, registro de usuarios, gestión de tokens JWT y validación de credenciales. Está diseñado para ser integrado como componente central de autenticación en la plataforma Familiar Task.

### Características principales

- ✅ Registro de nuevos usuarios
- ✅ Autenticación con email y contraseña
- ✅ Generación de tokens JWT (Access Token y Refresh Token)
- ✅ Validación y revalidación de tokens
- ✅ Encriptación segura de contraseñas
- ✅ Persistencia en PostgreSQL
- ✅ Integración con Azure Queue Storage para eventos de usuarios
- ✅ Arquitectura hexagonal (puertos y adaptadores)
- ✅ Contenedor Docker optimizado

---

## Requisitos Previos

- **Java**: 21 o superior
- **Maven**: 3.8 o superior
- **PostgreSQL**: 12 o superior
- **Docker**: 20.10 o superior (opcional, para ejecución en contenedor)

---

## Stack Tecnológico

| Componente | Versión | Propósito |
|-----------|---------|----------|
| Spring Boot | 4.0.5 | Framework web y seguridad |
| Spring Data JPA | 4.0.5 | Acceso a datos |
| Spring Security | 4.0.5 | Autenticación y autorización |
| PostgreSQL Driver | Latest | Base de datos relacional |
| JJWT | 0.11.5 | Manejo de tokens JWT |
| Lombok | Latest | Reducción de boilerplate |
| Azure SDK | 1.2.22 | Integración con Azure |
| Eclipse Temurin 21 | 21 | Runtime Java |

---

## Arquitectura del Proyecto

El proyecto sigue el patrón de **Arquitectura Hexagonal** (Puertos y Adaptadores):

```
src/main/java/com/fabrica/authentication/
├── application/              # Lógica de aplicación
│   ├── AuthService.java     # Servicio principal de autenticación
│   ├── dto/                 # Data Transfer Objects
│   │   ├── AuthResponse.java
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── TokenResponse.java
│   │   └── UserMessage.java
│   └── ports/               # Puertos de entrada y salida
│       ├── in/              # Puertos de entrada (use cases)
│       └── out/             # Puertos de salida (abstracciones)
│
├── domain/                  # Lógica de negocio
│   ├── model/              # Entidades del dominio
│   │   ├── User.java       # Entidad usuario
│   │   └── Token.java      # Entidad token
│   ├── exceptions/         # Excepciones de dominio
│   └── ports/              # Abstracciones de servicios externos
│
└── infrastructure/         # Implementaciones técnicas
    ├── database/           # Adaptadores de persistencia
    │   ├── entities/       # Entidades JPA
    │   └── repositories/   # Implementaciones de repositorios
    └── web/                # Adaptadores HTTP/REST
        ├── AuthController.java     # Controlador REST
        ├── UserQueueService.java   # Servicio de colas
        └── config/                 # Configuración de seguridad
```

### Flujo de Datos

```
Cliente REST
    ↓
AuthController (Puerto de Entrada)
    ↓
AuthService (Lógica de Aplicación)
    ↓
Puertos de Salida (Abstracciones)
    ├→ UserRepositoryPort → PostgreSQL
    ├→ TokenRepositoryPort → PostgreSQL
    ├→ JwtServicePort → Generación de tokens
    └→ UserQueuePort → Azure Queue Storage
```

---

## Instalación

### 1. Clonar o descargar el proyecto

```bash
cd c:\Users\brayan\dev\java\projects\familar-task\authentication
```

### 2. Instalar dependencias

```bash
./mvnw clean install
```

En Windows:
```bash
mvnw.cmd clean install
```

### 3. Configurar variables de entorno

Crear un archivo `.env` o configurar variables del sistema:

```bash
# Base de datos
DATABASE_URL=jdbc:postgresql://localhost:5432/fabrica
DB_USER=postgres
DB_PASSWORD=10293
```

### 4. Crear base de datos

```bash
psql -U postgres -d postgres -f init-db.sql
```

---

## Configuración

### application.properties

El archivo `src/main/resources/application.properties` contiene las configuraciones principales:

```properties
# Nombre de la aplicación
spring.application.name=authentication

# Base de Datos PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/fabrica
spring.datasource.username=postgres
spring.datasource.password=10293
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate/JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT - Tiempos de expiración
jwt.access.expiration=3600000      # 1 hora (ms)
jwt.refresh.expiration=604800000   # 7 días (ms)
jwt.secret=<tu-clave-secreta>

# Azure Storage - Colas de usuarios
azure.storage.users.connection-string=<azure-connection-string>
azure.storage.users.queue-name=authentication
azure.storage.users-task.connection-string=<azure-connection-string>
azure.storage.users-task.queue-name=users-task
```

### Configuración de Seguridad

La seguridad se configura en `src/main/java/com/fabrica/authentication/infrastructure/web/config/`.

---

## Ejecución

### Modo Desarrollo

```bash
# Con DevTools para hot reload
./mvnw spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

### Modo Producción

```bash
# Compilar JAR
./mvnw clean package

# Ejecutar JAR
java -jar target/authentication-0.0.1-SNAPSHOT.jar
```

### Con Docker

```bash
# Construir imagen
docker build -t authentication:latest .

# Ejecutar contenedor
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/fabrica \
  -e DB_USER=postgres \
  -e DB_PASSWORD=10293 \
  authentication:latest
```

---

## API REST

### Endpoints disponibles

#### 1. Registro de Usuario
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "Juan",
  "lastname": "Pérez",
  "email": "juan@example.com",
  "password": "SecurePassword123!"
}
```

**Respuesta exitosa (201):**
```json
{
  "user": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Juan",
    "lastname": "Pérez",
    "email": "juan@example.com",
    "createdAt": "2024-05-11T10:30:00"
  },
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600000
}
```

#### 2. Login de Usuario
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "juan@example.com",
  "password": "SecurePassword123!"
}
```

**Respuesta exitosa (200):**
```json
{
  "user": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Juan",
    "lastname": "Pérez",
    "email": "juan@example.com"
  },
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600000
}
```

#### 3. Revalidar Token
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Respuesta exitosa (200):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600000
}
```

### Códigos de Respuesta

| Código | Significado |
|--------|------------|
| 200 | OK - Solicitud exitosa |
| 201 | Created - Recurso creado |
| 400 | Bad Request - Solicitud inválida |
| 401 | Unauthorized - No autenticado |
| 409 | Conflict - Email ya existe |
| 500 | Internal Server Error - Error del servidor |

---

## Modelos de Datos

### User (Usuario)

Entidad que representa un usuario del sistema.

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;
    
    private String name;           // Nombre del usuario
    private String lastname;       // Apellido del usuario
    @Column(unique = true)
    private String email;          // Email único
    private String password;       // Contraseña encriptada
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Token

Entidad que almacena los tokens de autenticación.

```java
@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID tokenId;
    
    @ManyToOne
    private User user;             // Usuario propietario del token
    private String refreshToken;   // Refresh token
    private LocalDateTime expiresAt;
    private boolean revoked;       // Indicador de revocación
}
```

---

## Excepciones de Negocio

El servicio maneja las siguientes excepciones:

| Excepción | Descripción | HTTP Status |
|-----------|------------|------------|
| `UserNotFoundException` | Usuario no encontrado | 404 |
| `EmailAlreadyExitsException` | Email ya registrado | 409 |
| `InvalidTokenException` | Token inválido o expirado | 401 |
| `InvalidRefreshTokenException` | Refresh token inválido | 401 |

---

## Testing

### Ejecutar todas las pruebas

```bash
./mvnw test
```

### Ejecutar pruebas específicas

```bash
./mvnw test -Dtest=AuthServiceTest
```

### Cobertura de código

```bash
./mvnw test jacoco:report
# Reporte disponible en: target/site/jacoco/index.html
```

### Pruebas disponibles

- `AuthenticationApplicationTests` - Pruebas de integración de la aplicación

---

## Seguridad

### Buenas Prácticas Implementadas

1. **Encriptación de contraseñas**
   - Uso de BCrypt a través de Spring Security
   - Nunca se almacenan contraseñas en texto plano

2. **Tokens JWT**
   - Tokens con expiración configurable
   - Separación entre Access Token (corta duración) y Refresh Token (larga duración)

3. **Autenticación basada en Spring Security**
   - Protección contra CSRF
   - Validación de tokens en cada solicitud

4. **Base de datos**
   - Único email por usuario (constraint único)
   - UUIDs para identificadores

5. **Dockerfile**
   - Usuario no-root para ejecución
   - Multi-stage build para optimización

### Variables Sensibles

⚠️ **IMPORTANTE**: Nunca commitear variables sensibles. Usar:

- Variables de entorno
- Archivos `.env` en `.gitignore`
- Gestión de secretos en producción (Azure Key Vault)

---

## Integración con Azure

### Azure Queue Storage

El servicio se integra con Azure Queue Storage para enviar eventos de usuarios:

```properties
# Cola de autenticación
azure.storage.users.connection-string=<connection-string>
azure.storage.users.queue-name=authentication

# Cola de usuarios-tareas
azure.storage.users-task.connection-string=<connection-string>
azure.storage.users-task.queue-name=users-task
```

**Eventos enviados:**
- Nuevo usuario registrado
- Usuario autenticado

---

## Troubleshooting

### Error: "Connection refused" a la base de datos

```bash
# Verificar que PostgreSQL esté ejecutándose
psql -U postgres -d postgres

# Revisar la URL y credenciales en application.properties
```

### Error: "Token inválido"

- Verificar que `jwt.secret` sea el mismo en todas las instancias
- Comprobar que el token no esté expirado
- Usar Refresh Token para obtener uno nuevo

### Error: "Email ya existe"

- El email ya fue registrado
- Usar otro email o verificar si debe hacer login

### Compilación falla

```bash
# Limpiar caché de Maven
./mvnw clean

# Descargar dependencias nuevamente
./mvnw dependency:resolve
```

---

## Desarrollo

### Estructura de archivos fuente

```
src/main/
├── java/com/fabrica/authentication/
│   ├── AuthenticationApplication.java    # Clase principal
│   ├── application/                      # Capa de aplicación
│   ├── domain/                           # Capa de dominio
│   └── infrastructure/                   # Capa de infraestructura
└── resources/
    ├── application.properties            # Configuración
    ├── static/                           # Recursos estáticos
    └── templates/                        # Templates (si aplica)
```

### Comandos útiles de Maven

```bash
# Compilar
./mvnw compile

# Limpiar artefactos
./mvnw clean

# Ejecutar con perfil específico
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Generar documentación del proyecto
./mvnw site
```

---

## Deployment

### Deployment en Docker

1. **Construir imagen**
   ```bash
   docker build -t authentication:1.0 .
   ```

2. **Verificar imagen**
   ```bash
   docker images | grep authentication
   ```

3. **Ejecutar contenedor**
   ```bash
   docker run -d \
     --name auth-service \
     -p 8080:8080 \
     -e DATABASE_URL=jdbc:postgresql://db:5432/fabrica \
     -e DB_USER=postgres \
     -e DB_PASSWORD=10293 \
     authentication:1.0
   ```

### Deployment en Kubernetes (Ejemplo)

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: authentication-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: authentication
  template:
    metadata:
      labels:
        app: authentication
    spec:
      containers:
      - name: authentication
        image: authentication:1.0
        ports:
        - containerPort: 8080
        env:
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: db-secrets
              key: url
        - name: DB_USER
          valueFrom:
            secretKeyRef:
              name: db-secrets
              key: username
```

---

## Monitoring y Logs

### Ver logs de la aplicación

En desarrollo:
```bash
# Los logs se mostrarán en consola por defecto
./mvnw spring-boot:run
```

En Docker:
```bash
docker logs -f auth-service
```

### Propiedades de logging configurables

```properties
logging.level.root=INFO
logging.level.com.fabrica.authentication=DEBUG
logging.file.name=logs/application.log
```

---

## Contribución

### Pasos para contribuir

1. Fork del proyecto
2. Crear rama de features (`git checkout -b feature/AmazingFeature`)
3. Commit de cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

### Estándares de código

- Seguir convenciones de nombres Java
- Documentar métodos públicos
- Escribir pruebas unitarias
- Mantener cobertura de código > 80%

---

## Licencia

Este proyecto es parte de la plataforma Familiar Task.

---

## Contacto y Soporte

Para reportar bugs o solicitar features, abrir un issue en el repositorio.

---

## Historial de Cambios

### v0.0.1-SNAPSHOT (Actual)
- ✅ Implementación inicial
- ✅ Autenticación con JWT
- ✅ Registro de usuarios
- ✅ Integración con Azure Queue Storage
- ✅ Arquitectura hexagonal

---

## Referencias Útiles

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
- [JWT (JSON Web Tokens)](https://jwt.io/)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Azure SDK for Java](https://github.com/Azure/azure-sdk-for-java)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)

---

**Última actualización**: Mayo 2026

**Versión del documento**: 1.0
