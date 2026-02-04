package com.lifemap.service;

import com.lifemap.model.*;
import com.lifemap.model.projection.*;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WheelOfLifeService {
    private final MessageSource messageSource;
    private final LifeAreaRepository lifeAreaRepository;

    public WheelOfLifeService(LifeAreaRepository lifeAreaRepository, MessageSource messageSource) {this.lifeAreaRepository = lifeAreaRepository;
        this.messageSource = messageSource;
    }

    public WheelOfLife createDefaultWheelOfLife() {
        var locale = LocaleContextHolder.getLocale();
        var wheelOfLife = new WheelOfLife();

        var lifeAreas = new ArrayList<>(
                List.of("health", "career", "finances", "personalDevelopment", "fun", "relationships")
        );

        wheelOfLife.setLifeAreas(lifeAreas.stream()
                .map(area -> {
                    var lifeArea = new LifeArea();
                    var language = List.of("pl", "en").contains(locale.getLanguage()) ? locale : Locale.forLanguageTag("pl");
                    lifeArea.setName(messageSource.getMessage(("lifeArea." + area), null, language));
                    lifeArea.setRate((byte) 0);
                    lifeArea.setWheelOfLife(wheelOfLife);

                    return lifeArea;
                })
                .collect(Collectors.toSet())
        );

        return wheelOfLife;
    }

    public double calculateAverage(List<LifeAreaReadDTO> lifeAreas) {
        if (lifeAreas == null) return 0.0;

        var average = lifeAreas.stream().mapToDouble(LifeAreaReadDTO::getRate).sum() / (double) lifeAreas.size();

        return Math.round(average * 100.0) / 100.0;
    }
}
