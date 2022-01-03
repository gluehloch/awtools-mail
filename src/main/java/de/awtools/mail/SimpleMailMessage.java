package de.awtools.mail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SimpleMailMessage {

    private final List<String> from = new ArrayList<>();
    private final List<String> to = new ArrayList<>();
    private final List<String> cc = new ArrayList<>();
    private final List<String> bcc = new ArrayList<>();

    private final Date receivedDate;
    private final Date sendDate;
    private final String subject;
    private final String content;

    public SimpleMailMessage(List<String> from, List<String> to, List<String> cc, List<String> bcc, Date receivedDate, Date sendDate, String subject, String content) {
        this.from.addAll(from);
        this.to.addAll(to);
        this.cc.addAll(cc);
        this.bcc.addAll(bcc);
        this.receivedDate = receivedDate;
        this.sendDate = sendDate;
        this.subject = subject;
        this.content = content;
    }

    public List<String> from() {
        return from;
    }

    public List<String> to() {
        return to;
    }

    public List<String> cc() {
        return cc;
    }

    public List<String> bcc() {
        return bcc;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public String subject() {
        return subject;
    }

    public String content() {
        return content;
    }

}
