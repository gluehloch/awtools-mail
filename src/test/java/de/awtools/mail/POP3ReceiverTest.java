/*
 * $Id: POP3ReceiverTest.java 2333 2010-07-31 13:03:33Z andrewinkler $
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.pop3.Pop3Server;
import com.icegreen.greenmail.util.ServerSetupTest;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;

/**
 * Testet die Klasse {@link POP3Receiver}.
 * 
 * @author by Andre Winkler
 */
public class POP3ReceiverTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.POP3);

    @Tag("pop3")
    @DisplayName("Receive POP3 message")
    @Test
    void testInternetAddress() throws Exception {
        Pop3Server pop3 = greenMail.getPop3();
        pop3.createSession();
        
        Address[] addresses = new Address[] { new InternetAddress("andre.winkler@web.de") };

        InternetAddress internetAddress = new InternetAddress("andre.winkler@web.de");
        assertThat(internetAddress.getAddress()).isEqualTo("andre.winkler@web.de");

        Message message = mock(Message.class);
        when(message.getFrom()).thenReturn(addresses);
        when(message.getSubject()).thenReturn("This is a test.");
        when(message.getSentDate()).thenReturn(new Date());
        when(message.getContentType()).thenReturn("text/plain");
        when(message.getContent()).thenReturn("The content of the message.");

        POP3Receiver receiver = mock(POP3Receiver.class);
        when(receiver.messagesToArray()).thenReturn(new Message[] { message, message });
        receiver.download();
        Message[] messages = receiver.messagesToArray();

        verify(receiver).download();
        verify(receiver).messagesToArray();

        assertThat(messages.length).isEqualTo(2);
        assertThat(messages[0].getFrom()).isEqualTo(addresses);
        assertThat(messages[0].getSubject()).isEqualTo("This is a test.");
        assertThat(messages[0].getContent().toString()).isEqualTo("The content of the message.");
    }

}
