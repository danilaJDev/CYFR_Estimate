package ae.cyfr.estimateapp.config;

import ae.cyfr.estimateapp.model.Role;
import ae.cyfr.estimateapp.model.User;
import ae.cyfr.estimateapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner dataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("1"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }
        };
    }
}