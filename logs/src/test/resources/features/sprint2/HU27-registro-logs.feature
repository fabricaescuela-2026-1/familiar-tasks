# language: es
@sprint2 @HU27 @logs
Característica: HU27 - Registro de logs del sistema
  Como administrador del sistema
  Quiero que quede guardado un registro de las acciones realizadas
  Para tener trazabilidad de la operación

  @feliz
  Escenario: Registrar log de una acción de usuario
    Cuando se registra un log con id "log-001" usuario "user-1" elemento "task-100" acción "CREATED"
    Entonces el sistema confirma el registro del log
    Y la respuesta del log incluye el campo "timestamp" con un valor no vacío

  @feliz
  Escenario: Consultar todos los logs existentes
    Dado que se han registrado los siguientes logs:
      | id      | idUser | modifiedElement | action  |
      | log-010 | user-1 | task-100        | CREATED |
      | log-011 | user-2 | task-101        | UPDATED |
      | log-012 | user-3 | task-102        | DELETED |
    Cuando se consultan todos los logs registrados
    Entonces el sistema devuelve 3 logs

  @excepcion
  Escenario: Registrar log sin identificador de usuario
    Cuando se registra un log con id "log-002" usuario "" elemento "task-100" acción "CREATED"
    Entonces el sistema rechaza el registro del log
