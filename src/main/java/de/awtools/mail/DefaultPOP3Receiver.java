/*
 * ============================================================================
 * Project awtools-mail
 * Copyright (c) 2004-2022 by Andre Winkler. All rights reserved.
 * ============================================================================
 *          GNU LESSER GENERAL PUBLIC LICENSE
 *  TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  This library is free software; you can redistribute it and/ord
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package de.awtools.mail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.Address;
import jakarta.mail.FetchProfile;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Store;
import jakarta.mail.internet.InternetAddress;

/**
 * Default Implementierung des {@link POP3Receiver}.
 * 
 * @author Andre Winkler
 */
public final class DefaultPOP3Receiver implements POP3Receiver {

    /** Der private Logger der Klasse. */
    private final Logger log = LoggerFactory.getLogger(DefaultPOP3Receiver.class);

    /** Mail Store. */
    private Store store;

    /** Mail Folder. */
    private Folder folder;

    /**
     * Startet den 'Download' der Mails aus dem konfigurierten POP3 Account.
     * 
     * @param mailSession Mail-Konfiguration
     * @return Eine Liste von {@link SimpleMailMessage}.
     */
    public List<SimpleMailMessage> download(MailSession mailSession) {
        closeFolder();

        try {
            openSession(mailSession);
            openInbox();

            int numberOfMessages = numberOfMessages();
            if (numberOfMessages > 0) {
                return readAllMails(numberOfMessages);
            } else {
                return new ArrayList<>();
            }
        } finally {
            closeFolder();
        }
    }

    private List<SimpleMailMessage> readAllMails(int numberOfMessages) {
        List<Message> messages = new ArrayList<>();

        for (int i = 1; i <= numberOfMessages; i++) {
            log.debug(">>>> Received an email with index {}.", i);

            try {
                messages.add(folder.getMessage(i));
            } catch (MessagingException ex) {
                String errorMessage = String.format("Unable to get message at index %d.", i);
                throw new MailDownloadException(errorMessage, ex);
            }
        }

        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.ENVELOPE);
        fp.add(FetchProfile.Item.CONTENT_INFO);
        fp.add(FetchProfile.Item.FLAGS);
        fp.add("X-Mailer");

        try {
            folder.fetch(messages.toArray(new Message[messages.size()]), fp);
        } catch (MessagingException ex) {
            throw new MailDownloadException("Unable to read the mail folder.", ex);
        }

        log.debug("Fetched all mails.");

        List<SimpleMailMessage> simpleMailMessages = new ArrayList<>();
        try {
            for (Message message : messages) {
                SimpleMailMessage simpleMailMessage = new SimpleMailMessage(
                        map(message.getFrom()),
                        map(message.getRecipients(Message.RecipientType.TO)),
                        map(message.getRecipients(Message.RecipientType.CC)),
                        map(message.getRecipients(Message.RecipientType.BCC)),
                        message.getReceivedDate(),
                        message.getSentDate(),
                        message.getSubject(),
                        message.getContent().toString().strip());
                simpleMailMessages.add(simpleMailMessage);
            }
        } catch (IOException | MessagingException ex) {
            throw new MailDownloadException("Unable to read the mail folder.", ex);
        }

        return simpleMailMessages;
    }

    private static String map(Address address) {
        if (address instanceof InternetAddress internetAddress) {
            return internetAddress.getAddress();
        } else {
            return address.toString();
        }
    }

    private static List<String> map(Address[] addresses) {
        if (addresses == null) {
            return Collections.emptyList();
        } else {
            List<String> emailAddresses = new ArrayList<>();
            for (Address address : addresses) {
                emailAddresses.add(map(address));
            }
            return emailAddresses;
        }
    }

    private int numberOfMessages() {
        int numberOfMessages = 0;
        try {
            numberOfMessages = folder.getMessageCount();
        } catch (MessagingException ex) {
            throw new MailDownloadException(
                    "Unable to count the number of mails.", ex);
        }

        log.debug("Number of messages: {}", numberOfMessages);

        return numberOfMessages;
    }

    private void openInbox() {
        try {
            folder = store.getDefaultFolder();
        } catch (MessagingException ex) {
            unableToGetDefaultFolder(ex);
        }

        if (folder == null) {
            unableToGetDefaultFolder(null);
        }

        try {
            folder = folder.getFolder("INBOX");
        } catch (MessagingException ex) {
            unableToGetInboxFolder(ex);
        }

        if (folder == null) {
            unableToGetInboxFolder(null);
        }

        try {
            // folder.open(Folder.READ_WRITE);
            folder.open(Folder.READ_ONLY);
        } catch (MessagingException ex) {
            throw new MailDownloadException("Unable to open the INBOX folder.", ex);
        }
    }

    private void unableToGetInboxFolder(MessagingException ex) {
        throw new MailDownloadException("Unable to get folder INBOX.", ex);
    }

    private void unableToGetDefaultFolder(MessagingException ex) {
        throw new MailDownloadException("Unable to get the default folder.", ex);
    }

    private void openSession(MailSession mailSession) {
        try {
            store = mailSession.session().getStore("pop3");
        } catch (NoSuchProviderException ex) {
            throw new MailDownloadException("Unable to get the POP3 provider", ex);
        }

        try {
            store.connect(mailSession.host(), mailSession.port(), mailSession.user(), mailSession.password());
        } catch (MessagingException ex) {
            throw new MailDownloadException("Unable to connect to POP3 account.", ex);
        }

        log.debug("... Verbindungsaufbau hergestellt.");
    }

    private void closeFolder() {
        if (folder != null && folder.isOpen()) {
            try {
                folder.close(false);
                store.close();
            } catch (MessagingException ex) {
                throw new MailDownloadException("Unable to close the POP3 connection.");
            }
        }
    }

}
