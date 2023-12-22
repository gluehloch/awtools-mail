/*
 * ============================================================================
 * Project awtools-mail
 * Copyright (c) 2004-2023 by Andre Winkler. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.icegreen.greenmail.pop3.Pop3Server;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.util.ServerSetupTest;

/**
 * Testet die Klasse {@link POP3Receiver}.
 * 
 * @author by Andre Winkler
 */
public class POP3ReceiverTest {

    private static final String EMAIL_USER_ADDRESS = "hascode@localhost";
    private static final String EMAIL_TO = "someone@localhost.com";

    @Tag("pop3")
    @DisplayName("Receive POP3 message")
    @Test
    void receiveMailsFromPOP3() throws Exception {
        final GreenMail greenMail = new GreenMail(new ServerSetup[] { ServerSetupTest.SMTP.dynamicPort(), ServerSetupTest.POP3.dynamicPort()});
        greenMail.start();

        final String subject = GreenMailUtil.random();
        final String body = GreenMailUtil.random();
        final GreenMailUser user = greenMail.setUser("to@localhost", "test", "soooosecret");
//        user.deliver(message);
        
        GreenMailUtil.sendTextEmail("to@localhost", "from@localhost", "subject", "body", greenMail.getSmtp().getServerSetup());

        Pop3Server pop3 = greenMail.getPop3();
        Session session = pop3.createSession();

        MailSession mailSession = new MailSession(session, "localhost", pop3.getPort(), user.getLogin(), user.getPassword());

        POP3Receiver pop3Receiver = new DefaultPOP3Receiver();
        List<SimpleMailMessage> messages = pop3Receiver.download(mailSession);

        assertThat(messages).hasSize(1);
        SimpleMailMessage msg = messages.get(0);

        assertThat(msg.from().get(0)).isEqualTo("from@localhost");
        assertThat(msg.subject()).isEqualTo("subject");
        assertThat(messages.get(0).content()).isEqualTo("body");

        greenMail.stop();
    }

    @Tag("pop3")
    @DisplayName("Create an InternetAddress")
    @Test
    void internetAddress() throws AddressException {
        InternetAddress internetAddress = new InternetAddress("test@localhost");
        assertThat(internetAddress.getAddress()).isEqualTo("test@localhost");
    }

    MimeMessage createMimeMessage(String subject, String body) throws MessagingException {
        MimeMessage message = new MimeMessage((Session) null);
        message.setFrom(new InternetAddress(EMAIL_TO));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(EMAIL_USER_ADDRESS));
        message.setSubject(subject);
        message.setText(body);
        return message;
    }

}
