package de.demo.tasklist.service.impl;

import de.demo.tasklist.domain.MailType;
import de.demo.tasklist.domain.exception.ResourceNotFoundException;
import de.demo.tasklist.domain.user.Role;
import de.demo.tasklist.domain.user.User;
import de.demo.tasklist.repository.UserRepository;
import de.demo.tasklist.service.MailService;
import de.demo.tasklist.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Properties;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private static final String USER_NOT_FOUND = "User not found.";

    @Override
    @Cacheable(
            value = "UserService::getById",
            key = "#id"
    )
    public User getById(
            final Long id
    ) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(USER_NOT_FOUND));
    }

    @Override
    @Cacheable(
            value = "UserService::getByUsername",
            key = "#username"
    )
    public User getByUsername(
            final String username
    ) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(USER_NOT_FOUND));
    }

    @Override
    @Transactional
    @Caching(put = {
            @CachePut(
                    value = "UserService::getById",
                    key = "#user.id"
            ),
            @CachePut(
                    value = "UserService::getByUsername",
                    key = "#user.username"
            )
    })
    public User update(
            final User user
    ) {
        User existing = getById(user.getId());
        existing.setName(user.getName());
        user.setUsername(user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    @Caching(cacheable = {
            @Cacheable(
                    value = "UserService::getById",
                    condition = "#user.id!=null",
                    key = "#user.id"
            ),
            @Cacheable(
                    value = "UserService::getByUsername",
                    condition = "#user.username!=null",
                    key = "#user.username"
            )
    })
    public User create(
            final User user
    ) {
        log.debug("User.name: {} ", user.getName());
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalStateException("User already exists.");
        }
        if (!user.getPassword().equals(user.getPasswordConfirmation())) {
            throw new IllegalStateException(
                    "Password and password confirmation do not match."
            );
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roles = Set.of(Role.ROLE_USER);
        user.setRoles(roles);
        userRepository.save(user);
        mailService.sendEmail(user, MailType.REGISTRATION, new Properties());
        return user;
    }

    @Override
    @Cacheable(
            value = "UserService::isTaskOwner",
            key = "#userId + '.' + #taskId"
    )
    public boolean isTaskOwner(
            final Long userId,
            final Long taskId
    ) {
        log.debug("userId: {}; taskId: {};", userId, taskId);
        return userRepository.isTaskOwner(userId, taskId);
    }

    @Override
    @Cacheable(
            value = "UserService::getTaskAuthor",
            key = "#taskId"
    )
    public User getTaskAuthor(
            final Long taskId
    ) {
        return userRepository.findTaskAuthor(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(USER_NOT_FOUND));
    }

    @Override
    @Transactional
    @CacheEvict(
            value = "UserService::getById",
            key = "#id"
    )
    public void delete(
            final Long id
    ) {
        userRepository.deleteById(id);
    }

}
