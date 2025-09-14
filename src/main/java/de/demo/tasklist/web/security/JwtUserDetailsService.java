package de.demo.tasklist.web.security;

import de.demo.tasklist.domain.user.User;
import de.demo.tasklist.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(
            final String username
    ) {
        User user = userService.getByUsername(username);
        return JwtEntityFactory.create(user);
    }

}
