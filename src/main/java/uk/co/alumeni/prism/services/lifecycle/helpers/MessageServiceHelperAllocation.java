package uk.co.alumeni.prism.services.lifecycle.helpers;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.services.MessageService;

@Service
public class MessageServiceHelperAllocation extends PrismServiceHelperAbstract {

    @Inject
    private MessageService messageService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() {
        messageService.getMessageRecipientsPendingAllocation().forEach(messageRecipient -> allocateMessageRecipients(messageRecipient));
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void allocateMessageRecipients(Integer messageRecipient) {
        if (!isShuttingDown()) {
            messageService.allocateMessageRecipients(messageRecipient);
        }
    }

}
