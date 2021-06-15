package com.dalma.broker.api.configuration;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

import static springfox.documentation.swagger.web.UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	private static final String[] ENVS = new String[] {"local", "dev"};
	
    @Value("${swagger.enabled}")
    private Boolean swaggerEnabled;
    
    @Value("${spring.profiles.active}")
    private String profile;

    @Bean
    public Docket api() {
    	if (!swaggerEnabled.booleanValue() || Arrays.stream(ENVS).noneMatch(profile::equals)) {
    		return new Docket(DocumentationType.SWAGGER_2).enable(false);
    	}
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .paths(Predicates.not(PathSelectors.regex("/oauth.*")))
                .paths(Predicates.not(PathSelectors.regex("/api/login")))
                .paths(Predicates.not(PathSelectors.regex("/health.*")))
                .paths(Predicates.not(PathSelectors.regex("/metrics.*")))
                .paths(Predicates.not(PathSelectors.regex("/info")))
                .build();
    }

    @Bean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .docExpansion(DocExpansion.NONE)
                .operationsSorter(OperationsSorter.ALPHA)
                .defaultModelRendering(ModelRendering.MODEL)
                .supportedSubmitMethods(DEFAULT_SUBMIT_METHODS)
                .build();
    }
}
