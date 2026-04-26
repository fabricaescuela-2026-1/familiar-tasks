package com.fabrica.authentication;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Requiere conexión a PostgreSQL — no disponible en entorno CI sin base de datos")
@SpringBootTest
class AuthenticationApplicationTests {

	@Test
	void contextLoads() {
	}

}
