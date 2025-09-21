package de.demo.tasklist.service;

import java.util.Properties;

import de.demo.tasklist.domain.MailType;
import de.demo.tasklist.domain.user.User;

public interface MailService {

    void sendEmail(User user, MailType type, Properties params);
}
