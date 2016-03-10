package uk.co.alumeni.prism.mail;

import java.util.List;

import uk.co.alumeni.prism.dto.MailMessageDTO;

import com.google.common.collect.Lists;

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
