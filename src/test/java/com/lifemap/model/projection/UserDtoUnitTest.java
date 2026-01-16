package com.lifemap.model.projection;

import com.lifemap.model.Role;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class UserDtoUnitTest {

    private static PasswordEncoder encoder;

    private UserDTO userDTO;

    @BeforeAll
    static void setBeforeAll() {
        encoder = new BCryptPasswordEncoder();
    }

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setEmail("user@example.com");
        userDTO.setUsername("TestUser");
        userDTO.setPassword("#Password123");
    }

    @AfterEach
    void tearDown() {
        userDTO = null;
    }

    @Nested
    class ToUserTests {
        @Test
        void shouldCreateUserFromUserDTO() {
            //given + when
            var user = userDTO.toUser(encoder);
            user.setId(1L);

            //then
            assertThat(user, is(notNullValue()));
            assertThat(user.getId(), is(1L));
            assertThat(user.getUsername(), is(userDTO.getUsername()));
            assertThat(user.getEmail(), is(userDTO.getEmail()));
        }

        @Test
        void passwordShouldBeEncodedAfterCreatesUserFromUserDTO() {
            //given
            var expected = userDTO.getPassword();

            //when
            var user = userDTO.toUser(encoder);

            //then
            assertThat(user.getPassword(), is(not(expected)));
        }

        @Test
        void shouldAddRoleWhenUserIsCreatedFromUserDTO() {
            //given
            var expected = Role.ROLE_USER;

            //when
            var user = userDTO.toUser(encoder);

            //assertThat
            assertThat(user.getRole(), is(expected));
        }

        @Test
        void shouldNotCreateUserWhenDTOHasBlankData() {
            //given
            var userDTO = new UserDTO();
            userDTO.setPassword("#Password123");

            //when
            var user = userDTO.toUser(encoder);

            //then
            assertThat(user, is(nullValue()));
        }
    }
}