package com.zuehlke.pgadmissions.dao;

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
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourcePreviousState;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
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
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("role.id"), "roleId") //
                        .add(Projections.groupProperty("notificationDefinition.id"), "notificationDefinitionId") //
                        .add(Projections.groupProperty("stateAction.action.id"), "actionId")) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("stateAction.notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("notificationDefinition.notificationType", PrismNotificationType.INDIVIDUAL)) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("stateAction.actionCondition")) //
                        .add(Restrictions.eqProperty("resourceCondition.actionCondition", "stateAction.actionCondition"))) //
                .add(Restrictions.isNull("userRole.lastNotifiedDate")) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getIndividualUpdateDefinitions(Resource resource, Action action, Set<User> exclusions, LocalDate baseline) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(ResourcePreviousState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("role.id"), "roleId") //
                        .add(Projections.groupProperty("notificationDefinition.id"), "notificationDefinitionId")) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionNotifications", "stateActionNotification", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.role", "role", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.eq("notificationDefinition.notificationType", PrismNotificationType.INDIVIDUAL)) //
                .add(Restrictions.not( //
                        Restrictions.in("userRole.user", exclusions))) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("stateAction.actionCondition")) //
                        .add(Restrictions.eqProperty("resourceCondition.actionCondition", "stateAction.actionCondition"))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getIndividualReminderDefinitions(Resource resource, LocalDate baseline) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("role.id"), "roleId") //
                        .add(Projections.groupProperty("notificationDefinition.id"), "notificationDefinitionId") //
                        .add(Projections.groupProperty("stateAction.action.id"), "actionId")) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("stateAction.notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("notificationDefinition.notificationType", PrismNotificationType.INDIVIDUAL)) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("stateAction.actionCondition")) //
                        .add(Restrictions.eqProperty("resourceCondition.actionCondition", "stateAction.actionCondition"))) //
                .add(Restrictions.lt("userRole.lastNotifiedDate", baseline)) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getSyndicatedRequestDefinitions(Resource resource, LocalDate baseline) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        String lastNotifiedDateReference = "user.lastNotifiedDate" + resource.getClass().getSimpleName();
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("notificationDefinition.id"), "notificationDefinitionId")) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("stateAction.notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("notificationDefinition.notificationType", PrismNotificationType.SYNDICATED)) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("stateAction.actionCondition")) //
                        .add(Restrictions.eqProperty("resourceCondition.actionCondition", "stateAction.actionCondition"))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull(lastNotifiedDateReference)) //
                        .add(Restrictions.lt(lastNotifiedDateReference, baseline))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<UserNotificationDefinitionDTO> getSyndicatedUpdateDefinitions(Resource resource, Action action, User invoker, LocalDate baseline) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        String lastNotifiedDateReference = "user.lastNotifiedDate" + resource.getClass().getSimpleName();
        return (List<UserNotificationDefinitionDTO>) sessionFactory.getCurrentSession().createCriteria(CommentState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "userId") //
                        .add(Projections.groupProperty("notificationDefinition.id"), "notificationDefinitionId")) //
                .createAlias("comment", "comment", JoinType.INNER_JOIN) //
                .createAlias("comment." + resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionNotifications", "stateActionNotification", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.role", "role", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.notificationDefinition", "notificationDefinition", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.eq("notificationDefinition.notificationType", PrismNotificationType.SYNDICATED)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.user", resource.getUser())) //
                        .add(Restrictions.ne("userRole.user", invoker))) //
                .add(Restrictions.eq("comment." + resourceReference, resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("stateAction.actionCondition")) //
                        .add(Restrictions.eqProperty("resourceCondition.actionCondition", "stateAction.actionCondition"))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull(lastNotifiedDateReference)) //
                        .add(Restrictions.lt(lastNotifiedDateReference, baseline))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinitionDTO.class)) //
                .list();
    }

    public List<User> getRecommendationDefinitions(LocalDate baseline) {
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

    public void resetNotificationsIndividual(User user) {
        sessionFactory.getCurrentSession().createQuery( //
                "update UserRole " //
                        + "set lastNotifiedDate = null " //
                        + "where user = :user") //
                .setParameter("user", user) //
                .executeUpdate();
    }

    public void resetNotificationsSyndicated(User user, PrismScope resourceScope, Set<Integer> assignedResources) {
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
