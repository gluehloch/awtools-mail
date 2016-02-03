/*
 * $Id: POP3Receiver.java 2558 2010-09-12 13:04:04Z andrewinkler $
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

import java.beans.PropertyChangeListener;
import java.util.List;

import javax.mail.Message;

import org.apache.commons.lang.enums.Enum;

/**
 * Read mails from a POP3 account.
 * 
 * @version $LastChangedRevision: 2558 $ $LastChangedDate: 2010-09-12 15:04:04 +0200 (So, 12 Sep 2010) $
 * @author by Andre Winkler, $LastChangedBy: andrewinkler $
 */
public interface POP3Receiver {

    /** Der Name der Eigenschaft 'state'. */
    public static final String PROPERTY_STATE = "state";

    /** Der Name der Eigenschaft 'mailNum'. Anzahl der Mails. */
    public static final String PROPERTY_MAILNUM = "mailNum";

    /**
     * Der Name der Eigenschaft 'mailCounter'. Zählt die mails (0 .. mainNum-1).
     */
    public static final String PROPERTY_COUNTER = "mailCounter";

    /**
     * Bildet die verschiedenen Zustände ab, die während des Mailempfangs
     * auftreten können.
     */
    public static class ReceiverState extends Enum {

        /** serial version id */
        private static final long serialVersionUID = 6706344924026475104L;

        /** Initialisierungszustand. */
        public static final ReceiverState RECEIVERSTATE_INIT =
                new ReceiverState("init");

        /** Versuch Verbindungsaufbau. */
        public static final ReceiverState RECEIVERSTATE_TRY_CONNECT =
                new ReceiverState("try_connect");

        /** Verbindungsaufbau hergestellt. */
        public static final ReceiverState RECEIVERSTATE_CONNECTED =
                new ReceiverState("connected");

        /** Vorgang abgebrochen. */
        public static final ReceiverState RECEIVERSTATE_FAILED =
                new ReceiverState("failed");

        /** Vorgang beendet. Keine Mails vorhanden. */
        public static final ReceiverState RECEIVERSTATE_NO_MAILS =
                new ReceiverState("noMails");

        /** Vorgang beendet. Alle Mails abgeholt. */
        public static final ReceiverState RECEIVERSTATE_READY =
                new ReceiverState("mailsReceived");

        private ReceiverState(final String name) {
            super(name);
        }
    };

    /**
     * Startet den 'Download' der Mails aus dem konfigurierten POP3 Account.
     */
    public void download();

    /**
     * Liefert alle Mails zurück.
     * 
     * @return Ein Array mit Mails.
     */
    public Message[] messagesToArray();

    /**
     * Liefert alle Mails zurück.
     *
     * @return Eine Liste mit Mails.
     */
    public List<Message> messagesToList();

    /**
     * Add a PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Remove a PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);

}
