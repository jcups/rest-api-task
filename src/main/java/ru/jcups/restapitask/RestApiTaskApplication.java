package ru.jcups.restapitask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class RestApiTaskApplication {
    // TODO: 01.03.2022 Logging in any class
    // TODO: 01.03.2022 git
    
    public static void main(String[] args) {
        System.out.println("RestApiTaskApplication.main");
        System.out.println("args = " + Arrays.deepToString(args));
        ConfigurableApplicationContext context = SpringApplication.run(RestApiTaskApplication.class, args);
    }

}
