# language: es
@sprint2 @HU28 @logs
Característica: HU28 - Registro de auditorías de cambios
  Como administrador del sistema
  Quiero que todo cambio sobre las tareas quede registrado
  Para conocer qué se modificó, quién lo modificó y cuándo

  @feliz
  Escenario: Cambio sobre una tarea queda registrado en la auditoría
    Cuando Sofía modifica la tarea "task-555" con la acción "UPDATED" identificándose como "sofia-001" en el log "log-100"
    Entonces el sistema confirma el registro del log
    Y el log "log-100" registra que el usuario "sofia-001" realizó la acción "UPDATED" sobre "task-555"
    Y el log "log-100" tiene un timestamp asignado automáticamente

  @feliz
  Escenario: Eliminación de tarea queda registrada en la auditoría
    Cuando Sofía elimina la tarea "task-556" con la acción "DELETED" identificándose como "sofia-001" en el log "log-101"
    Entonces el sistema confirma el registro del log
    Y el log "log-101" registra que el usuario "sofia-001" realizó la acción "DELETED" sobre "task-556"
