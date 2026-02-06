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

import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
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
            mockMvc.perform(testUtils.buildRequest("POST", "name=spirituality&rate=8"))
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
            mockMvc.perform(testUtils.buildRequest("POST", "name=&rate=8"))
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
            var user = createTestUserWithWheelOfLifeAndLifeAreasWithoutIds();

            userRepository.save(user);

            //when+then
            mockMvc.perform(testUtils.buildRequest("POST", "lifeAreaId=1&action=delete"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/dashboard/wheelOfLife"));


            var lifeArea = lifeAreaRepository.findById(1L);

            assertTrue(lifeArea.isEmpty());
        }

        @Test
        public void shouldNotRemoveLifeAreaWhenGivenDataIsInvalid() throws Exception {
            //given
            var user = createTestUserWithWheelOfLifeAndLifeAreasWithoutIds();

            userRepository.save(user);
            var lifeAreaSizeBefore = user.getWheelOfLife().getLifeAreas().size();

            //when+then
            mockMvc.perform(testUtils.buildRequest("POST", "lifeAreaId=-1&action=delete"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("dashboard_wheelOfLife"));

            var lifeAreas = lifeAreaRepository.findAllByWheelOfLifeId(1L);

            assertNotNull(lifeAreas);
            assertThat(lifeAreas.size(), is(lifeAreaSizeBefore));

        }
    }

    @Nested
    @WithMockUser(username = "user@example.com", roles="USER")
    public class PatchLifeAreaRateTests {

        @Test
        public void shouldNotModifyLifeAreaWhenGivenDataIsInvalid() throws Exception {
            //given
            var user = createTestUserWithWheelOfLifeAndLifeAreasWithoutIds();
            userRepository.save(user);

            var ratesBefore = lifeAreaRepository.findById(1L).stream()
                            .collect(Collectors.toMap(LifeArea::getId, LifeArea::getRate));

            //when + then
            mockMvc.perform(testUtils.buildRequest("POST", "action=patch&lifeAreaId=1&lifeAreaRate=-1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("dashboard_wheelOfLife"));

            var ratesAfter = lifeAreaRepository.findById(1L).stream()
                    .collect(Collectors.toMap(LifeArea::getId, LifeArea::getRate));

            assertThat(ratesAfter, is(ratesBefore));

        }

        @Test
        public void shouldSuccessfullyPatchLifeAreaRate() throws Exception {
            //given
            var user = createTestUserWithWheelOfLifeAndLifeAreasWithoutIds();
            userRepository.save(user);

            var lifeAreaRateBefore = lifeAreaRepository.findById(1L)
                    .map(LifeArea::getRate)
                    .orElse(null);

            //when + then
            mockMvc.perform(testUtils.buildRequest("POST", "action=patch&lifeAreaId=1&lifeAreaRate=10"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/dashboard/wheelOfLife"));

            var lifeAreaRateAfter = lifeAreaRepository.findById(1L)
                    .map(LifeArea::getRate)
                    .orElse(null);

            assertNotNull(lifeAreaRateBefore);
            assertNotNull(lifeAreaRateAfter);
            assertThat(lifeAreaRateAfter, is(not(lifeAreaRateBefore)));
            assertThat(lifeAreaRateAfter, is((byte) 10));
        }
    }

    public User createTestUserWithWheelOfLifeAndLifeAreasWithoutIds() {
        var user = testUtils.createTestUserWithWheelOfLifeAndLifeAreas();
        user.setId(null);
        user.getWheelOfLife().setId(null);
        user.getWheelOfLife().getLifeAreas().forEach(la -> la.setId(null));

        return user;
    }
}
