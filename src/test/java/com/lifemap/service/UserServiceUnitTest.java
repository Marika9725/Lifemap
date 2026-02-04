package com.lifemap.service;

import com.lifemap.TestUtils;
import com.lifemap.model.*;
import com.lifemap.model.projection.UserDTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    private final TestUtils testUtils = new TestUtils();

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private WheelOfLifeService wheelOfLifeService;

    @InjectMocks
    private UserService userService;

    private UserDTO user;

    @BeforeEach
    public void setUp() {
        user = new UserDTO();
        user.setUsername("TestUser");
        user.setEmail("user@example.com");
        user.setPassword("#Password123");
    }

    @AfterEach
    public void tearDown() {
        user = null;
    }

    @Test
    public void shouldAddRejectionAndReturnFalseWhenEmailIsAlreadyExist() {
        //given
        var result = new BeanPropertyBindingResult(user, "username");

        when(repo.existsByEmail(anyString())).thenReturn(true);
        when(repo.existsByUsername(anyString())).thenReturn(false);

        //when
        var actual = userService.register(user, result, user.getPassword());

        //then
        assertFalse(actual);
        assertTrue(result.hasFieldErrors("email"));
        assertEquals("user.invalid.email.alreadyExists", Objects.requireNonNull(result.getFieldError("email")).getCode());
    }

    @Test
    public void shouldAddRejectionAndReturnFalseWhenUsernameIsAlreadyExist() {
        //given
        var result = new BeanPropertyBindingResult(user, "username");

        when(repo.existsByEmail(anyString())).thenReturn(false);
        when(repo.existsByUsername(anyString())).thenReturn(true);

        //when
        var actual = userService.register(user, result, user.getPassword());

        //then
        assertFalse(actual);
        assertTrue(result.hasFieldErrors("username"));
        assertEquals("user.invalid.username.alreadyExists", Objects.requireNonNull(result.getFieldError("username")).getCode());
    }

    @Test
    public void shouldAddRejectionAndReturnFalseWhenPasswordIsNotEqualToConfirmVersion() {
        //given
        var result = new BeanPropertyBindingResult(user, "username");

        when(repo.existsByEmail(anyString())).thenReturn(false);
        when(repo.existsByUsername(anyString())).thenReturn(false);

        //when
        var actual = userService.register(user, result, "#DifferentPassword123");

        //then
        assertFalse(actual);
        assertTrue(result.hasGlobalErrors());
        assertEquals("user.invalid.password", Objects.requireNonNull(result.getGlobalError()).getCode());
    }

    @Test
    public void shouldAddUserToRepositoryAndReturnTrueWhenThereAreNoErrors() {
        //given
        var result = new BeanPropertyBindingResult(user, "username");

        when(repo.existsByEmail(anyString())).thenReturn(false);
        when(repo.existsByUsername(anyString())).thenReturn(false);
        when(encoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        var toUser = user.toUser(encoder);
        toUser.setId(1L);
        when(repo.save(ArgumentMatchers.any(User.class))).thenReturn(toUser);

        //when
        var actual = userService.register(user, result, user.getPassword());

        //then
        assertTrue(actual);
        verify(repo).save(ArgumentMatchers.any(User.class));
    }

    @Test
    public void savedUserShouldHaveDefaultWheelOfLife() {
        //given
        var result = new BeanPropertyBindingResult(user, "username");
        when(repo.existsByEmail(anyString())).thenReturn(false);
        when(repo.existsByUsername(anyString())).thenReturn(false);
        when(encoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        var wheelOfLife = testUtils.createTestWheelOfLifeWithAreas();
        when(wheelOfLifeService.createDefaultWheelOfLife()).thenReturn(wheelOfLife);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(repo.save(userCaptor.capture())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        //when
        var isRegistered = userService.register(user, result, user.getPassword());
        var savedUser = userCaptor.getValue();

        //then
        assertTrue(isRegistered);
        assertThat(savedUser, notNullValue());
        assertThat(savedUser.getWheelOfLife(), notNullValue());
        assertThat(
                savedUser.getWheelOfLife().getLifeAreas().stream().map(LifeArea::getName).toList(),
                containsInAnyOrder("health", "finance", "relationships")
        );
    }
}