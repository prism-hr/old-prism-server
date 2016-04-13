package uk.co.alumeni.prism.services.delegates;

import javax.inject.Inject;

import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.NotificationConfiguration;
import uk.co.alumeni.prism.domain.workflow.NotificationDefinition;
import uk.co.alumeni.prism.dto.MailMessageDTO;
import uk.co.alumeni.prism.dto.NotificationDefinitionDTO;
import uk.co.alumeni.prism.event.NotificationEvent;
import uk.co.alumeni.prism.mail.MailSender;
import uk.co.alumeni.prism.services.NotificationService;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;

@Service
public class NotificationServiceDelegate {

    @Inject
    private NotificationService notificationService;

    @Inject
    private ApplicationContext applicationContext;

    @Async
    @TransactionalEventListener
    private void sendNotification(NotificationEvent notificationEvent) {
        NotificationDefinition definition = notificationEvent.getNotificationDefinition();
        NotificationDefinitionDTO definitionDTO = notificationEvent.getNotificationDefinitionDTO();

        User user = definitionDTO.getRecipient();
        NotificationConfiguration configuration = notificationService.getNotificationConfiguration(definitionDTO.getResource(), user, definition);
        MailMessageDTO message = new MailMessageDTO();

        message.setNotificationConfiguration(configuration);
        message.setNotificationDefinitionDTO(definitionDTO);

        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(definitionDTO.getResource());
        applicationContext.getBean(MailSender.class).localize(propertyLoader).sendEmail(message);
    }

}
