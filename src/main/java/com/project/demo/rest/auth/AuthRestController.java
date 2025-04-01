package com.project.demo.rest.auth;

import com.project.demo.logic.entity.auth.AuthenticationService;
import com.project.demo.logic.entity.auth.JwtService;
import com.project.demo.logic.entity.rol.Role;
import com.project.demo.logic.entity.rol.RoleEnum;
import com.project.demo.logic.entity.rol.RoleRepository;
import com.project.demo.logic.entity.user.LoginResponse;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RequestMapping("/auth")
@RestController
public class AuthRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthRestController(JwtService jwtService, AuthenticationService authenticationService, UserService userService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody User user) {
        User authenticatedUser = authenticationService.authenticate(user);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        Optional<User> foundedUser = userRepository.findByEmail(user.getEmail());
        foundedUser.ifPresent(loginResponse::setAuthUser);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);

        if (optionalRole.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Role not found");
        }
        user.setRole(optionalRole.get());
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<LoginResponse> oauth2Success(@AuthenticationPrincipal OAuth2User oAuth2User) {
        User user = userService.processOAuth2User(oAuth2User);

        String jwtToken = jwtService.generateToken(user);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        loginResponse.setAuthUser(user);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/link-google")
    public ResponseEntity<?> linkGoogle(@AuthenticationPrincipal OAuth2User oAuth2User, @RequestParam String email){
        Optional<User> uSerOptional = userRepository.findByEmail(email);
        if(uSerOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = uSerOptional.get();
        String googleId = oAuth2User.getAttribute("sub");

        user.setGoogleId(googleId);
        user.setProvider("Google");
        userRepository.save(user);

        return ResponseEntity.ok("Google account linked successfully!");
    }

    @PostMapping("/recover-with-google")
    public ResponseEntity<?> recoverAccessWithGoogle(@AuthenticationPrincipal OAuth2User oAuth2User){
        String googleId = oAuth2User.getAttribute("sub");

        Optional<User> userOptional = userService.findUserByGoogleId(googleId);
        if(userOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not linked google account found");
        }

        User user = userOptional.get();

        String jwtToken = jwtService.generateToken(user);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        loginResponse.setAuthUser(user);

        return ResponseEntity.ok(loginResponse);
    }
}