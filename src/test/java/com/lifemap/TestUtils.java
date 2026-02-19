package com.lifemap;

import com.lifemap.model.*;
import com.lifemap.model.projection.LifeAreaCreateDTO;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

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

        public List<LifeArea> createTestLifeAreas() {
            var names = Set.of("health", "finance", "relationships");
            var lifeAreas = new ArrayList<LifeArea>();

            var id = 1L;
            for (String name : names) {
                var lifeArea = new LifeArea();
                lifeArea.setId(id++);
                lifeArea.setName(name);
                lifeArea.setRate((byte) id++);
                lifeAreas.add(lifeArea);
            }

            return lifeAreas;
        }

    //endregion

    //region TestWheelOfLife
        public WheelOfLife createTestWheelOfLifeWithAreas() {
            var wheelOfLife = createTestWheelOfLife(1L);
            var lifeAreas = createTestLifeAreas();

            lifeAreas.forEach(lifeArea -> lifeArea.setWheelOfLife(wheelOfLife));
            wheelOfLife.setLifeAreas(Set.copyOf(lifeAreas));

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

    public MockHttpServletRequestBuilder buildRequest(String method, String params) {
        MockHttpServletRequestBuilder request;
        final String URL = "/dashboard/wheelOfLife";

        switch(method) {
            case "GET" -> request = get(URL);
            case "POST" -> request = post(URL);
            case "DELETE" -> request = delete(URL);
            case "PATCH" -> request = patch(URL);
            default -> throw new IllegalArgumentException("Method " + method + " not supported");
        }

        if (!method.equals("GET")) request = request.with(csrf());

        if (params != null && !params.trim().isEmpty()) {
            var pairs = params.split("&");
            for(String pair : pairs) {
                if (pair.endsWith("="))
                    request = request.param(pair.substring(0, pair.length() - 1), "");
                else {
                    var p = pair.split("=");
                    request = request.param(p[0], p[1]);
                }
            }
        }

        return request;
    }
}
