package de.demo.tasklist.service.impl;

import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.demo.tasklist.config.TestConfig;
import de.demo.tasklist.domain.MailType;
import de.demo.tasklist.domain.exception.ResourceNotFoundException;
import de.demo.tasklist.domain.user.Role;
import de.demo.tasklist.domain.user.User;
import de.demo.tasklist.repository.UserRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockitoBean
    private MailServiceImpl mailService;

    @Autowired
    private UserServiceImpl userService;

    @Test
    void getById() {
        Long id = 1L;
        User user = new User();
        user.setId(id);
        Mockito.when(this.userRepository.findById(id)).thenReturn(Optional.of(user));
        User testUser = this.userService.getById(id);
        Mockito.verify(this.userRepository).findById(id);
        Assertions.assertEquals(user, testUser);
    }

    @Test
    void getByNotExistingId() {
        Long id = 1L;
        Mockito.when(this.userRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.getById(id));
        Mockito.verify(this.userRepository).findById(id);
    }

    @Test
    void getByUsername() {
        String username = "username@gmail.com";
        User user = new User();
        user.setUsername(username);
        Mockito.when(this.userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        User testUser = this.userService.getByUsername(username);
        Mockito.verify(this.userRepository).findByUsername(username);
        Assertions.assertEquals(user, testUser);
    }

    @Test
    void getByNotExistingUsername() {
        String username = "username@gmail.com";
        Mockito.when(this.userRepository.findByUsername(username)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> this.userService.getByUsername(username));
        Mockito.verify(this.userRepository).findByUsername(username);
    }

    @Test
    void update() {
        Long id = 1L;
        String password = "password";
        User user = new User();
        user.setId(id);
        user.setPassword(password);
        Mockito.when(this.passwordEncoder.encode(password)).thenReturn("encodedPassword");
        Mockito.when(this.userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        User updated = this.userService.update(user);
        Mockito.verify(this.passwordEncoder).encode(password);
        Mockito.verify(this.userRepository).save(user);
        Assertions.assertEquals(user.getUsername(), updated.getUsername());
        Assertions.assertEquals(user.getName(), updated.getName());
    }

    @Test
    void isTaskOwner() {
        Long userId = 1L;
        Long taskId = 1L;
        Mockito.when(this.userRepository.isTaskOwner(userId, taskId)).thenReturn(true);
        boolean isOwner = this.userService.isTaskOwner(userId, taskId);
        Mockito.verify(this.userRepository).isTaskOwner(userId, taskId);
        Assertions.assertTrue(isOwner);
    }

    @Test
    void create() {
        String username = "username@gmail.com";
        String password = "password";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setPasswordConfirmation(password);
        Mockito.when(this.userRepository.findByUsername(username)).thenReturn(Optional.empty());
        Mockito.when(this.passwordEncoder.encode(password)).thenReturn("encodedPassword");
        User testUser = this.userService.create(user);
        Mockito.verify(this.userRepository).save(user);
        Mockito.verify(this.mailService).sendEmail(user, MailType.REGISTRATION, new Properties());
        Assertions.assertEquals(Set.of(Role.ROLE_USER), testUser.getRoles());
        Assertions.assertEquals("encodedPassword", testUser.getPassword());
    }

    @Test
    void createWithExistingUsername() {
        String username = "username@gmail.com";
        String password = "password";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setPasswordConfirmation(password);
        Mockito.when(this.userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));
        Mockito.when(this.passwordEncoder.encode(password)).thenReturn("encodedPassword");
        Assertions.assertThrows(IllegalStateException.class, () -> userService.create(user));
        Mockito.verify(this.userRepository, Mockito.never()).save(user);
    }

    @Test
    void createWithDifferentPasswords() {
        String username = "username@gmail.com";
        String password = "password1";
        String passwordConfirmation = "password2";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setPasswordConfirmation(passwordConfirmation);
        Mockito.when(this.userRepository.findByUsername(username)).thenReturn(Optional.empty());
        Assertions.assertThrows(IllegalStateException.class, () -> userService.create(user));
        Mockito.verify(this.userRepository, Mockito.never()).save(user);
    }

    @Test
    void isTaskOwnerWithFalse() {
        Long userId = 1L;
        Long taskId = 1L;
        Mockito.when(this.userRepository.isTaskOwner(userId, taskId)).thenReturn(false);
        boolean isOwner = this.userService.isTaskOwner(userId, taskId);
        Mockito.verify(this.userRepository).isTaskOwner(userId, taskId);
        Assertions.assertFalse(isOwner);
    }

    @Test
    void getTaskAuthor() {
        Long taskId = 1L;
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Mockito.when(this.userRepository.findTaskAuthor(taskId)).thenReturn(Optional.of(user));
        User author = this.userService.getTaskAuthor(taskId);
        Mockito.verify(this.userRepository).findTaskAuthor(taskId);
        Assertions.assertEquals(user, author);
    }

    @Test
    void getNotExistingTaskAuthor() {
        Long taskId = 1L;
        Mockito.when(this.userRepository.findTaskAuthor(taskId)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> this.userService.getTaskAuthor(taskId));
        Mockito.verify(this.userRepository).findTaskAuthor(taskId);
    }

    @Test
    void delete() {
        Long id = 1L;
        userService.delete(id);
        Mockito.verify(this.userRepository).deleteById(id);
    }
}
