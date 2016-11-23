package uk.co.alumeni.prism.mail;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.NotificationConfiguration;
import uk.co.alumeni.prism.domain.workflow.NotificationDefinition;
import uk.co.alumeni.prism.event.NotificationEvent;
import uk.co.alumeni.prism.services.NotificationService;

import java.util.List;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Service
@Transactional
@Scope(SCOPE_PROTOTYPE)
public class MailSenderMock extends MailSender {

    private List<NotificationConfiguration> sentMessages = Lists.newLinkedList();

    public void sendEmail(NotificationEvent notificationEvent) {
        NotificationService notificationService = getNotificationService();
        PrismNotificationDefinition prismNotificationDefinition = notificationEvent.getNotificationDefinition();
        NotificationDefinition notificationDefinition = notificationService.getById(prismNotificationDefinition);

        User recipient = getUser(notificationEvent.getRecipient());
        Resource resource = getResource(notificationEvent.getResource());

        sentMessages.add(notificationService.getNotificationConfiguration(resource, recipient, notificationDefinition));
    }

    public List<NotificationConfiguration> getSentMessages() {
        return sentMessages;
    }

    public void setSentMessages(List<NotificationConfiguration> sentMessages) {
        this.sentMessages = sentMessages;
    }

}
