package uk.co.alumeni.prism.services;

import com.google.common.base.Objects;
import com.google.common.collect.LinkedHashMultimap;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.alumeni.prism.dao.MessageDAO;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.message.Message;
import uk.co.alumeni.prism.domain.message.MessageDocument;
import uk.co.alumeni.prism.domain.message.MessageRecipient;
import uk.co.alumeni.prism.domain.message.MessageThread;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.domain.workflow.Role;
import uk.co.alumeni.prism.exceptions.PrismForbiddenException;
import uk.co.alumeni.prism.rest.dto.DocumentDTO;
import uk.co.alumeni.prism.rest.dto.MessageDTO;
import uk.co.alumeni.prism.rest.dto.user.UserEmailDTO;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newLinkedList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.joda.time.DateTime.now;

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

    public List<MessageThread> getMessageThreads(Resource resource, User user) {
        return newLinkedList(messageDAO.getMessageThreads(resource, user).stream().map(mt -> mt.getThread()).collect(toList()));
    }

    public LinkedHashMultimap<MessageThread, Message> getMessages(Collection<MessageThread> threads, User user) {
        LinkedHashMultimap<MessageThread, Message> messages = LinkedHashMultimap.create();
        messageDAO.getMessages(threads, user).stream().forEach(m -> messages.put(m.getThread(), m));
        return messages;
    }

    public LinkedHashMultimap<Message, User> getMessageRecipients(Collection<Message> messages) {
        LinkedHashMultimap<Message, User> recipients = LinkedHashMultimap.create();
        messageDAO.getMessageRecipients(messages).stream().forEach(r -> recipients.put(r.getMessage(), r.getUser()));
        return recipients;
    }

    public LinkedHashMultimap<Message, Document> getMessageDocuments(Collection<Message> messages) {
        LinkedHashMultimap<Message, Document> documents = LinkedHashMultimap.create();
        messageDAO.getMessageDocuments(messages).stream().forEach(r -> documents.put(r.getMessage(), r.getDocument()));
        return documents;
    }

    public void postMessage(Resource resource, Integer threadId, MessageDTO messageDTO) {
        DateTime baseline = now();
        User user = userService.getCurrentUser();
        Action messageAction = actionService.getMessageAction(resource);

        MessageThread thread = null;
        if (threadId == null) {
            thread = new MessageThread().withSubject(messageDTO.getSubject());
            entityService.save(thread);

            Comment comment = new Comment().withResource(resource).withUser(user).withAction(messageAction).withThread(thread).withDeclinedResponse(false)
                    .withCreatedTimestamp(baseline);
            actionService.executeUserAction(resource, messageAction, comment);
            thread.setComment(comment);
        } else if (actionService.checkActionAvailable(resource, messageAction, user)) {
            thread = getMessageThreadById(threadId);
        } else {
            Action viewEditAction = actionService.getViewEditAction(resource);
            if (actionService.checkActionAvailable(resource, viewEditAction, user)) {
                thread = getMessageThreadById(threadId);
                if (messageDAO.getMessages(thread, user).size() == 0) {
                    return;
                }
            }
        }

        Message message = new Message().withUser(user).withThread(thread).withContent(messageDTO.getContent()).withCreatedTimestamp(baseline);
        entityService.save(message);
        thread.addMessage(message);

        MessageRecipient sender = new MessageRecipient().withMessage(message).withUser(user).withSendTimestamp(baseline).withViewTimestamp(baseline);
        entityService.save(sender);
        message.addRecipient(sender);

        List<UserEmailDTO> recipientUsers = messageDTO.getRecipientUsers();
        if (isNotEmpty(recipientUsers)) {
            for (UserEmailDTO userDTO : recipientUsers) {
                MessageRecipient recipient = new MessageRecipient().withMessage(message).withUser(userService.getUserByEmail(userDTO.getEmail()));
                entityService.getOrCreate(recipient);
                message.addRecipient(recipient);
            }
        }

        List<PrismRole> recipientRoles = messageDTO.getRecipientRoles();
        if (isNotEmpty(recipientRoles)) {
            for (PrismRole role : messageDTO.getRecipientRoles()) {
                MessageRecipient recipient = new MessageRecipient().withMessage(message).withRole(roleService.getById(role));
                entityService.getOrCreate(recipient);
                message.addRecipient(recipient);
            }
        }

        List<DocumentDTO> documents = messageDTO.getDocuments();
        if (isNotEmpty(documents)) {
            if (messageDTO.getDocuments() != null) {
                for (DocumentDTO documentDTO : messageDTO.getDocuments()) {
                    MessageDocument document = new MessageDocument().withMessage(message).withDocument(documentService.getById(documentDTO.getId()));
                    entityService.getOrCreate(document);
                    message.addDocument(document);
                }
            }
        }
    }

    public void viewMessage(Integer recipientId) {
        MessageRecipient recipient = getMessageRecipientById(recipientId);
        if (Objects.equal(userService.getCurrentUser(), recipient.getUser())) {
            recipient.setViewTimestamp(now());
        }
    }

    public User validateViewMessages(Resource resource) {
        User user = userService.getCurrentUser();
        Action messageAction = actionService.getMessageAction(resource);
        if (messageAction == null || !actionService.checkActionAvailable(resource, messageAction, user)) {
            Action viewEditAction = actionService.getViewEditAction(resource);
            if (viewEditAction == null || !actionService.checkActionAvailable(resource, viewEditAction, user)
                    || messageDAO.getMessageThreads(resource, user).size() == 0) {
                throw new PrismForbiddenException("User cannot view or edit messages for the given resource");
            }
        }
        return user;
    }

}
