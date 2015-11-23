package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.dao.WorkflowDAO.getTargetActionConstraint;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType.INDIVIDUAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserNotification;
import com.zuehlke.pgadmissions.domain.workflow.NotificationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.StateAction;
import com.zuehlke.pgadmissions.domain.workflow.StateActionNotification;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinitionDTO;

@Repository
@SuppressWarnings("unchecked")
public class NotificationDAO {

    @Inject
    private WorkflowDAO workflowDAO;

    @Inject
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

    public List<UserNotificationDefinitionDTO> getIndividualRequestDefinitions(PrismScope scope, Resource resource) {
        return getIndividualRequestDefinitionCriteria(workflowDAO.getWorkflowCriteriaList(scope, getInvidualRequestDefinitionsProjection()), resource)
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getIndividualRequestDefinitions(PrismScope scope, PrismScope parentScope, Resource resource) {
        return getIndividualRequestDefinitionCriteria(workflowDAO.getWorkflowCriteriaList(scope, parentScope, getInvidualRequestDefinitionsProjection()), resource)
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getIndividualRequestDefinitions(PrismScope scope, PrismScope targeterScope, PrismScope targetScope,
            Collection<Integer> targeterEntities, Resource resource) {
        return getIndividualRequestDefinitionCriteria(
                workflowDAO.getWorkflowCriteriaList(scope, targeterScope, targetScope, targeterEntities, getInvidualRequestDefinitionsProjection()), resource)
                        .add(getTargetActionConstraint())
                        .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                        .list();
    }

    public List<UserNotificationDefinitionDTO> getIndividualUpdateDefinitions(PrismScope scope, Comment comment, Collection<User> exclusions) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CommentState.class)
                .setProjection(getIndividualUpdateDefinitionsProjection()) //
                .createAlias("comment", "comment", JoinType.INNER_JOIN) //
                .createAlias("comment." + scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN,
                        Restrictions.eq("stateActionAssignment.externalMode", false)) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN,
                        Restrictions.isNull("stateAction.actionCondition")) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.eqProperty("state", "stateAction.state")) //
                .add(Restrictions.isNull("state.hidden")) //
                .add(Restrictions.eq("action.systemInvocationOnly", false));

        return getIndividualUpdateDefinitionCriteria(criteria, comment, exclusions)
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)).list();
    }

    public List<UserNotificationDefinitionDTO> getIndividualUpdateDefinitions(PrismScope scope, PrismScope parentScope, Comment comment, Collection<User> exclusions) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CommentState.class) //
                .setProjection(getIndividualUpdateDefinitionsProjection()) //
                .createAlias("comment", "comment", JoinType.INNER_JOIN) //
                .createAlias("comment." + scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource." + parentScope.getLowerCamelName(), "parentResource", JoinType.INNER_JOIN) //
                .createAlias("parentResource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN,
                        Restrictions.eq("stateActionAssignment.externalMode", false)) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN,
                        Restrictions.isNull("stateAction.actionCondition")) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.eqProperty("state", "stateAction.state")) //
                .add(Restrictions.isNull("state.hidden")) //
                .add(Restrictions.eq("action.systemInvocationOnly", false));

        return getIndividualUpdateDefinitionCriteria(criteria, comment, exclusions)
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)).list();
    }

    public List<UserNotificationDefinitionDTO> getIndividualUpdateDefinitions(PrismScope scope, PrismScope targeterScope, PrismScope targetScope,
            Collection<Integer> targeterEntities, Comment comment, Collection<User> exclusions) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CommentState.class) //
                .setProjection(getIndividualUpdateDefinitionsProjection()) //
                .createAlias("comment", "comment", JoinType.INNER_JOIN) //
                .createAlias("comment." + scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.targets", "target", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert." + targeterScope.getLowerCamelName(), "targeterResource", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("targeterResource.advert", "targeterAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("targeterAdvert.targets", "targeterTarget", JoinType.INNER_JOIN) //
                .createAlias("targeterTarget.targetAdvert", "targetAdvert", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert." + targetScope.getLowerCamelName(), "targetResource", JoinType.INNER_JOIN) //
                .createAlias("targetResource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN,
                        Restrictions.eq("stateActionAssignment.externalMode", true)) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN,
                        Restrictions.isNull("stateAction.actionCondition")) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.in(scope.equals(APPLICATION) ? "resource.id" : "targeterResource.advert.id", targeterEntities)) //
                .add(Restrictions.eqProperty("state", "stateAction.state")) //
                .add(Restrictions.isNull("state.hidden")) //
                .add(Restrictions.eq("action.systemInvocationOnly", false));

        return getIndividualUpdateDefinitionCriteria(criteria, comment, exclusions).add(getTargetActionConstraint())
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)).list();
    }

    public UserNotification getUserNotification(Resource resource, User user, NotificationDefinition notificationDefinition) {
        return (UserNotification) sessionFactory.getCurrentSession().createCriteria(UserNotification.class)
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource))
                .add(Restrictions.eq("user", user))
                .add(Restrictions.eq("notificationDefinition", notificationDefinition))
                .uniqueResult();
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

    public void resetNotificationsSyndicated(PrismScope resourceScope, Collection<Integer> assignedResources) {
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

    private static ProjectionList getInvidualRequestDefinitionsProjection() {
        return getIndividualUpdateDefinitionsProjection()
                .add(Projections.groupProperty("stateAction.action.id").as("actionId"));
    }

    private static ProjectionList getIndividualUpdateDefinitionsProjection() {
        return Projections.projectionList() //
                .add(Projections.groupProperty("user.id").as("userId")) //
                .add(Projections.groupProperty("notificationDefinition.id").as("notificationDefinitionId"));
    }

    private static Criteria getIndividualRequestDefinitionCriteria(Criteria criteria, Resource resource) {
        return criteria.createAlias("stateAction.notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .createAlias("user.userNotifications", "userNotification", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.eq("userNotification." + resource.getResourceScope().getLowerCamelName(), resource)) //
                                .add(Restrictions.eqProperty("notificationDefinition.id", "userNotification.notificationDefinition.id"))) //
                .add(Restrictions.eq("notificationDefinition.notificationType", INDIVIDUAL)) //
                .add(Restrictions.eq("resource.id", resource.getId())) //
                .add(Restrictions.isNull("userNotification.id"));
    }

    private static Criteria getIndividualUpdateDefinitionCriteria(Criteria criteria, Comment comment, Collection<User> exclusions) {
        criteria.createAlias("stateAction.stateActionNotifications", "stateActionNotification", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .add(Restrictions.eqProperty("comment.action", "stateAction.action")) //
                .add(Restrictions.eq("notificationDefinition.notificationType", INDIVIDUAL)) //
                .add(Restrictions.eq("comment.id", comment.getId())); //

        if (isNotEmpty(exclusions)) {
            criteria.add(Restrictions.not( //
                    Restrictions.in("userRole.user", exclusions))); //
        }

        return criteria.add(Restrictions.eq("userAccount.enabled", true));
    }

}
