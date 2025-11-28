package com.lifemap.controller;

import com.lifemap.model.*;
import com.lifemap.model.projection.*;
import com.lifemap.service.LifeAreaService;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    private final UserRepository userRepository;
    private final LifeAreaService service;

    public DashboardController(UserRepository userRepository, LifeAreaService service) {
        this.userRepository = userRepository;
        this.service = service;
    }

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

    //TODO: test it!
    @GetMapping("/wheelOfLife")
    public String showWheelOfLife(@AuthenticationPrincipal User actualUser, Model model) {
        var user = fetchCurrentUser(actualUser);
        if (user == null) return "redirect:/logout";

        addAttributes(user.getWheelOfLife(), model);
        model.addAttribute("newLifeArea", new LifeAreaDTO());

        return "dashboard_wheelOfLife";
    }

    //TODO: test it!
    @PostMapping("/wheelOfLife")
    public String addLifeArea(@AuthenticationPrincipal User actualUser,
                              @ModelAttribute("newLifeArea") LifeAreaDTO toSave,
                              BindingResult result,
                              Model model
    ) {
        var user = fetchCurrentUser(actualUser);
        if (user == null) return "redirect:/logout";
        var wheelOfLife = user.getWheelOfLife();

        var isLifeAreaAdded = service.addLifeArea(toSave, wheelOfLife, result);

        addAttributes(wheelOfLife, model);

        if (result.hasErrors() || !isLifeAreaAdded) {
            model.addAttribute("newLifeArea", toSave);
            return "dashboard_wheelOfLife";
        }

        model.addAttribute("newLifeArea", new LifeAreaDTO());

        return "redirect:/dashboard/dashboard_wheelOfLife";
    }

    private com.lifemap.model.User fetchCurrentUser(User actualUser) {
        var optUser = userRepository.findByEmail(actualUser.getUsername());

        return optUser.orElse(null);
    }

    private void addAttributes(WheelOfLife wheelOfLife, Model model) {
        var wheelOfLifeDTO = new WheelOfLifeDTO(wheelOfLife);

        model.addAttribute("areas", wheelOfLifeDTO.getLifeAreas());
        //TODO: change calculateAverage() to variable in wheelOfLifeDTO and wheelOfLife entity ???
        model.addAttribute("average", wheelOfLifeDTO.calculateAverage());
    }
}
