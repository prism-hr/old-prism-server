package uk.co.alumeni.prism.services;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.dao.MessageDAO;
import uk.co.alumeni.prism.domain.message.Message;
import uk.co.alumeni.prism.domain.message.MessageRecipient;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Role;

@Service
@Transactional
public class MessageService {

    @Inject
    private MessageDAO messageDAO;

    @Inject
    private EntityService entityService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private UserService userService;

    public MessageRecipient getMessageRecipient(Integer messageRecipientId) {
        return entityService.getById(MessageRecipient.class, messageRecipientId);
    }

    public List<Integer> getMessagesRecipientsPendingAllocation() {
        return messageDAO.getMessagesRecipientsPendingAllocation();
    }

    public void allocateMessageRecipients(Integer messageRecipientId) {
        MessageRecipient messageRecipient = getMessageRecipient(messageRecipientId);

        Role role = messageRecipient.getRole();
        Message message = messageRecipient.getMessage();
        List<User> users = userService.getUsersWithRoles(message.getThread().getComment().getResource(), role.getId());
        if (users.size() > 0) {
            users.forEach(user -> entityService.getOrCreate(new MessageRecipient().withMessage(message).withUser(user).withRole(role)));
            entityService.delete(messageRecipient);
        }
    }

    public List<Integer> getMessagesRecipientsPendingNotification() {
        return messageDAO.getMessagesRecipientsPendingNotification();
    }

    public void notifyMessageRecipients(Integer messageRecipientId, DateTime baseline) {
        MessageRecipient messageRecipient = getMessageRecipient(messageRecipientId);
        notificationService.sendMessageNotification(messageRecipient);
        messageRecipient.setSendTimestamp(baseline);
    }

    public void setMessageThreadViewed(Integer thread, Integer message) {
        messageDAO.setMessageThreadViewed(thread, message);
    }

}
