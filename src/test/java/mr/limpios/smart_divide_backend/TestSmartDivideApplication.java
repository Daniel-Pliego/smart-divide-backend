package mr.limpios.smart_divide_backend;

import org.springframework.boot.SpringApplication;

public class TestSmartDivideApplication {

	public static void main(String[] args) {
		SpringApplication.from(SmartDivideApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
