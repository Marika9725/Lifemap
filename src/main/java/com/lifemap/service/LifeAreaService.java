package com.lifemap.service;

import com.lifemap.model.*;
import com.lifemap.model.projection.LifeAreaDTO;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class LifeAreaService {

    private final LifeAreaRepository lifeAreaRepository;

    public LifeAreaService(LifeAreaRepository lifeAreaRepository) {
        this.lifeAreaRepository = lifeAreaRepository;
    }

    public boolean addLifeArea(LifeAreaDTO toSave, WheelOfLife wheelOfLife, BindingResult result) {
        if (toSave == null || wheelOfLife == null || result == null) return false;

        validLifeAreaData(toSave, wheelOfLife.getId(), result);

        if (result.hasErrors()) return false;

        var lifeArea = createNewLifeArea(toSave, wheelOfLife);
        if (lifeArea != null) {
            var saved = lifeAreaRepository.save(lifeArea);
            return saved.getId() != null;
        }

        return false;
    }

    private void validLifeAreaData(LifeAreaDTO toSave, Long wheelOfLifeId, BindingResult result) {
        if (toSave.getName() == null || toSave.getName().isBlank()) {
            result.rejectValue("name", "lifeArea.invalid.name");
        }

        if (toSave.getName() != null && lifeAreaRepository.existsByNameAndWheelOfLifeId(toSave.getName(), wheelOfLifeId)) {
            result.rejectValue("name", "lifeArea.invalid.name.alreadyExists");
        }
    }

    private LifeArea createNewLifeArea(LifeAreaDTO toSave, WheelOfLife wheelOfLife) {
        var lifeArea = toSave.toLifeArea();

        if (lifeArea == null) return null;

        lifeArea.setWheelOfLife(wheelOfLife);
        wheelOfLife.getLifeAreas().add(lifeArea);

        return wheelOfLife.getLifeAreas().contains(lifeArea) ? lifeArea : null;
    }
}
