package com.zoom.config;

import com.zoom.entity.Role;
import com.zoom.entity.User;
import com.zoom.repository.UserRepository;
import com.zoom.service.RoleService;
import com.zoom.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();
        String email = user.getAttribute("email");

        User existingUser = userRepository.findByEmail(email);
        if (existingUser == null) {
            User newUser = new User();
            newUser.setEmail(email);
            Role role = new Role();
            role.setRole("ADMIN");
            role.setUsername(email);
            roleService.saveRole(role);
            userService.createUser(newUser);
            System.out.println("New user registered with email: " + email);
        } else {
            System.out.println("User already exists: " + email);
        }
        new DefaultRedirectStrategy().sendRedirect(request, response, "/userDashboard");
    }
}