package uk.co.alumeni.prism.services.delegates;

import com.google.common.collect.Sets;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import uk.co.alumeni.prism.event.NotificationEvent;
import uk.co.alumeni.prism.mail.MailSender;

import javax.inject.Inject;
import java.util.Set;

@Service
public class NotificationServiceDelegate {

    Set<NotificationEvent> notificationEvents = Sets.newConcurrentHashSet();

    @Inject
    private ApplicationContext applicationContext;

    @Async
    @TransactionalEventListener
    public void sendNotification(NotificationEvent notificationEvent) {
        if (!notificationEvents.contains(notificationEvent)) {
            notificationEvents.add(notificationEvent);
            applicationContext.getBean(MailSender.class).sendEmail(notificationEvent);
            notificationEvents.remove(notificationEvent);
        }
    }

}
