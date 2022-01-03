package de.awtools.mail;

import jakarta.mail.Session;

public record MailSession(Session session, String host, int port, String user, String password) {
}
