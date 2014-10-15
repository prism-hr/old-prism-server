package com.zuehlke.pgadmissions.mail;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.MailMessageDTO;
import com.zuehlke.pgadmissions.utils.HibernateUtils;

public class MailSenderMock extends MailSender {

    private List<MailMessageDTO> sentMessages = Lists.newLinkedList();

    public void sendEmail(final MailMessageDTO message) {
        sentMessages.add(message);
    }

    public MailMessageDTO assertEmailSent(User recipient, PrismNotificationTemplate templateId) {
        for (MailMessageDTO message : sentMessages) {
            if (HibernateUtils.sameEntities(recipient, message.getModelDTO().getUser())
                    && templateId == message.getConfiguration().getNotificationTemplate().getId()) {
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
                sb.append("Template: ").append(message.getConfiguration().getNotificationTemplate().getId()).append(", recipient:")
                        .append(message.getModelDTO().getUser()).append("; ");
            }
            throw new AssertionError(sb);
        }
    }

}
