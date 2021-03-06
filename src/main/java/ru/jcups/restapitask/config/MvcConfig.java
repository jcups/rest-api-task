package ru.jcups.restapitask.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("").setViewName("index");
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/users").setViewName("users");
        registry.addViewController("/market").setViewName("market");
        registry.addViewController("/market/{id}").setViewName("item");
        registry.addViewController("/bucket").setViewName("bucket");
    }
}
