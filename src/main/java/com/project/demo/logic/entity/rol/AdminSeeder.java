package com.project.demo.logic.entity.rol;

import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

@Component
@Order(2) // Ensure roles are seeded before this (RoleSeeder should be @Order(1))
public class AdminSeeder implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default.superadmin.password}")
    private String superAdminPassword;

    @Value("${app.default.ai.password}")
    private String aiPassword;

    public AdminSeeder(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        createSuperAdministrator();
        createAIUser();
    }

    private void createSuperAdministrator() {

        String email = "super.admin@gmail.com";

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            System.out.println("SuperAdmin already exists");
            return;
        }

        Optional<Role> roleOpt = roleRepository.findByName(RoleEnum.SUPER_ADMIN);
        if (roleOpt.isEmpty()) {
            System.out.println("SUPER_ADMIN role not found. Skipping SuperAdmin creation.");
            return;
        }

        User user = new User();
        user.setName("Super");
        user.setLastname("Admin");
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(superAdminPassword));
        user.setRole(roleOpt.get());
        user.setExperience(0L);

        userRepository.save(user);
        System.out.println("SuperAdmin created");
    }

    private void createAIUser() {

        String email = "gemini.google@gmail.com";

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            System.out.println("AI user already exists");
            return;
        }

        Optional<Role> roleOpt = roleRepository.findByName(RoleEnum.USER);
        if (roleOpt.isEmpty()) {
            System.out.println("USER role not found. Skipping AI user creation.");
            return;
        }

        User aiUser = new User();
        aiUser.setName("Gemini");
        aiUser.setLastname("Google");
        aiUser.setEmail(email);
        aiUser.setPassword(passwordEncoder.encode(aiPassword));
        aiUser.setRole(roleOpt.get());
        aiUser.setExperience(0L);

        userRepository.save(aiUser);
        System.out.println("AI user created");
    }
}