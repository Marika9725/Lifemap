package com.lifemap.service;

import com.lifemap.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WheelOfLifeService {
    @Autowired
    private MessageSource messageSource;
    private final  LifeAreaRepository lifeAreaRepository;

    public WheelOfLifeService(LifeAreaRepository lifeAreaRepository) {this.lifeAreaRepository = lifeAreaRepository;}


    //TODO: test -> shouldCreateDefaultWheelOfLife()
    public WheelOfLife createDefaultWheelOfLife() {
        var locale = LocaleContextHolder.getLocale();
        var wheelOfLife = new WheelOfLife();

        var lifeAreas = new ArrayList<>(List.of(
                messageSource.getMessage("lifeArea.health", null, locale),
                messageSource.getMessage("lifeArea.career", null, locale),
                messageSource.getMessage("lifeArea.finances", null, locale),
                messageSource.getMessage("lifeArea.personalDevelopment", null, locale),
                messageSource.getMessage("lifeArea.fun", null, locale),
                messageSource.getMessage("lifeArea.relationships", null, locale)
        ));

        wheelOfLife.setLifeAreas(lifeAreas.stream()
                .map(area -> {
                    var lifeArea = new LifeArea();
                    lifeArea.setName(area);
                    lifeArea.setRate((byte) 0);
                    lifeArea.setWheelOfLife(wheelOfLife);

                    return lifeArea;
                })
                .collect(Collectors.toSet())
        );

        return wheelOfLife;
    }

    //TODO: test it!
    public LifeArea addLifeArea(LifeArea lifeArea, WheelOfLife wheelOfLife) {
        if (lifeArea != null) {
            wheelOfLife.getLifeAreas().add(lifeArea);
        }

        return wheelOfLife.getLifeAreas().contains(lifeArea) ?  lifeArea : null;
    }
}
