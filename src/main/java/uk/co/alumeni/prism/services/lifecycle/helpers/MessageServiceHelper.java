package uk.co.alumeni.prism.services.lifecycle.helpers;

import org.springframework.stereotype.Service;
import uk.co.alumeni.prism.services.MessageService;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.joda.time.DateTime.now;

@Service
public class MessageServiceHelper extends PrismServiceHelperAbstract {

    @Inject
    private MessageService messageService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() {
        messageService.getMessagesNotificationsPending().forEach(messageNotification -> sendMessageNotification(messageNotification));
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void sendMessageNotification(Integer messageRecipient) {
        if (!isShuttingDown()) {
            messageService.sendMessageNotification(messageRecipient, now());
        }
    }

}
