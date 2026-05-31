# language: es
@sprint2 @HU17 @tareas
Característica: HU17 - Edición de tarea existente
  Como integrante de un hogar
  Quiero editar las tareas creadas
  Para reflejar los cambios y mantenerlas actualizadas

  Antecedentes:
    Dado que existen los estados "PENDIENTE" y "EN_PROGRESO" en el sistema de tareas
    Y existen las prioridades "ALTA" y "MEDIA" en el sistema de tareas
    Y existe el miembro "luis@familia.com" en el hogar "Familia Pérez"
    Y existe la tarea "Sacar la basura" asignada a "luis@familia.com" en "Familia Pérez"

  @feliz
  Escenario: Editar tarea exitosamente
    Cuando se edita la tarea "Sacar la basura" cambiando el nombre a "Sacar la basura el martes" y el estado a "EN_PROGRESO"
    Entonces el sistema confirma la actualización de la tarea
    Y la tarea "Sacar la basura el martes" queda registrada en "Familia Pérez"

  @excepcion
  Escenario: Editar tarea dejando el nombre vacío
    Cuando se edita la tarea "Sacar la basura" cambiando el nombre a "" y el estado a "EN_PROGRESO"
    Entonces el sistema rechaza la actualización de la tarea
    Y la respuesta indica que el nombre de la tarea es obligatorio
