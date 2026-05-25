package edu.cit.yungco.expensemini.config;

import edu.cit.yungco.expensemini.model.Category;
import edu.cit.yungco.expensemini.repository.CategoryRepository;
import edu.cit.yungco.expensemini.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SeedConfig {

    @Bean
    public CommandLineRunner seedCategories(CategoryRepository categoryRepository, UserRepository userRepository, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return args -> {
            if (categoryRepository.count() == 0) {
                categoryRepository.saveAll(List.of(
                        Category.builder().name("Food").description("Food mapped category").build(),
                        Category.builder().name("Transport").description("Transport mapped category").build(),
                        Category.builder().name("School").description("School mapped category").build(),
                        Category.builder().name("Personal").description("Personal mapped category").build(),
                        Category.builder().name("Other").description("Other mapped category").build()));
            }

            // Seed Admin User
            if (!userRepository.existsByEmail("admin@expensemini.com")) {
                edu.cit.yungco.expensemini.model.User admin = edu.cit.yungco.expensemini.model.User.builder()
                        .firstName("System")
                        .lastName("Admin")
                        .email("admin@expensemini.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(edu.cit.yungco.expensemini.model.Role.ADMIN)
                        .provider("LOCAL")
                        .build();
                userRepository.save(admin);
            }
        };
    }
}
