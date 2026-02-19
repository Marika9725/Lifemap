package com.lifemap.controller;

import com.lifemap.TestUtils;
import com.lifemap.model.*;
import com.lifemap.model.projection.LifeAreaReadDTO;
import jdk.jfr.StackTrace;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.*;

import java.util.Comparator;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class WheelOfLifeControllerIntegrationTest {

    private final TestUtils testUtils = new TestUtils();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LifeAreaRepository lifeAreaRepository;

    @Nested
    @WithMockUser(username = "user@example.com", roles = "USER")
    public class GetWheelOfLife {
        @Test
        public void shouldReturnWheelOfLifePageWithCorrectListOfWorstLifeAreas() throws Exception {
            //given
            var user = createTestUserWithWheelOfLifeAndLifeAreasWithoutIds();

            userRepository.save(user);

            var average = user.getWheelOfLife().getLifeAreas().stream()
                    .mapToInt(LifeArea::getRate)
                    .average()
                    .orElse(0);

            var expected = user.getWheelOfLife().getLifeAreas().stream()
                    .map(LifeAreaReadDTO::new)
                    .filter(la -> la.getRate() < Math.ceil(average))
                    .toList();

            //when+then
            mockMvc.perform(testUtils.buildRequest("GET", null))
                    .andExpect(status().isOk())
                    .andExpect(view().name("dashboard_wheelOfLife"))
                    .andExpect(model().attribute("worstAreas", expected));
        }

        @Test
        public void shouldReturnWheelOfLifePageWithLifeAreasSortedByCorrespondingSortByEnum() throws Exception {
            //given
            var user = createTestUserWithWheelOfLifeAndLifeAreasWithoutIds();
            userRepository.save(user);

            var expected = user.getWheelOfLife().getLifeAreas().stream()
                    .map(LifeAreaReadDTO::new)
                    .sorted(Comparator.comparing(LifeAreaReadDTO::getRate).reversed())
                    .toList();

            //when+then
            mockMvc.perform(testUtils.buildRequest("GET", "sortBy=RATE_DESC"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("dashboard_wheelOfLife"))
                    .andExpect(model().attribute("sortBy", SortBy.RATE_DESC))
                    .andExpect(model().attribute("areas", expected));
        }
    }

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
    public class DeleteLifeAreaTests {
        @Test
        @Transactional(propagation = Propagation.NOT_SUPPORTED)
        public void shouldSuccessfullyRemoveLifeAreaFromDatabase() throws Exception {
            //given
            var user = createTestUserWithWheelOfLifeAndLifeAreasWithoutIds();

            userRepository.save(user);
            var lifeArea = lifeAreaRepository.findAllByWheelOfLifeId(user.getWheelOfLife().getId()).getFirst();
            var lifeAreaId = lifeArea.getId();

            //when+then
            mockMvc.perform(testUtils.buildRequest("POST", "lifeAreaId=" + lifeAreaId + "&action=delete"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/dashboard/wheelOfLife"));

            var la = lifeAreaRepository.findById(lifeAreaId);

            assertFalse(la.isPresent());
        }

        @Test
        @Transactional
        public void shouldNotRemoveLifeAreaWhenGivenDataIsInvalid() throws Exception {
            //given
            var user = createTestUserWithWheelOfLifeAndLifeAreasWithoutIds();

            userRepository.save(user);
            var lifeAreaSizeBefore = user.getWheelOfLife().getLifeAreas().size();
            var wheelOfLifeId = user.getWheelOfLife().getId();

            //when+then
            mockMvc.perform(testUtils.buildRequest("POST", "lifeAreaId=-1&action=delete"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("dashboard_wheelOfLife"));

            var lifeAreas = lifeAreaRepository.findAllByWheelOfLifeId(wheelOfLifeId);

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

            var lifeAreaId = lifeAreaRepository.findAllByWheelOfLifeId(user.getWheelOfLife().getId()).getFirst().getId();

            var ratesBefore = lifeAreaRepository.findById(lifeAreaId).stream()
                            .collect(Collectors.toMap(LifeArea::getId, LifeArea::getRate));

            //when + then
            mockMvc.perform(testUtils.buildRequest("POST", "action=patch&lifeAreaId=" + lifeAreaId + "&lifeAreaRate=-1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("dashboard_wheelOfLife"));

            var ratesAfter = lifeAreaRepository.findById(lifeAreaId).stream()
                    .collect(Collectors.toMap(LifeArea::getId, LifeArea::getRate));

            assertThat(ratesAfter, is(ratesBefore));

        }

        @Test
        public void shouldSuccessfullyPatchLifeAreaRate() throws Exception {
            //given
            var user = createTestUserWithWheelOfLifeAndLifeAreasWithoutIds();
            userRepository.save(user);

            var lifeAreaId = lifeAreaRepository.findAllByWheelOfLifeId(user.getWheelOfLife().getId()).getFirst().getId();

            var lifeAreaRateBefore = lifeAreaRepository.findById(lifeAreaId)
                    .map(LifeArea::getRate)
                    .orElse(null);

            //when + then
            mockMvc.perform(testUtils.buildRequest("POST", "action=patch&lifeAreaId=" + lifeAreaId + "&lifeAreaRate=10"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/dashboard/wheelOfLife"));

            var lifeAreaRateAfter = lifeAreaRepository.findById(lifeAreaId)
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
