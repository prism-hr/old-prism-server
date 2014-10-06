package com.zuehlke.pgadmissions.mail;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.dto.MailMessageDTO;
import com.zuehlke.pgadmissions.utils.HibernateUtils;

public class MailSenderMock extends MailSender {

    private List<MailMessageDTO> sentMessages = Lists.newLinkedList();

    public void sendEmail(final MailMessageDTO message) {
        sentMessages.add(message);
        super.sendEmail(message);
    }

    public MailMessageDTO assertEmailSent(User recipient, PrismNotificationTemplate templateId) {
        for (MailMessageDTO message : sentMessages) {
            if (HibernateUtils.sameEntities(recipient, message.getTo())
                    && templateId == message.getTemplate().getNotificationConfiguration().getNotificationTemplate().getId()) {
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
                sb.append("Template: ").append(message.getTemplate()).append(", recipient:").append(message.getTo()).append("; ");
            }
            throw new AssertionError(sb);
        }
    }

}
