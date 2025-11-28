package com.lifemap.service;

import com.lifemap.model.*;
import com.lifemap.model.projection.LifeAreaDTO;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class LifeAreaService {

    private final LifeAreaRepository lifeAreaRepository;
    private final WheelOfLifeService wheelOfLifeService;

    public LifeAreaService(LifeAreaRepository lifeAreaRepository, WheelOfLifeService wheelOfLifeService) {this.lifeAreaRepository = lifeAreaRepository;
        this.wheelOfLifeService = wheelOfLifeService;
    }

    //TODO: test it!
    public boolean addLifeArea(LifeAreaDTO toSave, WheelOfLife wheelOfLife, BindingResult result) {

        if (lifeAreaRepository.existsByNameAndWheelOfLifeId(toSave.getName(), wheelOfLife.getId())) {
            result.rejectValue("name", "lifeArea.invalid.name.alreadyExists");
        }

        if (result.hasErrors()) return false;

        var lifeArea = createNewLifeArea(toSave, wheelOfLife);
        if (lifeArea != null) {
            var saved = lifeAreaRepository.save(lifeArea);
            return saved.getId() != null;
        }

        return false;
    }

    private LifeArea createNewLifeArea(LifeAreaDTO toSave, WheelOfLife wheelOfLife) {
        var lifeArea = toSave.toLifeArea();
        lifeArea.setWheelOfLife(wheelOfLife);

        return wheelOfLifeService.addLifeArea(lifeArea, wheelOfLife);
    }
}
