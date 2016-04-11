package uk.co.alumeni.prism.services;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.joda.time.DateTime.now;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.dao.MessageDAO;
import uk.co.alumeni.prism.domain.activity.ActivityEditable;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.message.Message;
import uk.co.alumeni.prism.domain.message.MessageDocument;
import uk.co.alumeni.prism.domain.message.MessageNotification;
import uk.co.alumeni.prism.domain.message.MessageThread;
import uk.co.alumeni.prism.domain.message.MessageThreadParticipant;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAccount;
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

    public List<MessageThread> getMessageThreads(ActivityEditable activity, User user, String searchTerm) {
        List<MessageThread> threads = newLinkedList(messageDAO.getMessageThreads(activity, user).stream().map(t -> t.getThread()).collect(toList()));
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

    public void createMessageThread(ActivityEditable activity, MessageDTO messageDTO) {
        createMessage(activity, null, messageDTO);
    }

    public void createMessage(ActivityEditable activity, Integer threadId, MessageDTO messageDTO) {
        DateTime baseline = now();
        User currentUser = userService.getCurrentUser();

        Resource resource = null;
        MessageThread thread = null;
        if (Resource.class.isAssignableFrom(activity.getClass())) {
            resource = (Resource) activity;
            Action messageAction = actionService.getMessageAction(resource);

            if (threadId == null) {
                Comment comment = new Comment().withResource(resource).withUser(currentUser).withAction(messageAction).withDeclinedResponse(false)
                        .withCreatedTimestamp(baseline);
                actionService.executeUserAction(resource, messageAction, comment);

                thread = new MessageThread().withSubject(messageDTO.getSubject()).withComment(comment).withSearchUser(resource.getUser())
                        .withSearchAdvert(resource.getAdvert()).withSearchResourceCode(resource.getCode());
                entityService.save(thread);
                comment.setThread(thread);
            } else if (actionService.checkActionAvailable(resource, messageAction, currentUser)) {
                thread = getMessageThreadById(threadId);
            } else {
                Action viewEditAction = actionService.getViewEditAction(resource);
                if (actionService.checkActionAvailable(resource, viewEditAction, currentUser)) {
                    thread = getMessageThreadById(threadId);
                }
            }
        } else {
            UserAccount userAccount = (UserAccount) activity;

            if (threadId == null) {
                User user = userAccount.getUser();
                if (userService.checkUserCanViewUserProfile(user, currentUser)) {
                    thread = new MessageThread().withSubject(messageDTO.getSubject()).withSearchUser(user);
                    entityService.save(thread);
                    thread.setUserAccount(userAccount);
                    userAccount.addThread(thread);
                }
            } else {
                thread = getMessageThreadById(threadId);
            }
        }

        if (thread == null || (threadId != null && !checkActiveMessageThreadParticipant(thread, currentUser))) {
            return;
        }

        Message message = new Message().withUser(currentUser).withThread(thread).withContent(messageDTO.getContent()).withCreatedTimestamp(baseline);
        entityService.save(message);
        thread.addMessage(message);

        MessageThreadParticipant sender = getOrCreateMessageThreadParticipant(thread, message, currentUser);
        sender.setLastViewedMessage(message);

        Set<Integer> userIds = newHashSet(currentUser.getId());
        List<UserDTO> userDTOs = messageDTO.getParticipantUsers();
        for (UserDTO userDTO : userDTOs) {
            User participantUser = userService.getById(userDTO.getId());
            if (!participantUser.equals(currentUser)) {
                getOrCreateMessageThreadParticipant(thread, message, participantUser);

                MessageNotification notification = new MessageNotification().withMessage(message).withUser(participantUser);
                entityService.getOrCreate(notification);
                message.addNotification(notification);
            }

            userIds.add(participantUser.getId());
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
        Message latestUnreadMessage = getMessageById(latestUnreadMessageId);
        MessageThreadParticipant participant = messageDAO.getMessageThreadParticipant(latestUnreadMessage.getThread(), userService.getCurrentUser());
        if (participant != null) {
            participant.setLastViewedMessage(latestUnreadMessage);
        }
    }

    public User validateViewMessages(ActivityEditable activity) {
        User user = userService.getCurrentUser();

        if (Resource.class.isAssignableFrom(activity.getClass())) {
            Resource resource = (Resource) activity;
            Action messageAction = actionService.getMessageAction(resource);
            if (messageAction == null || !actionService.checkActionAvailable(resource, messageAction, user)) {
                Action viewEditAction = actionService.getViewEditAction(resource);
                if (viewEditAction == null || !actionService.checkActionAvailable(resource, viewEditAction, user)
                        || messageDAO.getMessageThreads(resource, user).size() == 0) {
                    throw new PrismForbiddenException("User cannot view or edit messages for the given resource");
                }
            }
        } else {
            if (!userService.checkUserCanViewUserProfile(((UserAccount) activity).getUser(), userService.getCurrentUser())
                    && messageDAO.getMessageThreads(activity, user).size() == 0) {
                throw new PrismForbiddenException("User cannot view or edit messages for the given candidate");
            }
        }

        return user;
    }

    public void setMessageThreadSearchUser(Resource resource, User user) {
        messageDAO.setMessageThreadSearchUser(resource, user);
    }

    private boolean checkActiveMessageThreadParticipant(MessageThread thread, User user) {
        return messageDAO.getMessageThreadParticipant(thread, user) != null;
    }

    private MessageThreadParticipant getOrCreateMessageThreadParticipant(MessageThread thread, Message message, User user) {
        MessageThreadParticipant participant = messageDAO.getMessageThreadParticipant(thread, user);
        if (participant == null) {
            participant = new MessageThreadParticipant().withThread(thread).withUser(user).withStartMessage(message)
                    .withLastViewedMessage(messageDAO.getLastViewedMessage(thread, user));
            entityService.save(participant);
            thread.addParticipant(participant);
        }
        return participant;
    }

}
