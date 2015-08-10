package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getUserRoleConstraint;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_RECOMMENDATION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType.INDIVIDUAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType.SYNDICATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
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
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.NotificationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.Role;
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

    public void deleteObsoleteNotificationConfigurations(List<NotificationDefinition> definitions) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete NotificationConfiguration " //
                        + "where definition not in (:definitions)") //
                .setParameterList("definitions", definitions) //
                .executeUpdate();
    }

    public List<UserNotificationDefinitionDTO> getIndividualRequestDefinitions(Resource<?> resource, User invoker, LocalDate baseline) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("notificationDefinition.id"), "notificationDefinitionId") //
                        .add(Projections.groupProperty("stateAction.action.id"), "actionId")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userNotifications", "userNotification", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.eq("userNotification." + resourceReference, resource)) //
                                .add(Restrictions.eqProperty("notificationDefinition.id", "userNotification.notificationDefinition.id"))) //
                .add(Restrictions.eq("notificationDefinition.notificationType", INDIVIDUAL)) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(getUserRoleConstraint(resource, "stateActionAssignment")) //
                .add(Restrictions.isNull("userNotification.id")) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getIndividualUpdateDefinitions(Resource<?> resource, Action action, Set<User> exclusions) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourcePreviousState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
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
                .add(Restrictions.eq(resourceReference, resource)); //

        if (!exclusions.isEmpty()) {
            criteria.add(Restrictions.not( //
                    Restrictions.in("userRole.user", exclusions))); //
        }

        return (List<UserNotificationDefinitionDTO>) criteria //
                .add(getUserRoleConstraint(resource)) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getIndividualReminderDefinitions(Resource<?> resource, LocalDate baseline) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("notificationDefinition.id"), "notificationDefinitionId") //
                        .add(Projections.groupProperty("stateAction.action.id"), "actionId")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userNotifications", "userNotification", JoinType.INNER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.eq("userNotification." + resourceReference, resource)) //
                                .add(Restrictions.eqProperty("notificationDefinition.id", "userNotification.notificationDefinition.id"))) //
                .add(Restrictions.eq("notificationDefinition.notificationType", INDIVIDUAL)) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(getUserRoleConstraint(resource, "stateActionAssignment")) //
                .add(Restrictions.lt("userNotification.lastNotifiedDate", baseline)) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getSyndicatedRequestDefinitions(Resource<?> resource, LocalDate baseline) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("notificationDefinition.id"), "notificationDefinitionId")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userNotifications", "userNotification", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("userNotification.system")) //
                                .add(Restrictions.eqProperty("notificationDefinition.id", "userNotification.notificationDefinition.id"))) //
                .add(Restrictions.eq("notificationDefinition.notificationType", SYNDICATED)) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(getUserRoleConstraint(resource, "stateActionAssignment")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userNotification.id")) //
                        .add(Restrictions.lt("userNotification.lastNotifiedDate", baseline))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getSyndicatedUpdateDefinitions(Resource<?> resource, Action action, User invoker, LocalDate baseline) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(CommentState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("notificationDefinition.id"), "notificationDefinitionId")) //
                .createAlias("comment", "comment", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionNotifications", "stateActionNotification", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.role", "role", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userNotifications", "userNotification", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("userNotification.system")) //
                                .add(Restrictions.eqProperty("notificationDefinition.id", "userNotification.notificationDefinition.id"))) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.eq("notificationDefinition.notificationType", SYNDICATED)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.user", resource.getUser())) //
                        .add(Restrictions.ne("userRole.user", invoker))) //
                .add(Restrictions.eq("comment." + resourceReference, resource)) //
                .add(getUserRoleConstraint(resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userNotification.id")) //
                        .add(Restrictions.lt("userNotification.lastNotifiedDate", baseline))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<Integer> getRecommendationDefinitions(LocalDate baseline) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userNotifications", "userNotification", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.eq("userNotification.notificationDefinition.id", SYSTEM_APPLICATION_RECOMMENDATION_NOTIFICATION)) //
                .add(Restrictions.eq("userRole.role.id", APPLICATION_CREATOR)) //
                .add(Restrictions.eq("userAccount.sendApplicationRecommendationNotification", true)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userNotification.id")) //
                        .add(Restrictions.lt("userNotification.lastNotifiedDate", baseline))) //
                .list();
    }

    public UserNotification getUserNotification(Resource<?> resource, User user, NotificationDefinition notificationDefinition) {
        return (UserNotification) sessionFactory.getCurrentSession().createCriteria(UserNotification.class)
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource))
                .add(Restrictions.eq("user", user))
                .add(Restrictions.eq("notificationDefinition", notificationDefinition))
                .uniqueResult();
    }

    public List<Integer> getRecentSyndicatedUserNotifications(Resource<?> resource, User user, LocalDate baseline) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserNotification.class)
                .setProjection(Projections.property("id")) //
                .createAlias("notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq("notificationDefinition.notificationType", SYNDICATED)) //
                .add(Restrictions.ge("lastNotifiedDate", baseline)) //
                .list();
    }

    public void resetNotifications(User user) {
        sessionFactory.getCurrentSession().createQuery(
                "delete from UserNotification " //
                        + "where user = :user")
                .setParameter("user", user)
                .executeUpdate();
    }

    public void resetNotifications(User user, List<NotificationDefinition> definitions) {
        sessionFactory.getCurrentSession().createQuery(
                "delete from UserNotification " //
                        + "where user = :user " //
                        + "and notificationDefinition in (:definitions)") //
                .setParameter("user", user) //
                .setParameterList("definitions", definitions) //
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

    public List<NotificationDefinition> getNotificationDefinitionsIndividual(Role role) {
        return (List<NotificationDefinition>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("notificationDefinition")) //
                .createAlias("notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateActionAssignment.role", role)) //
                .list();
    }

}
