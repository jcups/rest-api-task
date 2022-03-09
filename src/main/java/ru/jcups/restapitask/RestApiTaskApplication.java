package ru.jcups.restapitask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class RestApiTaskApplication {
    private static final Logger logger = LoggerFactory.getLogger(RestApiTaskApplication.class);

    public static void main(String[] args) {
        logger.info("RestApiTaskApplication.main");
        logger.info("main() called with: args = [" + Arrays.toString(args) + "]");
        ConfigurableApplicationContext context = SpringApplication.run(RestApiTaskApplication.class, args);
        context.getBeanDefinitionNames();
    }

}
