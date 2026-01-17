package com.lifemap.controller;

import com.lifemap.config.SecurityConfig;
import com.lifemap.model.UserRepository;
import com.lifemap.service.CustomUserDetailsService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

@WebMvcTest(DashboardController.class)
@Import(SecurityConfig.class)
class DashboardControllerWebTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private UserRepository userRepository;

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

    @Test
    @WithMockUser
    public void HttpPOST_shouldLogoutUser() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(request().sessionAttributeDoesNotExist("SPRING_SECURITY_CONTEXT"));
    }
}