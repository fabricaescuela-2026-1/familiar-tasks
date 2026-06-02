# language: es
@sprint2 @HU13 @tareas
Característica: HU13 - Creación de tarea en un hogar
  Como integrante de un hogar
  Quiero crear tareas para los miembros del hogar
  Para organizar las responsabilidades del grupo

  Antecedentes:
    Dado que existen los estados "PENDIENTE" y "EN_PROGRESO" en el sistema de tareas
    Y existen las prioridades "ALTA" y "MEDIA" en el sistema de tareas
    Y existe el miembro "luis@familia.com" en el hogar "Familia Pérez"

  @feliz
  Escenario: Crear tarea exitosamente para un miembro del hogar
    Cuando se crea la tarea "Sacar la basura" con descripción "Recoger basura del lunes" prioridad "ALTA" estado "PENDIENTE" para "luis@familia.com" en "Familia Pérez" con vencimiento en 5 días
    Entonces el sistema confirma la creación de la tarea
    Y la tarea "Sacar la basura" queda registrada en "Familia Pérez"

  @excepcion
  Escenario: Crear tarea sin título
    Cuando se crea la tarea "" con descripción "Recoger basura del lunes" prioridad "ALTA" estado "PENDIENTE" para "luis@familia.com" en "Familia Pérez" con vencimiento en 5 días
    Entonces el sistema rechaza la creación de la tarea
    Y la respuesta indica que el nombre de la tarea es obligatorio

  @excepcion
  Escenario: Crear tarea con fecha de vencimiento en el pasado
    Cuando se crea la tarea "Pasear al perro" con descripción "Caminata por el parque" prioridad "MEDIA" estado "PENDIENTE" para "luis@familia.com" en "Familia Pérez" con vencimiento en -1 días
    Entonces el sistema rechaza la creación de la tarea
    Y la respuesta indica que la fecha debe ser futura

  @excepcion
  Escenario: Crear tarea asignada a una persona que no pertenece al hogar
    Cuando se crea la tarea "Lavar el carro" con descripción "Carro del fin de semana" prioridad "MEDIA" estado "PENDIENTE" para un usuario que no pertenece al hogar "Familia Pérez" con vencimiento en 3 días
    Entonces el sistema rechaza la creación de la tarea
    Y la respuesta indica que el usuario no pertenece al hogar
