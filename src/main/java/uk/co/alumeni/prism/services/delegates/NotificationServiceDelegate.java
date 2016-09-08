package uk.co.alumeni.prism.services.delegates;

import javax.inject.Inject;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import uk.co.alumeni.prism.event.NotificationEvent;
import uk.co.alumeni.prism.mail.MailSender;

@Service
public class NotificationServiceDelegate {

    @Inject
    private ApplicationContext applicationContext;

    @TransactionalEventListener
    public synchronized void sendNotification(NotificationEvent notificationEvent) {
        applicationContext.getBean(MailSender.class).sendEmail(notificationEvent);
    }

}
