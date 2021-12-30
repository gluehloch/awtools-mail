/*
 * ============================================================================
 * Project awtools-mail
 * Copyright (c) 2004-2018 by Andre Winkler. All rights reserved.
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
 */

package de.awtools.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.*;

import org.junit.Test;

/**
 * Utility für die Mail Bearbeitung.
 *
 * @author by Andre Winkler
 */
public class MailUtilsTest {

    private static final String HALLO_ANDRE = "Hallo Andre!";

    @Test
    public void testMailUtilsGetMessageForPlainText() throws Exception {
        Message message = mock(Message.class);
        when(message.getContentType()).thenReturn(MailUtils.TEXT_PLAIN);
        when(message.getContent()).thenReturn(HALLO_ANDRE);

        String body = MailUtils.getBody(message);
        verify(message).getContentType();
        verify(message).getContent();

        assertThat(message.getContent()).isEqualTo(HALLO_ANDRE);
        assertThat(body).isEqualTo(HALLO_ANDRE);
    }

    @Test
    public void testMailUtilsGetMessageForMultipart() throws Exception {
        Message message = mock(Message.class);
        Multipart multipart = mock(Multipart.class);
        BodyPart bodyPart = mock(BodyPart.class);

        when(message.getContentType()).thenReturn("multipart"); // Oh. I don´t know!
        when(message.getContent()).thenReturn(multipart);

        when(bodyPart.getContentType()).thenReturn(MailUtils.TEXT_PLAIN);
        when(bodyPart.getContent()).thenReturn(HALLO_ANDRE);

        when(multipart.getContentType()).thenReturn(MailUtils.TEXT_PLAIN);
        when(multipart.getCount()).thenReturn(1);
        when(multipart.getBodyPart(0)).thenReturn(bodyPart);

        String body = MailUtils.getBody(message);
        assertThat(body).isEqualTo(HALLO_ANDRE);
    }

    @Test
    public void testMailUtilsIsTextPlain() {
        assertThat(MailUtils.isTextPlain("text/plain")).isTrue();
        assertThat(MailUtils.isTextPlain("text/design")).isFalse();
        assertThat(MailUtils.isTextPlain(null)).isFalse();
        assertThat(MailUtils.isTextPlain("irgendwas")).isFalse();
    }

    @Test
    public void testStringFormat() {
        assertThat(String.format("Hallo %s", "Andre"))
                .isEqualTo("Hallo Andre");
        assertThat(String.format("Hallo %d", 4711)).isEqualTo("Hallo 4711");
        assertThat(String.format("Hallo %d.", 4711)).isEqualTo("Hallo 4711.");
    }

}
