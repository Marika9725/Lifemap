package com.lifemap.model.projection;

import com.lifemap.model.*;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.util.*;

@Getter
public class WheelOfLifeReadDTO {
    private final Long id;
    private final Set<LifeAreaReadDTO> lifeAreas;

    public WheelOfLifeReadDTO(@NonNull WheelOfLife wheelOfLife) {
        if (wheelOfLife.getId() == null)
            throw new NullPointerException("WheelOfLife id must not be null");

        this.id = wheelOfLife.getId();
        lifeAreas = new HashSet<>();
        wheelOfLife.getLifeAreas()
                .forEach(lifeArea -> lifeAreas.add(new LifeAreaReadDTO(lifeArea)));
    }

    public List<LifeAreaReadDTO> getLifeAreas() {
        return new ArrayList<>(lifeAreas);
    }
}
