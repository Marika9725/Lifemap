package com.lifemap.controller;

import com.lifemap.model.*;
import com.lifemap.model.projection.WheelOfLifeDTO;
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

    @GetMapping("/wheelOfLife")
    public String showWheelOfLife(@AuthenticationPrincipal User actualUser, Model model) {
        var optionalUser = repository.findByEmail(actualUser.getUsername());

        if (optionalUser.isEmpty()) return "redirect:/logout";
        var user = optionalUser.get();
        var wheelOfLifeDTO = new WheelOfLifeDTO(user.getWheelOfLife());

        model.addAttribute("wheelOfLife", wheelOfLifeDTO);

        return "dashboard_wheelOfLife";
    }

    @PostMapping(params = "wheelOfLife")
    public String shoWheelOfLife(Model model) {
        return "redirect:/dashboard/wheelOfLife";
    }
}
