package com.example.main.config;

import com.example.main.repository.AlgorithmRepository;
import com.example.main.repository.CodeSnippetRepository;
import com.example.main.service.AlgorithmSeeder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseSeederConfig {

    @Bean
    public CommandLineRunner initDatabase(AlgorithmRepository algorithmRepository,
                                          CodeSnippetRepository codeSnippetRepository) {
        return args -> {
            AlgorithmSeeder.seedDefaults(algorithmRepository, codeSnippetRepository);
        };
    }
}
