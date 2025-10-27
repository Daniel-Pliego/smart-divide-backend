package mr.limpios.smart_divide_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
public class SmartDivideApplication {
	public static void main(String[] args) {
		SpringApplication.run(SmartDivideApplication.class, args);
	}
}
