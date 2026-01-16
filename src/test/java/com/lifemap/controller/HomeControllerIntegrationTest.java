package com.lifemap.controller;

import com.lifemap.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
class HomeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void shouldSuccessfullyRegisterUser() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "TestUser")
                        .param("password", "#Password123")
                        .param("email", "user@example.com")
                        .param("confirmPassword", "#Password123")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        var user = userRepository.findByEmail("user@example.com")
                .orElseThrow(() -> new AssertionError("User not saved"));

        assertNotNull(user);
        assertThat(user.getUsername(), is("TestUser"));
        assertTrue(passwordEncoder.matches("#Password123", user.getPassword()));
        assertThat(user.getRole(), is(Role.ROLE_USER));
        assertThat(user.getEmail(), is("user@example.com"));
    }

    @Test
    public void shouldNotRegisterUserWhenDataIsInvalid() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", " ")
                        .param("password", "123")
                        .param("email", "user@example.com")
                        .param("confirmPassword", "1234")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("user", "username", "password"));

        assertFalse(userRepository.findByEmail("user@example.com").isPresent());
    }
}