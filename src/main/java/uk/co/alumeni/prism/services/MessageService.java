package uk.co.alumeni.prism.services;

import static com.google.common.collect.Lists.newLinkedList;
import static java.util.stream.Collectors.toList;
import static org.joda.time.DateTime.now;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.dao.MessageDAO;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.message.Message;
import uk.co.alumeni.prism.domain.message.MessageDocument;
import uk.co.alumeni.prism.domain.message.MessageRecipient;
import uk.co.alumeni.prism.domain.message.MessageThread;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.domain.workflow.Role;
import uk.co.alumeni.prism.rest.dto.MessageDTO;

import com.google.common.base.Objects;
import com.google.common.collect.LinkedHashMultimap;

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

    public void postMessage(Resource resource, MessageDTO messageDTO) {
        DateTime baseline = now();

        User user = userService.getCurrentUser();
        Action action = actionService.getMessageAction(resource);

        MessageThread thread = null;
        Integer messageThreadId = messageDTO.getId();
        if (messageThreadId == null) {
            thread = new MessageThread().withSubject(messageDTO.getSubject());
            entityService.save(thread);

            Comment comment = new Comment().withResource(resource).withUser(user).withAction(action).withThread(thread).withDeclinedResponse(false)
                    .withCreatedTimestamp(baseline);
            actionService.executeUserAction(resource, action, comment);
            thread.setComment(comment);
        } else if (actionService.checkActionExecutable(resource, action, user)) {
            thread = getMessageThreadById(messageThreadId);
        }

        if (thread != null) {
            Message message = new Message().withUser(user).withThread(thread).withContent(messageDTO.getContent()).withCreatedTimestamp(baseline);
            entityService.save(message);
            thread.addMessage(message);

            MessageRecipient sender = new MessageRecipient().withMessage(message).withUser(user).withSendTimestamp(baseline).withViewTimestamp(baseline);
            entityService.save(sender);
            message.addRecipient(sender);

            messageDTO.getRecipientUsers().forEach(userDTO -> {
                MessageRecipient recipient = new MessageRecipient().withMessage(message).withUser(userService.getUserByEmail(userDTO.getEmail()));
                entityService.getOrCreate(recipient);
                message.addRecipient(recipient);
            });

            messageDTO.getRecipientRoles().forEach(roleDTO -> {
                MessageRecipient recipient = new MessageRecipient().withMessage(message).withRole(roleService.getById(roleDTO));
                entityService.getOrCreate(recipient);
                message.addRecipient(recipient);
            });

            messageDTO.getDocuments().forEach(documentDTO -> {
                MessageDocument document = new MessageDocument().withMessage(message).withDocument(documentService.getById(documentDTO.getId()));
                entityService.getOrCreate(document);
                message.addDocument(document);
            });
        }
    }

    public void viewMessage(Integer recipientId) {
        MessageRecipient recipient = getMessageRecipientById(recipientId);
        if (Objects.equal(userService.getCurrentUser(), recipient.getUser())) {
            recipient.setViewTimestamp(now());
        }
    }

}
