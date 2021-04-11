package com.example.demo.controllers;

import com.example.demo.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "Информация о нас");
        return "about";
    }

    @GetMapping("/")
    public String home(Model model) {
        var map = new HashMap<String, String>();
        map.put("name", "NAME");
        model.addAttribute("data", map);
        return "home";
    }
}