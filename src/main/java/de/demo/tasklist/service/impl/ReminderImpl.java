package de.demo.tasklist.service.impl;

import de.demo.tasklist.domain.MailType;
import de.demo.tasklist.domain.task.Task;
import de.demo.tasklist.domain.user.User;
import de.demo.tasklist.service.MailService;
import de.demo.tasklist.service.Reminder;
import de.demo.tasklist.service.TaskService;
import de.demo.tasklist.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class ReminderImpl implements Reminder {

    private final TaskService taskService;
    private final UserService userService;
    private final MailService mailService;
    private final Duration duration = Duration.ofHours(1);

    //    @Scheduled(cron = "0 0 * * * *")
    @Scheduled(cron = "0 * * * * *")
    @Override
    public void remindForTask() {
        List<Task> tasks = taskService.getAllSoonTasks(duration);
        tasks.forEach(task -> {
            User user = userService.getTaskAuthor(task.getId());
            Properties properties = new Properties();
            properties.setProperty("task.title", task.getTitle());
            properties.setProperty("task.description", task.getDescription());
            mailService.sendEmail(user, MailType.REMINDER, properties);
        });
    }

}
