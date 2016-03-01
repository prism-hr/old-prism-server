package uk.co.alumeni.prism.mapping;

import static com.google.common.collect.Lists.newLinkedList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.message.Message;
import uk.co.alumeni.prism.domain.message.MessageRecipient;
import uk.co.alumeni.prism.domain.message.MessageThread;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;
import uk.co.alumeni.prism.rest.representation.message.MessageRecipientRepresentation;
import uk.co.alumeni.prism.rest.representation.message.MessageRepresentation;
import uk.co.alumeni.prism.rest.representation.message.MessageThreadRepresentation;
import uk.co.alumeni.prism.services.MessageService;

import com.google.common.collect.LinkedHashMultimap;

@Service
@Transactional
public class MessageMapper {

    @Inject
    private MessageService messageService;

    @Inject
    private DocumentMapper documentMapper;

    @Inject
    private UserMapper userMapper;

    public List<MessageThreadRepresentation> getMessageThreadRepresentations(Resource resource, String searchTerm) {
        User user = messageService.validateViewMessages(resource);
        List<MessageThreadRepresentation> threadRepresentations = newLinkedList();

        List<MessageThread> threads = messageService.getMessageThreads(resource, user, searchTerm);
        if (isNotEmpty(threads)) {
            LinkedHashMultimap<MessageThread, Message> messages = messageService.getMessages(threads, user, searchTerm);

            Collection<Message> unindexedMessages = messages.values();
            LinkedHashMultimap<Message, Document> documents = messageService.getMessageDocuments(unindexedMessages);
            LinkedHashMultimap<Message, MessageRecipient> users = messageService.getMessageRecipients(unindexedMessages);

            threads.stream().forEach(t -> {
                List<MessageRepresentation> messageRepresentations = newLinkedList();
                MessageThreadRepresentation threadRepresentation = new MessageThreadRepresentation()
                        .withId(t.getId()).withSubject(t.getSubject());

                messages.get(t).stream().forEach(message -> {
                    List<DocumentRepresentation> documentRepresentations = newLinkedList();
                    List<MessageRecipientRepresentation> messageRecipientRepresentations = newLinkedList();
                    MessageRepresentation messageRepresentation = new MessageRepresentation()
                            .withUser(userMapper.getUserRepresentationSimple(message.getUser(), user))
                            .withContent(message.getContent()).withCreatedTimestamp(message.getCreatedTimestamp());

                    users.get(message).stream().forEach(recipient -> {
                        User recipientUser = recipient.getUser();
                        DateTime viewTimestamp = recipient.getViewTimestamp();
                        messageRecipientRepresentations.add(new MessageRecipientRepresentation()
                                .withUser(userMapper.getUserRepresentationSimple(recipientUser, user)).withViewTimestamp(viewTimestamp));

                        if (recipientUser.equals(user)) {
                            messageRepresentation.setViewTimestamp(viewTimestamp);
                        }
                    });

                    messageRepresentation.setRecipients(messageRecipientRepresentations);

                    documents.get(message).forEach(document -> documentRepresentations.add(documentMapper.getDocumentRepresentation(document)));
                    messageRepresentation.setDocuments(documentRepresentations);

                    messageRepresentations.add(messageRepresentation);
                });

                threadRepresentation.setMessages(messageRepresentations);
                threadRepresentations.add(threadRepresentation);
            });
        }

        return threadRepresentations;
    }

}
