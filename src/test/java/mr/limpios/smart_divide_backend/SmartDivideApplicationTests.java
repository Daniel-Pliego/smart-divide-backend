package mr.limpios.smart_divide_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@TestPropertySource(properties = {"jwt.secret.key=test-secret-key-for-testing-purposes-only-do-not-use-in-production", "jwt.expiration.time=3600000"})
class SmartDivideApplicationTests {

	@Test
	void contextLoads() {
	}

}
