package com.lifemap.model.projection;

import com.lifemap.model.LifeArea;
import lombok.*;
import org.springframework.lang.NonNull;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LifeAreaReadDTO{
    @EqualsAndHashCode.Include
    private final Long id;
    private final String name;
    private final byte rate;

    public LifeAreaReadDTO(@NonNull LifeArea lifeArea) {
        if (lifeArea.getId() == null || lifeArea.getName() == null)
            throw new NullPointerException("LifeArea is null");
        this.id = lifeArea.getId();
        this.name = lifeArea.getName();
        this.rate = lifeArea.getRate();
    }
}
