package kr.co.music.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "Music API",
        version = "v1",
        description = "This is the API documentation for the Music App"
    )
)
class SwaggerConfig {

}