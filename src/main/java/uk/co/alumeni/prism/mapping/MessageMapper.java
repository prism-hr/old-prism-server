package uk.co.alumeni.prism.mapping;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newTreeMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isFalse;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.SYSTEM_CANDIDATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_STAFF_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.INSTITUTION_STAFF_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PARTNERSHIP_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.domain.activity.ActivityEditable;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.message.Message;
import uk.co.alumeni.prism.domain.message.MessageThread;
import uk.co.alumeni.prism.domain.message.MessageThreadParticipant;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAccount;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.dto.UserRoleDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceDTO;
import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;
import uk.co.alumeni.prism.rest.representation.message.MessageRepresentation;
import uk.co.alumeni.prism.rest.representation.message.MessageThreadParticipantRepresentation;
import uk.co.alumeni.prism.rest.representation.message.MessageThreadParticipantRepresentationPotential;
import uk.co.alumeni.prism.rest.representation.message.MessageThreadParticipantsRepresentationPotential;
import uk.co.alumeni.prism.rest.representation.message.MessageThreadRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;
import uk.co.alumeni.prism.services.ActionService;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.MessageService;
import uk.co.alumeni.prism.services.RoleService;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.services.UserService;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.TreeMultimap;

@Service
@Transactional
public class MessageMapper {

    @Inject
    private ActionService actionService;

    @Inject
    private AdvertService advertService;

    @Inject
    private DocumentMapper documentMapper;

    @Inject
    private MessageService messageService;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Inject
    private UserMapper userMapper;

    @Inject
    private UserService userService;

    public List<MessageThreadRepresentation> getMessageThreadRepresentations(ActivityEditable activity, String searchTerm) {
        User currentUser = messageService.validateViewMessages(activity);
        List<MessageThreadRepresentation> threadRepresentations = newLinkedList();

        List<MessageThread> threads = messageService.getMessageThreads(activity, currentUser, searchTerm);
        if (isNotEmpty(threads)) {
            LinkedHashMultimap<MessageThread, MessageThreadParticipant> participants = messageService.getMessageThreadParticipants(threads);
            LinkedHashMultimap<MessageThread, Message> messages = messageService.getMessages(threads, currentUser, searchTerm);
            LinkedHashMultimap<Message, Document> documents = messageService.getMessageDocuments(messages.values());

            threads.stream().forEach(thread -> {
                MessageThreadRepresentation threadRepresentation = new MessageThreadRepresentation()
                        .withId(thread.getId()).withSubject(thread.getSubject());

                ActivityEditable threadActivity = thread.getActivity();
                if (Resource.class.isAssignableFrom(threadActivity.getClass())) {
                    threadRepresentation.setResource(resourceMapper.getResourceRepresentationSimple((Resource) threadActivity));
                } else {
                    User user = ((UserAccount) threadActivity).getUser();
                    threadRepresentation.setUser(userMapper.getUserRepresentationSimple(user, currentUser));
                }

                Set<MessageThreadParticipantRepresentation> participantRepresentations = newLinkedHashSet();
                participants.get(thread).stream().forEach(participant -> {
                    User recipientUser = participant.getUser();

                    Message lastViewedMessage = participant.getLastViewedMessage();
                    MessageRepresentation lastViewedMessageRepresentation = new MessageRepresentation()
                            .withId(lastViewedMessage == null ? null : lastViewedMessage.getId());
                    participantRepresentations.add(new MessageThreadParticipantRepresentation()
                            .withUser(userMapper.getUserRepresentationSimple(recipientUser, currentUser))
                            .withLastViewedMessage(lastViewedMessageRepresentation));

                    if (recipientUser.equals(currentUser)) {
                        threadRepresentation.setLastViewedMessage(lastViewedMessageRepresentation);
                    }
                });

                threadRepresentation.setParticipants(newLinkedList(participantRepresentations));

                List<MessageRepresentation> messageRepresentations = newLinkedList();
                messages.get(thread).stream().forEach(message -> {
                    MessageRepresentation messageRepresentation = new MessageRepresentation()
                            .withId(message.getId())
                            .withUser(userMapper.getUserRepresentationSimple(message.getUser(), currentUser))
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

    public MessageThreadParticipantsRepresentationPotential getMessageThreadParticipantsRepresentation(ActivityEditable activity) {
        User currentUser = userService.getCurrentUser();

        if (Resource.class.isAssignableFrom(activity.getClass())) {
            return getMessageThreadParticipantsRepresentation((Resource) activity, currentUser);
        } else {
            return getMessageThreadParticipantsRepresentation(((UserAccount) activity).getUser(), currentUser);
        }
    }

    private MessageThreadParticipantsRepresentationPotential getMessageThreadParticipantsRepresentation(Resource resource, User currentUser) {
        Action action = actionService.getMessageAction(resource);
        MessageThreadParticipantsRepresentationPotential representation = new MessageThreadParticipantsRepresentationPotential();
        if (action != null) {
            List<PrismRole> recipientRoles = newLinkedList();
            List<PrismRole> partnerRecipientRoles = newLinkedList();

            List<Integer> stateActionAssignments = stateService.getStateActionAssignments(currentUser, resource, action);
            if (stateActionAssignments.size() > 0) {
                stateService.getStateActionRecipients(stateActionAssignments).stream().forEach(stateActionRecipient -> {
                    if (isFalse(stateActionRecipient.getExternalMode())) {
                        recipientRoles.add(stateActionRecipient.getRole());
                    } else {
                        partnerRecipientRoles.add(stateActionRecipient.getRole());
                    }
                });

                boolean hasRecipientRoles = recipientRoles.size() > 0;
                boolean hasPartnerRecipientRoles = partnerRecipientRoles.size() > 0;

                if (hasRecipientRoles || hasPartnerRecipientRoles) {
                    if (hasRecipientRoles) {
                        List<UserRoleDTO> recipientUserRoles = roleService.getUserRoles(resource, recipientRoles);
                        representation.addParticipants(getMessageThreadParticipantRepresentationsPotential(currentUser, recipientUserRoles));
                    }

                    if (hasPartnerRecipientRoles) {
                        Map<Integer, Advert> resourceAdverts = newHashMap();
                        resource.getAdvert().getEnclosingResources().stream().forEach(enclosingResource -> {
                            Advert enclosingAdvert = enclosingResource.getAdvert();
                            resourceAdverts.put(enclosingAdvert.getId(), enclosingAdvert);
                        });

                        Map<ResourceDTO, Resource> partnerResources = newHashMap();
                        advertService.getTargetedAdverts(resourceAdverts.values()).stream().forEach(partnerAdvert -> {
                            partnerAdvert.getEnclosingResources().stream().forEach(partnerResource -> {
                                ResourceDTO partnerResourceDTO = new ResourceDTO()
                                        .withScope(partnerResource.getResourceScope()).withId(partnerResource.getId());
                                partnerResources.put(partnerResourceDTO, partnerResource);
                            });
                        });

                        if (partnerResources.size() > 0) {
                            List<UserRoleDTO> partnerRecipientUserRoles = roleService.getUserRoles(partnerResources.values(), partnerRecipientRoles);
                            representation.addPartnerParticipants(getMessageThreadParticipantRepresentationsPotential(currentUser,
                                    partnerRecipientUserRoles));
                        }
                    }
                }
            }
        }
        return representation;
    }

    private MessageThreadParticipantsRepresentationPotential getMessageThreadParticipantsRepresentation(User user, User currentUser) {
        Set<Resource> resources = newHashSet();
        roleService.getUserRolesForWhichUserIsCandidate(user).stream().forEach(ur -> resources.add(ur.getResource()));

        List<PrismRole> recipientRoles = newArrayList(PARTNERSHIP_ADMINISTRATOR_GROUP.getRoles());
        recipientRoles.addAll(asList(INSTITUTION_STAFF_GROUP.getRoles()));
        recipientRoles.addAll(asList(DEPARTMENT_STAFF_GROUP.getRoles()));

        List<UserRoleDTO> partnerRecipientUserRoles = roleService.getUserRoles(resources, recipientRoles);

        List<Advert> adverts = resources.stream().map(Resource::getAdvert).collect(Collectors.toList());
        List<Advert> targeterAdverts = advertService.getTargeterAdverts(adverts);

        Set<Resource> targeterResources = newHashSet();
        targeterAdverts.stream().forEach(ta -> {
            Resource targeterResource = ta.getResource();
            targeterResources.add(targeterResource);
            if (targeterResource.getResourceScope().equals(DEPARTMENT)) {
                targeterResources.add(targeterResource);
            }
        });

        List<UserRoleDTO> recipientUserRoles = roleService.getUserRoles(targeterResources, recipientRoles);
        recipientUserRoles.add(new UserRoleDTO().withUser(user).withRole(SYSTEM_CANDIDATE));

        return new MessageThreadParticipantsRepresentationPotential().addParticipants(getMessageThreadParticipantRepresentationsPotential(currentUser,
                recipientUserRoles)).addPartnerParticipants(getMessageThreadParticipantRepresentationsPotential(currentUser, partnerRecipientUserRoles));
    }

    private List<MessageThreadParticipantRepresentationPotential> getMessageThreadParticipantRepresentationsPotential(User currentUser,
            List<UserRoleDTO> recipientUserRoles) {
        TreeMultimap<PrismRole, User> index = TreeMultimap.create();
        recipientUserRoles.stream().forEach(userRole -> index.put(userRole.getRole(), userRole.getUser()));

        Map<PrismRole, MessageThreadParticipantRepresentationPotential> recipients = newTreeMap();
        index.keySet().stream().forEach(key -> {
            List<UserRepresentationSimple> userRepresentations = newLinkedList();
            index.get(key).stream().forEach(value -> {
                if (!equal(value, currentUser)) {
                    userRepresentations.add(userMapper.getUserRepresentationSimple(value, currentUser));
                }
            });

            if (userRepresentations.size() > 0) {
                recipients.put(key, new MessageThreadParticipantRepresentationPotential().withRole(key).withUsers(userRepresentations));
            }
        });

        return newLinkedList(recipients.values());
    }

}
