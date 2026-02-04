package com.lifemap.service;

import com.lifemap.model.*;
import com.lifemap.model.projection.*;
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
    public boolean addLifeArea(LifeAreaCreateDTO toSave, WheelOfLife wheelOfLife, BindingResult result) {
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
    public boolean removeLifeArea(Long lifeAreaId) {
        if (lifeAreaId == null || lifeAreaId < 0) return false;

        return lifeAreaRepository.deleteByIdReturningCount(lifeAreaId) == 1;
    }

    private void checkLifeAreaData(LifeAreaCreateDTO toSave, WheelOfLife wheelOfLife, BindingResult result) {
        if (toSave.getName() == null || toSave.getName().isBlank()) {
            result.rejectValue("name", "lifeArea.invalid.name");
        }

        if (toSave.getName() != null && wheelOfLife.getLifeAreas().stream().anyMatch(la -> la.getName().equalsIgnoreCase(toSave.getName()))) {
            result.rejectValue("name", "lifeArea.invalid.name.alreadyExists");
        }
    }

    private LifeArea createNewLifeArea(LifeAreaCreateDTO toSave, WheelOfLife wheelOfLife) {
        var lifeArea = toSave.toLifeArea();

        if (lifeArea == null) return null;

        lifeArea.setWheelOfLife(wheelOfLife);
        wheelOfLife.getLifeAreas().add(lifeArea);

        return wheelOfLife.getLifeAreas().contains(lifeArea) ? lifeArea : null;
    }
}
