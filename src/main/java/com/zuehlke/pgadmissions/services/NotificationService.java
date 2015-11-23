package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.dao.WorkflowDAO.targetScopes;
import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_MANAGE_ACCOUNT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_CONNECTION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_JOIN_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_COMPLETE_REGISTRATION_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_CONNECTION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_CONNECTION_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_JOIN_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_JOIN_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_ORGANIZATION_INVITATION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_PASSWORD_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_USER_INVITATION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory.STUDENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.joda.time.LocalDate.now;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.NotificationDAO;
import com.zuehlke.pgadmissions.domain.Invitation;
import com.zuehlke.pgadmissions.domain.InvitationEntity;
import com.zuehlke.pgadmissions.domain.advert.AdvertTarget;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserNotification;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.NotificationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.MailMessageDTO;
import com.zuehlke.pgadmissions.dto.NotificationDefinitionDTO;
import com.zuehlke.pgadmissions.dto.UserConnectionDTO;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinitionDTO;
import com.zuehlke.pgadmissions.dto.UserRoleCategoryDTO;
import com.zuehlke.pgadmissions.mail.MailSender;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

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
    private UserService userService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private SystemService systemService;

    @Inject
    private EntityService entityService;

    @Inject
    private CustomizationService customizationService;

    @Inject
    private RoleService roleService;

    @Inject
    private ScopeService scopeService;

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

    public void sendIndividualWorkflowNotifications(Resource resource, Comment comment) {
        LocalDate baseline = new LocalDate();
        Set<User> exclusions = sendIndividualRequestNotifications(resource, comment, baseline);
        sendIndividualUpdateNotifications(resource, comment, exclusions, baseline);
        entityService.flush();
    }

    public void sendUserActivityNotification(Integer user) {
        // TODO: get the objects to build the mail
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

    public void sendNotification(PrismNotificationDefinition notificationTemplateId, NotificationDefinitionDTO notificationDefinitionDTO) {
        NotificationDefinition notificationTemplate = getById(notificationTemplateId);
        sendNotification(notificationTemplate, notificationDefinitionDTO);
    }

    public void sendUserInvitationNotification(User initiator, User recipient, Resource resource, String invitationMessage, PrismAction transitionAction) {
        sendNotification(getById(SYSTEM_USER_INVITATION_NOTIFICATION),
                new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(recipient).withResource(resource).withInvitationMessage(invitationMessage)
                        .withTransitionAction(transitionAction));
    }

    public void sendOrganizationInvitationNotification(User initiator, User recipient, Resource resource, AdvertTarget advertTarget, String personalMessage) {
        sendNotification(getById(SYSTEM_ORGANIZATION_INVITATION_NOTIFICATION),
                new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(recipient).withResource(resource).withInvitationMessage(personalMessage)
                        .withAdvertTarget(advertTarget).withTransitionAction(PrismAction.valueOf(resource.getResourceScope().name() + "_COMPLETE")));
    }

    public void sendCompleteRegistrationRequest(User initiator, ActionOutcomeDTO actionOutcome) {
        sendNotification(SYSTEM_COMPLETE_REGISTRATION_REQUEST, new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(initiator)
                .withResource(actionOutcome.getTransitionResource()).withTransitionAction(actionOutcome.getTransitionAction().getId()));
    }

    public void sendCompleteRegistrationForgottenRequest(User initiator) {
        sendNotification(SYSTEM_COMPLETE_REGISTRATION_REQUEST, new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(initiator)
                .withResource(systemService.getSystem()).withTransitionAction(SYSTEM_MANAGE_ACCOUNT));
    }

    public void sendResetPasswordNotification(User initiator, String newPassword) {
        System system = systemService.getSystem();
        sendNotification(SYSTEM_PASSWORD_NOTIFICATION, new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(initiator).withResource(system)
                .withTransitionAction(SYSTEM_MANAGE_ACCOUNT).withNewPassword(newPassword));
    }

    public void sendJoinRequest(User initiator, User recipient, ResourceParent resource) {
        System system = systemService.getSystem();
        NotificationDefinition definition = getById(SYSTEM_JOIN_REQUEST);
        sendNotification(definition,
                new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(recipient).withResource(resource).withTransitionAction(SYSTEM_VIEW_JOIN_LIST));
        createOrUpdateUserNotification(system, recipient, definition, now());
    }

    public void sendJoinNotification(User initiator, User recipient, ResourceParent resource) {
        sendNotification(getById(SYSTEM_JOIN_NOTIFICATION), new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(recipient).withResource(resource)
                .withTransitionAction(PrismAction.valueOf(resource.getResourceScope().name() + "_VIEW_EDIT")));
    }

    public void sendConnectionNotification(User initiator, User recipient, AdvertTarget advertTarget) {
        System system = systemService.getSystem();
        sendNotification(getById(SYSTEM_CONNECTION_NOTIFICATION), new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(recipient).withResource(system)
                .withAdvertTarget(advertTarget).withTransitionAction(SYSTEM_MANAGE_ACCOUNT));
    }

    public List<PrismNotificationDefinition> getEditableTemplates(PrismScope scope) {
        return (List<PrismNotificationDefinition>) (List<?>) customizationService.getDefinitions(NOTIFICATION, scope);
    }

    public void resetNotifications(User user) {
        notificationDAO.resetNotifications(user);
        for (PrismScope scope : PrismScope.values()) {
            List<PrismScope> parentScopes = scopeService.getParentScopesDescending(scope, SYSTEM);
            List<Integer> targeterEntities = advertService.getAdvertTargeterEntities(user, scope);
            List<Integer> resourceIds = resourceService.getResources(user, scope, parentScopes, targeterEntities).stream().map(a -> a.getId()).collect(toList());
            if (isNotEmpty(resourceIds)) {
                notificationDAO.resetNotificationsSyndicated(scope, resourceIds);
            }
        }
    }

    public void resetNotifications(UserRole userRole) {
        User user = userRole.getUser();
        Role role = userRole.getRole();
        List<NotificationDefinition> individualDefinitions = notificationDAO.getNotificationDefinitionsIndividual(role);
        if (isNotEmpty(individualDefinitions)) {
            notificationDAO.resetNotifications(user, individualDefinitions);
        }
    }

    public Set<User> sendIndividualRequestNotifications(Resource resource, Comment comment, LocalDate baseline) {
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
                for (PrismScope targeterScope : targetScopes) {
                    for (PrismScope targetScope : targetScopes) {
                        requests.addAll(notificationDAO.getIndividualRequestDefinitions(scope, targeterScope, targetScope, targeterEntities, resource));
                    }
                }
            }
        }

        Set<User> recipients = Sets.newHashSet();
        if (requests.size() > 0) {
            User initiator = comment.getUser();
            for (UserNotificationDefinitionDTO request : requests) {
                User recipient = userService.getById(request.getUserId());
                NotificationDefinition definition = getById(request.getNotificationDefinitionId());
                sendNotification(definition, new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(recipient).withResource(resource).withComment(comment)
                        .withTransitionAction(request.getActionId()));
                createOrUpdateUserNotification(resource, recipient, definition, baseline);
                recipients.add(recipient);
            }
        }

        return recipients;
    }

    private void sendIndividualUpdateNotifications(Resource resource, Comment comment, Set<User> exclusions, LocalDate baseline) {
        PrismScope scope = resource.getResourceScope();
        List<PrismScope> parentScopes = scopeService.getParentScopesDescending(scope, SYSTEM);

        Set<UserNotificationDefinitionDTO> updates = Sets.newHashSet();
        updates.addAll(notificationDAO.getIndividualUpdateDefinitions(scope, comment, exclusions));

        if (!scope.equals(SYSTEM)) {
            for (PrismScope parentScope : parentScopes) {
                updates.addAll(notificationDAO.getIndividualUpdateDefinitions(scope, parentScope, comment, exclusions));
            }
        }

        if (updates.size() > 0) {
            Action viewEditAction = actionService.getViewEditAction(resource);
            if (viewEditAction != null) {
                User initiator = comment.getUser();
                for (UserNotificationDefinitionDTO update : updates) {
                    User recipient = userService.getById(update.getUserId());
                    NotificationDefinition definition = getById(update.getNotificationDefinitionId());
                    sendNotification(definition, new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(recipient).withResource(resource).withComment(comment)
                            .withTransitionAction(viewEditAction.getId()));
                    createOrUpdateUserNotification(resource, recipient, definition, baseline);
                }
            }
        }
    }

    private UserConnectionDTO sendConnectionRequest(Invitation invitation, User recipient, AdvertTarget advertTarget, Set<UserConnectionDTO> sent) {
        UserConnectionDTO messageIndex;
        messageIndex = new UserConnectionDTO(recipient, advertTarget.getOtherAdvert().getId());
        if (!sent.contains(messageIndex)) {
            sendConnectionRequest(invitation.getUser(), recipient, advertTarget);
        }
        return messageIndex;
    }

    private void sendConnectionRequest(User initiator, User recipient, AdvertTarget advertTarget) {
        System system = systemService.getSystem();
        NotificationDefinition definition = getById(SYSTEM_CONNECTION_REQUEST);
        sendNotification(definition, new NotificationDefinitionDTO().withInitiator(initiator).withRecipient(recipient).withResource(system).withAdvertTarget(advertTarget)
                .withTransitionAction(SYSTEM_VIEW_CONNECTION_LIST));
        createOrUpdateUserNotification(system, recipient, definition, now());
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

    private void createOrUpdateUserNotification(Resource resource, User recipient, NotificationDefinition definition, LocalDate baseline) {
        entityService.createOrUpdate(new UserNotification().withResource(resource).withUser(recipient).withNotificationDefinition(definition)
                .withLastNotifiedDate(baseline));
    }

    private <T extends InvitationEntity> void dequeueUserInvitation(Invitation invitation, T invitationEntity) {
        invitationEntity.setInvitation(null);
        entityService.flush();

        if (invitation.getUserRoles().isEmpty() && invitation.getAdvertTargets().isEmpty()) {
            entityService.delete(invitation);
        }
    }

}
