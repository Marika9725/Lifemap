package com.lifemap.model.projection;

import com.lifemap.model.WheelOfLife;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

@Setter
@SuppressWarnings("unused")
public class WheelOfLifeUpdateDTO {
    private Set<LifeAreaUpdateDTO> lifeAreas;

    public WheelOfLifeUpdateDTO(WheelOfLife wheelOfLife) {
        lifeAreas = new HashSet<>();
        wheelOfLife.getLifeAreas()
                .forEach(lifeArea -> lifeAreas.add(new LifeAreaUpdateDTO(lifeArea)));
    }

    public List<LifeAreaUpdateDTO> getLifeAreas() {
        return lifeAreas.stream()
                .sorted(Comparator.comparing(LifeAreaUpdateDTO::getName))
                .collect(Collectors.toList());
    }
}
