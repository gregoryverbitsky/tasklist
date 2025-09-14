package de.demo.tasklist.service.impl;

import de.demo.tasklist.domain.user.User;
import de.demo.tasklist.service.AuthService;
import de.demo.tasklist.service.UserService;
import de.demo.tasklist.web.dto.auth.JwtRequest;
import de.demo.tasklist.web.dto.auth.JwtResponse;
import de.demo.tasklist.web.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public JwtResponse login(
            final JwtRequest loginRequest
    ) {
        JwtResponse jwtResponse = new JwtResponse();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword())
        );
        User user = userService.getByUsername(loginRequest.getUsername());
        jwtResponse.setId(user.getId());
        jwtResponse.setUsername(user.getUsername());
        jwtResponse.setAccessToken(jwtTokenProvider.createAccessToken(
                user.getId(), user.getUsername(), user.getRoles())
        );
        jwtResponse.setRefreshToken(jwtTokenProvider.createRefreshToken(
                user.getId(), user.getUsername())
        );
        return jwtResponse;
    }

    @Override
    public JwtResponse refresh(
            final String refreshToken
    ) {
        return jwtTokenProvider.refreshUserTokens(refreshToken);
    }

}
