package com.lifemap;

import com.lifemap.model.*;
import com.lifemap.model.projection.LifeAreaDTO;

import java.util.Set;
import java.util.stream.Collectors;

public class TestUtils {

    public User createTestUser() {
        var user = new User();
        user.setUsername("TestUser");
        user.setEmail("user@example.com");
        user.setPassword("#Password123");
        user.setRole(Role.ROLE_USER);

        return user;
    }

    public User createTestUserWithWheelOfLifeAndLifeAreas() {
        var user = createTestUser();

        var wheelOfLife = new WheelOfLife();
        wheelOfLife.setLifeAreas(Set.of(new LifeArea()));
        user.setWheelOfLife(wheelOfLife);

        return user;
    }

    public LifeArea createTestLifeArea() {
        var lifeArea = new LifeArea();
        lifeArea.setName("testArea");
        lifeArea.setRate((byte) 8);

        return lifeArea;
    }

    public WheelOfLife createTestWheelOfLife() {
        var wheelOfLife = new WheelOfLife();
        var names = Set.of("health", "finance", "relationships");
        var lifeAreas = names.stream().map(area -> {
            var lifeArea = new LifeArea();
            lifeArea.setName(area);
            lifeArea.setRate((byte) 8);
            lifeArea.setWheelOfLife(wheelOfLife);
            return lifeArea;
        }).collect(Collectors.toSet());
        wheelOfLife.setLifeAreas(lifeAreas);

        return wheelOfLife;
    }
}
