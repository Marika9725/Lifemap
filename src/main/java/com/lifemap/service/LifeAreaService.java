package com.lifemap.service;

import com.lifemap.model.*;
import com.lifemap.model.projection.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.*;
import java.util.stream.Collectors;

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
        if (checkId(lifeAreaId)) return false;

        return lifeAreaRepository.deleteByIdReturningCount(lifeAreaId) == 1;
    }

    @Transactional
    public boolean updateRate(Long lifeAreaId, byte rate) {
        if (checkId(lifeAreaId)) return false;
        if (rate < 0 || rate > 10) return false;

        return lifeAreaRepository.findById(lifeAreaId)
                .map(lifeArea -> {
                    lifeArea.setRate(rate);
                    return true;
                }).orElse(false);
    }

    public List<LifeAreaReadDTO> getSortedLifeAreas(Long wheelOfLifeId, SortBy sort) {
        if (checkId(wheelOfLifeId)) return List.of();
        sort = (sort == null) ? SortBy.NAME_ASC : sort;

        return lifeAreaRepository.findAllByWheelOfLifeId(wheelOfLifeId).stream()
                .sorted(
                        switch(sort) {
                            case NAME_DESC -> Comparator.comparing(LifeArea::getName).reversed();
                            case RATE_ASC -> Comparator.comparing(LifeArea::getRate);
                            case RATE_DESC -> Comparator.comparing(LifeArea::getRate).reversed();
                            default -> Comparator.comparing(LifeArea::getName);
                        }
                )
                .map(LifeAreaReadDTO::new)
                .collect(Collectors.toList());
    }

    public List<LifeAreaReadDTO> getWorstLifeAreas(Long wheelOfLifeId, double average) {
        if (checkId(wheelOfLifeId)) return List.of();

        return lifeAreaRepository.findAllByWheelOfLifeId(wheelOfLifeId).stream()
                .filter(la -> la.getRate() < Math.ceil(average))
                .map(LifeAreaReadDTO::new)
                .collect(Collectors.toList());

    }

    private boolean checkId(Long id) {
        return (id == null) || (id < 0);
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
