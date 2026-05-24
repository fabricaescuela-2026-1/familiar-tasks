# language: es
@sprint1 @HU02 @auth
Característica: HU02 - Validación de contraseña
  Como usuario registrado
  Quiero que el sistema valide mi contraseña al crearla o cambiarla
  Para asegurarme de que cumple los criterios de seguridad antes de guardarla

  @feliz
  Escenario: La contraseña cumple todos los requisitos
    Cuando se valida la contraseña "Segura123!"
    Entonces el sistema la acepta como válida

  @excepcion
  Esquema del escenario: La contraseña no cumple algún requisito de seguridad
    Cuando se valida la contraseña "<contraseña>"
    Entonces el sistema la rechaza
    Y la respuesta indica el requisito incumplido "<requisito>"

    Ejemplos:
      | contraseña   | requisito                       |
      | Cor1!        | longitud mínima de 8 caracteres |
      | sinmayus1!   | al menos una letra mayúscula    |
      | SINMINUS1!   | al menos una letra minúscula    |
      | SinDigitos!  | al menos un dígito              |
      | SinEspecial1 | al menos un carácter especial   |

  @excepcion
  Escenario: La confirmación de contraseña no coincide
    Cuando se ingresa la contraseña "Segura123!" y la confirmación "Otra456?"
    Entonces el sistema rechaza la operación
    Y la respuesta indica que las contraseñas no coinciden
