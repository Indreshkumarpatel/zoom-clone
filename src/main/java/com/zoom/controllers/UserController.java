package com.zoom.controllers;

import com.zoom.entity.Role;
import com.zoom.entity.User;
import com.zoom.service.RoleService;
import com.zoom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("user")
public class UserController {

    private UserService userService;
    private RoleService roleService;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, RoleService roleService,BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder=passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage(Model model, @RequestParam(required = false) String error) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        return "sign-in";
    }

    @GetMapping("/homepage")
    public String homePage() {
        return "user-dashboard";
    }

    @GetMapping("/signUp")
    public String signUp(Model model) {
        System.out.println("HELLO");
        User user = new User();
        model.addAttribute("user", user);
        return "sign-up";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        User existingUser = userService.findUserByEmail(user.getEmail());
        if (existingUser != null) {
            model.addAttribute("errorMessage", "User already exists!");
            return "sign-up";
        }

        Role role = new Role();
        role.setRole("ADMIN");
        role.setUsername(user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        roleService.saveRole(role);
        userService.createUser(user);
        return "sign-in";
    }
}
