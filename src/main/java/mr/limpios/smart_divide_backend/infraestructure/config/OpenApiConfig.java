package mr.limpios.smart_divide_backend.infraestructure.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Smart Divide Backend API", version = "v1.0.0",
        description = "API documentation for Smart Divide Endpoints"))
public class OpenApiConfig {

}
