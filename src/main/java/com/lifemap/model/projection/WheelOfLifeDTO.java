package com.lifemap.model.projection;

import com.lifemap.model.WheelOfLife;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

//@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
public class WheelOfLifeDTO {
    private Set<LifeAreaDTO> lifeAreas;

    public WheelOfLifeDTO(WheelOfLife wheelOfLife) {
        lifeAreas = new HashSet<>();
        wheelOfLife.getLifeAreas()
                .forEach(lifeArea -> lifeAreas.add(new LifeAreaDTO(lifeArea)));
    }

    public List<LifeAreaDTO> getLifeAreas() {
        return lifeAreas.stream()
                .sorted(Comparator.comparing(LifeAreaDTO::getName))
                .collect(Collectors.toList());
    }

    ///TODO: migrate this to WheelOfLifeService
    public double calculateAverage() {
        var average = lifeAreas.stream().mapToDouble(LifeAreaDTO::getRate).sum() / (double) lifeAreas.size();
        return Math.round(average * 100.0) / 100.0;
    }
}
