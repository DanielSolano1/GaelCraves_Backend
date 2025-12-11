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
	"supabase.api.key=test-key",
	// Dummy GitHub OAuth client for tests so ClientRegistrationRepository exists
	"spring.security.oauth2.client.registration.github.client-id=test-client-id",
	"spring.security.oauth2.client.registration.github.client-secret=test-client-secret",
	"spring.security.oauth2.client.registration.github.scope=read:user,user:email"
})
class GaelCravingsBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
