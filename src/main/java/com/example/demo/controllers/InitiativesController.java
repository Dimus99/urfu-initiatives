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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
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
    public String initiativesMain(Model model) {
        Iterable<Initiative> initiatives = initiativeRepo.findAll();
        model.addAttribute("initiatives", initiatives);
        return "initiatives-main";
    }

    @GetMapping("/initiatives/add")
    @PreAuthorize("hasAuthority('initiative:add')")
    public String initiativeAdd(Model model) {
        return "initiative-add";
    }

    @PostMapping("/initiatives/add")
    @PreAuthorize("hasAuthority('initiative:add')")
    public String initiativePostAdd(@RequestParam String name,
                                    @RequestParam String text,
                                    @RequestParam(required = false) Integer cost,
                                    @RequestParam String performerAddress,
                                    Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (cost == null) cost = 0;

        var initiative = new Initiative(name, text, cost, user, performerAddress);
        initiativeRepo.save(initiative);
        userRepo.save(user);
        return "redirect:/initiatives/";
    }


    @GetMapping("/initiatives/{id}")
    public String initiativeById(@PathVariable(value = "id") Long id, Model model) {
        if (!initiativeRepo.existsById(id)) {
            return "redirect:/initiatives/";
        }

        Optional<Initiative> initiativeOptional = initiativeRepo.findById(id);
        var result = new ArrayList<Initiative>();
        initiativeOptional.ifPresent(result::add);
        var initiative = initiativeOptional.get();
        model.addAttribute("initiative", result);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var isManage = user.getRole().getPermissions()
                .contains(Permission.INITIATIVE_MANAGE);

        model.addAttribute("isAuthor", initiative.getAuthor().equals(user));
        model.addAttribute("isManage", isManage);
        model.addAttribute("votesCount", initiative.getVotesCount());
        model.addAttribute("isVoted", user.getVotes().stream().anyMatch(x->x.getId().equals(initiative.getId())));
        model.addAttribute("isVotedStatus",
                initiative.getStatus().equals(InitiativeStatus.STUDENT_VOTE));
        model.addAttribute("isApproved", initiative.isApproved());
        return "initiative-by-id";
    }

    @GetMapping("/initiatives/{id}/edit")
    @PreAuthorize("hasAuthority('initiative:manage')")
    public String initiativeEdit(@PathVariable(value = "id") Long id, Model model) {
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
    public String initiativeEdit(@PathVariable(value = "id") Long id, @RequestParam String name,
                                 @RequestParam String status,
                                 @RequestParam String text,
                                 @RequestParam Integer cost,
                                 @RequestParam String performerAddress,
                                 @RequestParam int votesNeed,
                                 Model model) {
        Initiative initiative = initiativeRepo.findById(id).orElseThrow();
        initiative.setName(name);
        initiative.setStatus(InitiativeStatus.valueOf(status));
        initiative.setText(text);
        initiative.setCost(cost);
        initiative.setPerformerAddress(performerAddress);
        initiative.setVotesNeed(votesNeed);
        initiativeRepo.save(initiative);
        return "redirect:/initiatives/%s/".formatted(id);
    }

    @GetMapping("/initiatives/{id}/edit-my")
    public String initiativeEditMy(@PathVariable(value = "id") Long id, Model model) {
        if (!initiativeRepo.existsById(id)) {
            return "redirect:/initiatives";
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Initiative> initiative = initiativeRepo.findById(id);
        var result = new ArrayList<Initiative>();
        initiative.ifPresent(result::add);

        if (!user.equals(initiative.get().getAuthor())) {
            return "redirect:/initiatives";
        }

        model.addAttribute("initiative", result);
        model.addAttribute("isManage", false); // isManage == true if it's moderator
        return "initiative-edit";
    }

    @PostMapping("/initiatives/{id}/edit-my")
    public String initiativeEditMy(@PathVariable(value = "id") Long id, @RequestParam String name,
                                   @RequestParam String text,
                                   @RequestParam Integer cost,
                                   @RequestParam String performerAddress,
                                   Model model) {

        Initiative initiative = initiativeRepo.findById(id).orElseThrow();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.equals(initiative.getAuthor())) {
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
    public String initiativeRemove(@PathVariable(value = "id") Long id,
                                   Model model) {
        Initiative initiative = initiativeRepo.findById(id).orElseThrow();
        initiativeRepo.delete(initiative);
        return "redirect:/initiatives/";
    }

    @PostMapping("/initiatives/{id}/vote")
    public String initiativeVoting(@PathVariable(value = "id") Long id,
                                   Model model) {
        Initiative initiative = initiativeRepo.findById(id).orElseThrow();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        initiative.addVote(user);
        initiativeRepo.save(initiative);
        userRepo.save(user);
        return "redirect:/initiatives/%s/".formatted(id);
    }

    @PostMapping("/initiatives/{id}/vote-remove")
    public String initiativeVotingRemove(@PathVariable(value = "id") Long id,
                                         Model model) throws Exception {
        Initiative initiative = initiativeRepo.findById(id).orElseThrow();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        initiative.removeVote(user);
        initiativeRepo.save(initiative);
        userRepo.save(user);
        return "redirect:/initiatives/%s/".formatted(id);
    }

    @GetMapping("/initiatives/my")
    public String myInitiatives(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Iterable<Initiative> initiatives = user.getInitiatives();
        model.addAttribute("initiatives", initiatives);
        return "initiatives-main";
    }
}