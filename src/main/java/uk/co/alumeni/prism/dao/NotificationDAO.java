package uk.co.alumeni.prism.dao;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationType.INDIVIDUAL;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.comment.CommentState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationPurpose;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserNotification;
import uk.co.alumeni.prism.domain.workflow.NotificationDefinition;
import uk.co.alumeni.prism.domain.workflow.Role;
import uk.co.alumeni.prism.domain.workflow.StateAction;
import uk.co.alumeni.prism.domain.workflow.StateActionNotification;
import uk.co.alumeni.prism.dto.UserNotificationDTO;
import uk.co.alumeni.prism.dto.UserNotificationDefinitionDTO;

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
                        .add(WorkflowDAO.getTargetActionConstraint())
                        .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                        .list();
    }

    public List<UserNotificationDefinitionDTO> getIndividualUpdateDefinitions(PrismScope scope, Comment comment, Collection<User> exclusions) {
        Criteria criteria = getWorkflowCriteriaListComment(scope) //
                .createAlias("resource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("commentState.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN,
                        Restrictions.eqProperty("comment.action", "stateAction.action")) //
                .createAlias("stateAction.stateActionNotifications", "stateActionNotification", JoinType.INNER_JOIN,
                        Restrictions.eqProperty("userRole.role", "stateActionNotification.role"));

        return getIndividualUpdateDefinitionCriteria(criteria, comment, exclusions)
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)).list();
    }

    public List<UserNotificationDefinitionDTO> getIndividualUpdateDefinitions(PrismScope scope, PrismScope parentScope, Comment comment, Collection<User> exclusions) {
        Criteria criteria = getWorkflowCriteriaListComment(scope) //
                .createAlias("resource." + parentScope.getLowerCamelName(), "parentResource", JoinType.INNER_JOIN) //
                .createAlias("parentResource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("commentState.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN,
                        Restrictions.eqProperty("comment.action", "stateAction.action")) //
                .createAlias("stateAction.stateActionNotifications", "stateActionNotification", JoinType.INNER_JOIN,
                        Restrictions.eqProperty("userRole.role", "stateActionNotification.role"));

        return getIndividualUpdateDefinitionCriteria(criteria, comment, exclusions)
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)).list();
    }

    public UserNotification getUserNotification(Resource resource, User user, NotificationDefinition notificationDefinition) {
        return (UserNotification) sessionFactory.getCurrentSession().createCriteria(UserNotification.class)
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource))
                .add(Restrictions.eq("user", user))
                .add(Restrictions.eq("notificationDefinition", notificationDefinition))
                .uniqueResult();
    }

    public void resetUserNotifications(DateTime baseline) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete UserNotification "
                        + "where active = 0 "
                        + "and notifiedTimestamp < :baseline") //
                .setParameter("baseline", baseline) //
                .executeUpdate();
    }

    public void resetUserNotifications(User user) {
        sessionFactory.getCurrentSession().createQuery(
                "update UserNotification "
                        + "set active = 0 " //
                        + "where user = :user")
                .setParameter("user", user)
                .executeUpdate();
    }

    public void resetUserNotifications(User user, List<NotificationDefinition> definitions) {
        sessionFactory.getCurrentSession().createQuery(
                "update UserNotification "
                        + "set active = 0 " //
                        + "where user = :user " //
                        + "and notificationDefinition in (:definitions)") //
                .setParameter("user", user) //
                .setParameterList("definitions", definitions) //
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

    public List<UserNotificationDTO> getRecentRequestCounts(Collection<UserNotificationDefinitionDTO> requests, DateTime baseline) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserNotification.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id").as("userId"))
                        .add(Projections.groupProperty("notificationDefinition.id").as("notificationDefinitionId")) //
                        .add(Projections.countDistinct("id").as("sentCount"))) //
                .createAlias("notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN);

        Junction recipientConstraint = Restrictions.disjunction();
        requests.forEach(notification -> {
            recipientConstraint.add(Restrictions.conjunction() //
                    .add(Restrictions.eq("user.id", notification.getUserId())) //
                    .add(Restrictions.eq("notificationDefinition.id", notification.getNotificationDefinitionId())));
        });

        return criteria.add(Restrictions.ge("notifiedTimestamp", baseline)) //
                .add(Restrictions.eq("notificationDefinition.notificationPurpose", PrismNotificationPurpose.REQUEST)) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDTO.class)) //
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
                                .add(Restrictions.eqProperty("notificationDefinition.id", "userNotification.notificationDefinition.id")) //
                                .add(Restrictions.eq("userNotification.active", true))) //
                .add(Restrictions.eq("notificationDefinition.notificationType", INDIVIDUAL)) //
                .add(Restrictions.eq("resource.id", resource.getId())) //
                .add(Restrictions.isNull("userNotification.id"));
    }

    private static Criteria getIndividualUpdateDefinitionCriteria(Criteria criteria, Comment comment, Collection<User> exclusions) {
        criteria.createAlias("stateActionNotification.notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .add(Restrictions.eqProperty("comment.action", "stateAction.action")) //
                .add(Restrictions.eq("notificationDefinition.notificationType", INDIVIDUAL)) //
                .add(Restrictions.eq("comment.id", comment.getId())); //

        if (isNotEmpty(exclusions)) {
            criteria.add(Restrictions.not( //
                    Restrictions.in("userRole.user", exclusions))); //
        }

        return criteria.add(Restrictions.eq("userAccount.enabled", true));
    }

    private Criteria getWorkflowCriteriaListComment(PrismScope scope) {
        return sessionFactory.getCurrentSession().createCriteria(CommentState.class) //
                .setProjection(getIndividualUpdateDefinitionsProjection()) //
                .createAlias("comment", "comment", JoinType.INNER_JOIN) //
                .createAlias("comment." + scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("comment.commentStates", "commentState", JoinType.INNER_JOIN);
    }

}
