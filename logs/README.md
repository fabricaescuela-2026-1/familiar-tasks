# Módulo `logs`

## Descripción

Este módulo es un microservicio responsable del registro (logs) de eventos en la plataforma. Permite crear registros de acciones realizadas por usuarios y recuperar los registros existentes.

## Arquitectura

Se sigue un estilo de arquitectura hexagonal/limpia con los siguientes paquetes principales:

- `domain`: modelo de dominio y puertos (interfaces) (`Log`, `CreateLogUseCase`, `LogRepositoryPort`).
- `application`: lógica de aplicación (casos de uso, DTOs, mappers, servicios).
- `infrastructure`: adaptadores de entrada y salida, configuración (REST controller, adapters de persistencia, configuraciones).

Puntos clave:

- Caso de uso para crear logs: `CreateLogUseCase` (implementado por `LogService`).
- Adaptador de salida: `LogRepositoryAdapter` usa MongoDB para persistir `LogEntity`.
- Adaptador de entrada: `LogController` expone API REST en `/api/logs`.

Archivos relevantes:

- [LogsApplication.java](src/main/java/com.fabricaescuela/logs/LogsApplication.java#L1-L20)
- [LogController.java](src/main/java/com.fabricaescuela/logs/infrastructure/adapter/in/web/LogController.java#L1-L200)
- [LogService.java](src/main/java/com.fabricaescuela/logs/application/service/LogService.java#L1-L200)
- [Log.java](src/main/java/com.fabricaescuela/logs/domain/model/Log.java#L1-L200)
- [LogRepositoryAdapter.java](src/main/java/com.fabricaescuela/logs/infrastructure/adapter/out/LogRepositoryAdapter.java#L1-L200)
- [LogEntity.java](src/main/java/com.fabricaescuela/logs/infrastructure/adapter/out/persistence/LogEntity.java#L1-L200)
- [pom.xml](pom.xml)
- [application.properties](src/main/resources/application.properties#L1-L50)

## Modelo de datos

Registro (`Log`) con los campos:

- `id`: identificador del log (String).
- `idUser`: identificador del usuario que realizó la acción (String).
- `timestamp`: fecha y hora del evento (LocalDateTime).
- `modifiedElement`: elemento modificado (String).
- `action`: acción realizada (String).

La validación principal ocurre en el `record` `Log` y en el DTO `LogRequest`.

## API

Base path: `/api/logs`

- POST `/api/logs`
  - Crea un nuevo log.
  - Body (JSON):

```json
{
  "id": "...",
  "idUser": "...",
  "modifiedElement": "...",
  "action": "..."
}
```
  - Respuesta: `201 Created` con `LogResponse` (incluye `timestamp`).

- GET `/api/logs`
  - Obtiene todos los logs.
  - Respuesta: `200 OK` con array de `LogResponse`.

DTOs:

- `LogRequest` (validaciones `@NotBlank` en campos necesarios).
- `LogResponse` (id, idUser, timestamp, modifiedElement, action).

## Persistencia

Se utiliza MongoDB. La entidad `LogEntity` está mapeada al collection `logs`.

Archivo de repositorio: `LogMongoRepository` (extiende `MongoRepository`).

## Configuración / Variables de entorno

El módulo utiliza las siguientes variables de entorno (definidas en `application.properties`):

- `MONGODB_URI` — cadena de conexión hacia MongoDB.
- `MONGODB_DATABASE` — nombre de la base de datos.
- `SERVICEBUS_CONNECTION_STRING` — (opcional) cadena para Azure Service Bus si se usa integración JMS.
- `SERVICEBUS_IDLE_TIMEOUT` — timeout para Service Bus.

Ejemplo (Linux / macOS / PowerShell):

```bash
export MONGODB_URI="mongodb://user:pass@host:27017"
export MONGODB_DATABASE="mydb"
export SERVICEBUS_CONNECTION_STRING="Endpoint=..."
export SERVICEBUS_IDLE_TIMEOUT="30000"
```

En Windows PowerShell:

```powershell
$env:MONGODB_URI="mongodb://user:pass@host:27017"
$env:MONGODB_DATABASE="mydb"
$env:SERVICEBUS_CONNECTION_STRING="Endpoint=..."
$env:SERVICEBUS_IDLE_TIMEOUT="30000"
```

## Build y ejecución

Construir con Maven:

```bash
mvn -f logs/pom.xml clean package
```

Ejecutar con Spring Boot (usando variables de entorno):

```bash
mvn -f logs/pom.xml spring-boot:run
```

O ejecutar el jar generado:

```bash
java -jar target/logs-0.0.1-SNAPSHOT.jar
```

También existe un `Dockerfile` en la raíz del módulo para construir la imagen del servicio.

## Ejemplos de uso (curl)

Crear un log:

```bash
curl -X POST http://localhost:8080/api/logs \
  -H "Content-Type: application/json" \
  -d '{"id":"1","idUser":"user-1","modifiedElement":"task","action":"created"}'
```

Obtener todos los logs:

```bash
curl http://localhost:8080/api/logs
```

## Notas de desarrollo

- El módulo está preparado para usar Azure Service Bus (dependencias en `pom.xml`) pero la integración depende de configuración adicional fuera del alcance de este README.
- Seguir las convenciones de la arquitectura hexagonal: las puertas/puertos (`ports`) definen contratos y los adaptadores implementan detalles técnicos.

## Contribuir

1. Abrir una rama feature/bugfix.
2. Ejecutar pruebas (si existen) y formatear el código.
3. Abrir PR con descripción clara del cambio.

---

Si quieres, puedo añadir ejemplos de configuración de Docker Compose o snippets para despliegue en Kubernetes.
