# language: es
@sprint2 @HU04 @auth
Característica: HU04 - Autenticación con JWT
  Como usuario registrado
  Quiero que el sistema me identifique después de iniciar sesión
  Para poder usar la aplicación sin tener que ingresar mis datos cada vez

  Antecedentes:
    Dado que el servicio de autenticación está disponible

  @feliz
  Escenario: Inicio de sesión exitoso
    Dado que existe un usuario con correo "andres@familia.com" y contraseña "Segura123!"
    Cuando solicita iniciar sesión con correo "andres@familia.com" y contraseña "Segura123!"
    Entonces el sistema autentica al usuario
    Y devuelve un token de acceso válido
    Y devuelve un token de refresco

  @excepcion
  Escenario: Inicio de sesión con contraseña incorrecta
    Dado que existe un usuario con correo "juan@familia.com" y contraseña "Segura123!"
    Cuando solicita iniciar sesión con correo "juan@familia.com" y contraseña "Equivocada1!"
    Entonces el sistema rechaza el inicio de sesión
    Y la respuesta indica que las credenciales son incorrectas

  @excepcion
  Escenario: Inicio de sesión con correo no registrado
    Dado que no existe ningún usuario con correo "fantasma@familia.com"
    Cuando solicita iniciar sesión con correo "fantasma@familia.com" y contraseña "Segura123!"
    Entonces el sistema rechaza el inicio de sesión
    Y la respuesta indica que las credenciales son incorrectas
