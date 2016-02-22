package uk.co.alumeni.prism.mapping;

import static com.google.common.collect.Lists.newLinkedList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.message.Message;
import uk.co.alumeni.prism.domain.message.MessageThread;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;
import uk.co.alumeni.prism.rest.representation.message.MessageRepresentation;
import uk.co.alumeni.prism.rest.representation.message.MessageThreadRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;
import uk.co.alumeni.prism.services.MessageService;
import uk.co.alumeni.prism.services.ResourceService;

import com.google.common.collect.LinkedHashMultimap;

@Service
@Transactional
public class MessageMapper {

    @Inject
    private MessageService messageService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private DocumentMapper documentMapper;

    @Inject
    private UserMapper userMapper;

    public List<MessageThreadRepresentation> getMessageThreadRepresentations(Resource resource) {
        User user = resourceService.validateViewResource(resource);
        List<MessageThreadRepresentation> threadRepresentations = newLinkedList();

        List<MessageThread> threads = messageService.getMessageThreads(resource, user);
        if (isNotEmpty(threads)) {
            LinkedHashMultimap<MessageThread, Message> messages = messageService.getMessages(threads, user);

            Collection<Message> unindexedMessages = messages.values();
            LinkedHashMultimap<Message, User> users = messageService.getMessageRecipients(unindexedMessages);
            LinkedHashMultimap<Message, Document> documents = messageService.getMessageDocuments(unindexedMessages);

            threads.stream().forEach(t -> {
                List<MessageRepresentation> messageRepresentations = newLinkedList();
                MessageThreadRepresentation threadRepresentation = new MessageThreadRepresentation().withId(t.getId()).withSubject(t.getSubject());
                
                messages.get(t).stream().forEach(m -> {
                    List<UserRepresentationSimple> userRepresentations = newLinkedList();
                    List<DocumentRepresentation> documentRepresentations= newLinkedList();
                    MessageRepresentation messageRepresentation = new MessageRepresentation().withUser(userMapper.getUserRepresentationSimple(m.getUser(), user))
                            .withContent(m.getContent()).withCreatedTimestamp(m.getCreatedTimestamp());

                    users.get(m).stream().forEach(r -> userRepresentations.add(userMapper.getUserRepresentationSimple(r, user)));
                    messageRepresentation.setRecipients(userRepresentations);
                    
                    documents.get(m).forEach(d -> documentRepresentations.add(documentMapper.getDocumentRepresentation(d)));
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
