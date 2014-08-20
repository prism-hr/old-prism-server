package com.zuehlke.pgadmissions.mail;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.utils.HibernateUtils;

public class MailSenderMock extends MailSender {

    private List<MailMessageDTO> sentMessages = Lists.newLinkedList();

    public MailMessageDTO assertEmailSent(User recipient, PrismNotificationTemplate templateId) {
        for (MailMessageDTO message : sentMessages) {
            if (HibernateUtils.sameEntities(recipient, message.getTo().get(0)) && templateId == message.getTemplate().getNotificationTemplate().getId()) {
                sentMessages.remove(message);
                return message;
            }
        }
        throw new AssertionError("Expected message with template " + templateId + " and recipient " + recipient + " has not been sent");
    }

    public void verify() {
        if (!sentMessages.isEmpty()) {
            StringBuilder sb = new StringBuilder("Unexpected messages sent: ");
            for (MailMessageDTO message : sentMessages) {
                sb.append("Template: " + message.getTemplate() + ", recipient:" + message.getTo() + "; ");
            }
            throw new AssertionError(sb);
        }
    }

}
