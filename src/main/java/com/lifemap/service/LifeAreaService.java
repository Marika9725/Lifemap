package com.lifemap.service;

import com.lifemap.model.*;
import com.lifemap.model.projection.LifeAreaDTO;
import org.slf4j.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

@Service
public class LifeAreaService {

    private final LifeAreaRepository lifeAreaRepository;

    public LifeAreaService(LifeAreaRepository lifeAreaRepository) {
        this.lifeAreaRepository = lifeAreaRepository;
    }

    @Transactional
    public boolean addLifeArea(LifeAreaDTO toSave, WheelOfLife wheelOfLife, BindingResult result) {
        if (toSave == null || wheelOfLife == null || result == null) return false;

        checkLifeAreaData(toSave, wheelOfLife, result);

        if (result.hasErrors()) return false;

        var lifeArea = createNewLifeArea(toSave, wheelOfLife);
        if (lifeArea != null) {
            var saved = lifeAreaRepository.save(lifeArea);
            return saved.getId() != null;
        }

        return false;
    }

    @Transactional
    public boolean removeLifeArea(String lifeAreaName, WheelOfLife wheelOfLife) {
        if (lifeAreaName == null || lifeAreaName.isBlank()) return false;
        if (wheelOfLife == null || wheelOfLife.getId() < 0) return false;

        return wheelOfLife.getLifeAreas().removeIf(la -> la.getName().equalsIgnoreCase(lifeAreaName));
    }

    private void checkLifeAreaData(LifeAreaDTO toSave, WheelOfLife wheelOfLife, BindingResult result) {
        if (toSave.getName() == null || toSave.getName().isBlank()) {
            result.rejectValue("name", "lifeArea.invalid.name");
        }

        if (toSave.getName() != null && wheelOfLife.getLifeAreas().stream().anyMatch(la -> la.getName().equalsIgnoreCase(toSave.getName()))) {
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
