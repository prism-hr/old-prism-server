package uk.co.alumeni.prism.services.delegates;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang.BooleanUtils.isTrue;

import java.util.Set;

import javax.inject.Inject;

import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition;
import uk.co.alumeni.prism.event.NotificationEvent;
import uk.co.alumeni.prism.mail.MailSender;

@Service
public class NotificationServiceDelegate {

    private Set<NotificationEvent> executions = newHashSet();

    @Inject
    private ApplicationContext applicationContext;

    @Async
    @TransactionalEventListener
    public synchronized void sendNotification(NotificationEvent notificationEvent) {
        if (isTrue(notificationEvent.getSent())) {
            executions.remove(notificationEvent);
        } else if (!executions.contains(notificationEvent)) {
            executions.add(notificationEvent);
            applicationContext.getBean(MailSender.class).sendEmail(notificationEvent);
        }
    }

    public synchronized Set<PrismNotificationDefinition> getExecutionBatches() {
        return executions.stream().map(execution -> execution.getNotificationDefinition()).collect(toSet());
    }

}
