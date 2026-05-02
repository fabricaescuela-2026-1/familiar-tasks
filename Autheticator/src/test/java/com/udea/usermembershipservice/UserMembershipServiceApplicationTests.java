package com.udea.usermembershipservice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Requiere conexión a PostgreSQL — no disponible en entorno CI sin base de datos")
@SpringBootTest
class UserMembershipServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
