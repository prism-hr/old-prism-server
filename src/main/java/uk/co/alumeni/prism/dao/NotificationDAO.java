package uk.co.alumeni.prism.dao;

import static uk.co.alumeni.prism.dao.WorkflowDAO.getTargetActionConstraint;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationPurpose.REQUEST;
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

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceState;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserNotification;
import uk.co.alumeni.prism.domain.workflow.NotificationConfiguration;
import uk.co.alumeni.prism.domain.workflow.NotificationDefinition;
import uk.co.alumeni.prism.domain.workflow.Role;
import uk.co.alumeni.prism.domain.workflow.StateAction;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.domain.workflow.StateTransitionNotification;
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
        return (List<NotificationDefinition>) sessionFactory.getCurrentSession().createCriteria(StateTransitionNotification.class) //
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
        return getIndividualRequestDefinitionCriteria(workflowDAO.getWorkflowCriteriaList(scope, parentScope, getInvidualRequestDefinitionsProjection()),
                resource)
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

    public List<UserNotificationDefinitionDTO> getIndividualUpdateDefinitions(PrismScope scope, Resource resource, StateTransition stateTransition) {
        Criteria criteria = getWorkflowCriteriaListComment(scope) //
                .createAlias("resource.userRoles", "userRole", JoinType.INNER_JOIN);

        return getIndividualUpdateDefinitionCriteria(criteria, resource, stateTransition)
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)).list();
    }

    public List<UserNotificationDefinitionDTO> getIndividualUpdateDefinitions(PrismScope scope, PrismScope parentScope, Resource resource,
            StateTransition stateTransition) {
        Criteria criteria = getWorkflowCriteriaListComment(scope) //
                .createAlias("resource." + parentScope.getLowerCamelName(), "parentResource", JoinType.INNER_JOIN) //
                .createAlias("parentResource.userRoles", "userRole", JoinType.INNER_JOIN);

        return getIndividualUpdateDefinitionCriteria(criteria, resource, stateTransition)
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
                .add(Restrictions.eq("notificationDefinition.notificationPurpose", REQUEST)) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDTO.class)) //
                .list();
    }

    public void deleteNotificationConfigurationDocuments(NotificationConfiguration notificationConfiguration) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete NotificationConfigurationDocument " //
                        + "where notificationConfiguration = :notificationConfiguration") //
                .setParameter("notificationConfiguration", notificationConfiguration) //
                .executeUpdate();
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
                                .add(Restrictions.eq("notificationDefinition.notificationPurpose", REQUEST))
                                .add(Restrictions.eq("userNotification.active", true))) //
                .add(Restrictions.eq("notificationDefinition.notificationType", INDIVIDUAL)) //
                .add(Restrictions.eq("resource.id", resource.getId())) //
                .add(Restrictions.isNull("userNotification.id"));
    }

    private static Criteria getIndividualUpdateDefinitionCriteria(Criteria criteria, Resource resource, StateTransition stateTransition) {
        return criteria.createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateTransitions", "stateTransition", JoinType.INNER_JOIN) //
                .createAlias("stateTransition.stateTransitionNotifications", "stateTransitionNotification", JoinType.INNER_JOIN)
                .createAlias("stateTransitionNotification.notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("resource.id", resource.getId())) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .add(Restrictions.eq("stateTransition.id", stateTransition.getId())) //
                .add(Restrictions.eqProperty("userRole.role", "stateTransitionNotification.role"))
                .add(Restrictions.eq("notificationDefinition.notificationType", INDIVIDUAL));
    }

    private Criteria getWorkflowCriteriaListComment(PrismScope scope) {
        return sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(getIndividualUpdateDefinitionsProjection()) //
                .createAlias(scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN); //
    }

}
