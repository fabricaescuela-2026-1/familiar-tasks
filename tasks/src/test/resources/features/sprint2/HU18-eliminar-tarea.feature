# language: es
@sprint2 @HU18 @tareas
Característica: HU18 - Eliminación de tarea existente
  Como integrante de un hogar
  Quiero eliminar tareas que ya no aplican
  Para mantener limpia la lista de actividades

  Antecedentes:
    Dado que existen los estados "PENDIENTE" y "EN_PROGRESO" en el sistema de tareas
    Y existen las prioridades "ALTA" y "MEDIA" en el sistema de tareas
    Y existe el miembro "luis@familia.com" en el hogar "Familia Pérez"
    Y existe la tarea "Pasear al perro" asignada a "luis@familia.com" en "Familia Pérez"

  @feliz
  Escenario: Eliminar tarea exitosamente
    Cuando se elimina la tarea "Pasear al perro"
    Entonces el sistema confirma la eliminación de la tarea
    Y la tarea "Pasear al perro" ya no existe en "Familia Pérez"

  @excepcion
  Escenario: Eliminar tarea inexistente
    Cuando se elimina una tarea que no existe
    Entonces el sistema rechaza la eliminación de la tarea
