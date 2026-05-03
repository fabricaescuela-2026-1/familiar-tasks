# tasks

Microservicio de **gestion de tareas** del sistema `familiar-tasks`. Expone CRUD sobre la entidad `Task` y valida que el usuario pertenezca al hogar antes de crear o actualizar una tarea consultando al microservicio `Autheticator`.

> Requiere autenticacion JWT en todos los endpoints. El secreto se comparte con el microservicio `authentication`. La copia local de usuarios se sincroniza desde la cola `authentication` (Azure Storage Queue).

## Stack

- Java 21
- Spring Boot 4.0.5 (`webmvc`, `data-jpa`, `security`, `restclient`, `devtools`)
- PostgreSQL
- Lombok
- Azure Storage Queue (consumidor de la cola `authentication`)
- Azure Service Bus (productor de logs de auditoria)
- JWT: `io.jsonwebtoken:jjwt 0.11.5`, `com.auth0:java-jwt 4.5.1`
- Springdoc OpenAPI (Swagger UI)
- Maven (wrapper incluido)

## Arquitectura

Hexagonal (ports & adapters):

```
src/main/java/com/fabricaescuela/tasks/
├── domain/                   # modelos y reglas
│   ├── model/                # Task, Guest, User
│   ├── exceptions/
│   └── ports/
│       ├── in/               # TaskUseCasePort
│       └── out/              # TaskRepositoryPort, UserValidationPort, TaskAuditLogPort
├── application/              # servicios + DTOs
│   ├── TaskService.java
│   ├── UserSynchronizationService.java
│   └── dto/
└── infraestructure/          # adapters
    ├── adapter/in/           # UserDetailServiceImpl
    ├── adapter/out/          # UserValidationAdapter (REST a Autheticator), AuthClient, audit/, UserRegistrationMessageListener
    ├── database/             # JPA entities, repositories, mappers
    ├── presentation/         # TaskController, DTOs
    ├── config/               # SecurityConfig, AuditLogConfig, RestTemplateConfig, JwtTokenValidator
    └── util/                 # JwtUtils
```

## Endpoints

Todos requieren `Authorization: Bearer <token>`. Swagger UI esta publico en `/swagger-ui.html`.

| Metodo | Path | Descripcion |
|---|---|---|
| `POST` | `/task/create` | Crear una tarea (publica `task_created` en Service Bus) |
| `PUT` | `/task/update/{id}` | Actualizar una tarea |
| `DELETE` | `/task/delete/{id}` | Eliminar una tarea |
| `GET` | `/task/all` | Listar todas las tareas |

Antes de crear/actualizar una tarea, `TaskService` consulta a `Autheticator` (`GET /get/memberHome?personId=...&homeId=...`) para confirmar que el `guestId` pertenece al `homeId`. Si no, responde con `UserNotValidException`.

## Eventos de mensajeria

### Consume (Azure Storage Queue)

- **Cola:** `authentication`
- **Origen:** microservicio `authentication`
- **Frecuencia:** polling cada 5 segundos via `@Scheduled`
- **Mensaje:** `UserRegistrationEvent` con `{ userId, name, lastname, email, passwordHash, createdAt }`
- **Comportamiento:** [`UserSynchronizationService`](src/main/java/com/fabricaescuela/tasks/application/UserSynchronizationService.java) crea el usuario en la tabla local `guests` si no existe. Esa copia local sirve para que `UserDetailsService` pueda autenticar peticiones por JWT.

### Publica (Azure Service Bus)

- **Cola:** definida por `AUDIT_LOG_QUEUE_NAME` (la misma que usa `Autheticator`)
- **Disparador:** `POST /task/create` exitoso (`repository.save` retorno la tarea persistida)
- **Mensaje:** [`TaskCreatedLog`](src/main/java/com/fabricaescuela/tasks/application/dto/TaskCreatedLog.java) con `{ logId, userId, modifiedElement, action: "task_created" }`
  - `userId` = `guestId` de la tarea (dueño/asignado)
  - `modifiedElement` = `taskId` (UUID generado al persistir)
- **Tolerancia a fallos:** los errores de envio se loguean pero **no se propagan** — la tarea queda persistida aunque la cola este caida.

## Variables de entorno

| Variable | Descripcion | Ejemplo |
|---|---|---|
| `DATABASE_URL` | JDBC URL de Postgres | `jdbc:postgresql://localhost:5432/familiar_tasks` |
| `DATABASE_USERNAME` | Usuario de BD | `postgres` |
| `DATABASE_PASSWORD` | Password de BD | *(secreto)* |
| `PORT` | Puerto HTTP del servicio | `8081` (default) |
| `JWT_SECRET` | Clave HMAC compartida con `authentication` y `Autheticator` | *(secreto, minimo 32 bytes)* |
| `USER_VALIDATION_SERVICE_URL` | URL base del `Autheticator` (validacion de membresia) | `https://familiar-tasks.onrender.com` |
| `AUTH_SERVICE_URL` | URL base de `authentication` (refresh de token) | `http://localhost:8080` |
| `AZURE_STORAGE_CONNECTION_STRING` | Conn string del Storage Account de la cola `authentication` | `DefaultEndpointsProtocol=https;AccountName=...` |
| `AUDIT_LOG_CONNECTION_STRING` | Conn string del namespace de Service Bus | `Endpoint=sb://registrerlogs.servicebus.windows.net/;SharedAccessKeyName=...;SharedAccessKey=...` |
| `AUDIT_LOG_QUEUE_NAME` | Cola de Service Bus para los logs | `audit_log_queue_name` |

Hay un [`.env.example`](.env.example) con valores de referencia (sin secretos). **No commitear secretos** ni copias del `.env` real.

## Levantar en local

Pre-requisitos:
- JDK 21
- PostgreSQL accesible (Hibernate hace `ddl-auto=update`, las tablas se crean al primer arranque)
- Acceso al namespace de Service Bus y a la Storage Account de la cola `authentication`
- `Autheticator` y `authentication` corriendo (o sus URLs publicas)

Linux/macOS:
```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/familiar_tasks
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=...
export JWT_SECRET=...
export USER_VALIDATION_SERVICE_URL=http://localhost:8080
export AUTH_SERVICE_URL=http://localhost:8080
export AZURE_STORAGE_CONNECTION_STRING="DefaultEndpointsProtocol=https;..."
export AUDIT_LOG_CONNECTION_STRING="Endpoint=sb://..."
export AUDIT_LOG_QUEUE_NAME=audit_log_queue_name

./mvnw spring-boot:run
```

Windows PowerShell:
```powershell
$env:DATABASE_URL = "jdbc:postgresql://localhost:5432/familiar_tasks"
$env:DATABASE_USERNAME = "postgres"
$env:DATABASE_PASSWORD = "..."
$env:JWT_SECRET = "..."
$env:USER_VALIDATION_SERVICE_URL = "http://localhost:8080"
$env:AUTH_SERVICE_URL = "http://localhost:8080"
$env:AZURE_STORAGE_CONNECTION_STRING = "DefaultEndpointsProtocol=https;..."
$env:AUDIT_LOG_CONNECTION_STRING = "Endpoint=sb://..."
$env:AUDIT_LOG_QUEUE_NAME = "audit_log_queue_name"

.\mvnw.cmd spring-boot:run
```

Servicio en `http://localhost:8081`. Swagger UI en `http://localhost:8081/swagger-ui.html`.

Hay un archivo [`task.http`](task.http) con peticiones de ejemplo (compatible con la extension REST Client de VS Code).

## Tests

```bash
./mvnw test
```

## Notas operativas

- El init del schema esta en [`init_database.sql`](init_database.sql) (referencia). Hibernate con `ddl-auto=update` agrega columnas nuevas pero **no elimina las viejas**: si se hereda una BD con columnas obsoletas (por ejemplo un `id_home` huerfano), hay que limpiarlas a mano con `ALTER TABLE ... DROP COLUMN ...`.

## Contribuir

- **Una rama por cambio**: `feat/...`, `fix/...`, `docs/...`. No mezclar features distintas.
- **Validar local antes del PR**: `./mvnw test` debe pasar y la app debe levantar localmente.
- **No commitear secretos**: connection strings, JWT secrets y SAS keys solo como variables de entorno.
- **Estilo**: hexagonal, mantener separacion entre `domain`, `application` e `infraestructure`.
