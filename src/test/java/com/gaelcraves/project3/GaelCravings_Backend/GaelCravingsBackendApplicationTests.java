package com.gaelcraves.project3.GaelCravings_Backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
	"spring.datasource.url=jdbc:h2:mem:testdb",
	"spring.datasource.driver-class-name=org.h2.Driver",
	"spring.jpa.hibernate.ddl-auto=create-drop",
	"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
	"app.jwt.secret=dGVzdC1qd3Qtc2VjcmV0LWtleS1mb3ItZGV2ZWxvcG1lbnQtb25seQ==",
	"app.cors.allowed-origin=http://localhost:3000",
	"supabase.project.url=http://localhost",
	"supabase.api.key=test-key"
})
class GaelCravingsBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
