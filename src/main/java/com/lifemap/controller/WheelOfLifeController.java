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

import java.util.function.Function;

@Controller
@RequestMapping("/dashboard/wheelOfLife")
@AllArgsConstructor
public class WheelOfLifeController {

    private final UserRepository userRepository;
    private final LifeAreaService lifeAreaService;
    private final WheelOfLifeService wheelOfLifeService;

    @GetMapping
    public String showWheelOfLife(
            @AuthenticationPrincipal User actualUser,
            Model model
    ) {
        var wheelOfLife = getWheelOfLife(actualUser);
        if (wheelOfLife == null) return "redirect:/logout";

        addAttributes(wheelOfLife, model, new LifeAreaCreateDTO());

        return "dashboard_wheelOfLife";
    }

    @PostMapping
    public String addLifeArea(
            @AuthenticationPrincipal User actualUser,
            @ModelAttribute("newLifeArea") LifeAreaCreateDTO toSave,
            BindingResult result,
            Model model
    ) {
        return handleLifeAreaOperation(
                actualUser,
                model,
                wheel -> lifeAreaService.addLifeArea(toSave, wheel, result),
                toSave
        );
    }

    @PostMapping(params = "action=delete")
    public String deleteLifeArea(
            @AuthenticationPrincipal User actualUser,
            @RequestParam Long lifeAreaId,
            Model model
    ) {

        return handleLifeAreaOperation(
                actualUser,
                model,
                wheel -> lifeAreaService.removeLifeArea(lifeAreaId),
                new LifeAreaCreateDTO()
        );
    }

    private String handleLifeAreaOperation(
            User actualUser,
            Model model,
            Function<WheelOfLife, Boolean> operation,
            LifeAreaCreateDTO lifeAreaCreateDTO
    ) {
        var wheelOfLife = getWheelOfLife(actualUser);
        if (wheelOfLife == null) return "redirect:/logout";

        var success = operation.apply(wheelOfLife);
        if (!success) {
            addAttributes(wheelOfLife, model, lifeAreaCreateDTO);
            return "dashboard_wheelOfLife";
        }

        return "redirect:/dashboard/wheelOfLife";
    }

    private WheelOfLife getWheelOfLife(User actualUser) {
        var user = userRepository.findByEmail(actualUser.getUsername()).orElse(null);
        if (user == null) return null;

        return user.getWheelOfLife();
    }

    private void addAttributes(WheelOfLife wheelOfLife, Model model, LifeAreaCreateDTO lifeAreaCreateDTO) {
        var wheelOfLifeDTO = new WheelOfLifeReadDTO(wheelOfLife);

        model.addAttribute("areas", wheelOfLifeDTO.getLifeAreas());
        //TODO: change calculateAverage() to variable in wheelOfLifeDTO and wheelOfLife entity ???
        model.addAttribute("average", wheelOfLifeService.calculateAverage(wheelOfLifeDTO.getLifeAreas()));
        model.addAttribute("newLifeArea", lifeAreaCreateDTO);
    }
}