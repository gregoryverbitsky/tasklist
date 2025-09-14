package de.demo.tasklist.service;

import de.demo.tasklist.domain.MailType;
import de.demo.tasklist.domain.user.User;

import java.util.Properties;

public interface MailService {

    void sendEmail(
            User user,
            MailType type,
            Properties params
    );

}
