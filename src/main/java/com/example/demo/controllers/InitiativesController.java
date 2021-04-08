package com.example.demo.controllers;

import com.example.demo.models.Initiative;
import com.example.demo.models.InitiativeStatus;
import com.example.demo.models.Permission;
import com.example.demo.models.User;
import com.example.demo.repos.InitiativeRepo;
import com.example.demo.repos.UserRepo;
import com.example.demo.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;


@Controller
public class InitiativesController {
    @Autowired
    private InitiativeRepo initiativeRepo;

    @Autowired
    private UserRepo userRepo;

    @GetMapping("/initiatives")
    public String blogMain(Model model) {
        Iterable<Initiative> initiatives = initiativeRepo.findAll();
        model.addAttribute("initiatives", initiatives);
        return "initiatives-main";
    }

    @GetMapping("/initiatives/add")
    @PreAuthorize("hasAuthority('initiative:add')")
    public String blogAdd(Model model) {
        return "initiative-add";
    }

    @PostMapping("/initiatives/add")
    @PreAuthorize("hasAuthority('initiative:add')")
    public String blockPostAdd(@RequestParam String name,
                               @RequestParam String text,
                               @RequestParam(value = "0", required = false) Integer cost,
                               @RequestParam String performerAddress,
                               Model model) {
        var user = SecurityContextHolder.getContext().getAuthentication();
        var author = user.getName();
        if (cost == null) cost = 0;
        var initiative = new Initiative(name, text, cost, author, performerAddress);
        initiativeRepo.save(initiative);
        return "redirect:/initiatives/";
    }


    @GetMapping("/initiatives/{id}")
    public String blogById(@PathVariable(value = "id") Long id, Model model) {
        if (!initiativeRepo.existsById(id)) {
            return "redirect:/initiatives/";
        }

        Optional<Initiative> initiative = initiativeRepo.findById(id);
        var result = new ArrayList<Initiative>();
        initiative.ifPresent(result::add);
        model.addAttribute("initiative", result);
        var userName = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepo.findByEmail(userName).orElseThrow();
        var securityUser = SecurityUser.fromUser(user);
        var isManage = securityUser.getAuthorities()
                .contains(new SimpleGrantedAuthority(
                        Permission.INITIATIVE_MANAGE.getPermission()));

        model.addAttribute("userName", userName);
        model.addAttribute("isManage", isManage);
        model.addAttribute("votesCount", initiative.get().getVotesCount());
        model.addAttribute("isVoted", user.getVotes().contains(initiative.get()));
        model.addAttribute("isVotedStatus",
                initiative.get().getStatus().equals(InitiativeStatus.STUDENT_VOTE));
        return "initiative-by-id";
    }

    @GetMapping("/initiatives/{id}/edit")
    @PreAuthorize("hasAuthority('initiative:manage')")
    public String blogEdit(@PathVariable(value = "id") Long id, Model model) {
        if (!initiativeRepo.existsById(id)) {
            return "redirect:/initiatives";
        }
        Optional<Initiative> initiative = initiativeRepo.findById(id);
        var result = new ArrayList<Initiative>();
        initiative.ifPresent(result::add);
        model.addAttribute("initiative", result);
        model.addAttribute("initiativeStatus", Arrays.stream(InitiativeStatus.values()).filter(z -> z != result.get(0).getStatus()).toArray());
        model.addAttribute("isManage", true);
        return "initiative-edit";
    }

    @PostMapping("/initiatives/{id}/edit")
    @PreAuthorize("hasAuthority('initiative:manage')")
    public String blogEdit(@PathVariable(value = "id") Long id, @RequestParam String name,
                           @RequestParam String status,
                           @RequestParam String text,
                           @RequestParam Integer cost,
                           @RequestParam String author,
                           @RequestParam String performerAddress,
                           @RequestParam int votesNeed,
                           Model model) {
        Initiative initiative = initiativeRepo.findById(id).orElseThrow();
        initiative.setName(name);
        initiative.setStatus(InitiativeStatus.valueOf(status));
        initiative.setText(text);
        initiative.setCost(cost);
        initiative.setAuthor(author);
        initiative.setPerformerAddress(performerAddress);
        initiative.setVotesNeed(votesNeed);
        initiativeRepo.save(initiative);
        return "redirect:/initiatives/%s/".formatted(id);
    }

    @GetMapping("/initiatives/{id}/edit-my")
    public String blogEditMy(@PathVariable(value = "id") Long id, Model model) {
        if (!initiativeRepo.existsById(id)) {
            return "redirect:/initiatives";
        }
        var user = SecurityContextHolder.getContext().getAuthentication();
        var userName = user.getName();
        Optional<Initiative> initiative = initiativeRepo.findById(id);
        var result = new ArrayList<Initiative>();
        initiative.ifPresent(result::add);

        if (!userName.equals(initiative.get().getAuthor())) {
            return "redirect:/initiatives";
        }

        model.addAttribute("initiative", result);
        model.addAttribute("isManage", false); // isManage = true if it's moderator
        return "initiative-edit";
    }

    @PostMapping("/initiatives/{id}/edit-my")
    public String blogEditMy(@PathVariable(value = "id") Long id, @RequestParam String name,
                             @RequestParam String text,
                             @RequestParam Integer cost,
                             @RequestParam String performerAddress,
                             Model model) {

        Initiative initiative = initiativeRepo.findById(id).orElseThrow();
        var user = SecurityContextHolder.getContext().getAuthentication();
        var userName = user.getName();
        if (!userName.equals(initiative.getAuthor())) {
            return "redirect:/initiatives";
        }
        initiative.setName(name);
        initiative.setText(text);
        initiative.setCost(cost);
        initiative.setPerformerAddress(performerAddress);
        initiativeRepo.save(initiative);
        return "redirect:/initiatives/%s/".formatted(id);
    }

    @PostMapping("/initiatives/{id}/remove")
    @PreAuthorize("hasAuthority('initiative:manage')")
    public String blogRemove(@PathVariable(value = "id") Long id,
                             Model model) {
        Initiative initiative = initiativeRepo.findById(id).orElseThrow();
        initiativeRepo.delete(initiative);
        return "redirect:/initiatives/";
    }

    @PostMapping("/initiatives/{id}/vote")
    public String blogVoting(@PathVariable(value = "id") Long id,
                             Model model) {
        Initiative initiative = initiativeRepo.findById(id).orElseThrow();
        var userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(userName).orElseThrow();
        initiative.addVote(user);
        initiativeRepo.save(initiative);
        return "redirect:/initiatives/%s/".formatted(id);
    }

    @PostMapping("/initiatives/{id}/vote-remove")
    public String blogVotingRemove(@PathVariable(value = "id") Long id,
                             Model model) throws Exception {
        Initiative initiative = initiativeRepo.findById(id).orElseThrow();
        var userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(userName).orElseThrow();
        initiative.removeVote(user);
        initiativeRepo.save(initiative);
        return "redirect:/initiatives/%s/".formatted(id);
    }

    @GetMapping("/initiatives/my")
    public String myInitiatives(Model model) {
        var user = SecurityContextHolder.getContext().getAuthentication();
        var userName = user.getName();
        Iterable<Initiative> initiatives = initiativeRepo.findInitiativesByAuthor(userName);
        model.addAttribute("initiatives", initiatives);
        return "initiatives-main";
    }
}