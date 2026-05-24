# language: es
@sprint2 @HU05 @auth
Característica: HU05 - Renovación de token
  Como usuario con sesión activa
  Quiero que mi sesión se mantenga activa si estoy usando la app
  Para no tener que volver a ingresar mis datos mientras estoy trabajando

  @feliz
  Escenario: Renovación exitosa con un token de refresco vigente
    Dado que el usuario tiene un token de refresco vigente
    Cuando solicita renovar su sesión
    Entonces el sistema devuelve un nuevo token de acceso válido
    Y devuelve un nuevo token de refresco

  @excepcion
  Escenario: No se puede renovar una sesión con token de refresco vencido
    Dado que el usuario tiene un token de refresco vencido
    Cuando solicita renovar su sesión
    Entonces el sistema rechaza la renovación
    Y la respuesta indica que la sesión expiró
    Y solicita iniciar sesión de nuevo

  @excepcion
  Escenario: Sesión expirada al intentar una acción autenticada
    Dado que el usuario tiene un token de acceso vencido
    Cuando intenta ejecutar una acción autenticada
    Entonces el sistema rechaza la acción
    Y la respuesta indica que la sesión expiró
