package com.lifemap.controller;

import com.lifemap.model.UserRepository;
import com.sun.security.auth.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    private final UserRepository repository;

    public DashboardController(UserRepository repository) {this.repository = repository;}

    @GetMapping
    public String showDashboard() {
        System.out.println("run to dashboard!");
        return "dashboard";
    }

    @PostMapping(params = "home")
    public String showDashboard(Model model) {
        return "redirect:/dashboard";
    }

    @GetMapping("/lifeCircle")
    public String showLifeCircle(@AuthenticationPrincipal User actualUser, Model model) {
        var user = repository.findByEmail(actualUser.getUsername()).get();

        if (user == null) return "redirect:/login";

        var lifeCircle = user.getLifeAreas();

        model.addAttribute("lifeCircle", lifeCircle);

        return "dashboard_lifeCircle";
    }

    @PostMapping(params = "lifeCircle")
    public String showLifeCircle(Model model) {
        return "redirect:/dashboard/lifeCircle";
    }
}
