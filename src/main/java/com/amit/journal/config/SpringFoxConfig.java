package com.amit.journal.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ParameterType;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

@Configuration
public class SpringFoxConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .globalRequestParameters(
                        singletonList(new springfox.documentation.builders.RequestParameterBuilder()
                                .name("userId")
                                .description("userId")
                                .in(ParameterType.HEADER)
                                .required(true)
                                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                                .build()));
    }
    private List<Parameter> operationParameters() {
        List<Parameter> headers = new ArrayList<>();
        headers.add(new ParameterBuilder().name("userId")
                .description("userId")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(true).build());
        return headers;
    }
}
