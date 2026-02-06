package com.lifemap.controller;

import com.lifemap.TestUtils;
import com.lifemap.config.SecurityConfig;
import com.lifemap.model.*;
import com.lifemap.model.projection.*;
import com.lifemap.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.validation.BindingResult;

import java.util.*;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WheelOfLifeController.class)
@Import(SecurityConfig.class)
class WheelOfLifeControllerWebTest {
   @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private LifeAreaService lifeAreaService;

    @MockitoBean
    private WheelOfLifeService wheelOfLifeService;

    private final TestUtils testUtils = new TestUtils();

    @Nested
    class OverallTests {
        @ParameterizedTest
        @CsvSource({
                "GET, ",
                "POST,",
                "DELETE, action=delete&lifeAreaId=1",
                "PATCH, action=patch&lifeAreaRate=1&lifeAreaId=1"
        })
        @WithAnonymousUser
        public void shouldRedirectToLoginPageWhenUserIsNotLogin(String method, String params) throws Exception {
            var requestBuilder = testUtils.buildRequest(method, params);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("http://localhost/login"));
        }

        @ParameterizedTest
        @CsvSource({
                "GET, ",
                "POST, ",
                "POST, action=delete&lifeAreaId=1",
                "POST, action=patch&lifeAreaId=1&lifeAreaRate=5"
        })
        @WithMockUser
        public void shouldRedirectToLoginPageWhenPrincipalUserIsNotFoundInDatabase(String method) throws Exception {
            var requestBuilder = testUtils.buildRequest(method, null);
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/logout"));
        }

    }

    @Nested
    class HttpGET_wheelOfLife {

        @Test
        @WithMockUser
        public void shouldReturnWheelOfLifePage() throws Exception {
            //given
            var user = testUtils.createTestUserWithWheelOfLifeAndLifeAreas();

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

            //when + then
            mockMvc.perform(testUtils.buildRequest("GET", null))
                    .andExpect(status().isOk())
                    .andExpect(view().name("dashboard_wheelOfLife"))
                    .andExpect(model().attribute("average", instanceOf(Double.class)))
                    .andExpect(model().attribute("areas", allOf(
                            instanceOf(List.class),
                            everyItem(instanceOf(LifeAreaReadDTO.class))
                    )))
                    .andExpect(model().attribute("newLifeArea", allOf(
                            instanceOf(LifeAreaCreateDTO.class),
                            hasProperty("name", nullValue()),
                            hasProperty("rate", is((byte) 0))
                    )));
        }

    }

    @Nested
    @WithMockUser
    class HttpPOST_wheelOfLife_AreaLife {

        @Test
        public void shouldReturnWheelOfLifePageWithSubmittedDataWhenAddingLifeAreaFails() throws Exception {
            //given
            var user = testUtils.createTestUserWithWheelOfLifeAndLifeAreas();

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(lifeAreaService.addLifeArea(
                    ArgumentMatchers.any(LifeAreaCreateDTO.class),
                    ArgumentMatchers.any(WheelOfLife.class),
                    ArgumentMatchers.any(BindingResult.class))
            ).thenReturn(false);

            //when + then
            mockMvc.perform(testUtils.buildRequest("POST", "name=Zdrowie&rate=8"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("dashboard_wheelOfLife"))
                    .andExpect(model().attribute("newLifeArea", allOf(
                            instanceOf(LifeAreaCreateDTO.class),
                            hasProperty("name", is("Zdrowie")),
                            hasProperty("rate", is((byte) 8))
                    )));
        }

        @Test
        public void shouldAddAverageAndAreasAttributesWhenAddingLifeAreaFails() throws Exception {
            //given
            var user = testUtils.createTestUserWithWheelOfLifeAndLifeAreas();

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(lifeAreaService.addLifeArea(
                    ArgumentMatchers.any(LifeAreaCreateDTO.class),
                    ArgumentMatchers.any(WheelOfLife.class),
                    ArgumentMatchers.any(BindingResult.class)
            )).thenReturn(false);

            //when + then
            mockMvc.perform(testUtils.buildRequest("POST", "name=Zdrowie&rate=8"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("dashboard_wheelOfLife"))
                    .andExpect(model().attributeExists("average", "areas"))
                    .andExpect(model().attribute("average", instanceOf(Double.class)))
                    .andExpect(model().attribute("areas", allOf(
                            instanceOf(List.class),
                            everyItem(instanceOf(LifeAreaReadDTO.class))
                    )));
        }

        @Test
        public void shouldRedirectToWheelOfLifePageWithNewLifeAreaDTOWhenAddingLifeAreaIsSuccessful() throws Exception {
            //given
            var user = testUtils.createTestUserWithWheelOfLifeAndLifeAreas();

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(lifeAreaService.addLifeArea(
                    ArgumentMatchers.any(LifeAreaCreateDTO.class),
                    ArgumentMatchers.any(WheelOfLife.class),
                    ArgumentMatchers.any(BindingResult.class))
            ).thenReturn(true);

            //when + then
            mockMvc.perform(testUtils.buildRequest("POST", "name=Zdrowie&rate=8"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/dashboard/wheelOfLife"));
        }

    }

    @Nested
    @WithMockUser
    class HttpDELETE_wheelOfLife_AreaLife {

        @Test
        public void shouldReturnOldWheelOfLifePageWhenDeletingLifeAreaFails() throws Exception {
            //given
            var user = testUtils.createTestUserWithWheelOfLifeAndLifeAreas();
            var wheelOfLife = user.getWheelOfLife();
            var wheelOfLifeReadDTO = new WheelOfLifeReadDTO(wheelOfLife);

            var averageBefore = wheelOfLifeService.calculateAverage(wheelOfLifeReadDTO.getLifeAreas());
            var lifeAreasNumBefore = wheelOfLife.getLifeAreas().size();

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(lifeAreaService.removeLifeArea(anyLong())).thenReturn(false);

            //when + then
            mockMvc.perform(testUtils.buildRequest("POST", "action=delete&lifeAreaId=1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("dashboard_wheelOfLife"))
                    .andExpect(model().attribute("areas", hasSize(lifeAreasNumBefore)))
                    .andExpect(model().attribute("average", is(averageBefore)));
        }

        @Test
        public void shouldRedirectToWheelOfLifePageWhenDeletingLifeAreaIsSuccessful() throws Exception {
            //given
            var user = testUtils.createTestUserWithWheelOfLifeAndLifeAreas();

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(lifeAreaService.removeLifeArea(anyLong())).thenReturn(true);

            //when + then
            mockMvc.perform(testUtils.buildRequest("POST", "action=delete&lifeAreaId=1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/dashboard/wheelOfLife"))
                    .andExpect(view().name("redirect:/dashboard/wheelOfLife"));
        }
    }

    @Nested
    @WithMockUser(username = "user@example.com")
    class HttpPATCH_wheelOfLife_AreaLifeRate {

        @Test
        public void shouldReturnOldWheelOfLifePageWhenPatchingLifeAreaFails() throws Exception {
            //given
            var user = testUtils.createTestUserWithWheelOfLifeAndLifeAreas();
            var averageBefore = wheelOfLifeService.calculateAverage(new WheelOfLifeReadDTO(user.getWheelOfLife()).getLifeAreas());

            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(lifeAreaService.updateRate(anyLong(), anyByte())).thenReturn(false);

            //when+then
            mockMvc.perform(testUtils.buildRequest("POST", "action=patch&lifeAreaId=1&lifeAreaRate=1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("dashboard_wheelOfLife"))
                    .andExpect(model().attribute("average", is(averageBefore)));

        }

        @Test
        public void shouldRedirectToWheelOfLifePageWhenPatchingLifeAreaRateIsSuccessful() throws Exception {
            //given
            var user = testUtils.createTestUserWithWheelOfLifeAndLifeAreas();

            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(lifeAreaService.updateRate(anyLong(), anyByte())).thenReturn(true);

            //when + then
            mockMvc.perform(testUtils.buildRequest("POST", "action=patch&lifeAreaId=1&lifeAreaRate=1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/dashboard/wheelOfLife"))
                    .andExpect(view().name("redirect:/dashboard/wheelOfLife"));
        }
    }
}