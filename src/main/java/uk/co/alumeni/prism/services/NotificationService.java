package uk.co.alumeni.prism.services;

import static com.google.common.collect.Sets.newHashSet;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static uk.co.alumeni.prism.PrismConstants.REQUEST_BUFFER;
import static uk.co.alumeni.prism.dao.WorkflowDAO.organizationScopes;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_MANAGE_ACCOUNT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_ACTIVITY_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_CONNECTION_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_JOIN_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismConfiguration.NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_ACTIVITY_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_COMPLETE_REGISTRATION_FORGOTTEN_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_COMPLETE_REGISTRATION_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_CONNECTION_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_CONNECTION_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_JOIN_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_JOIN_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_ORGANIZATION_INVITATION_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_PASSWORD_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_REMINDER_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_USER_INVITATION_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationPurpose.REQUEST_EAGER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationPurpose.UPDATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.STUDENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import jersey.repackaged.com.google.common.collect.Maps;

import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.dao.NotificationDAO;
import uk.co.alumeni.prism.domain.Invitation;
import uk.co.alumeni.prism.domain.InvitationEntity;
import uk.co.alumeni.prism.domain.advert.AdvertTarget;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismConfiguration;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserNotification;
import uk.co.alumeni.prism.domain.user.UserRole;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.domain.workflow.NotificationConfiguration;
import uk.co.alumeni.prism.domain.workflow.NotificationConfigurationDocument;
import uk.co.alumeni.prism.domain.workflow.NotificationDefinition;
import uk.co.alumeni.prism.domain.workflow.Role;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.domain.workflow.WorkflowConfiguration;
import uk.co.alumeni.prism.dto.ActionOutcomeDTO;
import uk.co.alumeni.prism.dto.MailMessageDTO;
import uk.co.alumeni.prism.dto.NotificationDefinitionDTO;
import uk.co.alumeni.prism.dto.UserConnectionDTO;
import uk.co.alumeni.prism.dto.UserNotificationDTO;
import uk.co.alumeni.prism.dto.UserNotificationDefinitionDTO;
import uk.co.alumeni.prism.dto.UserRoleCategoryDTO;
import uk.co.alumeni.prism.mail.MailSender;
import uk.co.alumeni.prism.rest.dto.DocumentDTO;
import uk.co.alumeni.prism.rest.dto.NotificationConfigurationDTO;
import uk.co.alumeni.prism.rest.representation.advert.AdvertListRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Service
@Transactional
@SuppressWarnings("unchecked")
public class NotificationService {

    @Inject
    private NotificationDAO notificationDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private AdvertService advertService;

    @Inject
    private CustomizationService customizationService;

    @Inject
    private DocumentService documentService;

    @Inject
    private EntityService entityService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private ScopeService scopeService;

    @Inject
    private SystemService systemService;

    @Inject
    private UserService userService;

    @Inject
    private ApplicationContext applicationContext;

    public NotificationDefinition getById(PrismNotificationDefinition id) {
        return entityService.getByProperty(NotificationDefinition.class, "id", id);
    }

    public NotificationConfiguration getNotificationConfiguration(Resource resource, User user, NotificationDefinition definition) {
        return (NotificationConfiguration) customizationService.getConfiguration(NOTIFICATION, resource, definition);
    }

    public List<NotificationDefinition> getDefinitions() {
        return entityService.getAll(NotificationDefinition.class);
    }

    public List<NotificationDefinition> getWorkflowDefinitions() {
        List<NotificationDefinition> templates = Lists.newLinkedList();
        templates.addAll(notificationDAO.getWorkflowRequestDefinitions());
        templates.addAll(notificationDAO.getWorkflowUpdateDefinitions());
        return templates;
    }

    public void deleteObsoleteNotificationConfigurations() {
        notificationDAO.deleteObsoleteNotificationConfigurations(getWorkflowDefinitions());
    }

    public void sendIndividualWorkflowNotifications(Resource resource, Comment comment, Set<UserNotificationDefinitionDTO> updates) {
        Set<User> exclusions = sendIndividualRequestNotifications(resource, comment);
        sendIndividualUpdateNotifications(resource, comment, updates, exclusions);
        entityService.flush();
    }

    public void sendUserActivityNotification(Integer user, UserActivityRepresentation userActivityRepresentation,
            AdvertListRepresentation advertListRepresentation) {
        if (userActivityRepresentation.hasNotifiableUpdates() || advertListRepresentation.hasNotifiableUpdates()) {
            User recipient = userService.getById(user);
            System resource = systemService.getSystem();
            NotificationDefinition definition = getById(SYSTEM_ACTIVITY_NOTIFICATION);
            NotificationDefinitionDTO definitionDTO = new NotificationDefinitionDTO().withInitiator(resource.getUser()).withRecipient(recipient)
                    .withResource(resource)
                    .withTransitionAction(SYSTEM_VIEW_ACTIVITY_LIST).withUserActivityRepresentation(userActivityRepresentation)
                    .withAdvertListRepresentation(advertListRepresentation);
            sendIndividualUpdateNotification(resource, recipient, definition, definitionDTO);
        }
    }

    public void sendUserReminderNotification(Integer user, UserActivityRepresentation userActivityRepresentation) {
        if (userActivityRepresentation.hasNotifiableReminders()) {
            User recipient = userService.getById(user);
            System resource = systemService.getSystem();
            NotificationDefinition definition = getById(SYSTEM_REMINDER_NOTIFICATION);
            NotificationDefinitionDTO definitionDTO = new NotificationDefinitionDTO().withInitiator(resource.getUser()).withRecipient(recipient)
                    .withResource(resource)
                    .withTransitionAction(SYSTEM_VIEW_ACTIVITY_LIST).withUserActivityRepresentation(userActivityRepresentation);
            sendIndividualUpdateNotification(resource, recipient, definition, definitionDTO);
        }
    }

    public void sendInvitationRequest(Integer userRoleId, Set<UserRoleCategoryDTO> sent) {
        UserRole userRole = roleService.getUserRoleById(userRoleId);
        Resource resource = userRole.getResource();
        Invitation invitation = userRole.getInvitation();

        UserRoleCategoryDTO messageIndex = new UserRoleCategoryDTO(userRole.getUser(), resource, userRole.getRole().getRoleCategory());
        if (!sent.contains(messageIndex)) {
            PrismAction transitionAction = userRole.getRole().getRoleCategory().equals(STUDENT) ? SYSTEM_MANAGE_ACCOUNT
                    : PrismAction.valueOf(resource.getResourceScope().name() + "_VIEW_EDIT");
            sendUserInvitationNotification(invitation.getUser(), userRole.getUser(), resource, invitation.getMessage(), transitionAction);
        }

        dequeueUserInvitation(invitation, userRole);
    }

    public void sendConnectionRequest(Integer advertTargetId, Set<UserConnectionDTO> sent) {
        AdvertTarget advertTarget = entityService.getById(AdvertTarget.class, advertTargetId);
        if (!advertTarget.getAcceptAdvert().getResource().getResourceStates().stream().allMatch(rs -> rs.getState().getId().name().endsWith("_UNSUBMITTED"))) {
            Invitation invitation = advertTarget.getInvitation();

            UserConnectionDTO messageIndex = null;
            User recipient = advertTarget.getAcceptAdvertUser();
            if (recipient == null) {
                ResourceParent resource = advertTarget.getAcceptAdvert().getResource();
                List<User> recipientAdmins = userService.getResourceUsers(resource, PrismRole.valueOf(resource.getResourceScope().name() + "_ADMINISTRATOR"));
                for (User recipientAdmin : recipientAdmins) {
                    messageIndex = sendConnectionRequest(invitation, recipientAdmin, advertTarget, sent);
                }
            } else {
                if (isNotEmpty(roleService.getVerifiedRoles(recipient, advertTarget.getAcceptAdvert().getResource()))) {
                    messageIndex = sendConnectionRequest(invitation, recipient, advertTarget, sent);
                }
            }

            if (messageIndex != null) {
                dequeueUserInvitation(invitation, advertTarget);
                sent.add(messageIndex);
            }
        }
    }

    public void sendUserInvitationNotification(User initiator, User recipient, Resource resource, String invitationMessage, PrismAction transitionAction) {
        NotificationDefinition definition = getById(SYSTEM_USER_INVITATION_NOTIFICATION);
        NotificationDefinitionDTO definitionDTO = new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(recipient).withResource(resource)
                .withInvitationMessage(invitationMessage).withTransitionAction(transitionAction);
        sendIndividualUpdateNotification(resource, initiator, definition, definitionDTO);
    }

    public void sendOrganizationInvitationNotification(User initiator, User recipient, Resource resource, AdvertTarget advertTarget, String personalMessage) {
        NotificationDefinition definition = getById(SYSTEM_ORGANIZATION_INVITATION_NOTIFICATION);
        NotificationDefinitionDTO definitionDTO = new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(recipient).withResource(resource)
                .withInvitationMessage(personalMessage).withAdvertTarget(advertTarget)
                .withTransitionAction(PrismAction.valueOf(resource.getResourceScope().name() + "_COMPLETE"));
        sendIndividualUpdateNotification(resource, initiator, definition, definitionDTO);
    }

    public void sendCompleteRegistrationRequest(User initiator, ActionOutcomeDTO actionOutcome) {
        Resource resource = actionOutcome.getResource();
        NotificationDefinition definition = getById(SYSTEM_COMPLETE_REGISTRATION_REQUEST);
        NotificationDefinitionDTO definitionDTO = new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(initiator)
                .withResource(resource).withTransitionAction(actionOutcome.getTransitionAction().getId());
        sendIndividualUpdateNotification(resource, initiator, definition, definitionDTO);
    }

    public void sendCompleteRegistrationForgottenRequest(User initiator) {
        System system = systemService.getSystem();
        NotificationDefinition definition = getById(SYSTEM_COMPLETE_REGISTRATION_FORGOTTEN_REQUEST);
        NotificationDefinitionDTO definitionDTO = new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(initiator)
                .withResource(systemService.getSystem()).withTransitionAction(SYSTEM_MANAGE_ACCOUNT);
        sendIndividualUpdateNotification(system, initiator, definition, definitionDTO);
    }

    public void sendResetPasswordNotification(User initiator, String newPassword) {
        System system = systemService.getSystem();
        NotificationDefinition definition = getById(SYSTEM_PASSWORD_NOTIFICATION);
        NotificationDefinitionDTO definitionDTO = new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(initiator).withResource(system)
                .withTransitionAction(SYSTEM_MANAGE_ACCOUNT).withNewPassword(newPassword);
        sendIndividualUpdateNotification(system, initiator, definition, definitionDTO);
    }

    public void sendJoinRequest(User initiator, User recipient, ResourceParent resource) {
        NotificationDefinition definition = getById(SYSTEM_JOIN_REQUEST);
        NotificationDefinitionDTO definitionDTO = new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(recipient).withResource(resource)
                .withTransitionAction(SYSTEM_VIEW_JOIN_LIST);
        sendIndividualUpdateNotification(resource, recipient, definition, definitionDTO);
    }

    public void sendJoinNotification(User initiator, User recipient, ResourceParent resource) {
        NotificationDefinition definition = getById(SYSTEM_JOIN_NOTIFICATION);
        NotificationDefinitionDTO definitionDTO = new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(recipient).withResource(resource)
                .withTransitionAction(PrismAction.valueOf(resource.getResourceScope().name() + "_VIEW_EDIT"));
        sendIndividualUpdateNotification(resource, recipient, definition, definitionDTO);
    }

    public void sendConnectionNotification(User initiator, User recipient, AdvertTarget advertTarget) {
        System system = systemService.getSystem();
        NotificationDefinition definition = getById(SYSTEM_CONNECTION_NOTIFICATION);
        NotificationDefinitionDTO definitionDTO = new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(recipient).withResource(system)
                .withAdvertTarget(advertTarget).withTransitionAction(SYSTEM_MANAGE_ACCOUNT);
        sendIndividualUpdateNotification(system, recipient, definition, definitionDTO);
    }

    public List<PrismNotificationDefinition> getEditableTemplates(PrismScope scope) {
        return (List<PrismNotificationDefinition>) (List<?>) customizationService.getDefinitions(NOTIFICATION, scope);
    }

    public void resetUserNotifications() {
        notificationDAO.resetUserNotifications(DateTime.now().minusDays(1));
    }

    public void resetUserNotifications(User user) {
        notificationDAO.resetUserNotifications(user);
    }

    public void resetUserNotifications(UserRole userRole) {
        User user = userRole.getUser();
        Role role = userRole.getRole();
        List<NotificationDefinition> individualDefinitions = notificationDAO.getNotificationDefinitionsIndividual(role);
        if (isNotEmpty(individualDefinitions)) {
            notificationDAO.resetUserNotifications(user, individualDefinitions);
        }
    }

    public Set<User> sendIndividualRequestNotifications(Resource resource, Comment comment) {
        PrismScope scope = resource.getResourceScope();
        List<PrismScope> parentScopes = scopeService.getParentScopesDescending(scope, SYSTEM);

        Set<UserNotificationDefinitionDTO> requests = Sets.newHashSet();
        requests.addAll(notificationDAO.getIndividualRequestDefinitions(scope, resource));

        if (!scope.equals(SYSTEM)) {
            for (PrismScope parentScope : parentScopes) {
                requests.addAll(notificationDAO.getIndividualRequestDefinitions(scope, parentScope, resource));
            }

            List<Integer> targeterEntities = advertService.getAdvertTargeterEntities(resource.getResourceScope());
            if (isNotEmpty(targeterEntities)) {
                for (PrismScope targeterScope : organizationScopes) {
                    for (PrismScope targetScope : organizationScopes) {
                        requests.addAll(notificationDAO.getIndividualRequestDefinitions(scope, targeterScope, targetScope, targeterEntities, resource));
                    }
                }
            }
        }

        Set<User> recipients = Sets.newHashSet();
        if (requests.size() > 0) {
            User initiator = comment.getUser();
            Map<UserNotificationDTO, Long> recentRequests = Maps.newHashMap();
            notificationDAO.getRecentRequestCounts(requests, DateTime.now().minusDays(1)).forEach(rr -> recentRequests.put(rr, rr.getSentCount()));

            for (UserNotificationDefinitionDTO request : requests) {
                User recipient = userService.getById(request.getUserId());
                Long recentRequestCount = recentRequests
                        .get(new UserNotificationDTO().withUserId(request.getUserId()).withNotificationDefinitionId(request.getNotificationDefinitionId()));
                NotificationDefinition definition = getById(request.getNotificationDefinitionId());
                NotificationDefinitionDTO definitionDTO = new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(recipient)
                        .withResource(resource)
                        .withComment(comment).withTransitionAction(request.getActionId());

                recipient = sendIndividualRequestNotification(resource, recipient, definition, definitionDTO, recentRequestCount);
                if (recipient != null) {
                    recipients.add(recipient);
                }
            }
        }

        return recipients;
    }

    public NotificationConfiguration createOrUpdateNotificationConfiguration(
            PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType,
            NotificationConfigurationDTO notificationConfigurationDTO) {
        WorkflowConfiguration<?> configuration = customizationService.createConfiguration(configurationType, resource, opportunityType,
                notificationConfigurationDTO);
        resourceService.executeUpdate(resource, userService.getCurrentUser(),
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
        configuration = entityService.createOrUpdate(configuration);

        List<DocumentDTO> documents = notificationConfigurationDTO.getDocuments();
        NotificationConfiguration notificationConfiguration = (NotificationConfiguration) configuration;
        notificationDAO.deleteNotificationConfigurationDocuments(notificationConfiguration);
        if (documents != null) {
            for (DocumentDTO documentDTO : documents) {
                Document document = documentService.getById(documentDTO.getId());
                NotificationConfigurationDocument notificationConfigurationDocument = entityService.getOrCreate(new NotificationConfigurationDocument()
                        .withNotificationConfiguration(notificationConfiguration).withDocument(document));
                notificationConfiguration.addDocument(notificationConfigurationDocument);
            }
        }

        return notificationConfiguration;
    }

    private void sendIndividualUpdateNotifications(Resource resource, Comment comment, Set<UserNotificationDefinitionDTO> updates, Set<User> exclusions) {
        if (updates.size() > 0) {
            Action viewEditAction = actionService.getViewEditAction(resource);
            if (viewEditAction != null) {
                User initiator = comment.getUser();
                for (UserNotificationDefinitionDTO update : updates) {
                    User recipient = userService.getById(update.getUserId());
                    if (!exclusions.contains(recipient)) {
                        NotificationDefinition definition = getById(update.getNotificationDefinitionId());
                        NotificationDefinitionDTO definitionDTO = new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(recipient)
                                .withResource(resource).withComment(comment).withTransitionAction(viewEditAction.getId());
                        sendIndividualUpdateNotification(resource, recipient, definition, definitionDTO);
                    }
                }
            }
        }
    }

    public Set<UserNotificationDefinitionDTO> getIndividualUpdateDefinitions(Resource resource, StateTransition stateTransition) {
        PrismScope scope = resource.getResourceScope();
        List<PrismScope> parentScopes = scopeService.getParentScopesDescending(scope, SYSTEM);

        Set<UserNotificationDefinitionDTO> updates = newHashSet();
        updates.addAll(notificationDAO.getIndividualUpdateDefinitions(scope, resource, stateTransition));

        if (!scope.equals(SYSTEM)) {
            for (PrismScope parentScope : parentScopes) {
                updates.addAll(notificationDAO.getIndividualUpdateDefinitions(scope, parentScope, resource, stateTransition));
            }
        }
        return updates;
    }

    private UserConnectionDTO sendConnectionRequest(Invitation invitation, User recipient, AdvertTarget advertTarget, Set<UserConnectionDTO> sent) {
        UserConnectionDTO messageIndex;
        messageIndex = new UserConnectionDTO(recipient, advertTarget.getOtherAdvert().getId());
        if (!sent.contains(messageIndex)) {
            User initiator = invitation.getUser();
            sendConnectionRequest(initiator, recipient, advertTarget);
        }
        return messageIndex;
    }

    private void sendConnectionRequest(User initiator, User recipient, AdvertTarget advertTarget) {
        System system = systemService.getSystem();
        NotificationDefinition definition = getById(SYSTEM_CONNECTION_REQUEST);
        NotificationDefinitionDTO definitionDTO = new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(recipient).withResource(system)
                .withAdvertTarget(advertTarget).withTransitionAction(SYSTEM_VIEW_CONNECTION_LIST);
        sendIndividualUpdateNotification(system, recipient, definition, definitionDTO);
    }

    private <T extends InvitationEntity> void dequeueUserInvitation(Invitation invitation, T invitationEntity) {
        invitationEntity.setInvitation(null);
        entityService.flush();

        if (invitation.getUserRoles().isEmpty() && invitation.getAdvertTargets().isEmpty()) {
            entityService.delete(invitation);
        }
    }

    private void sendIndividualUpdateNotification(Resource resource, User recipient, NotificationDefinition definition, NotificationDefinitionDTO definitionDTO) {
        sendNotification(definition, definitionDTO);
        createUserNotification(resource, recipient, definition);
    }

    private User sendIndividualRequestNotification(Resource resource, User recipient, NotificationDefinition definition,
            NotificationDefinitionDTO definitionDTO, Long recentRequestCount) {
        recentRequestCount = recentRequestCount == null ? 0 : recentRequestCount;
        if (definition.getNotificationPurpose().equals(REQUEST_EAGER) || recentRequestCount < REQUEST_BUFFER) {
            sendNotification(definition, definitionDTO.withBuffered(recentRequestCount == (REQUEST_BUFFER - 1)));
            createUserNotification(resource, recipient, definition);
            return recipient;
        }
        return null;
    }

    private void sendNotification(NotificationDefinition definition, NotificationDefinitionDTO definitionDTO) {
        User user = definitionDTO.getRecipient();
        NotificationConfiguration configuration = getNotificationConfiguration(definitionDTO.getResource(), user, definition);
        MailMessageDTO message = new MailMessageDTO();

        message.setNotificationConfiguration(configuration);
        message.setNotificationDefinitionDTO(definitionDTO);

        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(definitionDTO.getResource());
        applicationContext.getBean(MailSender.class).localize(propertyLoader).sendEmail(message);
    }

    private void createUserNotification(Resource resource, User recipient, NotificationDefinition definition) {
        if (definition.getNotificationPurpose().equals(UPDATE)) {
            entityService.createOrUpdate(new UserNotification().withResource(resource).withUser(recipient).withNotificationDefinition(definition)
                    .withActive(true).withNotifiedTimestamp(DateTime.now()));
        } else {
            entityService.save(new UserNotification().withResource(resource).withUser(recipient).withNotificationDefinition(definition).withActive(true)
                    .withNotifiedTimestamp(DateTime.now()));
        }
    }

}
