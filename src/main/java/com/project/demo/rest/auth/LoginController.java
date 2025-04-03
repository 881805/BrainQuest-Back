package com.project.demo.rest.auth;


import com.project.demo.auth.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(){
        return "redirect:/team";
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal OAuth2User oAuth2User){
        if(oAuth2User != null){
            userService.processOAuth2User(oAuth2User);
        }
        return "home";
    }

}
