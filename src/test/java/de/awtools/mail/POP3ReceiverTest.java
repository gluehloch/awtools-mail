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

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;

import org.junit.Before;
import org.junit.Test;

/**
 * Testet die Klasse {@link POP3Receiver}.
 * 
 * @version $LastChangedRevision: 2333 $ $LastChangedDate: 2010-07-31 15:03:33 +0200 (Sa, 31 Jul 2010) $
 * @author by Andre Winkler, $LastChangedBy: andrewinkler $
 */
public class POP3ReceiverTest {

    private Address[] address;

    @Before
    public void setUp() throws Exception {
        address = new Address[] { new InternetAddress("andre.winkler@web.de") };
    }

    @Test
    public void testInternetAddress() throws Exception {
        InternetAddress internetAddress =
                new InternetAddress("andre.winkler@web.de");
        assertThat(internetAddress.getAddress(),
            equalTo("andre.winkler@web.de"));

        Message message = mock(Message.class);
        when(message.getFrom()).thenReturn(address);
        when(message.getSubject()).thenReturn("This is a test.");
        when(message.getSentDate()).thenReturn(new Date());
        when(message.getContentType()).thenReturn("text/plain");
        when(message.getContent()).thenReturn("The content of the message.");

        POP3Receiver receiver = mock(POP3Receiver.class);
        when(receiver.messagesToArray()).thenReturn(
            new Message[] { message, message });
        receiver.download();
        Message[] messages = receiver.messagesToArray();

        verify(receiver).download();
        verify(receiver).messagesToArray();

        assertThat(messages.length, equalTo(2));
        assertThat(messages[0].getFrom(), equalTo(address));
        assertThat(messages[0].getSubject(), equalTo("This is a test."));
        assertThat(messages[0].getContent().toString(),
            equalTo("The content of the message."));
    }

}
