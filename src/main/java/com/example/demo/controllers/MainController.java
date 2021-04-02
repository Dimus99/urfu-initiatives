package com.example.demo.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Информация о нас");
        return "login";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "Информация о нас");
        return "about";
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('users:manage')")
    public String users(Model model) {
        model.addAttribute("title", "Информация о нас");
        return "users";
    }

    @GetMapping("/")
    public String home(Model model) {
        var map = new HashMap<String, String>();
        map.put("name", "NAME");
        model.addAttribute("data", map);
        return "home";
    }

}