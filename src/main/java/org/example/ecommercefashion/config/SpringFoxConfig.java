package org.example.ecommercefashion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.ApiKey;

import java.util.Collections;

@Configuration
@EnableSwagger2
@EnableWebMvc
public class SpringFoxConfig {
  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
        .paths(PathSelectors.any())
        .build()
        .securitySchemes(Collections.singletonList(securityScheme()))
        .securityContexts(Collections.singletonList(securityContext()));
  }

  private SecurityScheme securityScheme() {
    return new ApiKey("Bearer", "Authorization", "header");
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder()
        .securityReferences(
            Collections.singletonList(
                new springfox.documentation.service.SecurityReference(
                    "Bearer",
                    new springfox.documentation.service.AuthorizationScope[] {
                      new springfox.documentation.service.AuthorizationScope(
                          "global", "accessEverything")
                    })))
        .build();
  }
}
