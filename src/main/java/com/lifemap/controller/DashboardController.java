package com.lifemap.controller;

import com.lifemap.model.*;
import com.lifemap.model.projection.*;
import com.lifemap.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    @GetMapping
    public String showDashboard() {
        System.out.println("run to dashboard!");
        return "dashboard";
    }

    @PostMapping(params = "home")
    public String redirectToDashboard(Model model) {
        return "redirect:/dashboard";
    }

    @PostMapping(params = "wheelOfLife")
    public String redirectToWheelOfLife() {
        return "redirect:/dashboard/wheelOfLife";
    }
}
