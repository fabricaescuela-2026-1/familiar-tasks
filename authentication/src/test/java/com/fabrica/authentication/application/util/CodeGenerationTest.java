package com.fabrica.authentication.application.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class CodeGenerationTest {

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void generateSixDigitCodeRetornaCadenaDeSeisDigitos() {
        // Act
        String code = CodeGeneration.generateSixDigitCode();

        // Assert
        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.chars().allMatch(Character::isDigit));
    }

    @Test
    void generateSixDigitCodeEsParseableComoEntero() {
        // Act
        String code = CodeGeneration.generateSixDigitCode();

        // Assert
        int valor = assertDoesNotThrow(() -> Integer.parseInt(code));
        assertTrue(valor >= 0 && valor < 1_000_000);
    }

    @Test
    void generateSixDigitCodeRepetidoSiempreProduceSeisCaracteres() {
        for (int i = 0; i < 50; i++) {
            String code = CodeGeneration.generateSixDigitCode();
            assertEquals(6, code.length());
            assertTrue(code.chars().allMatch(Character::isDigit));
        }
    }

    @Test
    void constructorPrivadoExisteParaImpedirInstanciacion() throws Exception {
        // Arrange
        Constructor<CodeGeneration> ctor = CodeGeneration.class.getDeclaredConstructor();

        // Act - Assert
        assertTrue(Modifier.isPrivate(ctor.getModifiers()));
        ctor.setAccessible(true);
        assertNotNull(ctor.newInstance());
    }
}
