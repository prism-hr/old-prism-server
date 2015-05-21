package com.zuehlke.pgadmissions.mail;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dto.MailMessageDTO;

public class MailSenderMock extends MailSender {

    private List<MailMessageDTO> sentMessages = Lists.newLinkedList();

    public void sendEmail(final MailMessageDTO message) {
        sentMessages.add(message);
    }

    public List<MailMessageDTO> getSentMessages() {
        return sentMessages;
    }

    public void setSentMessages(List<MailMessageDTO> sentMessages) {
        this.sentMessages = sentMessages;
    }

}
