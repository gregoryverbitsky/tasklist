package de.demo.tasklist.web.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import de.demo.tasklist.domain.user.User;
import de.demo.tasklist.service.AuthService;
import de.demo.tasklist.service.UserService;
import de.demo.tasklist.web.dto.auth.JwtRequest;
import de.demo.tasklist.web.dto.auth.JwtResponse;
import de.demo.tasklist.web.dto.user.UserDto;
import de.demo.tasklist.web.dto.validation.OnCreate;
import de.demo.tasklist.web.mappers.UserMapper;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Auth API")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    @Operation(operationId = "Login", summary = "Existing user login", description = "Login for existing user")
    public JwtResponse login(@Validated @RequestBody final JwtRequest loginRequest) {
        return this.authService.login(loginRequest);
    }

    @PostMapping("/register")
    @Operation(operationId = "Register", summary = "Register a new user", description = "Register a new user")
    public UserDto register(@Validated(OnCreate.class) @RequestBody final UserDto userDto) {
        userDto.setId(null);
        User user = this.userMapper.toEntity(userDto);
        User createdUser = this.userService.create(user);
        return this.userMapper.toDto(createdUser);
    }

    @PostMapping("/refresh")
    @Operation(operationId = "Refresh", summary = "Using the Refresh token to get another access token", description = "Using the Refresh token to get another access token")
    public JwtResponse refresh(@RequestBody final String refreshToken) {
        return this.authService.refresh(refreshToken);
    }
}
