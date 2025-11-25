package com.lifemap.model.projection;

import com.lifemap.model.WheelOfLife;
import lombok.*;

import java.util.*;

@Getter
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
}
