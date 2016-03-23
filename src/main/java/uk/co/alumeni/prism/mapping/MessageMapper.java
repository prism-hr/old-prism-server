package uk.co.alumeni.prism.mapping;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newTreeMap;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.domain.activity.ActivityEditable;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.message.Message;
import uk.co.alumeni.prism.domain.message.MessageThread;
import uk.co.alumeni.prism.domain.message.MessageThreadParticipant;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.dto.UserRoleDTO;
import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;
import uk.co.alumeni.prism.rest.representation.message.MessageRepresentation;
import uk.co.alumeni.prism.rest.representation.message.MessageThreadParticipantRepresentation;
import uk.co.alumeni.prism.rest.representation.message.MessageThreadParticipantRepresentationPotential;
import uk.co.alumeni.prism.rest.representation.message.MessageThreadRepresentation;
import uk.co.alumeni.prism.rest.representation.message.MessageThreadSubjectRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;
import uk.co.alumeni.prism.services.MessageService;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.TreeMultimap;

@Service
@Transactional
public class MessageMapper {

    @Inject
    private DocumentMapper documentMapper;

    @Inject
    private MessageService messageService;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private UserMapper userMapper;

    public List<MessageThreadRepresentation> getMessageThreadRepresentations(ActivityEditable activity, String searchTerm) {
        User user = messageService.validateViewMessages(activity);
        List<MessageThreadRepresentation> threadRepresentations = newLinkedList();

        List<MessageThread> threads = messageService.getMessageThreads(activity, user, searchTerm);
        if (isNotEmpty(threads)) {
            LinkedHashMultimap<MessageThread, MessageThreadParticipant> participants = messageService.getMessageThreadParticipants(threads);
            LinkedHashMultimap<MessageThread, Message> messages = messageService.getMessages(threads, user, searchTerm);
            LinkedHashMultimap<Message, Document> documents = messageService.getMessageDocuments(messages.values());

            threads.stream().forEach(thread -> {
                MessageThreadSubjectRepresentation subject = new MessageThreadSubjectRepresentation().withSubject(thread.getSubject());
                if (Resource.class.isAssignableFrom(activity.getClass())) {
                    subject.setResource(resourceMapper.getResourceRepresentationSimple((Resource) activity));
                }

                MessageThreadRepresentation threadRepresentation = new MessageThreadRepresentation()
                        .withId(thread.getId()).withSubject(subject);

                Set<MessageThreadParticipantRepresentation> participantRepresentations = newLinkedHashSet();
                participants.get(thread).stream().forEach(participant -> {
                    User recipientUser = participant.getUser();

                    Message lastViewedMessage = participant.getLastViewedMessage();
                    MessageRepresentation lastViewedMessageRepresentation = new MessageRepresentation()
                            .withId(lastViewedMessage == null ? null : lastViewedMessage.getId());
                    participantRepresentations.add(new MessageThreadParticipantRepresentation()
                            .withUser(userMapper.getUserRepresentationSimple(recipientUser, user))
                            .withLastViewedMessage(lastViewedMessageRepresentation));

                    if (recipientUser.equals(user)) {
                        threadRepresentation.setLastViewedMessage(lastViewedMessageRepresentation);
                    }
                });

                threadRepresentation.setParticipants(newLinkedList(participantRepresentations));

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

    public List<MessageThreadParticipantRepresentationPotential> getMessageThreadParticipantRepresentationsPotential(User user,
            List<UserRoleDTO> recipientUserRoles) {
        TreeMultimap<PrismRole, User> index = TreeMultimap.create();
        recipientUserRoles.stream().forEach(userRole -> index.put(userRole.getRole(), userRole.getUser()));

        Map<PrismRole, MessageThreadParticipantRepresentationPotential> recipients = newTreeMap();
        index.keySet().stream().forEach(key -> {
            List<UserRepresentationSimple> userRepresentations = newLinkedList();
            index.get(key).stream().forEach(value -> {
                if (!equal(value, user)) {
                    userRepresentations.add(userMapper.getUserRepresentationSimple(value, user));
                }
            });

            if (userRepresentations.size() > 0) {
                recipients.put(key, new MessageThreadParticipantRepresentationPotential().withRole(key).withUsers(userRepresentations));
            }
        });

        return newLinkedList(recipients.values());
    }

}
