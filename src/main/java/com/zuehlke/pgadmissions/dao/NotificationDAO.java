package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.dao.WorkflowDAO.getEndorsementActionFilterConstraint;
import static com.zuehlke.pgadmissions.dao.WorkflowDAO.getUserRoleWithTargetConstraint;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType.INDIVIDUAL;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourcePreviousState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserNotification;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.NotificationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.StateAction;
import com.zuehlke.pgadmissions.domain.workflow.StateActionNotification;
import com.zuehlke.pgadmissions.dto.UserNotificationDTO;
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

    public List<UserNotificationDefinitionDTO> getIndividualRequestDefinitions(Resource resource, LocalDate baseline) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (List<UserNotificationDefinitionDTO>) workflowDAO.getWorklflowCriteria(resource.getResourceScope(), Projections.projectionList() //
                .add(Projections.groupProperty("user.id").as("userId")) //
                .add(Projections.groupProperty("notificationDefinition.id").as("notificationDefinitionId")) //
                .add(Projections.groupProperty("stateAction.action.id").as("actionId")))
                .createAlias("stateAction.notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .createAlias("user.userNotifications", "userNotification", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.eq("userNotification." + resourceReference, resource)) //
                                .add(Restrictions.eqProperty("notificationDefinition.id", "userNotification.notificationDefinition.id"))) //
                .add(Restrictions.eq("notificationDefinition.notificationType", INDIVIDUAL)) //
                .add(Restrictions.eq("resource.id", resource.getId())) //
                .add(getUserRoleWithTargetConstraint(resource)) //
                .add(getEndorsementActionFilterConstraint())
                .add(Restrictions.isNull("userNotification.id")) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getIndividualUpdateDefinitions(Resource resource, Action action, Set<User> exclusions) {
        Criteria criteria = workflowDAO.getWorklflowCriteria(resource.getResourceScope(), Projections.projectionList() //
                .add(Projections.groupProperty("user.id").as("userId")) //
                .add(Projections.groupProperty("notificationDefinition.id").as("notificationDefinitionId")), ResourcePreviousState.class)
                .createAlias("stateAction.stateActionNotifications", "stateActionNotification", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.eq("notificationDefinition.notificationType", INDIVIDUAL)) //
                .add(Restrictions.eq("resource.id", resource.getId())); //

        if (!exclusions.isEmpty()) {
            criteria.add(Restrictions.not( //
                    Restrictions.in("userRole.user", exclusions))); //
        }

        return (List<UserNotificationDefinitionDTO>) criteria //
                .add(getUserRoleWithTargetConstraint(resource)) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .add(getEndorsementActionFilterConstraint())
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
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

    public List<UserNotificationDTO> getRecentRequests(List<Integer> users, LocalDate lastNotifiedDate) {
        return (List<UserNotificationDTO>) sessionFactory.getCurrentSession().createCriteria(UserNotification.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id").as("userId")) //
                        .add(Projections.groupProperty("notificationDefinition.id").as("notificationDefinitionId")) //
                        .add(Projections.countDistinct("id").as("sentCount"))) //
                .add(Restrictions.in("user.id", users)) //
                .add(Restrictions.ge("lastNotifiedDate", lastNotifiedDate)) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDTO.class)) //
                .list();
    }

}
