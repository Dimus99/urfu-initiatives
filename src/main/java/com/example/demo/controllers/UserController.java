package com.example.demo.controllers;

import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

@Controller
@PreAuthorize("hasRole('users:manage')")
public class UserController {
    @Autowired
    UserRepo userRepo;

    @GetMapping("/users")
    public String users(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.getRole().equals(Role.ADMIN))
            return "redirect:/";
        var users = userRepo.findAll();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/users/{id}/edit")
    public String userEdit(@PathVariable(value = "id") String id, Model model) {
        User userMy = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!userMy.getRole().equals(Role.ADMIN))
            return "redirect:/";
        var user = userRepo.findById(id).orElseThrow();
        model.addAttribute("roles", Arrays.stream(Role.values()).filter(z -> z != user.getRole()).toArray());
        model.addAttribute("user", user);
        return "user-edit";
    }

    @PostMapping("/users/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public String userPostEdit(@PathVariable(value = "id") String id,
                               @RequestParam String role, Model model) {
        User userMy = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!userMy.getRole().equals(Role.ADMIN))
            return "redirect:/";
        var user = userRepo.findById(id).orElseThrow();
       user.setRole(Role.valueOf(role));
       userRepo.save(user);
        return "redirect:/users";
    }
}
