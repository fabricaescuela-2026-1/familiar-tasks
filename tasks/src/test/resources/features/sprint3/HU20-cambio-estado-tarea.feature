# language: es
@sprint3 @HU20 @tareas
Característica: HU20 - Cambio de estado de tareas
  Como miembro del grupo familiar
  Quiero poder cambiar el estado de una tarea
  Para que todos sepan si está pendiente, en progreso o terminada

  Antecedentes:
    Dado que existen los estados "PENDIENTE" y "EN_PROGRESO" en el sistema de tareas
    Y existen las prioridades "ALTA" y "MEDIA" en el sistema de tareas
    Y existe el estado "COMPLETADA" en el sistema de tareas
    Y existe el miembro "tomas@familia.com" en el hogar "Familia Gomez"
    Y existe el miembro "gabriela@familia.com" en el hogar "Familia Gomez"

  @feliz
  Escenario: Cambiar estado a en progreso
    Dado que "tomas@familia.com" tiene la tarea "Lavar platos" en estado "PENDIENTE" en "Familia Gomez"
    Cuando "tomas@familia.com" cambia el estado de la tarea "Lavar platos" a "EN_PROGRESO"
    Entonces el sistema confirma el cambio de estado
    Y la tarea "Lavar platos" aparece en estado "EN_PROGRESO" en el listado

  @feliz
  Escenario: Cambiar estado a completada
    Dado que "tomas@familia.com" tiene la tarea "Tender ropa" en estado "EN_PROGRESO" en "Familia Gomez"
    Cuando "tomas@familia.com" cambia el estado de la tarea "Tender ropa" a "COMPLETADA"
    Entonces el sistema confirma el cambio de estado
    Y la tarea "Tender ropa" aparece en estado "COMPLETADA" en el listado

  @excepcion
  Escenario: Miembro sin permisos intenta cambiar estado
    Dado que "tomas@familia.com" tiene la tarea "Sacar la basura" en estado "PENDIENTE" en "Familia Gomez"
    Cuando "gabriela@familia.com" cambia el estado de la tarea "Sacar la basura" a "EN_PROGRESO"
    Entonces el sistema rechaza el cambio de estado por falta de permisos
    Y la tarea "Sacar la basura" sigue en estado "PENDIENTE" en el listado
