package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.NotificationTemplateDefinition;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateAction;
import com.zuehlke.pgadmissions.domain.workflow.StateActionNotification;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinitionDTO;

@Repository
@SuppressWarnings("unchecked")
public class NotificationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<NotificationTemplateDefinition> getWorkflowRequestTemplates() {
        return (List<NotificationTemplateDefinition>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("notificationTemplate")) //
                .list();
    }

    public List<NotificationTemplateDefinition> getWorkflowUpdateTemplates() {
        return (List<NotificationTemplateDefinition>) sessionFactory.getCurrentSession().createCriteria(StateActionNotification.class) //
                .setProjection(Projections.groupProperty("notificationTemplate")) //
                .list();
    }

    public void deleteObsoleteNotificationConfigurations(List<NotificationTemplateDefinition> activeTemplates) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete NotificationConfiguration " //
                        + "where notificationTemplate not in (:configurableTemplates)") //
                .setParameterList("configurableTemplates", activeTemplates) //
                .executeUpdate();
    }

    public List<UserNotificationDefinitionDTO> getIndividualRequestNotifications(Resource resource, User invoker, LocalDate baseline) {
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(Action.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("role.id"), "roleId") //
                        .add(Projections.groupProperty("notificationTemplate.id"), "notificationTemplateId") //
                        .add(Projections.groupProperty("stateAction.action.id"), "actionId")) //
                .createAlias("stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("stateAction.notificationTemplate", "notificationTemplate", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", resource.getState())) //
                .add(Restrictions.eq("notificationTemplate.notificationType", PrismNotificationType.INDIVIDUAL)) //
                .add(Restrictions.ne("userRole.user", invoker)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userRole.lastNotifiedDate")) //
                        .add(Restrictions.lt("userRole.lastNotifiedDate", baseline))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getIndividualUpdateNotifications(Resource resource, State state, Action action, User invoker, LocalDate baseline) {
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(Action.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("role.id"), "roleId") //
                        .add(Projections.groupProperty("notificationTemplate.id"), "notificationTemplateId")) //
                .createAlias("stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionNotifications", "stateActionNotification", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.role", "role", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.notificationTemplate", "notificationTemplate", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", state)) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.eq("notificationTemplate.notificationType", PrismNotificationType.INDIVIDUAL)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.user", resource.getUser())) //
                        .add(Restrictions.ne("userRole.user", invoker))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getIndividualRequestReminders(Resource resource, LocalDate baseline) {
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(Action.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("role.id"), "roleId") //
                        .add(Projections.groupProperty("notificationTemplate.id"), "notificationTemplateId") //
                        .add(Projections.groupProperty("stateAction.action.id"), "actionId")) //
                .createAlias("stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("stateAction.notificationTemplate", "notificationTemplate", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", resource.getState())) //
                .add(Restrictions.eq("notificationTemplate.notificationType", PrismNotificationType.INDIVIDUAL)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .add(Restrictions.lt("userRole.lastNotifiedDate", baseline)) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getSyndicatedRequestNotifications(Resource resource, LocalDate baseline) {
        String lastNotifiedDateReference = "user.lastNotifiedDate" + resource.getClass().getSimpleName();

        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(Action.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("notificationTemplate.id"), "notificationTemplateId")) //
                .createAlias("stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("stateAction.notificationTemplate", "notificationTemplate", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", resource.getState())) //
                .add(Restrictions.eq("notificationTemplate.notificationType", PrismNotificationType.SYNDICATED)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull(lastNotifiedDateReference)) //
                        .add(Restrictions.lt(lastNotifiedDateReference, baseline))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getSyndicatedUpdateNotifications(Resource resource, State state, Action action, User invoker, LocalDate baseline) {
        String lastNotifiedDateReference = "user.lastNotifiedDate" + resource.getClass().getSimpleName();

        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(Action.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("notificationTemplate.id"), "notificationTemplateId")) //
                .createAlias("stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionNotifications", "stateActionNotification", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.role", "role", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.notificationTemplate", "notificationTemplate", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", state)) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.eq("notificationTemplate.notificationType", PrismNotificationType.SYNDICATED)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.user", resource.getUser())) //
                        .add(Restrictions.ne("userRole.user", invoker))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull(lastNotifiedDateReference)) //
                        .add(Restrictions.lt(lastNotifiedDateReference, baseline))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<User> getRecommendationNotifications(LocalDate baseline) {
        LocalDate lastSentBaseline = baseline.minusWeeks(1);
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .setProjection(Projections.property("id")) //
                .createAlias("userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("userAccount.sendApplicationRecommendationNotification", true)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userAccount.sendApplicationRecommendationNotification")) //
                        .add(Restrictions.lt("userAccount.lastNotifiedDateApplicationRecommendation", lastSentBaseline))) //
                .list();
    }

    public List<PrismNotificationTemplate> geEditableTemplates(PrismScope scope) {
        return (List<PrismNotificationTemplate>) sessionFactory.getCurrentSession().createCriteria(NotificationTemplateDefinition.class) //
                .setProjection(Projections.property("id")) //
                .createAlias("scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.ge("scope.precedence", scope.getPrecedence())) //
                .addOrder(Order.asc("scope.id")) //
                .addOrder(Order.asc("id")) //
                .list();
    }

}
