# language: es
@sprint1 @HU08 @grupos
Característica: HU08 - Creación de grupo familiar
  Como persona que quiere organizar su hogar
  Quiero crear un grupo familiar
  Para gestionar las tareas con mi familia

  Antecedentes:
    Dado que existe el rol "Administrador" en el sistema
    Y la persona con correo "ana@familia.com" está registrada

  @feliz
  Escenario: Crear grupo familiar exitosamente
    Dado que la persona "ana@familia.com" no pertenece a ningún grupo familiar
    Cuando "ana@familia.com" solicita crear el grupo familiar con nombre "Familia Pérez"
    Entonces el sistema crea el grupo "Familia Pérez"
    Y la persona "ana@familia.com" queda registrada como administrador del grupo "Familia Pérez"

  @excepcion
  Escenario: Crear grupo familiar con nombre vacío
    Dado que la persona "ana@familia.com" no pertenece a ningún grupo familiar
    Cuando "ana@familia.com" solicita crear el grupo familiar con nombre ""
    Entonces el sistema rechaza la creación del grupo
    Y la respuesta indica que el nombre del grupo es obligatorio

  @excepcion
  Escenario: Crear grupo familiar con nombre duplicado
    Dado que ya existe un grupo familiar con nombre "Familia Pérez"
    Cuando "ana@familia.com" solicita crear el grupo familiar con nombre "Familia Pérez"
    Entonces el sistema rechaza la creación del grupo
    Y la respuesta indica que el nombre del grupo ya está en uso
