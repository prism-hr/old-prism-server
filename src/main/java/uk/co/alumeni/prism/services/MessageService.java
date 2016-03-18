package uk.co.alumeni.prism.services;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
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
import uk.co.alumeni.prism.domain.message.MessageNotification;
import uk.co.alumeni.prism.domain.message.MessageThread;
import uk.co.alumeni.prism.domain.message.MessageThreadParticipant;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.exceptions.PrismForbiddenException;
import uk.co.alumeni.prism.rest.dto.DocumentDTO;
import uk.co.alumeni.prism.rest.dto.MessageDTO;
import uk.co.alumeni.prism.rest.dto.user.UserDTO;

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
    private UserService userService;

    public MessageThread getMessageThreadById(Integer messageThreadId) {
        return entityService.getById(MessageThread.class, messageThreadId);
    }

    public Message getMessageById(Integer messageId) {
        return entityService.getById(Message.class, messageId);
    }

    public MessageNotification getMessageNotificationById(Integer messageNotificationId) {
        return entityService.getById(MessageNotification.class, messageNotificationId);
    }

    public List<Integer> getMessagesNotificationsPending() {
        return messageDAO.getMessageNotificationsPending();
    }

    public void sendMessageNotification(Integer messageRecipientId, DateTime baseline) {
        MessageNotification messageNotification = getMessageNotificationById(messageRecipientId);
        notificationService.sendMessageNotification(messageNotification);
        entityService.delete(messageNotification);
    }

    public List<MessageThread> getMessageThreads(Resource resource, User user, String searchTerm) {
        List<MessageThread> threads = newLinkedList(messageDAO.getMessageThreads(resource, user).stream().map(t -> t.getThread()).collect(toList()));
        if (threads.size() > 0 && isNotBlank(searchTerm)) {
            threads = newLinkedList(messageDAO.getMatchingMessageThreads(threads, searchTerm).stream().map(t -> t.getThread()).collect(toList()));
        }
        return threads;
    }

    public LinkedHashMultimap<MessageThread, Message> getMessages(Collection<MessageThread> threads, User user, String searchTerm) {
        List<Message> messages = messageDAO.getMessages(threads, user);
        if (messages.size() > 0 && isNotBlank(searchTerm)) {
            messages = messageDAO.getMatchingMessages(messages, searchTerm);
        }
        LinkedHashMultimap<MessageThread, Message> messagesMap = LinkedHashMultimap.create();
        messages.stream().forEach(m -> messagesMap.put(m.getThread(), m));
        return messagesMap;
    }

    public LinkedHashMultimap<MessageThread, MessageThreadParticipant> getMessageThreadParticipants(Collection<MessageThread> threads) {
        LinkedHashMultimap<MessageThread, MessageThreadParticipant> recipients = LinkedHashMultimap.create();
        messageDAO.getMessageThreadParticipants(threads).stream().forEach(mtr -> recipients.put(mtr.getThread(), mtr));
        return recipients;
    }

    public LinkedHashMultimap<Message, Document> getMessageDocuments(Collection<Message> messages) {
        LinkedHashMultimap<Message, Document> documents = LinkedHashMultimap.create();
        messageDAO.getMessageDocuments(messages).stream().forEach(md -> documents.put(md.getMessage(), md.getDocument()));
        return documents;
    }

    public void createMessage(Resource resource, Integer threadId, MessageDTO messageDTO) {
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

        List<Integer> userIds = newArrayList();
        MessageThreadParticipant sender = entityService.createOrUpdate(new MessageThreadParticipant().withThread(thread).withUser(user)
                .withStartMessage(message).withLastViewedMessage(message));
        thread.addParticipant(sender);
        userIds.add(user.getId());

        List<UserDTO> userDTOs = messageDTO.getRecipientUsers();
        for (UserDTO userDTO : userDTOs) {
            User participantUser = userService.getById(userDTO.getId());
            if (!participantUser.equals(user)) {
                MessageThreadParticipant participant = messageDAO.getMessageThreadParticipant(participantUser, message.getId());
                if (participant.getCloseMessage() != null) {
                    participant = entityService.getOrCreate(new MessageThreadParticipant().withThread(thread)
                            .withUser(participantUser).withStartMessage(message).withLastViewedMessage(participant.getLastViewedMessage()));
                    thread.addParticipant(participant);
                    userIds.add(participantUser.getId());
                }

                MessageNotification notification = new MessageNotification().withMessage(message).withUser(participantUser);
                entityService.getOrCreate(notification);
                message.addNotification(notification);
            }
        }

        messageDAO.closeMessageThreadParticipants(thread, message, userIds);

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

    public void viewMessageThread(Integer latestUnreadMessageId) {
        MessageThreadParticipant participant = messageDAO.getMessageThreadParticipant(userService.getCurrentUser(), latestUnreadMessageId);
        if (participant != null) {
            participant.setLastViewedMessage(getMessageById(latestUnreadMessageId));
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
