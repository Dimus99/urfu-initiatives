package com.example.demo.controllers;

import com.example.demo.models.Initiative;
import com.example.demo.models.InitiativeStatus;
import com.example.demo.repos.InitiativeRepo;
import com.example.demo.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;


@Controller
public class BlogController {
    @Autowired
    private InitiativeRepo initiativeRepo;

    @GetMapping("/blog")
    public String blogMain(Model model) {
        Iterable<Initiative> initiatives = initiativeRepo.findAll();
        model.addAttribute("initiatives", initiatives);
        return "blog-main";
    }

    @GetMapping("/blog/add")
    @PreAuthorize("hasAuthority('initiative:add')")
    public String blogAdd(Model model) {
        return "blog-add";
    }

    @PostMapping("/blog/add")
    @PreAuthorize("hasAuthority('initiative:add')")
    public String blockPostAdd(@RequestParam String name,
                               @RequestParam String text,
                               @RequestParam Integer cost,
                               @RequestParam String performerAddress,
                               Model model) {
        var user = SecurityContextHolder.getContext().getAuthentication();
        var author = user.getName();
        var initiative = new Initiative(name,text,cost,author,performerAddress);
        initiativeRepo.save(initiative);
        return "redirect:/blog/";
    }

    @GetMapping("/blog/{id}")
    public String blogById(@PathVariable(value = "id") Long id, Model model) {
        if (!initiativeRepo.existsById(id)){
            return "redirect:/blog/";
        }

        Optional<Initiative> initiative = initiativeRepo.findById(id);
        var result = new ArrayList<Initiative>();
        initiative.ifPresent(result::add);
        model.addAttribute("initiative", result);
        return "blog-by-id";
    }

    @GetMapping("/blog/{id}/edit")
    public String blogEdit(@PathVariable(value = "id") Long id, Model model) {
        if (!initiativeRepo.existsById(id)) {
            return "redirect:/blog";
        }
        Optional<Initiative> initiative = initiativeRepo.findById(id);
        var result = new ArrayList<Initiative>();
        initiative.ifPresent(result::add);
        model.addAttribute("initiative", result);
        model.addAttribute("initiativeStatus", Arrays.stream(InitiativeStatus.values()).filter(z->z != result.get(0).getStatus()).toArray());
        return "blog-edit";
    }

    @PostMapping("/blog/{id}/edit")
    @PreAuthorize("hasAuthority('initiative:add')")
    public String blogEdit(@PathVariable(value = "id") Long id, @RequestParam String name,
                               @RequestParam String status,
                               @RequestParam String text,
                               @RequestParam Integer cost,
                               @RequestParam String author,
                               @RequestParam String performerAddress,
                               Model model) {
        Initiative initiative = initiativeRepo.findById(id).orElseThrow();
        initiative.setName(name);
        initiative.setStatus(InitiativeStatus.valueOf(status));
        initiative.setText(text);
        initiative.setCost(cost);
        initiative.setAuthor(author);
        initiative.setPerformerAddress(performerAddress);
        initiativeRepo.save(initiative);
        return "redirect:/blog/%s/".formatted(id);
    }

    @PostMapping("/blog/{id}/remove")
    @PreAuthorize("hasAuthority('initiative:add')")
    public String blogRemove(@PathVariable(value = "id") Long id,
                           Model model) {
        Initiative initiative = initiativeRepo.findById(id).orElseThrow();
        initiativeRepo.delete(initiative);
        return "redirect:/blog/";
    }
}