package de.demo.tasklist.web.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.crypto.SecretKey;

import jakarta.annotation.PostConstruct;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import lombok.RequiredArgsConstructor;

import de.demo.tasklist.domain.exception.AccessDeniedException;
import de.demo.tasklist.domain.user.Role;
import de.demo.tasklist.domain.user.User;
import de.demo.tasklist.service.UserService;
import de.demo.tasklist.service.props.JwtProperties;
import de.demo.tasklist.web.dto.auth.JwtResponse;

@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(this.jwtProperties.getSecret().getBytes());
    }

    public String createAccessToken(final Long userId, final String username, final Set<Role> roles) {
        Claims claims = Jwts.claims().subject(username).add("id", userId).add("roles", resolveRoles(roles)).build();
        Instant validity = Instant.now().plus(this.jwtProperties.getAccess(), ChronoUnit.HOURS);
        return Jwts.builder().claims(claims).expiration(Date.from(validity)).signWith(key).compact();
    }

    private List<String> resolveRoles(final Set<Role> roles) {
        return roles.stream().map(Enum::name).toList();
    }

    public String createRefreshToken(final Long userId, final String username) {
        Claims claims = Jwts.claims().subject(username).add("id", userId).build();
        Instant validity = Instant.now().plus(this.jwtProperties.getRefresh(), ChronoUnit.DAYS);
        return Jwts.builder().claims(claims).expiration(Date.from(validity)).signWith(key).compact();
    }

    public JwtResponse refreshUserTokens(final String refreshToken) {
        JwtResponse jwtResponse = new JwtResponse();
        if (!isValid(refreshToken)) {
            throw new AccessDeniedException();
        }
        Long userId = Long.valueOf(getId(refreshToken));
        User user = this.userService.getById(userId);
        jwtResponse.setId(userId);
        jwtResponse.setUsername(user.getUsername());
        jwtResponse.setAccessToken(createAccessToken(userId, user.getUsername(), user.getRoles()));
        jwtResponse.setRefreshToken(createRefreshToken(userId, user.getUsername()));
        return jwtResponse;
    }

    public boolean isValid(final String token) {
        Jws<Claims> claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
        return claims.getPayload().getExpiration().after(new Date());
    }

    private String getId(final String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("id", String.class);
    }

    private String getUsername(final String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public Authentication getAuthentication(final String token) {
        String username = getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
