package mr.limpios.smart_divide_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@SpringBootApplication
@RestController
public class SmartDivideApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartDivideApplication.class, args);
	}
	@GetMapping("/hello")
	 public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
      return String.format("Hello %s!", name);
	}
	

}
