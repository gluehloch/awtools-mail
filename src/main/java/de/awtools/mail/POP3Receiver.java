/*
 * ============================================================================
 * Project awtools-mail
 * Copyright (c) 2004-2021 by Andre Winkler. All rights reserved.
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

import java.util.List;

import jakarta.mail.Message;
import jakarta.mail.Session;

/**
 * Read mails from a POP3 account.
 * 
 * @author Andre Winkler
 */
public interface POP3Receiver {

    /**
     * Startet den 'Download' der Mails aus dem konfigurierten POP3 Account.
     *
     * @param session Mail-Session
     * @param host Host
     * @param port Port
     * @param user Username
     * @param password Password     *
     */
    void download(Session session, String host, int port, String user, String password);

    /**
     * Liefert alle Mails zurück.
     * 
     * @return Ein Array mit Mails.
     */
    Message[] messagesToArray();

    /**
     * Liefert alle Mails zurück.
     *
     * @return Eine Liste mit Mails.
     */
    List<Message> messagesToList();

}
