package com.example.tasklist.service.impl;

import com.example.tasklist.config.TestConfig;
import de.demo.tasklist.domain.MailType;
import de.demo.tasklist.domain.user.User;
import de.demo.tasklist.repository.TaskRepository;
import de.demo.tasklist.repository.UserRepository;
import de.demo.tasklist.service.MailService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Properties;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
@ExtendWith(MockitoExtension.class)
public class MailServiceImplTest {

      @MockitoBean
    private AuthenticationManager authenticationManager;

      @MockitoBean
    private Configuration configuration;

      @MockitoBean
    private UserRepository userRepository;

      @MockitoBean
    private TaskRepository taskRepository;

      @MockitoBean
    private JavaMailSender javaMailSender;

    @Autowired
    private MailService mailServiceImpl;

    @Test
    void sendEmailForRegistration() {
        try {
            String name = "Mike";
            String username = "mike@gmail.com";
            User user = new User();
            user.setName(name);
            user.setUsername(username);
            Mockito.when(javaMailSender.createMimeMessage())
                    .thenReturn(Mockito.mock(MimeMessage.class));
            Mockito.when(configuration.getTemplate("register.ftlh"))
                    .thenReturn(Mockito.mock(Template.class));
            mailServiceImpl.sendEmail(user, MailType.REGISTRATION, new Properties());
            Mockito.verify(configuration).getTemplate("register.ftlh");
            Mockito.verify(javaMailSender).send(Mockito.any(MimeMessage.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void sendEmailForRemind() {
        try {
            String name = "Mike";
            String username = "mike@gmail.com";
            User user = new User();
            user.setName(name);
            user.setUsername(username);
            Mockito.when(javaMailSender.createMimeMessage())
                    .thenReturn(Mockito.mock(MimeMessage.class));
            Mockito.when(configuration.getTemplate("reminder.ftlh"))
                    .thenReturn(Mockito.mock(Template.class));
            mailServiceImpl.sendEmail(user, MailType.REMINDER, new Properties());
            Mockito.verify(configuration).getTemplate("reminder.ftlh");
            Mockito.verify(javaMailSender).send(Mockito.any(MimeMessage.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
