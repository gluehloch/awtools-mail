/*
 * $Id: DefaultPOP3Receiver.java 2732 2011-01-04 14:53:44Z andrewinkler $
 * ============================================================================
 * Project awtools-mail
 * Copyright (c) 2004-2010 by Andre Winkler. All rights reserved.
 * ============================================================================
 *          GNU LESSER GENERAL PUBLIC LICENSE
 *  TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  This library is free software; you can redistribute it and/or
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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default Implementierung des {@link POP3Receiver}.
 * 
 * @author Andre Winkler
 */
public final class DefaultPOP3Receiver implements POP3Receiver {

    /** Der private Logger der Klasse. */
    private final Logger log = LoggerFactory
            .getLogger(DefaultPOP3Receiver.class);

    /** Eine Mail-Session. */
    private Session session;

    /** Mail Store. */
    private Store store;

    /** Mail Folder. */
    private Folder folder;

    /** Eine Liste mit allen <code>Message</code>s. */
    private List<Message> messages = new ArrayList<Message>();

    /** Mail User. */
    private final String user;

    /** Mail Password. */
    private final String password;

    /** Mail Port. */
    private final int port;

    /** Mail Host. */
    private final String host;

    private final PropertyChangeSupport propertyChangeSupport;

    /**
     * Konstruktor.
     * 
     * @param _host
     *            Der Mail-Server.
     * @param _port
     *            Der Mail-Port.
     * @param _user
     *            Der Mail-User.
     * @param _password
     *            Das Mail-Password.
     */
    public DefaultPOP3Receiver(final String _host, final int _port,
            final String _user, final String _password) {

        propertyChangeSupport = new PropertyChangeSupport(this);

        Properties props = System.getProperties();
        session = Session.getDefaultInstance(props, null);
        session.setDebug(false);

        host = _host;
        port = _port;
        user = _user;
        password = _password;
    }

    /**
     * Startet den 'Download' der Mails aus dem konfigurierten POP3 Account.
     */
    public void download() {
        closeFolder();

        try {
            openSession();
            openInbox();

            int numberOfMessages = numberOfMessages();
            if (numberOfMessages == 0) {
                fireFindNoMailsEvent();
            } else {
                readAllMails(numberOfMessages);
            }
        } finally {
            closeFolder();
        }
    }

    private void readAllMails(int numberOfMessages) {
        for (int i = 1; i <= numberOfMessages; i++) {
            log.debug(">>>> Received mail with index {0}.", i);

            try {
                messages.add(folder.getMessage(i));
            } catch (MessagingException ex) {
                String errorMessage = String.format(
                        "Unable to get message at index %d.", i);
                throw new MailDownloadException(errorMessage, ex);
            }

            propertyChangeSupport
                    .firePropertyChange(PROPERTY_COUNTER, i - 1, i);
        }

        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.ENVELOPE);
        fp.add(FetchProfile.Item.CONTENT_INFO);
        fp.add(FetchProfile.Item.FLAGS);
        fp.add("X-Mailer");

        try {
            folder.fetch(messagesToArray(), fp);
        } catch (MessagingException ex) {
            throw new MailDownloadException("Unable to read the mail folder.",
                    ex);
        }

        log.debug("Fetched all mails.");

        propertyChangeSupport.firePropertyChange(PROPERTY_STATE,
                ReceiverState.RECEIVERSTATE_CONNECTED,
                ReceiverState.RECEIVERSTATE_READY);
    }

    private int numberOfMessages() {
        int numberOfMessages = 0;
        try {
            numberOfMessages = folder.getMessageCount();
        } catch (MessagingException ex) {
            throw new MailDownloadException(
                    "Unable to count the number of mails.", ex);
        }

        log.debug("Number of messages: {0}", numberOfMessages);

        propertyChangeSupport.firePropertyChange(PROPERTY_MAILNUM, 0,
                numberOfMessages);

        return numberOfMessages;
    }

    private void fireFindNoMailsEvent() {
        propertyChangeSupport.firePropertyChange(PROPERTY_STATE,
                ReceiverState.RECEIVERSTATE_CONNECTED,
                ReceiverState.RECEIVERSTATE_NO_MAILS);
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
            folder.open(Folder.READ_WRITE);
        } catch (MessagingException ex) {
            throw new MailDownloadException("Unable to open the INBOX folder.",
                    ex);
        }
    }

    private void unableToGetInboxFolder(MessagingException ex) {
        propertyChangeSupport.firePropertyChange(PROPERTY_STATE,
                ReceiverState.RECEIVERSTATE_CONNECTED,
                ReceiverState.RECEIVERSTATE_FAILED);

        throw new MailDownloadException("Unable to get folder INBOX.", ex);
    }

    private void unableToGetDefaultFolder(MessagingException ex) {
        propertyChangeSupport.firePropertyChange(PROPERTY_STATE,
                ReceiverState.RECEIVERSTATE_CONNECTED,
                ReceiverState.RECEIVERSTATE_FAILED);

        throw new MailDownloadException("Unable to get the default folder.",
                ex);
    }

    private void openSession() {

        propertyChangeSupport.firePropertyChange(PROPERTY_STATE,
                ReceiverState.RECEIVERSTATE_INIT,
                ReceiverState.RECEIVERSTATE_TRY_CONNECT);

        try {
            store = session.getStore("pop3");
        } catch (NoSuchProviderException ex) {
            throw new MailDownloadException("Unable to get the POP3 provider",
                    ex);
        }

        try {
            store.connect(host, port, user, password);
        } catch (MessagingException ex) {
            throw new MailDownloadException(
                    "Unable to connect to POP3 account.", ex);
        }

        log.debug("... Verbindungsaufbau hergestellt.");
        propertyChangeSupport.firePropertyChange(PROPERTY_STATE,
                ReceiverState.RECEIVERSTATE_TRY_CONNECT,
                ReceiverState.RECEIVERSTATE_CONNECTED);
    }

    private void closeFolder() {
        if (folder != null && folder.isOpen()) {
            try {
                folder.close(false);
                store.close();
            } catch (MessagingException ex) {
                throw new MailDownloadException(
                        "Unable to close the POP3 connection.");
            }
        }
    }

    // TODO: Concurrent access problems, if download is running!
    public Message[] messagesToArray() {
        return ((Message[]) messages.toArray(new Message[messages.size()]));
    }

    // TODO: Concurrent access problems, if download is running!
    public List<Message> messagesToList() {
        return messages;
    }

    public void addPropertyChangeListener(
            final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(
            final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("Auslesen der Mails aus dem POP3-Account...");
        buf.append("Host=");
        buf.append(host);
        buf.append(", User=");
        buf.append(user);
        buf.append(", Pwd=");
        buf.append(password);
        buf.append(", Port=");
        buf.append(port);
        return buf.toString();
    }

}
