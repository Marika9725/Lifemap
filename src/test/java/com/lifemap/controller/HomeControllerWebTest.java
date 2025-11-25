package com.lifemap.controller;

import com.lifemap.config.SecurityConfig;
import com.lifemap.model.projection.UserDTO;
import com.lifemap.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
@Import(SecurityConfig.class)
class HomeControllerWebTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder encoder;

    @MockitoBean
    private CustomUserDetailsService service;

    @MockitoBean
    private UserService userService;

    @Nested
    class HttpGET_Home {
        @Test
        void homePageShouldBeAccessibleWithoutLogin() throws Exception {
            mockMvc.perform(get("/")).andExpect(status().isOk());
        }

        @Test
        void httpGET_shouldReturnHomePageWithDefaultLang() throws Exception {
            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attribute("lang", "pl"));
        }

        @Test
        void httpGet_shouldReturnHomePageWithCustomLang() throws Exception {
            mockMvc.perform(get("/").param("lang", "en"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attribute("lang", "en"));
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "de"})
        void httpGet_shouldReturnHomePageWithDefaultLangWhenLangIsValid(String lang) throws Exception {
            mockMvc.perform(get("/").param("lang", lang))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attribute("lang", "pl"));
        }
    }

    @Nested
    class HttpGET_Login {
        @Test
        public void loginPageShouldBeAccessibleWithoutLogin() throws Exception {
            mockMvc.perform(get("/login")).andExpect(status().isOk());
        }

        @Test
        public void httpGET_shouldReturnLoginPageWithDefaultLangAndNewUserDTO() throws Exception {
            mockMvc.perform(get("/login"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("login"))
                    .andExpect(model().attribute("lang", "pl"))
                    .andExpect(model().attribute("user", any(UserDTO.class)))
                    .andExpect(model().attribute("user", allOf(
                            hasProperty("username", nullValue()),
                            hasProperty("email", nullValue()),
                            hasProperty("password", nullValue())
                    )));
        }

        @Test
        public void httpGET_shouldReturnLoginPageWithCustomLangAndNewUserDTO() throws Exception {
            mockMvc.perform(get("/login").param("lang", "en"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("login"))
                    .andExpect(model().attribute("lang", "en"))
                    .andExpect(model().attribute("user", any(UserDTO.class)))
                    .andExpect(model().attribute("user", allOf(
                            hasProperty("username", nullValue()),
                            hasProperty("email", nullValue()),
                            hasProperty("password", nullValue())
                    )));
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "de"})
        public void httpGET_shouldReturnLoginPageWithDefaultLangAndNewUserDTOWhenLangIsValid(String lang) throws Exception {
            mockMvc.perform(get("/login").param("lang", lang))
                    .andExpect(status().isOk())
                    .andExpect(view().name("login"))
                    .andExpect(model().attribute("lang", "pl"))
                    .andExpect(model().attribute("user", any(UserDTO.class)))
                    .andExpect(model().attribute("user", allOf(
                            hasProperty("username", nullValue()),
                            hasProperty("email", nullValue()),
                            hasProperty("password", nullValue())
                    )));
        }
    }

    @Nested
    class HttpPost_Login {
        @Test
        public void shouldRedirectToDashboardAfterSuccessfulLogin() throws Exception {
            //given
            var user = new User(
                    "user@examplecom",
                    encoder.encode("#Password123"),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );

            when(service.loadUserByUsername(anyString())).thenReturn(user);

            //when & then
            mockMvc.perform(post("/login")
                            .with(csrf())
                            .param("email", "user@example.com")
                            .param("password", "#Password123")
                    )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/dashboard"));
        }

        @Test
        public void shouldNotRedirectToDashboardWhenLoginFailed() throws Exception {
            //given
            when(service.loadUserByUsername(anyString())).thenReturn(null);

            //when & then
            mockMvc.perform(post("/login")
                            .with(csrf())
                            .param("email", "user@example.com")
                            .param("password", "#Password123")
                    )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login?error"));
        }
    }

    @Nested
    class HttpGET_Register {
        @Test
        public void registerPageShouldBeAccessibleWithoutLogin() throws Exception {
            mockMvc.perform(get("/register")).andExpect(status().isOk());
        }

        @Test
        public void httpGET_shouldReturnRegisterPageWithDefaultLangAndNewUserDTO() throws Exception {
            mockMvc.perform(get("/register"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("register"))
                    .andExpect(model().attribute("lang", "pl"))
                    .andExpect(model().attribute("user", any(UserDTO.class)))
                    .andExpect(model().attribute("user", allOf(
                            hasProperty("username", nullValue()),
                            hasProperty("email", nullValue()),
                            hasProperty("password", nullValue())
                    )));
        }

        @Test
        public void httpGET_shouldReturnRegisterPageWithCustomLangAndNewUserDTO() throws Exception {
            mockMvc.perform(get("/register").param("lang", "en"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("register"))
                    .andExpect(model().attribute("lang", "en"))
                    .andExpect(model().attribute("user", any(UserDTO.class)))
                    .andExpect(model().attribute("user", allOf(
                            hasProperty("username", nullValue()),
                            hasProperty("email", nullValue()),
                            hasProperty("password", nullValue())
                    )));
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "de"})
        public void httpGET_shouldReturnRegisterPageWithDefaultLangAndNewUserDTOWhenLangIsValid(String lang) throws Exception {
            mockMvc.perform(get("/register").param("lang", lang))
                    .andExpect(status().isOk())
                    .andExpect(view().name("register"))
                    .andExpect(model().attribute("lang", "pl"))
                    .andExpect(model().attribute("user", any(UserDTO.class)))
                    .andExpect(model().attribute("user", allOf(
                            hasProperty("username", nullValue()),
                            hasProperty("email", nullValue()),
                            hasProperty("password", nullValue())
                    )));
        }
    }

    @Nested
    class HttpPost_Register {

        @Test
        public void shouldRedirectToLoginPageWhenUserIsSuccessfulRegistered() throws Exception {
            //given
            when(userService.register(
                    ArgumentMatchers.any(UserDTO.class),
                    ArgumentMatchers.any(BindingResult.class),
                    anyString())
            ).thenReturn(true);

            //when & then
            mockMvc.perform(post("/register")
                            .param("username", "testUser")
                            .param("password", "#Password123")
                            .param("email", "user@example.com")
                            .param("confirmPassword", "#Password123")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        }

        @Test
        public void shouldReturnRegisterPageWhenBindingResultHasErrors() throws Exception {
            //given
            when(userService.register(
                    ArgumentMatchers.any(UserDTO.class),
                    ArgumentMatchers.any(BindingResult.class),
                    anyString()
            )).thenReturn(true);

            //when & then
            mockMvc.perform(post("/register")
                            .param("username", "testUser")
                            .param("password", "#Password123")
                            .param("email", "user.example.com")
                            .param("confirmPassword", "#Password123")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("register"))
                    .andExpect(model().attribute("user", any(UserDTO.class)))
                    .andExpect(model().attribute("user", allOf(
                            hasProperty("username", equalTo("testUser")),
                            hasProperty("password", equalTo("#Password123")),
                            hasProperty("email", equalTo("user.example.com"))
                    )));
        }

        @Test
        public void shouldReturnRegisterPageWhenUserIsNotRegistered() throws Exception {
            //given
            when(userService.register(
                    ArgumentMatchers.any(UserDTO.class),
                    ArgumentMatchers.any(BindingResult.class),
                    anyString())
            ).thenReturn(false);

            //when & then
            mockMvc.perform(post("/register")
                            .param("username", "user")
                            .param("password", "pass")
                            .param("email", "user@example.com")
                            .param("confirmPassword", "pass")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("register"))
                    .andExpect(model().attribute("user", any(UserDTO.class)))
                    .andExpect(model().attribute("user", allOf(
                            hasProperty("username", equalTo("user")),
                            hasProperty("password", equalTo("pass")),
                            hasProperty("email", equalTo("user@example.com"))
                    )));
        }
    }
}