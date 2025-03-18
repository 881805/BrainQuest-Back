package com.project.demo.logic.entity.rol;

import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public AdminSeeder(
            RoleRepository roleRepository,
            UserRepository  userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createSuperAdministrator();
        this.createAIUser();
    }

   private void createAIUser() {
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);
        Optional<User> optionalUser = userRepository.findByEmail("gemini.google@gmail.com");

        if (optionalRole.isEmpty()) {
            throw new RuntimeException("El rol USER no existe en la base de datos.");
        }

        if (optionalUser.isPresent()) {
            return; // El usuario ya existe, no se crea de nuevo
        }

        User aiUser = new User();
        aiUser.setName("Gemini");
        aiUser.setLastname("Google");
        aiUser.setEmail("gemini.google@gmail.com");
        aiUser.setPassword(passwordEncoder.encode("123456976345425843252sdfgsr@D!"));
        aiUser.setRole(optionalRole.get());

        userRepository.save(aiUser);
    }

    private void createSuperAdministrator() {
        User superAdmin = new User();
        superAdmin.setName("Super");
        superAdmin.setLastname("Admin");
        superAdmin.setEmail("super.admin@gmail.com");
        superAdmin.setPassword("superadmin123");

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.SUPER_ADMIN);
        Optional<User> optionalUser = userRepository.findByEmail(superAdmin.getEmail());

        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            return;
        }

        var user = new User();
        user.setName(superAdmin.getName());
        user.setLastname(superAdmin.getLastname());
        user.setEmail(superAdmin.getEmail());
        user.setPassword(passwordEncoder.encode(superAdmin.getPassword()));
        user.setRole(optionalRole.get());

        userRepository.save(user);
    }
}
