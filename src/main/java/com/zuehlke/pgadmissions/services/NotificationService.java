package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_MANAGE_ACCOUNT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_COMPLETE_REGISTRATION_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_INVITATION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_PASSWORD_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_RECOMMENDATION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.NotificationDAO;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserNotification;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.NotificationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.AdvertRecommendationDTO;
import com.zuehlke.pgadmissions.dto.MailMessageDTO;
import com.zuehlke.pgadmissions.dto.NotificationDefinitionModelDTO;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinitionDTO;
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
        return entityService.list(NotificationDefinition.class);
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
        User author = systemService.getSystem().getUser();
        LocalDate baseline = new LocalDate();
        Set<User> exclusions = sendIndividualRequestNotifications(resource, comment, author, baseline);
        sendIndividualUpdateNotifications(resource, comment, author, exclusions, baseline);
        entityService.flush();
    }
    
    public void sendSyndicatedUserNotifications() {
        
    }

    public List<Integer> getRecommendationDefinitions(LocalDate baseline) {
        return notificationDAO.getRecommendationDefinitions(baseline);
    }

    public void sendRecommendationNotification(Integer userId, LocalDate baseline, LocalDate lastRecommendedBaseline) {
        User user = userService.getById(userId);
        List<AdvertRecommendationDTO> advertRecommendations = advertService.getRecommendedAdverts(user);
        if (!advertRecommendations.isEmpty()) {
            System system = systemService.getSystem();
            User author = system.getUser();

            NotificationDefinition definition = getById(SYSTEM_RECOMMENDATION_NOTIFICATION);
            sendNotification(definition, new NotificationDefinitionModelDTO().withUser(user).withAuthor(author).withResource(system)
                    .withTransitionAction(SYSTEM_MANAGE_ACCOUNT).withAdvertRecommendations(advertRecommendations));
        }
    }

    public void sendNotification(PrismNotificationDefinition notificationTemplateId, NotificationDefinitionModelDTO modelDTO) {
        NotificationDefinition notificationTemplate = getById(notificationTemplateId);
        sendNotification(notificationTemplate, modelDTO);
    }

    public void sendInvitationNotifications(Comment comment) {
        for (CommentAssignedUser assignee : comment.getAssignedUsers()) {
            User invitee = assignee.getUser();
            if (assignee.getRoleTransitionType() == CREATE && invitee.getUserAccount() == null) {
                sendInvitationNotification(comment.getUser(), invitee);
            }
        }
    }

    public void sendInvitationNotification(User user, User invitee) {
        System system = systemService.getSystem();
        NotificationDefinition definition = getById(SYSTEM_INVITATION_NOTIFICATION);
        sendNotification(definition, new NotificationDefinitionModelDTO().withUser(invitee).withAuthor(system.getUser()).withInvoker(user).withResource(system)
                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST));
    }

    public void sendRegistrationNotification(User user, ActionOutcomeDTO actionOutcome) {
        sendRegistrationNotification(user, actionOutcome, null);
    }

    public void sendRegistrationNotification(User user, ActionOutcomeDTO actionOutcome, Comment comment) {
        System system = systemService.getSystem();
        sendNotification(SYSTEM_COMPLETE_REGISTRATION_REQUEST,
                new NotificationDefinitionModelDTO().withUser(user).withAuthor(system.getUser()).withResource(actionOutcome.getTransitionResource())
                        .withComment(comment).withTransitionAction(actionOutcome.getTransitionAction().getId()));
    }

    public void sendResetPasswordNotification(User user, String newPassword) {
        System system = systemService.getSystem();
        sendNotification(SYSTEM_PASSWORD_NOTIFICATION, new NotificationDefinitionModelDTO().withUser(user).withAuthor(system.getUser()).withResource(system)
                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST).withNewPassword(newPassword));
    }

    public List<PrismNotificationDefinition> getEditableTemplates(PrismScope scope) {
        return (List<PrismNotificationDefinition>) (List<?>) customizationService.getDefinitions(PrismConfiguration.NOTIFICATION, scope);
    }

    public void resetNotifications(Resource resource) {
        resource.setLastRemindedRequestIndividual(null);
        resource.setLastRemindedRequestSyndicated(null);
        resource.setLastNotifiedUpdateSyndicated(null);
    }

    public void resetNotifications(User user) {
        notificationDAO.resetNotifications(user);
        for (PrismScope scope : PrismScope.values()) {
            List<PrismScope> parentScopes = scopeService.getParentScopesDescending(scope, SYSTEM);
            List<Integer> resourceIds = resourceService.getResources(user, scope, parentScopes).stream().map(a -> a.getId()).collect(Collectors.toList());
            if (!resourceIds.isEmpty()) {
                notificationDAO.resetNotificationsSyndicated(scope, resourceIds);
            }
        }
    }

    public void resetNotifications(UserRole userRole) {
        User user = userRole.getUser();
        Role role = userRole.getRole();
        List<NotificationDefinition> individualDefinitions = notificationDAO.getNotificationDefinitionsIndividual(role);
        notificationDAO.resetNotifications(user, individualDefinitions);
    }

    private Set<User> sendIndividualRequestNotifications(Resource resource, Comment comment, User author, LocalDate baseline) {
        Set<User> recipients = Sets.newHashSet();
        List<UserNotificationDefinitionDTO> requests = notificationDAO.getIndividualRequestDefinitions(resource, baseline);

        if (requests.size() > 0) {
            for (UserNotificationDefinitionDTO request : requests) {
                User user = userService.getById(request.getUserId());
                NotificationDefinition definition = getById(request.getNotificationDefinitionId());

                sendNotification(definition,
                        new NotificationDefinitionModelDTO().withUser(user).withAuthor(author).withResource(resource).withComment(comment)
                                .withTransitionAction(request.getActionId()));

                recipients.add(user);
                createOrUpdateUserNotification(resource, user, definition, baseline);
            }
        }

        return recipients;
    }

    private void sendIndividualUpdateNotifications(Resource resource, Comment comment, User author, Set<User> userExclusions, LocalDate baseline) {
        List<UserNotificationDefinitionDTO> updates = notificationDAO.getIndividualUpdateDefinitions(resource, comment.getAction(), userExclusions);

        if (updates.size() > 0) {
            PrismAction transitionActionId = actionService.getViewEditAction(resource).getId();

            for (UserNotificationDefinitionDTO update : updates) {
                User user = userService.getById(update.getUserId());
                NotificationDefinition definition = getById(update.getNotificationDefinitionId());

                sendNotification(definition,
                        new NotificationDefinitionModelDTO().withUser(user).withAuthor(author).withResource(resource).withComment(comment)
                                .withTransitionAction(transitionActionId));

                createOrUpdateUserNotification(resource, user, definition, baseline);
            }
        }
    }

    private void sendNotification(NotificationDefinition template, NotificationDefinitionModelDTO modelDTO) {
        User user = modelDTO.getUser();
        NotificationConfiguration configuration = getNotificationConfiguration(modelDTO.getResource(), user, template);
        MailMessageDTO message = new MailMessageDTO();

        message.setConfiguration(configuration);
        message.setModelDTO(modelDTO);

        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(modelDTO.getResource());
        applicationContext.getBean(MailSender.class).localize(propertyLoader).sendEmail(message);
    }

    public void createOrUpdateUserNotification(Resource resource, User user, NotificationDefinition definition, LocalDate baseline) {
        entityService.createOrUpdate(new UserNotification().withResource(resource).withUser(user).withNotificationDefinition(definition)
                .withLastNotifiedDate(baseline));
    }

}
