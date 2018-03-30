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

/**
 * Bildet die verschiedenen Zustände ab, die während des Mailempfangs
 * auftreten können.
 * 
 * @author Andre Winkler
 */
public enum ReceiverState {

    RECEIVERSTATE_INIT("init"),
    RECEIVERSTATE_TRY_CONNECT("try_connect"),
    RECEIVERSTATE_CONNECTED("connected"),
    RECEIVERSTATE_FAILED("failed"),
    RECEIVERSTATE_NO_MAILS("noMails"),
    RECEIVERSTATE_READY("mailsReceived");

    private final String state;

    private ReceiverState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

};
