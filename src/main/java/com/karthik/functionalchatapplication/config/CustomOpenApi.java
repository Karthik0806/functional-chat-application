package com.karthik.functionalchatapplication.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomOpenApi {
    public OpenAPI customOpenAPI(){
        return new OpenAPI().info(
                new Info().title("functional-chat-application API")
                        .version("1.0")
                        .description("Real time chat application APIs")
        );
    }
}
