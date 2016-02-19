package uk.co.alumeni.prism.services;

import static org.joda.time.DateTime.now;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.dao.MessageDAO;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.message.Message;
import uk.co.alumeni.prism.domain.message.MessageDocument;
import uk.co.alumeni.prism.domain.message.MessageRecipient;
import uk.co.alumeni.prism.domain.message.MessageThread;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.domain.workflow.Role;
import uk.co.alumeni.prism.rest.dto.resource.ResourceDTO;

@Service
@Transactional
public class MessageService {

    @Inject
    private MessageDAO messageDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private DocumentService documentService;

    @Inject
    private EntityService entityService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    public MessageThread getMessageThreadById(Integer messageThreadId) {
        return entityService.getById(MessageThread.class, messageThreadId);
    }

    public MessageRecipient getMessageRecipientById(Integer messageRecipientId) {
        return entityService.getById(MessageRecipient.class, messageRecipientId);
    }

    public List<Integer> getMessageRecipientsPendingAllocation() {
        return messageDAO.getMessageRecipientsPendingAllocation();
    }

    public void allocateMessageRecipients(Integer messageRecipientId) {
        MessageRecipient messageRecipient = getMessageRecipientById(messageRecipientId);

        Role role = messageRecipient.getRole();
        Message message = messageRecipient.getMessage();
        List<User> users = userService.getUsersWithRoles(message.getThread().getComment().getResource(), role.getId());
        if (users.size() > 0) {
            users.forEach(user -> entityService.getOrCreate(new MessageRecipient().withMessage(message).withUser(user)));
        }
    }

    public List<Integer> getMessagesRecipientsPendingNotification() {
        return messageDAO.getMessageRecipientsPendingNotification();
    }

    public void notifyMessageRecipients(Integer messageRecipientId, DateTime baseline) {
        MessageRecipient messageRecipient = getMessageRecipientById(messageRecipientId);
        notificationService.sendMessageNotification(messageRecipient);
        messageRecipient.setSendTimestamp(baseline);
    }

    public void createOrUpdateMessageThread(MessageThreadDTO threadDTO) {
        ResourceDTO resourceDTO = threadDTO.getResource();
        Resource resource = resourceService.getById(resourceDTO.getScope(), resourceDTO.getId());

        User user = userService.getCurrentUser();
        Action action = actionService.getMessageAction(resource);

        DateTime baseline = now();

        MessageThread thread = null;
        Integer messageThreadId = threadDTO.getId();
        if (messageThreadId == null) {
            thread = new MessageThread().withSubject(threadDTO.getSubject());
            entityService.save(thread);

            Comment comment = new Comment().withResource(resource).withUser(user).withAction(action).withThread(thread).withDeclinedResponse(false)
                    .withCreatedTimestamp(baseline);
            actionService.executeUserAction(resource, action, comment);
            thread.setComment(comment);
        } else if (actionService.checkActionExecutable(resource, action, user)) {
            thread = getMessageThreadById(messageThreadId);
        }

        if (thread != null) {
            MessageDTO messageDTO = threadDTO.getMessage();
            Message message = new Message().withUser(user).withThread(thread).withContent(messageDTO.getContent()).withCreatedTimestamp(baseline);
            entityService.save(message);
            thread.addMessage(message);

            messageDTO.getRecipientUsers().forEach(userDTO -> {
                MessageRecipient recipient = new MessageRecipient().withMessage(message).withUser(userService.getUserByEmail(userDTO.getEmail()))
                        .withSendTimestamp(baseline).withViewTimestamp(baseline);
                entityService.save(recipient);
                message.addRecipient(recipient);
            });

            messageDTO.getRecipientRoles().forEach(roleDTO -> {
                MessageRecipient recipient = new MessageRecipient().withMessage(message).withRole(roleService.getById(roleDTO))
                        .withSendTimestamp(baseline).withViewTimestamp(baseline);
                entityService.save(recipient);
                message.addRecipient(recipient);
            });

            messageDTO.getDocuments().forEach(documentDTO -> {
                MessageDocument document = new MessageDocument().withMessage(message).withDocument(documentService.getById(documentDTO.getId()));
                entityService.save(document);
                message.addDocument(document);
            });

            viewMessageThread(thread.getId(), message.getId());
        }
    }

    public void viewMessageThread(Integer thread, Integer message) {
        messageDAO.viewMessageThread(thread, message);
    }

}
