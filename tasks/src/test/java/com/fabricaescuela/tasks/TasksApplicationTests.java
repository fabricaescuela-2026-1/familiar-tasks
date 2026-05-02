package com.fabricaescuela.tasks;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Requiere conexión a PostgreSQL — no disponible en entorno CI sin base de datos")
@SpringBootTest
class TasksApplicationTests {

	@Test
	void contextLoads() {
		// requiere PostgreSQL — cubierto en pruebas de integración
	}

}
