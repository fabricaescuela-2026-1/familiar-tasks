# Autheticator (user-membership-service)

Microservicio de **gestión de hogares y miembros** del sistema `familiar-tasks`. Mantiene los modelos `Home`, `MemberHome` y `Role`, y expone endpoints REST para asociar/desasociar usuarios a hogares y administrar sus roles.

> Este microservicio **no realiza registro ni login local**. Los usuarios se replican desde el microservicio `authentication` mediante una cola de Azure Storage Queue. La autenticación de las peticiones se hace por JWT compartido con `authentication`.

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
src/main/java/com/udea/usermembershipservice/
├── domain/                  # modelos y excepciones puras
│   ├── model/               # Home, MemberHome, Person, Role
│   └── exception/
├── aplication/              # casos de uso, puertos, DTOs
│   ├── port/in/             # interfaces de entrada
│   ├── port/out/            # interfaces de salida (repos, queue, audit)
│   └── useCase/             # implementaciones + DTOs
└── infrastructure/          # adapters
    ├── adapter/in/web/      # controllers REST
    ├── adapter/in/queue/    # listener de la cola authentication
    ├── adapter/out/persistence/  # JPA
    ├── adapter/out/auth/    # cliente al servicio authentication
    ├── adapter/out/audit/   # publisher a Service Bus
    ├── config/              # SecurityConfiguration, BeanConfiguration, JwtTokenValidator
    └── util/                # JwtUtils
```

## Endpoints

Todos requieren `Authorization: Bearer <token>` salvo `/swagger-ui/**`, `/v3/api-docs/**` y `GET /get/memberHome` (consumido por `tasks` para validar membresia).

### Hogares

| Metodo | Path | Descripcion |
|---|---|---|
| `POST` | `/registerHome` | Crear un hogar |
| `GET` | `/getHomes` | Listar hogares |
| `GET` | `/getHomeByName` | Buscar hogar por nombre |
| `POST` | `/deleteHome` | Eliminar hogar |
| `GET` | `/GetMemberHome` | Listar miembros de un hogar |

### Roles

| Metodo | Path | Descripcion |
|---|---|---|
| `POST` | `/registerRole` | Registrar rol |
| `GET` | `/getRoles` | Listar roles |
| `GET` | `/getRoleByName` | Buscar rol por nombre |
| `POST` | `/deleteRole` | Eliminar rol |

### Miembros de hogar

| Metodo | Path | Descripcion |
|---|---|---|
| `POST` | `/save/memberHome` | Asociar usuario a hogar |
| `GET` | `/delete/memberHome` | Eliminar asociacion |
| `GET` | `/get/memberHome` | Obtener una asociacion (publico, lo consume `tasks`) |
| `POST` | `/updateRole` | Cambiar el rol de un miembro (publica `role_changed` en Service Bus) |

## Eventos de mensajeria

### Consume (Azure Storage Queue)

- **Cola:** `authentication`
- **Origen:** microservicio `authentication`
- **Frecuencia:** polling cada 5 segundos via `@Scheduled`
- **Mensaje:** [`UserRegistrationEvent`](src/main/java/com/udea/usermembershipservice/aplication/useCase/dto/queue/UserRegistrationEvent.java) con `{ userId, name, lastname, email, passwordHash, createdAt }`
- **Comportamiento:** [`UserSynchronizationService`](src/main/java/com/udea/usermembershipservice/aplication/useCase/UserSynchronizationService.java) crea el usuario en la tabla local `Users.person` si no existe. La copia local sirve para enriquecer los DTOs de membresia (nombre, email, etc.).

### Publica (Azure Service Bus)

- **Cola:** definida por `AUDIT_LOG_QUEUE_NAME`
- **Disparador:** `POST /updateRole` exitoso
- **Mensaje:** [`RoleChangedLog`](src/main/java/com/udea/usermembershipservice/aplication/useCase/dto/audit/RoleChangedLog.java) con `{ logId, userId, modifiedElement, action: "role_changed" }`
  - `userId` = id del administrador que ejecuto el cambio
  - `modifiedElement` = email del miembro al que se le cambio el rol
- **Tolerancia a fallos:** los errores de envio se loguean pero **no se propagan** — el cambio de rol queda persistido aunque la cola este caida.

## Variables de entorno

| Variable | Descripcion | Ejemplo |
|---|---|---|
| `DB_URL` | JDBC URL de Postgres | `jdbc:postgresql://localhost:5432/familiar_tasks` |
| `DB_USERNAME` | Usuario de BD | `postgres` |
| `DB_PASSWORD` | Password de BD | *(secreto)* |
| `PORT` | Puerto HTTP del servicio | `8080` (default) |
| `JWT_SECRET` | Clave HMAC compartida con `authentication` | *(secreto, minimo 32 bytes)* |
| `AZURE_STORAGE_CONNECTION_STRING` | Conn string del Storage Account de la cola `authentication` | `DefaultEndpointsProtocol=https;AccountName=...` |
| `AUTH_SERVICE_URL` | URL base de `authentication` (usado para refrescar tokens) | `http://localhost:8080` |
| `AUDIT_LOG_CONNECTION_STRING` | Conn string del namespace de Service Bus | `Endpoint=sb://registrerlogs.servicebus.windows.net/;SharedAccessKeyName=...;SharedAccessKey=...` |
| `AUDIT_LOG_QUEUE_NAME` | Cola de Service Bus para los logs | `audit_log_queue_name` |

> Las connection strings se obtienen del portal de Azure → recurso → **Shared access policies** (Service Bus) o **Access keys** (Storage Account). Nunca se commitean al repo.

## Levantar en local

Pre-requisitos:
- JDK 21
- PostgreSQL accesible y schema `Users` creado (Hibernate hace `ddl-auto=update`, las tablas se crean al primer arranque)
- Acceso al namespace de Service Bus y a la Storage Account de la cola `authentication`

Linux/macOS:
```bash
export DB_URL=jdbc:postgresql://localhost:5432/familiar_tasks
export DB_USERNAME=postgres
export DB_PASSWORD=...
export JWT_SECRET=...
export AZURE_STORAGE_CONNECTION_STRING="DefaultEndpointsProtocol=https;..."
export AUTH_SERVICE_URL=http://localhost:8081
export AUDIT_LOG_CONNECTION_STRING="Endpoint=sb://..."
export AUDIT_LOG_QUEUE_NAME=audit_log_queue_name

./mvnw spring-boot:run
```

Windows PowerShell:
```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/familiar_tasks"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "..."
$env:JWT_SECRET = "..."
$env:AZURE_STORAGE_CONNECTION_STRING = "DefaultEndpointsProtocol=https;..."
$env:AUTH_SERVICE_URL = "http://localhost:8081"
$env:AUDIT_LOG_CONNECTION_STRING = "Endpoint=sb://..."
$env:AUDIT_LOG_QUEUE_NAME = "audit_log_queue_name"

.\mvnw.cmd spring-boot:run
```

Servicio en `http://localhost:8080`. Swagger UI en `http://localhost:8080/swagger-ui.html`.

## Tests

Tests unitarios:
```bash
./mvnw test
```

Smoke test contra Service Bus real (requiere las dos env vars de auditoria seteadas, sino se omite):

PowerShell:
```powershell
$env:AUDIT_LOG_CONNECTION_STRING = "Endpoint=sb://...;SharedAccessKey=..."
$env:AUDIT_LOG_QUEUE_NAME = "audit_log_queue_name"
.\mvnw.cmd test "-Dtest=AuditLogServiceBusSmokeIT"
```

Si la auth y la cola estan correctas, el test pasa y aparece un mensaje en la cola que se puede verificar con **Service Bus Explorer** desde el portal de Azure.

## Contribuir

- **Una rama por cambio**: `feat/...`, `fix/...`, `docs/...`. No mezclar features distintas.
- **Validar local antes del PR**: `./mvnw test` debe pasar y la app debe levantar localmente.
- **No commitear secretos**: connection strings, JWT secrets y SAS keys solo como variables de entorno.
- **Estilo**: hexagonal, mantener separacion entre `domain`, `aplication` e `infrastructure`. Los puertos viven en `aplication.port` y los adapters en `infrastructure.adapter`.
