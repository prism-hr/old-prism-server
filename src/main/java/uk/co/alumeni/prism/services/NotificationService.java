package uk.co.alumeni.prism.services;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.alumeni.prism.dao.NotificationDAO;
import uk.co.alumeni.prism.domain.Invitation;
import uk.co.alumeni.prism.domain.InvitationEntity;
import uk.co.alumeni.prism.domain.activity.ActivityEditable;
import uk.co.alumeni.prism.domain.advert.AdvertTarget;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.domain.definitions.workflow.*;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.message.Message;
import uk.co.alumeni.prism.domain.message.MessageNotification;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAccount;
import uk.co.alumeni.prism.domain.user.UserNotification;
import uk.co.alumeni.prism.domain.user.UserRole;
import uk.co.alumeni.prism.domain.workflow.*;
import uk.co.alumeni.prism.dto.*;
import uk.co.alumeni.prism.event.NotificationEvent;
import uk.co.alumeni.prism.rest.dto.DocumentDTO;
import uk.co.alumeni.prism.rest.dto.NotificationConfigurationDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceDTO;
import uk.co.alumeni.prism.rest.representation.advert.AdvertListRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static jersey.repackaged.com.google.common.collect.Maps.newHashMap;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.joda.time.DateTime.now;
import static uk.co.alumeni.prism.PrismConstants.REQUEST_BUFFER;
import static uk.co.alumeni.prism.dao.WorkflowDAO.organizationScopes;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismConfiguration.NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationPurpose.REQUEST_EAGER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationPurpose.UPDATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.STUDENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;

@Service
@Transactional
@SuppressWarnings("unchecked")
public class NotificationService {

    @Inject
    private NotificationDAO notificationDAO;

    @Inject
    private ActionService actionService;

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
    private ApplicationEventPublisher applicationEventPublisher;

    public NotificationDefinition getById(PrismNotificationDefinition id) {
        return entityService.getById(NotificationDefinition.class, id);
    }

    public NotificationConfiguration getNotificationConfigurationById(Integer notificationConfigurationId) {
        return entityService.getById(NotificationConfiguration.class, notificationConfigurationId);
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
            sendNotification(new NotificationEvent(this).withNotificationDefinition(SYSTEM_ACTIVITY_NOTIFICATION)
                    .withInitiator(resource.getUser().getId()).withRecipient(recipient.getId())
                    .withResource(new ResourceDTO().withScope(resource.getResourceScope()).withId(resource.getId()))
                    .withTransitionAction(SYSTEM_VIEW_ACTIVITY_LIST)
                    .withUserActivityRepresentation(userActivityRepresentation)
                    .withAdvertListRepresentation(advertListRepresentation));
        }
    }

    public void sendUserReminderNotification(Integer user, UserActivityRepresentation userActivityRepresentation) {
        if (userActivityRepresentation.hasNotifiableReminders()) {
            User recipient = userService.getById(user);
            System resource = systemService.getSystem();
            sendNotification(new NotificationEvent(this).withNotificationDefinition(SYSTEM_REMINDER_NOTIFICATION).withInitiator(resource.getUser().getId())
                    .withRecipient(recipient.getId()).withResource(new ResourceDTO().withScope(resource.getResourceScope()).withId(resource.getId()))
                    .withTransitionAction(SYSTEM_VIEW_ACTIVITY_LIST).withUserActivityRepresentation(userActivityRepresentation));
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
        sendNotification(new NotificationEvent(this).withNotificationDefinition(SYSTEM_USER_INVITATION_NOTIFICATION).withInitiator(initiator.getId())
                .withRecipient(recipient.getId()).withResource(new ResourceDTO().withScope(resource.getResourceScope()).withId(resource.getId()))
                .withInvitationMessage(invitationMessage).withTransitionAction(transitionAction));
    }

    public void sendOrganizationInvitationNotification(User initiator, User recipient, Resource resource, AdvertTarget advertTarget, String personalMessage) {
        sendNotification(new NotificationEvent(this).withNotificationDefinition(SYSTEM_ORGANIZATION_INVITATION_NOTIFICATION).withInitiator(initiator.getId())
                .withRecipient(recipient.getId()).withResource(new ResourceDTO().withScope(resource.getResourceScope()).withId(resource.getId()))
                .withInvitationMessage(personalMessage).withAdvertTarget(advertTarget.getId())
                .withTransitionAction(PrismAction.valueOf(resource.getResourceScope().name() + "_COMPLETE")));
    }

    public void sendCompleteRegistrationRequest(User initiator, ActionOutcomeDTO actionOutcome) {
        Integer initiatorId = initiator.getId();
        Resource resource = actionOutcome.getResource();
        sendNotification(new NotificationEvent(this).withNotificationDefinition(SYSTEM_COMPLETE_REGISTRATION_REQUEST).withInitiator(initiatorId)
                .withRecipient(initiatorId).withResource(new ResourceDTO().withScope(resource.getResourceScope()).withId(resource.getId()))
                .withTransitionAction(actionOutcome.getTransitionAction().getId()));
    }

    public void sendCompleteRegistrationForgottenRequest(User initiator) {
        Integer initiatorId = initiator.getId();
        System resource = systemService.getSystem();
        sendNotification(new NotificationEvent(this).withNotificationDefinition(SYSTEM_COMPLETE_REGISTRATION_FORGOTTEN_REQUEST).withInitiator(initiatorId)
                .withRecipient(initiatorId).withResource(new ResourceDTO().withScope(resource.getResourceScope()).withId(resource.getId()))
                .withTransitionAction(SYSTEM_MANAGE_ACCOUNT));
    }

    public void sendResetPasswordNotification(User initiator, String newPassword) {
        Integer initiatorId = initiator.getId();
        System resource = systemService.getSystem();
        sendNotification(new NotificationEvent(this).withNotificationDefinition(SYSTEM_PASSWORD_NOTIFICATION).withInitiator(initiatorId)
                .withRecipient(initiatorId).withResource(new ResourceDTO().withScope(resource.getResourceScope()).withId(resource.getId()))
                .withTransitionAction(SYSTEM_MANAGE_ACCOUNT).withNewPassword(newPassword));
    }

    public void sendJoinRequest(User initiator, User recipient, ResourceParent resource) {
        sendNotification(new NotificationEvent(this).withNotificationDefinition(SYSTEM_JOIN_REQUEST).withInitiator(initiator.getId())
                .withRecipient(recipient.getId()).withResource(new ResourceDTO().withScope(resource.getResourceScope()).withId(resource.getId()))
                .withTransitionAction(SYSTEM_VIEW_JOIN_LIST));
    }

    public void sendJoinNotification(User initiator, User recipient, ResourceParent resource) {
        sendNotification(new NotificationEvent(this).withNotificationDefinition(SYSTEM_JOIN_NOTIFICATION).withInitiator(initiator.getId())
                .withRecipient(recipient.getId()).withResource(new ResourceDTO().withScope(resource.getResourceScope()).withId(resource.getId()))
                .withTransitionAction(PrismAction.valueOf(resource.getResourceScope().name() + "_VIEW_EDIT")));
    }

    public void sendConnectionNotification(User initiator, User recipient, AdvertTarget advertTarget) {
        System resource = systemService.getSystem();
        sendNotification(new NotificationEvent(this).withNotificationDefinition(SYSTEM_CONNECTION_NOTIFICATION).withInitiator(initiator.getId())
                .withRecipient(recipient.getId()).withResource(new ResourceDTO().withScope(resource.getResourceScope()).withId(resource.getId()))
                .withAdvertTarget(advertTarget.getId()).withTransitionAction(SYSTEM_MANAGE_ACCOUNT));
    }

    public void sendMessageNotification(MessageNotification messageRecipient) {
        User initiator = systemService.getSystem().getUser();
        Message message = messageRecipient.getMessage();
        ActivityEditable activity = message.getThread().getActivity();

        Resource resource;
        PrismNotificationDefinition definition;
        NotificationEvent notificationEvent = new NotificationEvent(this).withInitiator(initiator.getId())
                .withRecipient(messageRecipient.getUser().getId()).withMessage(message.getId());
        if (Resource.class.isAssignableFrom(activity.getClass())) {
            resource = (Resource) activity;
            definition = PrismNotificationDefinition.valueOf(resource.getResourceScope().name() + "_MESSAGE_NOTIFICATION");
            notificationEvent.setTransitionAction(actionService.getMessageAction(resource).getId());
        } else {
            resource = systemService.getSystem();
            definition = SYSTEM_MESSAGE_CANDIDATE_NOTIFICATION;
            notificationEvent.setCandidate(((UserAccount) activity).getUser().getId());
        }

        notificationEvent.setNotificationDefinition(definition);
        notificationEvent.setResource(new ResourceDTO().withScope(resource.getResourceScope()).withId(resource.getId()));
        sendNotification(notificationEvent);
    }

    public List<PrismNotificationDefinition> getEditableTemplates(PrismScope scope) {
        return (List<PrismNotificationDefinition>) (List<?>) customizationService.getDefinitions(NOTIFICATION, scope);
    }

    public void resetUserNotifications() {
        notificationDAO.resetUserNotifications(now().minusDays(1));
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

        Set<UserNotificationDefinitionDTO> requests = newHashSet();
        requests.addAll(notificationDAO.getIndividualRequestDefinitions(scope, resource));

        if (!scope.equals(SYSTEM)) {
            for (PrismScope parentScope : parentScopes) {
                requests.addAll(notificationDAO.getIndividualRequestDefinitions(scope, parentScope, resource));
            }

            for (PrismScope targeterScope : organizationScopes) {
                for (PrismScope targetScope : organizationScopes) {
                    requests.addAll(notificationDAO.getIndividualRequestDefinitions(scope, targeterScope, targetScope, resource));
                }
            }
        }

        Set<User> recipients = newHashSet();
        if (requests.size() > 0) {
            User initiator = comment.getUser();
            Map<UserNotificationDTO, Long> recentRequests = newHashMap();
            notificationDAO.getRecentRequestCounts(requests, DateTime.now().minusDays(1)).forEach(rr -> recentRequests.put(rr, rr.getSentCount()));

            for (UserNotificationDefinitionDTO request : requests) {
                User recipient = userService.getById(request.getUserId());
                Long recentRequestCount = recentRequests
                        .get(new UserNotificationDTO().withUserId(request.getUserId()).withNotificationDefinitionId(request.getNotificationDefinitionId()));

                NotificationEvent notificationEvent = new NotificationEvent(this).withNotificationDefinition(request.getNotificationDefinitionId())
                        .withInitiator(initiator.getId()).withRecipient(recipient.getId())
                        .withResource(new ResourceDTO().withScope(resource.getResourceScope()).withId(resource.getId())).withComment(comment.getId())
                        .withTransitionAction(request.getActionId());

                if (sendIndividualRequestNotification(notificationEvent, recentRequestCount)) {
                    recipients.add(recipient);
                }
            }
        }

        return recipients;
    }

    public NotificationConfiguration createOrUpdateNotificationConfiguration(PrismConfiguration configurationType, Resource resource,
            PrismOpportunityType opportunityType, NotificationConfigurationDTO notificationConfigurationDTO) {
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

    public void createUserNotification(Resource resource, User recipient, NotificationDefinition definition) {
        if (definition.getNotificationPurpose().equals(UPDATE)) {
            entityService.createOrUpdate(new UserNotification().withResource(resource).withUser(recipient)
                    .withNotificationDefinition(definition).withActive(true).withNotifiedTimestamp(now()));
        } else {
            entityService.save(new UserNotification().withResource(resource).withUser(recipient)
                    .withNotificationDefinition(definition).withActive(true).withNotifiedTimestamp(now()));
        }
    }

    private void sendIndividualUpdateNotifications(Resource resource, Comment comment, Set<UserNotificationDefinitionDTO> updates, Set<User> exclusions) {
        if (updates.size() > 0) {
            Action viewEditAction = actionService.getViewEditAction(resource);
            if (viewEditAction != null) {
                User initiator = comment.getUser();
                for (UserNotificationDefinitionDTO update : updates) {
                    User recipient = userService.getById(update.getUserId());
                    if (!exclusions.contains(recipient)) {
                        sendNotification(new NotificationEvent(this).withNotificationDefinition(update.getNotificationDefinitionId())
                                .withInitiator(initiator.getId()).withRecipient(recipient.getId())
                                .withResource(new ResourceDTO().withScope(resource.getResourceScope()).withId(resource.getId())).withComment(comment.getId())
                                .withTransitionAction(viewEditAction.getId()));
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
        System resource = systemService.getSystem();
        sendNotification(new NotificationEvent(this).withNotificationDefinition(SYSTEM_CONNECTION_REQUEST).withInitiator(initiator.getId())
                .withRecipient(recipient.getId()).withResource(new ResourceDTO().withScope(resource.getResourceScope()).withId(resource.getId()))
                .withAdvertTarget(advertTarget.getId()).withTransitionAction(SYSTEM_VIEW_CONNECTION_LIST));
    }

    private <T extends InvitationEntity> void dequeueUserInvitation(Invitation invitation, T invitationEntity) {
        invitationEntity.setInvitation(null);
        entityService.flush();

        if (invitation.getUserRoles().isEmpty() && invitation.getAdvertTargets().isEmpty()) {
            entityService.delete(invitation);
        }
    }

    private boolean sendIndividualRequestNotification(NotificationEvent notificationEvent, Long recentRequestCount) {
        recentRequestCount = recentRequestCount == null ? 0 : recentRequestCount;
        if (notificationEvent.getNotificationDefinition().getNotificationPurpose().equals(REQUEST_EAGER) || recentRequestCount < REQUEST_BUFFER) {
            sendNotification(notificationEvent.withBuffered(recentRequestCount == (REQUEST_BUFFER - 1)));
            return true;
        }
        return false;
    }

    private void sendNotification(NotificationEvent notificationEvent) {
        applicationEventPublisher.publishEvent(notificationEvent);
    }

}
