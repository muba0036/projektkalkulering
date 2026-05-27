package com.alphasolutions.projectcalc.service;

import com.alphasolutions.projectcalc.model.User;
import com.alphasolutions.projectcalc.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void authenticate_returnsUserOnCorrectCredentials() {
        User u = new User(1, "admin", "admin123", "ADMIN");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(u));

        Optional<User> result = userService.authenticate("admin", "admin123");

        assertTrue(result.isPresent());
        assertEquals("admin", result.get().getUsername());
    }

    @Test
    void authenticate_returnsEmptyOnWrongPassword() {
        User u = new User(1, "admin", "admin123", "ADMIN");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(u));

        Optional<User> result = userService.authenticate("admin", "wrong");

        assertFalse(result.isPresent());
    }

    @Test
    void authenticate_returnsEmptyForUnknownUser() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        assertFalse(userService.authenticate("ghost", "any").isPresent());
    }

    @Test
    void createUser_setsDefaultRole() {
        User u = new User(0, "newuser", "pw", null);
        when(userRepository.save(u)).thenReturn(1);

        userService.createUser(u);

        assertEquals("USER", u.getRole());
    }

    @Test
    void createUser_throwsForBlankUsername() {
        User u = new User(0, "", "pw", "USER");
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(u));
    }

    @Test
    void createUser_throwsForBlankPassword() {
        User u = new User(0, "user", "", "USER");
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(u));
    }
}
