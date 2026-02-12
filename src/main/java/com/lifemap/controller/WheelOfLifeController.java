package com.lifemap.controller;

import com.lifemap.model.*;
import com.lifemap.model.projection.*;
import com.lifemap.service.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Function;

@Controller
@RequestMapping("/dashboard/wheelOfLife")
@AllArgsConstructor
public class WheelOfLifeController {

    private final UserRepository userRepository;
    private final LifeAreaService lifeAreaService;
    private final WheelOfLifeService wheelOfLifeService;
    private final Logger logger = LoggerFactory.getLogger(WheelOfLifeController.class);

    @GetMapping
    public String showWheelOfLife(
            @AuthenticationPrincipal User actualUser,
            @RequestParam(defaultValue = "NAME_ASC") SortBy sortBy,
            Model model
    ) {
        var wheelOfLife = getWheelOfLife(actualUser);
        if (wheelOfLife == null) return "redirect:/logout";

        addAttributes(wheelOfLife, model, new LifeAreaCreateDTO(), sortBy);

        return "dashboard_wheelOfLife";
    }

    @PostMapping
    public String addLifeArea(
            @AuthenticationPrincipal User actualUser,
            @Valid @ModelAttribute("newLifeArea") LifeAreaCreateDTO toSave,
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
                _ -> lifeAreaService.removeLifeArea(lifeAreaId),
                new LifeAreaCreateDTO()
        );
    }

    @PostMapping(params = "action=patch")
    public String patchLifeAreaRate(
            @AuthenticationPrincipal User actualUser,
            @RequestParam Long lifeAreaId,
            @RequestParam byte lifeAreaRate,
            Model model
    ) {

        return handleLifeAreaOperation(
                actualUser,
                model,
                _ -> lifeAreaService.updateRate(lifeAreaId, lifeAreaRate),
                new LifeAreaCreateDTO()
        );
    }

    private String handleLifeAreaOperation(
            User actualUser,
            Model model,
            Function<WheelOfLife, Boolean> operation,
            LifeAreaCreateDTO lifeAreaCreateDTO
    ) {
        logger.debug("Processing life area operation for user: {}", actualUser.getUsername());
        var wheelOfLife = getWheelOfLife(actualUser);
        if (wheelOfLife == null) {
            logger.warn("Redirecting user {} to logout - wheelOfLife not found", actualUser.getUsername());
            return "redirect:/logout";
        }

        var success = operation.apply(wheelOfLife);
        if (!success) {
            logger.warn("Life area operation failed for user: {}", actualUser.getUsername());
            addAttributes(wheelOfLife, model, lifeAreaCreateDTO, null);
            return "dashboard_wheelOfLife";
        }

        logger.debug("Redirecting user {} to wheelOfLife page - operation succeeded", actualUser.getUsername());
        return "redirect:/dashboard/wheelOfLife";
    }

    private WheelOfLife getWheelOfLife(User actualUser) {
        var user = userRepository.findByEmail(actualUser.getUsername()).orElse(null);
        if (user == null) return null;

        return user.getWheelOfLife();
    }

    private void addAttributes(WheelOfLife wheelOfLife, Model model, LifeAreaCreateDTO lifeAreaCreateDTO, SortBy sort) {
        var sortedLifeAreas = lifeAreaService.getSortedLifeAreas(wheelOfLife.getId(), sort);
        var average = wheelOfLifeService.calculateAverage(sortedLifeAreas);
        model.addAllAttributes(Map.of(
                "areas", sortedLifeAreas,
                "worstAreas", Objects.requireNonNullElse(lifeAreaService.getWorstLifeAreas(wheelOfLife.getId(), average), List.of()),
                //TODO: change calculateAverage() to variable in wheelOfLifeDTO and wheelOfLife entity ???
                "average", average,
                "newLifeArea", lifeAreaCreateDTO,
                "sortBy", Objects.requireNonNullElse(sort, SortBy.NAME_ASC)
        ));
    }
}