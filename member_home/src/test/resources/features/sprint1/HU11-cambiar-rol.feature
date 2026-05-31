# language: es
@sprint1 @HU11 @roles
Característica: HU11 - Cambiar rol de miembros
  Como administrador del grupo familiar
  Quiero poder cambiar el rol de los miembros
  Para controlar quién puede gestionar tareas y miembros

  Antecedentes:
    Dado que existen los roles "Administrador" y "Miembro" en el sistema
    Y existe el grupo familiar "Familia Pérez"

  @feliz
  Escenario: Cambio de rol exitoso por parte de un administrador
    Dado que "admin@familia.com" es administrador del grupo "Familia Pérez"
    Y "miembro@familia.com" es miembro del grupo "Familia Pérez" con rol "Miembro"
    Cuando "admin@familia.com" cambia el rol de "miembro@familia.com" en "Familia Pérez" a "Administrador"
    Entonces el sistema actualiza el rol de "miembro@familia.com" a "Administrador" en el grupo "Familia Pérez"

  @excepcion
  Escenario: Miembro sin permisos intenta cambiar un rol
    Dado que "miembro1@familia.com" es miembro del grupo "Familia Pérez" con rol "Miembro"
    Y "miembro2@familia.com" es miembro del grupo "Familia Pérez" con rol "Miembro"
    Cuando "miembro1@familia.com" intenta cambiar el rol de "miembro2@familia.com" en "Familia Pérez" a "Administrador"
    Entonces el sistema rechaza el cambio de rol
    Y la respuesta indica que no tiene permisos para esa acción

  @excepcion
  Escenario: Único administrador intenta quitarse el rol
    Dado que "admin@familia.com" es el único administrador del grupo "Familia Pérez"
    Cuando "admin@familia.com" intenta cambiar su propio rol en "Familia Pérez" a "Miembro"
    Entonces el sistema rechaza el cambio de rol
    Y la respuesta indica que el grupo debe tener al menos un administrador
