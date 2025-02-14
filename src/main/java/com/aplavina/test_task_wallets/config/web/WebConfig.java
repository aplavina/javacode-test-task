package com.aplavina.test_task_wallets.config.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Value("${api.version}")
    private String apiVersion;

    @Override
    public void configurePathMatch(PathMatchConfigurer pathMatchConfigurer) {
        pathMatchConfigurer.addPathPrefix(apiVersion,
                HandlerTypePredicate.forAnnotation(RestController.class));
    }
}