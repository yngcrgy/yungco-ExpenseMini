package edu.cit.yungco.expensemini.config;

import edu.cit.yungco.expensemini.model.Category;
import edu.cit.yungco.expensemini.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SeedConfig {

    @Bean
    public CommandLineRunner seedCategories(CategoryRepository categoryRepository) {
        return args -> {
            if (categoryRepository.count() == 0) {
                categoryRepository.saveAll(List.of(
                        Category.builder().name("Food").description("Food mapped category").build(),
                        Category.builder().name("Transport").description("Transport mapped category").build(),
                        Category.builder().name("School").description("School mapped category").build(),
                        Category.builder().name("Personal").description("Personal mapped category").build(),
                        Category.builder().name("Other").description("Other mapped category").build()));
            }
        };
    }
}
