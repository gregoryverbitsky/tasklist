package de.demo.tasklist.web.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import de.demo.tasklist.domain.user.User;
import de.demo.tasklist.service.UserService;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(final String username) {
        User user = this.userService.getByUsername(username);
        return JwtEntityFactory.create(user);
    }
}
