/*
 * $Id: MailUtils.java 3141 2012-01-21 13:02:42Z andrewinkler $
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
 */

package de.awtools.mail;

import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

/**
 * Utility to get the mail text content. This is a simplification and does not
 * respect other content types than 'text/plain'.
 * 
 * @version $LastChangedRevision: 3141 $ $LastChangedDate: 2010-09-12 15:04:04
 *          +0200 (So, 12 Sep 2010) $
 * @author by Andre Winkler, $LastChangedBy: andrewinkler $
 */
public class MailUtils {

	/** A text formatted message. */
	public static final String TEXT_PLAIN = "text/plain";

	/**
	 * Extrahiert aus einer Mail-Message den Mail-Body.
	 * 
	 * @param msg
	 *            Eine Message.
	 * @return Der Mail-Body der Mail-Message.
	 * @throws MessagingException
	 *             Siehe Exception Beschreibung.
	 * @throws IOException
	 *             Siehe Exception Beschreibung.
	 */
	public static String getBody(Message msg) throws MessagingException,
			IOException {

		String contentType = msg.getContentType();
		String mailMessage = null;

		if (isTextPlain(contentType)) {
			mailMessage = (String) msg.getContent();
		} else if (isMultipartMessage(msg)) {
			StringBuffer mailText = new StringBuffer();
			Multipart mp = (Multipart) msg.getContent();
			BodyPart bp;
			for (int i = 0; i < mp.getCount(); i++) {
				bp = mp.getBodyPart(i);
				contentType = bp.getContentType();
				if (isTextPlain(contentType)) {
					mailText.append((String) bp.getContent());
				}
			}
			mailMessage = mailText.toString();
		} else {
			throwIOException();
		}

		return mailMessage;
	}

	private static void throwIOException() throws IOException {
		throw new IOException(
				"Content type not supported. It is not text/plain"
						+ " and not a multipart mail body type.");
	}

	/**
	 * It this a multiple part message?
	 * 
	 * @param msg
	 *            The message.
	 * @return Returns <code>true</code>, if this is a multiple part message.
	 * @throws IOException
	 *             Something goes wrong.
	 * @throws MessagingException
	 *             Other things are also very bad.
	 */
	private static boolean isMultipartMessage(Message msg) throws IOException,
			MessagingException {

		return (msg.getContent() instanceof Multipart);
	}

	/**
	 * Check content type. Returns <code>true</code>, if content type is of
	 * text/plain.
	 * 
	 * @param contentType
	 *            The content type of the mail.
	 * @return <code>true</code> if mail has type <code>text/plain</code>.
	 */
	public static boolean isTextPlain(String contentType) {
		boolean isPlainText = false;
		if (contentType == null) {
			isPlainText = false;
		} else if (contentType.length() < TEXT_PLAIN.length()) {
			isPlainText = false;
		} else {
			isPlainText = contentType.substring(0, 10).equals(TEXT_PLAIN);
		}
		return isPlainText;
	}

}
