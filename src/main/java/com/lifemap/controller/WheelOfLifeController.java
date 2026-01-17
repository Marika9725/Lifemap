package com.lifemap.controller;

import com.lifemap.model.*;
import com.lifemap.model.projection.*;
import com.lifemap.service.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard/wheelOfLife")
@AllArgsConstructor
public class WheelOfLifeController {

    private final UserRepository userRepository;
    private final LifeAreaService lifeAreaService;
    private final WheelOfLifeService wheelOfLifeService;

    @GetMapping
    public String showWheelOfLife(@AuthenticationPrincipal
                                  User actualUser, Model model) {
        var user = fetchCurrentUser(actualUser);
        if (user == null) return "redirect:/logout";

        addAttributes(user.getWheelOfLife(), model);
        model.addAttribute("newLifeArea", new LifeAreaDTO());

        return "dashboard_wheelOfLife";
    }

    @PostMapping
    public String addLifeArea(@AuthenticationPrincipal User actualUser,
                              @ModelAttribute("newLifeArea") LifeAreaDTO toSave,
                              BindingResult result,
                              Model model
    ) {
        var user = fetchCurrentUser(actualUser);
        if (user == null) return "redirect:/logout";
        var wheelOfLife = user.getWheelOfLife();

        var isLifeAreaAdded = lifeAreaService.addLifeArea(toSave, wheelOfLife, result);

        if (result.hasErrors() || !isLifeAreaAdded) {
            addAttributes(wheelOfLife, model);
            model.addAttribute("newLifeArea", toSave);
            return "dashboard_wheelOfLife";
        }

        return "redirect:/dashboard/wheelOfLife";
    }

    private com.lifemap.model.User fetchCurrentUser(User actualUser) {
        var optUser = userRepository.findByEmail(actualUser.getUsername());

        return optUser.orElse(null);
    }

    private void addAttributes(WheelOfLife wheelOfLife, Model model) {
        var wheelOfLifeDTO = new WheelOfLifeDTO(wheelOfLife);

        model.addAttribute("areas", wheelOfLifeDTO.getLifeAreas());
        //TODO: change calculateAverage() to variable in wheelOfLifeDTO and wheelOfLife entity ???
        model.addAttribute("average", wheelOfLifeService.calculateAverage(wheelOfLife));
    }
}
