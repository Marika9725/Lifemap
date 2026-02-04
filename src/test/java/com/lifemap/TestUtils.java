package com.lifemap;

import com.lifemap.model.*;
import com.lifemap.model.projection.LifeAreaCreateDTO;

import java.util.*;

public class TestUtils {


    //region TestUser
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
            var wheelOfLife = createTestWheelOfLifeWithAreas();

            user.setWheelOfLife(wheelOfLife);
            wheelOfLife.setUser(user);

            return user;
        }
    //endregion

    //region TestLifeArea
        public static LifeArea createTestLifeArea() {
            return createTestLifeArea(1L, "testArea", (byte) 8);
        }

        public static LifeArea createTestLifeArea(Long id, String name, byte rate) {
            var lifeArea = new LifeArea();
            lifeArea.setId(id);
            lifeArea.setName(name);
            lifeArea.setRate(rate);

            return lifeArea;
        }
    //endregion

    //region TestWheelOfLife
        public WheelOfLife createTestWheelOfLifeWithAreas() {
            var wheelOfLife = createTestWheelOfLife(1L);
            var names = Set.of("health", "finance", "relationships");
            var lifeAreas = new HashSet<LifeArea>();

            var id = 1L;
            for (String name : names) {
                var lifeArea = new LifeArea();
                lifeArea.setId(id++);
                lifeArea.setName(name);
                lifeArea.setRate((byte) 8);
                lifeArea.setWheelOfLife(wheelOfLife);
                lifeAreas.add(lifeArea);
            }

            wheelOfLife.setLifeAreas(lifeAreas);

            return wheelOfLife;
        }

        public static WheelOfLife createTestWheelOfLife() {
            return createTestWheelOfLife(1L);
        }

        public static WheelOfLife createTestWheelOfLife(Long id) {
            return createTestWheelOfLife(id, new HashSet<>());
        }

        public static WheelOfLife createTestWheelOfLife(Long id, Set<LifeArea> lifeAreas) {
            var wheelOfLife = new WheelOfLife();
            wheelOfLife.setId(id);
            wheelOfLife.setLifeAreas(lifeAreas);

            return wheelOfLife;
        }
    //endregion

    //region TestLifeAreaCreateDTO
        public LifeAreaCreateDTO createTestLifeAreaCreateDTO() {
            return createTestLifeAreaCreateDTO("testArea");
        }

        public LifeAreaCreateDTO createTestLifeAreaCreateDTO(String name) {
            var lifeAreaCreateDTO = new LifeAreaCreateDTO();
            lifeAreaCreateDTO.setName(name);
            lifeAreaCreateDTO.setRate((byte) 8);

            return lifeAreaCreateDTO;
        }
    //endregion
}
