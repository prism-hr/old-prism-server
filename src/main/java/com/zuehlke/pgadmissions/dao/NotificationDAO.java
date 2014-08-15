package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateActionNotification;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserNotification;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinition;

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
    
    public List<UserNotificationDefinition> getRequestNotifications(Resource resource, Action action, User invoker) {
        return getRequestNotifications(resource, action, invoker, true);
    }
    
    public List<UserNotificationDefinition> getDeferredRequestNotifications(Resource resource, Action action, User invoker) {
        return getRequestNotifications(resource, action, invoker, false);
    }
    
    public List<UserNotificationDefinition> getUpdateNotifications(Resource resource, Action action, User invoker) {
        return getUpdateNotifications(resource, action, invoker, true);
    }
    
    public List<UserNotificationDefinition> getDeferredUpdateNotifications(Resource resource, Action action, User invoker) {
        return getUpdateNotifications(resource, action, invoker, false);
    }
    
    public void deleteObseleteNotificationConfigurations(List<NotificationTemplate> activeNotificationWorkflowTemplates) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete NotificationConfiguration " //
                        + "where notificationTemplate not in (:configurableTemplates)") //
                .setParameterList("configurableTemplates", activeNotificationWorkflowTemplates) //
                .executeUpdate();
    }
    
    public void deleteUserNotification(UserRole roleToRemove) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete UserNotification " //
                    + "where userRole = :roleToRemove") //
                .setParameter("roleToRemove", roleToRemove) //
                .executeUpdate();
    }
    
    public UserNotification getUserNotification(Resource resource, UserRole userRole, NotificationTemplate notificationTemplate) {
        return (UserNotification) sessionFactory.getCurrentSession().createCriteria(UserNotification.class) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCaseName(), resource.getId())) //
                .add(Restrictions.eq("userRole", userRole)) //
                .add(Restrictions.eq("notificationTemplate", notificationTemplate)) //
                .uniqueResult();
    }
    
    private List<UserNotificationDefinition> getRequestNotifications(Resource resource, Action action, User invoker, boolean onStateChange) {
        String resourceReference = resource.getResourceScope().getLowerCaseName();
        
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(resource.getClass(), resourceReference) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty(resourceReference + ".id"), "resourceId") //
                        .add(Projections.groupProperty(resourceReference + ".scope.id"), "resourceScopeId") //
                        .add(Projections.groupProperty("userRole.id"), "userRoleId") //
                        .add(Projections.groupProperty("notificationTemplate.id"), "notificationTemplateId")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.notificationTemplate", "notiticationTemplate", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", resource.getId())) //
                .add(Restrictions.eq("stateAction.action", action));
        
        if (onStateChange) {
            criteria.add(Restrictions.neProperty("state", "previousState")) //
                    .add(Restrictions.eq("notificationTemplate.notificationType", PrismNotificationType.INDIVIDUAL)); //
        }
        
        return (List<UserNotificationDefinition>) criteria.add(Restrictions.ne("userRole.user", invoker)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("user.userAccount")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinition.class)) //
                .list();
    }
    
    private List<UserNotificationDefinition> getUpdateNotifications(Resource resource, Action action, User invoker, boolean onStateChange) {
        String resourceReference = resource.getResourceScope().getLowerCaseName();
        
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(resource.getClass(), resourceReference) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty(resourceReference + ".id"), "resourceId") //
                        .add(Projections.groupProperty(resourceReference + ".scope.id"), "resourceScopeId") //
                        .add(Projections.groupProperty("userRole.id"), "userRoleId") //
                        .add(Projections.groupProperty("notificationTemplate.id"), "notificationTemplateId")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionNotifications", "stateActionNotifications", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.notificationTemplate", "notiticationTemplate", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", resource.getId())) //
                .add(Restrictions.eq("stateAction.action", action)); //
        
        if (onStateChange) {
            criteria.add(Restrictions.eq("notificationTemplate.notificationType", PrismNotificationType.INDIVIDUAL)) //
                    .add(Restrictions.disjunction() //
                            .add(Restrictions.eq("action.actionCategory", PrismActionCategory.CREATE_RESOURCE)) //
                            .add(Restrictions.ne("userRole.user", invoker))); //
        } else {
            criteria.add(Restrictions.eq("notificationTemplate.notificationType", PrismNotificationType.SYNDICATED)) //
                    .add(Restrictions.ne("userRole.user", invoker));
        }
        
        return (List<UserNotificationDefinition>) criteria.add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("user.userAccount")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinition.class)) //
                .list();
    }

}
