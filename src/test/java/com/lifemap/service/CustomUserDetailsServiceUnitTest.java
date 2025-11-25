package com.lifemap.service;

import com.lifemap.model.*;
import com.lifemap.model.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.*;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceUnitTest {

    @Mock
    private UserRepository repo;

    @InjectMocks
    private CustomUserDetailsService service;

    @Nested
    class LoadUserByUsernameTests {
        @Test
        public void shouldReturnSecurityUserWhenUsernameExists() {
            //given
            var username = "TestUser";
            var user = new User();
            user.setEmail("user@example.com");
            user.setPassword("encodedPassword");
            user.setRole(Role.ROLE_USER);

            when(repo.findByEmail(username)).thenReturn(Optional.of(user));

            //when
            var actual = service.loadUserByUsername(username);

            //then
            assertInstanceOf(UserDetails.class, actual);
            assertThat(actual.getUsername(), is(user.getEmail()));
            assertThat(actual.getPassword(), is(user.getPassword()));
            assertTrue(actual.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch("ROLE_USER"::equals)
            );
        }

        @Test
        public void shouldThrowUsernameNotFoundExceptionWhenUsernameDoesNotExists() {
            //given
            var username = "TestUser";

            when(repo.findByEmail(username)).thenReturn(Optional.empty());

            //when & then
            assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(username));
        }
    }
}