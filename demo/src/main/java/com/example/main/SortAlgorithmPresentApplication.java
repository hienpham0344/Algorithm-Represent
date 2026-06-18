package com.example.main;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SortAlgorithmPresentApplication {
    private static ConfigurableApplicationContext context;

    public static synchronized ConfigurableApplicationContext run(String... args) {
        if (context == null) {
            context = new SpringApplicationBuilder(SortAlgorithmPresentApplication.class)
                    .headless(false)
                    .run(args);
        }
        return context;
    }

    public static synchronized ConfigurableApplicationContext context() {
        return context;
    }
}
