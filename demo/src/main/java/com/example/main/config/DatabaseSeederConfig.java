package com.example.main.config;

import com.example.main.dto.request.UserAccountRequest;
import com.example.main.enums.Role;
import com.example.main.repository.AlgorithmRepository;
import com.example.main.repository.CodeSnippetRepository;
import com.example.main.repository.UserRepository;
import com.example.main.service.AlgorithmSeeder;
import com.example.main.service.UserAccountService;
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

    @Bean
    public CommandLineRunner initUsers(UserRepository userRepository,
                                       UserAccountService userAccountService) {
        return args -> {
            if (!userRepository.existsByUsernameIgnoreCase("admin")) {
                userAccountService.create(
                        new UserAccountRequest("admin", "admin123", Role.ADMIN, "ACTIVE"));
            }
            if (!userRepository.existsByUsernameIgnoreCase("user")) {
                userAccountService.create(
                        new UserAccountRequest("user", "user123", Role.USER, "ACTIVE"));
            }
        };
    }
}
