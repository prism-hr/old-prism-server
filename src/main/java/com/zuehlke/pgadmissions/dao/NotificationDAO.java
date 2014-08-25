package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateActionNotification;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserNotification;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinitionDTO;

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

    public void deleteObseleteNotificationConfigurations(List<NotificationTemplate> activeNotificationWorkflowTemplates) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete NotificationConfiguration " //
                        + "where notificationTemplate not in (:configurableTemplates)") //
                .setParameterList("configurableTemplates", activeNotificationWorkflowTemplates) //
                .executeUpdate();
    }

    public UserNotification getUserNotification(Resource resource, UserRole userRole, NotificationTemplate notificationTemplate) {
        return (UserNotification) sessionFactory.getCurrentSession().createCriteria(UserNotification.class) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCaseName(), resource.getId())) //
                .add(Restrictions.eq("userRole", userRole)) //
                .add(Restrictions.eq("notificationTemplate", notificationTemplate)) //
                .uniqueResult();
    }

    public List<UserNotificationDefinitionDTO> getRequestNotifications(Resource resource, User invoker) {
        String resourceReference = resource.getResourceScope().getLowerCaseName();

        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(resource.getClass(), resourceReference) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty(resourceReference + ".id"), "resourceId") //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("role.id"), "roleId") //
                        .add(Projections.groupProperty("notificationTemplate.id"), "notificationTemplateId")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("stateAction.notificationTemplate", "notificationTemplate", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", resource.getId())) //
                .add(Restrictions.eq("notificationTemplate.notificationType", PrismNotificationType.INDIVIDUAL)) //
                .add(Restrictions.ne("userRole.user", invoker)) //
                .add(Restrictions.isNull("userRole.notificationTemplate")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("user.userAccount")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }
    
    public List<UserNotificationDefinitionDTO> getUpdateNotifications(Resource resource, Action action, User invoker) {
        String resourceReference = resource.getResourceScope().getLowerCaseName();

        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(resource.getClass(), resourceReference) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty(resourceReference + ".id"), "resourceId") //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("role.id"), "roleId") //
                        .add(Projections.groupProperty("notificationTemplate.id"), "notificationTemplateId")) //
                .createAlias("previousState", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionNotifications", "stateActionNotification", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.role", "role", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.notificationTemplate", "notificationTemplate", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", resource.getId())) //
                .add(Restrictions.eq("stateAction.action", action))
                .add(Restrictions.eq("notificationTemplate.notificationType", PrismNotificationType.INDIVIDUAL)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("action.actionCategory", PrismActionCategory.CREATE_RESOURCE)) //
                        .add(Restrictions.ne("userRole.user", invoker))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("user.userAccount")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }
    
    public List<UserNotificationDefinitionDTO> getRequestReminders(Scope scope, LocalDate baseline) {
        PrismScope scopeId = scope.getId();
        Class<? extends Resource> resourceClass = scopeId.getResourceClass();
        String resourceReference = scope.getId().getLowerCaseName();

        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(resourceClass, resourceReference) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty(scope.getId().getLowerCaseName() + ".id"), "resourceId") //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("role.id"), "roleId") //
                        .add(Projections.groupProperty("notificationTemplate.id"), "notificationTemplateId") //
                        .add(Projections.property("userRole.notificationLastSentDate"), "lastSentDate")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("stateAction.notificationTemplate", "notificationTemplate", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("notificationTemplate.notificationType", PrismNotificationType.INDIVIDUAL)) //
                .add(Restrictions.eqProperty("stateAction.notificationTemplate", "userRole.notificationTemplate"))
                .add(Restrictions.lt("userRole.notificationLastSentDate", baseline)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eqProperty("userRole.system", "system")) //
                        .add(Restrictions.eqProperty("userRole.institution", "institution")) //
                        .add(Restrictions.eqProperty("userRole.program", "program")) //
                        .add(Restrictions.eqProperty("userRole.project", "project")) //
                        .add(Restrictions.eqProperty("userRole.application", "application"))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("user.userAccount")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }
    
    public List<UserNotificationDefinitionDTO> getSyndicatedRequestNotifications(Scope scope, LocalDate baseline) {
        PrismScope scopeId = scope.getId();
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(scopeId.getResourceClass(), scopeId.getLowerCaseName()) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("notificationTemplate.id"), "notificationTemplateId") //
                        .add(Projections.property("userNotification.lastSentDate"), "lastSentDate")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("stateAction.notificationTemplate", "notificationTemplate", JoinType.INNER_JOIN) //
                .createAlias("user.userNotifications", "userNotification", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq("notificationTemplate.notificationType", PrismNotificationType.SYNDICATED)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eqProperty("userRole.system", "system")) //
                        .add(Restrictions.eqProperty("userRole.institution", "institution")) //
                        .add(Restrictions.eqProperty("userRole.program", "program")) //
                        .add(Restrictions.eqProperty("userRole.project", "project")) //
                        .add(Restrictions.eqProperty("userRole.application", "application"))) //
                .add(Restrictions.disjunction()
                        .add(Restrictions.isNull("userNotification.id"))
                        .add(Restrictions.lt("userNotification.lastSentDate", baseline))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("user.userAccount")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getSyndicatedUpdateNotifications(Scope scope, LocalDate baseline) {
        DateTime updateRangeStart = baseline.minusDays(1).toDateTimeAtStartOfDay();
        DateTime updateRangeClose = updateRangeStart.plusDays(1).minusSeconds(1);
        
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(Comment.class, "comment") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("notificationTemplate.id"), "notificationTemplateId") //
                        .add(Projections.property("userNotification.lastSentDate"), "lastSentDate")) //
                .createAlias("comment.transitionState", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionNotifications", "stateActionNotification", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.notificationTemplate", "notificationTemplate", JoinType.INNER_JOIN) //
                .createAlias("user.userNotifications", "userNotification", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.between("createdTimestamp", updateRangeStart, updateRangeClose)) //
                .add(Restrictions.eq("stateAction.action", "action")) //
                .add(Restrictions.eq("notificationTemplate.notificationType", PrismNotificationType.SYNDICATED)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eqProperty("userRole.system", "system")) //
                        .add(Restrictions.eqProperty("userRole.institution", "institution")) //
                        .add(Restrictions.eqProperty("userRole.program", "program")) //
                        .add(Restrictions.eqProperty("userRole.project", "project")) //
                        .add(Restrictions.eqProperty("userRole.application", "application"))) //
                .add(Restrictions.disjunction()
                        .add(Restrictions.isNull("userNotification.id"))
                        .add(Restrictions.lt("userNotification.lastSentDate", baseline))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("user.userAccount")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .list();
    }
    
    public List<User> getRecommendationNotifications(LocalDate baseline) {
        LocalDate lastSentBaseline = baseline.minusDays(7);
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .setProjection(Projections.property("id")) //
                .createAlias("userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("userNotifications", "userNotification", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq("userAccount.sendRecommendationEmail", true)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userNotification.id")) //
                        .add(Restrictions.lt("lastSentDate", lastSentBaseline))) //
                .list();
    }

}
