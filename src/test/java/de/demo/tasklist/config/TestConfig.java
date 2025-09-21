package de.demo.tasklist.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import io.minio.MinioClient;

import lombok.RequiredArgsConstructor;

import de.demo.tasklist.repository.TaskRepository;
import de.demo.tasklist.repository.UserRepository;
import de.demo.tasklist.service.ImageService;
import de.demo.tasklist.service.impl.AuthServiceImpl;
import de.demo.tasklist.service.impl.ImageServiceImpl;
import de.demo.tasklist.service.impl.MailServiceImpl;
import de.demo.tasklist.service.impl.TaskServiceImpl;
import de.demo.tasklist.service.impl.UserServiceImpl;
import de.demo.tasklist.service.props.JwtProperties;
import de.demo.tasklist.service.props.MinioProperties;
import de.demo.tasklist.web.security.JwtTokenProvider;
import de.demo.tasklist.web.security.JwtUserDetailsService;
import freemarker.template.Configuration;

@TestConfiguration
@RequiredArgsConstructor
public class TestConfig {

    @Bean
    @Primary
    public BCryptPasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtProperties jwtProperties() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("dmdqYmhqbmttYmNhamNjZWhxa25hd2puY2xhZWtic3ZlaGtzYmJ1dg==");
        return jwtProperties;
    }

    @Bean
    public UserDetailsService userDetailsService(final UserRepository userRepository,
            final ApplicationContext applicationContext) {
        return new JwtUserDetailsService(userService(userRepository, applicationContext));
    }

    @Bean
    public MinioClient minioClient() {
        return Mockito.mock(MinioClient.class);
    }

    @Bean
    public MinioProperties minioProperties() {
        MinioProperties properties = new MinioProperties();
        properties.setBucket("images");
        return properties;
    }

    @Bean
    public Configuration configuration() {
        return Mockito.mock(Configuration.class);
    }

    @Bean
    public JavaMailSender mailSender() {
        return Mockito.mock(JavaMailSender.class);
    }

    @Bean
    @Primary
    public MailServiceImpl mailService() {
        return new MailServiceImpl(configuration(), mailSender());
    }

    @Bean
    @Primary
    public ImageService imageService() {
        return new ImageServiceImpl(minioClient(), minioProperties());
    }

    @Bean
    public JwtTokenProvider tokenProvider(final UserRepository userRepository,
            final ApplicationContext applicationContext) {
        return new JwtTokenProvider(jwtProperties(), userDetailsService(userRepository, applicationContext),
                userService(userRepository, applicationContext));
    }

    @Bean
    @Primary
    public UserServiceImpl userService(final UserRepository userRepository,
            final ApplicationContext applicationContext) {
        return new UserServiceImpl(userRepository, testPasswordEncoder(), mailService(), applicationContext);
    }

    @Bean
    @Primary
    public TaskServiceImpl taskService(final TaskRepository taskRepository,
            final ApplicationContext applicationContext) {
        return new TaskServiceImpl(taskRepository, imageService(), applicationContext);
    }

    @Bean
    @Primary
    public AuthServiceImpl authService(final UserRepository userRepository,
            final AuthenticationManager authenticationManager, final ApplicationContext applicationContext) {
        return new AuthServiceImpl(authenticationManager, userService(userRepository, applicationContext),
                tokenProvider(userRepository, applicationContext));
    }

    @Bean
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    public TaskRepository taskRepository() {
        return Mockito.mock(TaskRepository.class);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return Mockito.mock(AuthenticationManager.class);
    }
}
