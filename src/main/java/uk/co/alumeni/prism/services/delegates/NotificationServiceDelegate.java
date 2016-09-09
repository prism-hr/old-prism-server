package uk.co.alumeni.prism.services.delegates;

import java.util.Set;

import javax.inject.Inject;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import com.google.common.collect.Sets;

import uk.co.alumeni.prism.event.NotificationEvent;
import uk.co.alumeni.prism.mail.MailSender;

@Service
public class NotificationServiceDelegate {

    Set<NotificationEvent> notificationEvents = Sets.newConcurrentHashSet();

    @Inject
    private ApplicationContext applicationContext;

    @TransactionalEventListener
    public void sendNotification(NotificationEvent notificationEvent) {
        if (!notificationEvents.contains(notificationEvent)) {
            notificationEvents.add(notificationEvent);
            applicationContext.getBean(MailSender.class).sendEmail(notificationEvent);
            notificationEvents.remove(notificationEvent);
        }
    }

}
