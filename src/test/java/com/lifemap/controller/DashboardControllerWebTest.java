package com.lifemap.controller;

import com.lifemap.TestUtils;
import com.lifemap.config.SecurityConfig;
import com.lifemap.model.*;
import com.lifemap.model.projection.LifeAreaDTO;
import com.lifemap.service.*;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@Import(SecurityConfig.class)
class DashboardControllerWebTest {
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
    class HttpGET_dashboard {
        @Test
        @WithAnonymousUser
        public void dashboardShouldNotBeAccessibleWithoutLogin() throws Exception {
            mockMvc.perform(get("/dashboard"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login"));
        }

        @Test
        @WithMockUser
        public void shouldReturnDashboardPage() throws Exception {
            mockMvc.perform(get("/dashboard"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("dashboard"));
        }
    }

    @Nested
    @WithMockUser
    class HttpPOST_dashboard {
        @Test
        public void shouldRedirectToDashboardPageWhenThereIsHomeParam() throws Exception {
            mockMvc.perform(post("/dashboard")
                            .with(csrf())
                            .param("home", "")
                    )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/dashboard"));
        }

        @Test
        public void shouldRedirectToWheelOfLifePageWhenThereIsWheelOfLifeParam() throws Exception {
            mockMvc.perform(post("/dashboard")
                    .with(csrf())
                    .param("wheelOfLife", "")
            )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/dashboard/wheelOfLife"));
        }

        @Test
        public void shouldReturn400WhenThereIsNoParam() throws Exception {
            mockMvc.perform(post("/dashboard").with(csrf()))
                    .andExpect(status().is(400));
        }
    }

    @Nested
    class HttpGET_wheelOfLife {
        @Test
        @WithAnonymousUser
        public void wheelOfLifePageShouldNotBeAccessibleWithoutLogin() throws Exception {
            mockMvc.perform(get("/dashboard/wheelOfLife"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login"));
        }

        @Test
        @WithMockUser
        public void shouldReturnWheelOfLifePage() throws Exception {
            //given
            var user = testUtils.createTestUserWithWheelOfLifeAndLifeAreas();

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

            //when + then
            mockMvc.perform(get("/dashboard/wheelOfLife"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("dashboard_wheelOfLife"))
                    .andExpect(model().attribute("average", instanceOf(Double.class)))
                    .andExpect(model().attribute("areas", allOf(
                            instanceOf(List.class),
                            everyItem(instanceOf(LifeAreaDTO.class))
                    )))
                    .andExpect(model().attribute("newLifeArea", allOf(
                            instanceOf(LifeAreaDTO.class),
                            hasProperty("name", nullValue()),
                            hasProperty("rate", is((byte) 0))
                    )));
        }

        @Test
        @WithMockUser
        public void shouldRedirectToLoginPageWhenPrincipalUserIsNotFoundInDatabase() throws Exception {
            mockMvc.perform(get("/dashboard/wheelOfLife"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/logout"));
        }
    }

    @Nested
    class HttpPOST_wheelOfLife {
        @Test
        @WithAnonymousUser
        public void shouldNotAddLifeAreaWhenUserIsNotLogin() throws Exception {
            mockMvc.perform(post("/dashboard/wheelOfLife").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("http://localhost/login"));
        }

        @Test
        @WithMockUser
        public void shouldRedirectToLoginPageWhenPrincipalUserIsNotFoundInDatabase() throws Exception {
            mockMvc.perform(post("/dashboard/wheelOfLife")
                            .with(csrf())
                    )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/logout"));
        }

        @Test
        @WithMockUser
        public void shouldReturnWheelOfLifePageWithSubmittedDataWhenAddingLifeAreaFails() throws Exception {
            //given
            var user = testUtils.createTestUserWithWheelOfLifeAndLifeAreas();

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(lifeAreaService.addLifeArea(
                    ArgumentMatchers.any(LifeAreaDTO.class),
                    ArgumentMatchers.any(WheelOfLife.class),
                    ArgumentMatchers.any(BindingResult.class))
            ).thenReturn(false);

            //when + then
            mockMvc.perform(post("/dashboard/wheelOfLife")
                            .with(csrf())
                            .param("name", "Zdrowie")
                            .param("rate", "8")
                    )
                    .andExpect(status().isOk())
                    .andExpect(view().name("dashboard_wheelOfLife"))
                    .andExpect(model().attribute("newLifeArea", allOf(
                            instanceOf(LifeAreaDTO.class),
                            hasProperty("name", is("Zdrowie")),
                            hasProperty("rate", is((byte) 8))
                    )));
        }

        @Test
        @WithMockUser
        public void shouldAddAverageAndAreasAttributesWhenAddingLifeAreaFails() throws Exception {
            //given
            var user = testUtils.createTestUserWithWheelOfLifeAndLifeAreas();

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(lifeAreaService.addLifeArea(
                    ArgumentMatchers.any(LifeAreaDTO.class),
                    ArgumentMatchers.any(WheelOfLife.class),
                    ArgumentMatchers.any(BindingResult.class)
            )).thenReturn(false);

            //when + then
            mockMvc.perform(post("/dashboard/wheelOfLife")
                            .with(csrf())
                            .param("name", "Zdrowie")
                            .param("rate", "8")
                    )
                    .andExpect(status().isOk())
                    .andExpect(view().name("dashboard_wheelOfLife"))
                    .andExpect(model().attributeExists("average", "areas"))
                    .andExpect(model().attribute("average", instanceOf(Double.class)))
                    .andExpect(model().attribute("areas", allOf(
                            instanceOf(List.class),
                            everyItem(instanceOf(LifeAreaDTO.class))
                    )));
        }

        @Test
        @WithMockUser
        public void shouldRedirectToWheelOfLifePageWithNewLifeAreaDTOWhenAddingLifeAreaIsSuccessful() throws Exception {
            //given
            var user = testUtils.createTestUserWithWheelOfLifeAndLifeAreas();

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(lifeAreaService.addLifeArea(
                    ArgumentMatchers.any(LifeAreaDTO.class),
                    ArgumentMatchers.any(WheelOfLife.class),
                    ArgumentMatchers.any(BindingResult.class))
            ).thenReturn(true);

            //when + then
            mockMvc.perform(post("/dashboard/wheelOfLife")
                            .with(csrf())
                            .param("name", "Zdrowie")
                            .param("rate", "8")
                    )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/dashboard/wheelOfLife"));
        }
    }

    @Test
    @WithMockUser
    public void HttpPOST_shouldLogoutUser() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(request().sessionAttributeDoesNotExist("SPRING_SECURITY_CONTEXT"));
    }
}