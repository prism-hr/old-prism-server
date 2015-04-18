package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_RECOMMENDATION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType.INDIVIDUAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType.SYNDICATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;

import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.comment.CommentState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourcePreviousState;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserNotification;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.NotificationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.StateAction;
import com.zuehlke.pgadmissions.domain.workflow.StateActionNotification;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinitionDTO;

@Repository
@SuppressWarnings("unchecked")
public class NotificationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<NotificationDefinition> getWorkflowRequestDefinitions() {
        return (List<NotificationDefinition>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("notificationDefinition")) //
                .list();
    }

    public List<NotificationDefinition> getWorkflowUpdateDefinitions() {
        return (List<NotificationDefinition>) sessionFactory.getCurrentSession().createCriteria(StateActionNotification.class) //
                .setProjection(Projections.groupProperty("notificationDefinition")) //
                .list();
    }

    public void deleteObsoleteNotificationConfigurations(List<NotificationDefinition> activeDefinitions) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete NotificationConfiguration " //
                        + "where notificationDefinition not in (:configurableTemplates)") //
                .setParameterList("configurableTemplates", activeDefinitions) //
                .executeUpdate();
    }

    public List<UserNotificationDefinitionDTO> getIndividualRequestDefinitions(Resource resource, User invoker, LocalDate baseline) {
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("role.id"), "roleId") //
                        .add(Projections.groupProperty("notificationDefinition.id"), "notificationDefinitionId") //
                        .add(Projections.groupProperty("stateAction.action.id"), "actionId")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("userRole.userNotifications", "userNotification", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.eq("userNotification." + resource.getResourceScope().getLowerCamelName(), resource)) //
                                .add(Restrictions.eqProperty("notificationDefinition.id", "userNotification.notificationDefinition.id"))) //
                .add(Restrictions.eq("notificationDefinition.notificationType", INDIVIDUAL)) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .add(Restrictions.isNull("userNotification.id")) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getIndividualUpdateDefinitions(Resource resource, Action action, Set<User> exclusions, LocalDate baseline) {
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(ResourcePreviousState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("role.id"), "roleId") //
                        .add(Projections.groupProperty("notificationDefinition.id"), "notificationDefinitionId")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionNotifications", "stateActionNotification", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.role", "role", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.eq("notificationDefinition.notificationType", INDIVIDUAL)) //
                .add(Restrictions.not( //
                        Restrictions.in("userRole.user", exclusions))) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getIndividualReminderDefinitions(Resource resource, LocalDate baseline) {
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("role.id"), "roleId") //
                        .add(Projections.groupProperty("notificationDefinition.id"), "notificationDefinitionId") //
                        .add(Projections.groupProperty("stateAction.action.id"), "actionId")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("userRole.userNotifications", "userNotification", JoinType.INNER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.eq("userNotification." + resource.getResourceScope().getLowerCamelName(), resource)) //
                                .add(Restrictions.eqProperty("notificationDefinition.id", "userNotification.notificationDefinition.id"))) //
                .add(Restrictions.eq("notificationDefinition.notificationType", INDIVIDUAL)) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .add(Restrictions.lt("userNotification.lastNotifiedDate", baseline)) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getSyndicatedRequestDefinitions(Resource resource, LocalDate baseline) {
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("role.id"), "roleId") //
                        .add(Projections.groupProperty("notificationDefinition.id"), "notificationDefinitionId")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("userRole.userNotifications", "userNotification", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("userNotification.system")) //
                                .add(Restrictions.eqProperty("notificationDefinition.id", "userNotification.notificationDefinition.id"))) //
                .add(Restrictions.eq("notificationDefinition.notificationType", SYNDICATED)) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userNotification.id")) //
                        .add(Restrictions.lt("userNotification.lastNotifiedDate", baseline))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getSyndicatedUpdateDefinitions(Resource resource, Action action, User invoker, LocalDate baseline) {
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(CommentState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("role.id"), "roleId") //
                        .add(Projections.groupProperty("notificationDefinition.id"), "notificationDefinitionId")) //
                .createAlias("comment", "comment", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionNotifications", "stateActionNotification", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.role", "role", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("userRole.userNotifications", "userNotification", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("userNotification.system")) //
                                .add(Restrictions.eqProperty("notificationDefinition.id", "userNotification.notificationDefinition.id"))) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.eq("notificationDefinition.notificationType", SYNDICATED)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.user", resource.getUser())) //
                        .add(Restrictions.ne("userRole.user", invoker))) //
                .add(Restrictions.eq("comment." + resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userNotification.id")) //
                        .add(Restrictions.lt("userNotification.lastNotifiedDate", baseline))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getRecommendationDefinitions(Integer userId, LocalDate baseline) {
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("application.id"), "resourceId") //
                        .add(Projections.groupProperty("role.id"), "roleId")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("userNotifications", "userNotification", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("userNotification.application")) //
                                .add(Restrictions.eq("notificationDefinition.id", SYSTEM_APPLICATION_RECOMMENDATION_NOTIFICATION))) //
                .add(Restrictions.eq("user.id", userId)) //
                .add(Restrictions.eq("role.id", APPLICATION_CREATOR)) //
                .add(Restrictions.eq("userAccount.sendApplicationRecommendationNotification", true)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userNotification.id")) //
                        .add(Restrictions.lt("userNotification.lastNotifiedDate", baseline))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public UserNotification getUserNotification(Resource resource, UserRole userRole, NotificationDefinition notificationDefinition) {
        return (UserNotification) sessionFactory.getCurrentSession().createCriteria(UserNotification.class)
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource))
                .add(Restrictions.eq("userRole", userRole))
                .add(Restrictions.eq("notificationDefinition", notificationDefinition))
                .uniqueResult();
    }

    public List<Integer> getRecentSyndicatedUserNotifications(Resource resource, User user, LocalDate baseline) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserNotification.class)
                .setProjection(Projections.property("id")) //
                .createAlias("userRole", "userRole", JoinType.INNER_JOIN) //
                .createAlias("notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.eq("notificationDefinition.notificationType", SYNDICATED)) //
                .add(Restrictions.ge("lastNotifiedDate", baseline)) //
                .list();
    }

    public void resetNotificationsIndividual(User user) {
        sessionFactory.getCurrentSession().createQuery( //
                "update UserNotification " //
                        + "set lastNotifiedDate = null " //
                        + "where userRole in (select id from UserRole where user = :user)") //
                .setParameter("user", user) //
                .executeUpdate();
    }

    public void removeUserNotifications(UserRole userRole) {
        sessionFactory.getCurrentSession().createQuery(
                "delete from UserNotification where userRole = :userRole")
                .setParameter("userRole", userRole)
                .executeUpdate();
    }

    public void resetNotificationsSyndicated(PrismScope resourceScope, Set<Integer> assignedResources) {
        sessionFactory.getCurrentSession().createQuery( //
                "update " + resourceScope.getResourceClass().getSimpleName() + " " //
                        + "set lastRemindedRequestIndividual = null, " //
                        + "lastRemindedRequestSyndicated = null, " //
                        + "lastNotifiedUpdateSyndicated = null " //
                        + "where id in (:assignedResources)") //
                .setParameterList("assignedResources", assignedResources) //
                .executeUpdate();
    }

}
