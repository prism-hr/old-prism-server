package uk.co.alumeni.prism.mapping;

import static com.google.common.collect.Lists.newLinkedList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.message.Message;
import uk.co.alumeni.prism.domain.message.MessageThread;
import uk.co.alumeni.prism.domain.message.MessageThreadParticipant;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;
import uk.co.alumeni.prism.rest.representation.message.MessageRepresentation;
import uk.co.alumeni.prism.rest.representation.message.MessageThreadParticipantRepresentation;
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
            LinkedHashMultimap<MessageThread, MessageThreadParticipant> participants = messageService.getMessageThreadParticipants(threads);
            LinkedHashMultimap<MessageThread, Message> messages = messageService.getMessages(threads, user, searchTerm);
            LinkedHashMultimap<Message, Document> documents = messageService.getMessageDocuments(messages.values());

            threads.stream().forEach(thread -> {
                MessageThreadRepresentation threadRepresentation = new MessageThreadRepresentation()
                        .withId(thread.getId()).withSubject(thread.getSubject());

                List<MessageThreadParticipantRepresentation> participantRepresentations = newLinkedList();
                participants.get(thread).stream().forEach(participant -> {
                    User recipientUser = participant.getUser();

                    Message lastViewedMessage = participant.getLastViewedMessage();
                    MessageRepresentation lastViewedMessageRepresentation = new MessageRepresentation()
                            .withId(lastViewedMessage == null ? null : lastViewedMessage.getId());
                    participantRepresentations.add(new MessageThreadParticipantRepresentation()
                            .withUser(userMapper.getUserRepresentationSimple(recipientUser, user))
                            .withLastViewedMessage(lastViewedMessageRepresentation));

                    if (recipientUser.equals(user)) {
                        threadRepresentation.setLastViewedTimestamp(lastViewedMessageRepresentation);
                    }
                });

                threadRepresentation.setParticipants(participantRepresentations);

                List<MessageRepresentation> messageRepresentations = newLinkedList();
                messages.get(thread).stream().forEach(message -> {
                    MessageRepresentation messageRepresentation = new MessageRepresentation()
                            .withId(message.getId())
                            .withUser(userMapper.getUserRepresentationSimple(message.getUser(), user))
                            .withContent(message.getContent())
                            .withCreatedTimestamp(message.getCreatedTimestamp());

                    List<DocumentRepresentation> documentRepresentations = newLinkedList();
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
