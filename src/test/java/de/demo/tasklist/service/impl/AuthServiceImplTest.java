package de.demo.tasklist.service.impl;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.demo.tasklist.config.TestConfig;
import de.demo.tasklist.domain.exception.ResourceNotFoundException;
import de.demo.tasklist.domain.user.Role;
import de.demo.tasklist.domain.user.User;
import de.demo.tasklist.repository.TaskRepository;
import de.demo.tasklist.repository.UserRepository;
import de.demo.tasklist.service.AuthService;
import de.demo.tasklist.web.dto.auth.JwtRequest;
import de.demo.tasklist.web.dto.auth.JwtResponse;
import de.demo.tasklist.web.security.JwtTokenProvider;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserServiceImpl userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthService authService;

    @Test
    void login() {
        Long userId = 1L;
        String username = "username";
        String password = "password";
        Set<Role> roles = Collections.emptySet();
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        JwtRequest request = new JwtRequest();
        request.setUsername(username);
        request.setPassword(password);
        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        user.setRoles(roles);
        Mockito.when(this.userService.getByUsername(username)).thenReturn(user);
        Mockito.when(this.tokenProvider.createAccessToken(userId, username, roles)).thenReturn(accessToken);
        Mockito.when(this.tokenProvider.createRefreshToken(userId, username)).thenReturn(refreshToken);
        JwtResponse response = this.authService.login(request);
        Mockito.verify(this.authenticationManager)
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        Assertions.assertEquals(response.getUsername(), username);
        Assertions.assertEquals(response.getId(), userId);
        Assertions.assertNotNull(response.getAccessToken());
        Assertions.assertNotNull(response.getRefreshToken());
    }

    @Test
    void loginWithIncorrectUsername() {
        String username = "username";
        String password = "password";
        JwtRequest request = new JwtRequest();
        request.setUsername(username);
        request.setPassword(password);
        User user = new User();
        user.setUsername(username);
        Mockito.when(this.userService.getByUsername(username)).thenThrow(ResourceNotFoundException.class);
        Mockito.verifyNoInteractions(this.tokenProvider);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> this.authService.login(request));
    }

    @Test
    void refresh() {
        String refreshToken = "refreshToken";
        String accessToken = "accessToken";
        String newRefreshToken = "newRefreshToken";
        JwtResponse response = new JwtResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(newRefreshToken);
        Mockito.when(this.tokenProvider.refreshUserTokens(refreshToken)).thenReturn(response);
        JwtResponse testResponse = this.authService.refresh(refreshToken);
        Mockito.verify(this.tokenProvider).refreshUserTokens(refreshToken);
        Assertions.assertEquals(testResponse, response);
    }
}
