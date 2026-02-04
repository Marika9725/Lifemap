package com.lifemap.controller;

import com.lifemap.TestUtils;
import com.lifemap.model.*;
import com.lifemap.service.WheelOfLifeService;
import org.hibernate.AssertionFailure;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@Transactional
public class WheelOfLifeControllerIntegrationTest {

    private final TestUtils testUtils = new TestUtils();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LifeAreaRepository lifeAreaRepository;

    @Autowired
    private WheelOfLifeService wheelOfLifeService;

    @Nested
    @WithMockUser(username = "user@example.com", roles = "USER")
    public class AddLifeAreaTests {
        @Test
        public void shouldSuccessfullyAddNewLifeAreaToDatabase() throws Exception {
            //given
            var user = testUtils.createTestUser();

            var wheelOfLife = new WheelOfLife();
            wheelOfLife.setUser(user);
            user.setWheelOfLife(wheelOfLife);

            userRepository.save(user);

            //when + then
            mockMvc.perform(post("/dashboard/wheelOfLife")
                            .with(csrf())
                            .param("name", "spirituality")
                            .param("rate", "8")
                    )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/dashboard/wheelOfLife"));

            var savedUser = userRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new AssertionError("User not saved"));
            var wheelOfLifeId = savedUser.getWheelOfLife().getId();
            var lifeAreas = lifeAreaRepository.findAllByWheelOfLifeId(wheelOfLifeId);

            assertNotNull(lifeAreas);
            assertTrue(lifeAreas.stream()
                    .anyMatch(lifeArea -> lifeArea.getName().equalsIgnoreCase("spirituality")
                            && lifeArea.getRate() == (byte) 8)
            );
        }

        @Test
        public void shouldNotAddNewLifeAreaToDatabaseWhenGivenDataIsInvalid() throws Exception {
            //given
            var user = testUtils.createTestUser();

            var wheelOfLife = new WheelOfLife();
            wheelOfLife.setUser(user);
            user.setWheelOfLife(wheelOfLife);

            userRepository.save(user);

            //when + then
            mockMvc.perform(post("/dashboard/wheelOfLife")
                            .with(csrf())
                            .param("name", "")
                            .param("rate", "8")
                    )
                    .andExpect(status().isOk())
                    .andExpect(view().name("dashboard_wheelOfLife"));

            var savedUser = userRepository.findByEmail("user@example.com")
                    .orElseThrow(() -> new AssertionError("User not found"));

            var wheelOfLifeId = savedUser.getWheelOfLife().getId();
            var lifeAreas = lifeAreaRepository.findAllByWheelOfLifeId(wheelOfLifeId);

            assertNotNull(lifeAreas);
            assertThat(lifeAreas.size(), is(0));
        }
    }

    @Nested
    @WithMockUser(username = "user@example.com", roles = "USER")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public class DeleteLifeAreaTests {
        @Test
        public void shouldSuccessfullyRemoveLifeAreaFromDatabase() throws Exception {
            //given
            var user = testUtils.createTestUserWithWheelOfLifeAndLifeAreas();
            user.setId(null);
            user.getWheelOfLife().setId(null);
            user.getWheelOfLife().getLifeAreas().forEach(la -> la.setId(null));

            userRepository.save(user);

            //when+then
            mockMvc.perform(post("/dashboard/wheelOfLife")
                            .with(csrf())
                            .param("lifeAreaId", "1")
                            .param("action", "delete")
                    )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/dashboard/wheelOfLife"));


            var lifeArea = lifeAreaRepository.findById(1L);

            assertTrue(lifeArea.isEmpty());
        }

        @Test
        public void shouldNotRemoveLifeAreaWhenGivenDataIsInvalid() throws Exception {
            //given
            var user = testUtils.createTestUserWithWheelOfLifeAndLifeAreas();
            user.setId(null);
            user.getWheelOfLife().setId(null);
            user.getWheelOfLife().getLifeAreas().forEach(la -> la.setId(null));

            userRepository.save(user);
            var lifeAreaSizeBefore = user.getWheelOfLife().getLifeAreas().size();

            //when+then
            mockMvc.perform(post("/dashboard/wheelOfLife")
                            .with(csrf())
                            .param("lifeAreaId", "-1")
                            .param("action", "delete")
                    )
                    .andExpect(status().isOk())
                    .andExpect(view().name("dashboard_wheelOfLife"));

            var lifeAreas = lifeAreaRepository.findAllByWheelOfLifeId(1L);

            assertNotNull(lifeAreas);
            assertThat(lifeAreas.size(), is(lifeAreaSizeBefore));

        }
    }
}
