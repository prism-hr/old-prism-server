package com.zuehlke.pgadmissions.dao;

import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinitionDTO;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class NotificationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public NotificationConfiguration getConfiguration(Resource resource, NotificationTemplate template) {
        return (NotificationConfiguration) sessionFactory.getCurrentSession().createCriteria(NotificationConfiguration.class) //
                .add(Restrictions.eq("notificationTemplate", template)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("system", resource.getSystem())) //
                        .add(Restrictions.eq("institution", resource.getInstitution())) //
                        .add(Restrictions.eq("program", resource.getProgram()))) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<NotificationTemplate> getWorkflowRequestTemplates() {
        return (List<NotificationTemplate>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("notificationTemplate")) //
                .list();
    }

    public List<NotificationTemplate> getWorkflowUpdateTemplates() {
        return (List<NotificationTemplate>) sessionFactory.getCurrentSession().createCriteria(StateActionNotification.class) //
                .setProjection(Projections.groupProperty("notificationTemplate")) //
                .list();
    }

    public void deleteObsoleteNotificationConfigurations(List<NotificationTemplate> activeNotificationWorkflowTemplates) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete NotificationConfiguration " //
                        + "where notificationTemplate not in (:configurableTemplates)") //
                .setParameterList("configurableTemplates", activeNotificationWorkflowTemplates) //
                .executeUpdate();
    }

    public List<UserNotificationDefinitionDTO> getIndividualRequestNotifications(Resource resource, User invoker) {
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(Action.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("role.id"), "roleId") //
                        .add(Projections.groupProperty("notificationTemplate.id"), "notificationTemplateId")
                        .add(Projections.groupProperty("stateAction.action.id"), "actionId")) //
                .createAlias("stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
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
                        .add(Restrictions.isNull("userAccount.password")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .add(Restrictions.isNull("userRole.lastNotifiedDate")) //
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
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", state)) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.eq("notificationTemplate.notificationType", PrismNotificationType.INDIVIDUAL)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("actionCategory", PrismActionCategory.CREATE_RESOURCE)) //
                        .add(Restrictions.ne("userRole.user", invoker))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userAccount.password")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .add(Restrictions.lt("userRole.lastNotifiedDate", baseline)) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getIndividualRequestReminders(Resource resource, LocalDate baseline) {
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(Action.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("role.id"), "roleId") //
                        .add(Projections.groupProperty("notificationTemplate.id"), "notificationTemplateId")
                        .add(Projections.groupProperty("stateAction.action.id"), "actionId")) //
                .createAlias("stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("stateAction.notificationTemplate", "notificationTemplate", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", resource.getState())) //
                .add(Restrictions.eq("notificationTemplate.notificationType", PrismNotificationType.INDIVIDUAL)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userAccount.password")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
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
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
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
                        .add(Restrictions.isNull("userAccount.password")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull(lastNotifiedDateReference))
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
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", state)) //
                .add(Restrictions.eq("stateAction.action", action))
                .add(Restrictions.eq("notificationTemplate.notificationType", PrismNotificationType.SYNDICATED)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("actionCategory", PrismActionCategory.CREATE_RESOURCE)) //
                        .add(Restrictions.ne("userRole.user", invoker))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userAccount.password")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull(lastNotifiedDateReference))
                        .add(Restrictions.lt(lastNotifiedDateReference, baseline))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<User> getRecommendationNotifications(LocalDate baseline) {
        LocalDate lastSentBaseline = baseline.minusWeeks(1);
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .setProjection(Projections.property("id")) //
                .createAlias("userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("userAccount.sendRecommendationNotification", true)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userAccount.lastNotifiedDateRecommendation")) //
                        .add(Restrictions.lt("userAccount.lastNotifiedDateRecommendation", lastSentBaseline))) //
                .list();
    }

    public List<PrismNotificationTemplate> getAvailableTemplates(PrismScope scope) {
        return (List<PrismNotificationTemplate>) sessionFactory.getCurrentSession().createCriteria(NotificationTemplate.class) //
                .setProjection(Projections.property("id")) //
                .createAlias("scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.ge("scope.precedence", scope.getPrecedence())) //
                .addOrder(Order.asc("scope.id")) //
                .addOrder(Order.asc("id")) //
                .list();
    }

}
