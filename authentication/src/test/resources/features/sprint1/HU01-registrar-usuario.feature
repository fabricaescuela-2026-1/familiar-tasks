# language: es
@sprint1 @HU01 @auth
Característica: HU01 - Registrar usuario
  Como persona que quiere usar la aplicación
  Quiero crear una cuenta con mi correo y una contraseña
  Para poder ingresar al sistema y organizar las tareas de mi hogar

  Antecedentes:
    Dado que el servicio de autenticación está disponible

  @feliz
  Escenario: Registro exitoso con datos válidos
    Dado que no existe ningún usuario registrado con el correo "laura@familia.com"
    Cuando la persona solicita registrarse con:
      | nombre | apellido | correo            | contraseña |
      | Laura  | Pérez    | laura@familia.com | Segura123! |
    Entonces el sistema confirma el registro exitoso
    Y queda registrada la cuenta del correo "laura@familia.com"

  @excepcion
  Escenario: Registro rechazado porque el correo ya está en uso
    Dado que ya existe un usuario registrado con el correo "duplicado@familia.com"
    Cuando la persona solicita registrarse con:
      | nombre | apellido | correo                | contraseña |
      | Junior | Gómez    | duplicado@familia.com | Segura123! |
    Entonces el sistema rechaza el registro
    Y la respuesta indica que el correo ya está en uso

  @excepcion
  Esquema del escenario: Registro rechazado por contraseña que no cumple los requisitos
    Cuando la persona solicita registrarse con la contraseña "<contraseña>"
    Entonces el sistema rechaza el registro
    Y la respuesta indica el requisito incumplido "<requisito>"

    Ejemplos:
      | contraseña   | requisito                       |
      | Cor1!        | longitud mínima de 8 caracteres |
      | sinmayus1!   | al menos una letra mayúscula    |
      | SINMINUS1!   | al menos una letra minúscula    |
      | SinDigitos!  | al menos un dígito              |
      | SinEspecial1 | al menos un carácter especial   |

  @excepcion
  Esquema del escenario: Registro rechazado por campo obligatorio vacío
    Cuando la persona solicita registrarse sin diligenciar el campo "<campo>"
    Entonces el sistema rechaza el registro
    Y la respuesta indica que el campo "<campo>" es obligatorio

    Ejemplos:
      | campo      |
      | nombre     |
      | apellido   |
      | correo     |
      | contraseña |
