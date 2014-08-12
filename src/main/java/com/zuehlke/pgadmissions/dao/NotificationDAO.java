package com.zuehlke.pgadmissions.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.StateActionNotification;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserNotification;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinition;

@Repository
@SuppressWarnings("unchecked")
public class NotificationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public NotificationTemplateVersion getActiveVersionToSend(Resource resource, NotificationTemplate template) {
        return (NotificationTemplateVersion) sessionFactory.getCurrentSession().createCriteria(NotificationConfiguration.class) //
                .setProjection(Projections.property("notificationTemplateVersion")) //
                .add(Restrictions.eq("notificationTemplate", template)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("system", resource.getSystem())) //
                                .add(Restrictions.isNull("institution")) //
                                .add(Restrictions.isNull("program"))) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("institution", resource.getInstitution())) //
                                .add(Restrictions.isNull("program"))) //
                        .add(Restrictions.eq("program", resource.getProgram()))) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("program")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public NotificationTemplateVersion getActiveVersionToEdit(Resource resource, NotificationTemplate template) {
        return (NotificationTemplateVersion) sessionFactory.getCurrentSession().createCriteria(NotificationConfiguration.class) //
                .setProjection(Projections.property("notificationTemplateVersion")) //
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource)) //
                .add(Restrictions.eq("notificationTemplate", template)) //
                .uniqueResult();
    }

    public List<NotificationTemplateVersion> getVersions(Resource resource, NotificationTemplate template) {
        return (List<NotificationTemplateVersion>) sessionFactory.getCurrentSession().createCriteria(NotificationTemplateVersion.class) //
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource)) //
                .add(Restrictions.eq("notificationTemplate", template)) //
                .addOrder(Order.desc("id")) //
                .list();
    }

    public NotificationConfiguration getConfiguration(Resource resource, NotificationTemplate template) {
        return (NotificationConfiguration) sessionFactory.getCurrentSession().createCriteria(NotificationConfiguration.class) //
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource)) //
                .add(Restrictions.eq("notificationTemplate", template)) //
                .uniqueResult();
    }

    public List<NotificationTemplate> getActiveTemplatesToManage() {
        Set<NotificationTemplate> templates = Sets.newHashSet(sessionFactory.getCurrentSession().createCriteria(StateAction.class)
                .setProjection(Projections.groupProperty("notificationTemplate")) //
                .list());

        templates.addAll(Sets.newHashSet(sessionFactory.getCurrentSession().createCriteria(StateActionNotification.class)
                .setProjection(Projections.groupProperty("notificationTemplate")) //
                .list()));

        return Lists.newArrayList(templates);
    }

    public void deleteObseleteNotificationConfigurations(List<NotificationTemplate> activeNotificationWorkflowTemplates) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete NotificationConfiguration " //
                        + "where notificationTemplate not in (:configurableTemplates)") //
                .setParameterList("configurableTemplates", activeNotificationWorkflowTemplates) //
                .executeUpdate();
    }

    public List<UserNotificationDefinition> getUpdateNotifications(Resource resource, Action action, User invoker) {
        return (List<UserNotificationDefinition>) sessionFactory.getCurrentSession().createCriteria(StateActionNotification.class, "stateActionNotification") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("userRole.id"), "userRoleId") //
                        .add(Projections.groupProperty("notificationTemplate.id"), "notificationTemplateId")) //
                .createAlias("stateActionNotification.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.notificationTemplate", "notiticationTemplate", JoinType.INNER_JOIN) //
                .createAlias("stateActionNotification.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", resource.getPreviousState())) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("notificationTemplate.notificationType", PrismNotificationType.INDIVIDUAL))
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
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinition.class)) //
                .list();
    }
    
    public List<UserNotificationDefinition> getRequestNotifications(Resource resource, Action action, User invoker) {
        return (List<UserNotificationDefinition>) sessionFactory.getCurrentSession().createCriteria(StateActionAssignment.class, "stateActionAssignment") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("userRole.id"), "userRoleId") //
                        .add(Projections.groupProperty("notificationTemplate.id"), "notificationTemplateId")) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.notificationTemplate", "notiticationTemplate", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", resource.getState())) //
                .add(Restrictions.eq("stateAction.action", action)) //
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
    
    public List<UserNotificationDefinition> getPendingNotifications(Scope scope) {
        return null;
    }

    public List<UserNotificationDefinition> getPendingBatchedNotifications(Scope scope, LocalDate baseline) {
       return (List<UserNotificationDefinition>) sessionFactory.getCurrentSession().createSQLQuery(
               "To do ... get the string via freemarker")
               .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinition.class)) //
               .list();
    }
    
    public UserNotification getUserNotification(UserNotificationDefinition definition) {
        return (UserNotification) sessionFactory.getCurrentSession().createCriteria(UserNotification.class) //
                .add(Restrictions.eq("user.id", definition.getUserRoleId())) //
                .add(Restrictions.eq("notificationTemplate.id", definition.getNotificationTemplateId())) //
                .uniqueResult();        
    }
    
    public void updateUserNotification(Resource resource, User user, NotificationTemplate notificationTemplate, LocalDate createdDate) {
        sessionFactory.getCurrentSession().createQuery( //
                "update userNotification "
                    + "set createdDate = :createdDate" //
                    + "where " + resource.getResourceScope().getLowerCaseName() + " = :resource " //
                        + "and userRole in ( "
                            + "from userRole "
                            + "where user = :user)  " //
                        + "and notificationTemplate = :notificationTemplate") //
                .setParameter("createdDate", createdDate) //
                .setParameter("resource", resource) //
                .setParameter("user", user) //
                .setParameter("notificationTemplate", notificationTemplate) //
                .executeUpdate();
    }

    public void deleteUserNotification(Resource resource, User user, NotificationTemplate notificationTemplate) {
        sessionFactory.getCurrentSession().createQuery( //
            "delete UserNotification " //
            + "where " + resource.getResourceScope().getLowerCaseName() + " = :resource " //
                + "and userRole in ( " //
                    + "from UserRole " //
                    + "where user = :user) " //
                + "and notificationTemplate = :notificationTemplate")
            .setParameter("resource", resource) //
            .setParameter("user", user) //
            .setParameter("notificationTemplate", notificationTemplate) //
            .executeUpdate();
    }
    
    public void deleteUserNotification(UserRole roleToRemove) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete UserNotification " //
                    + "where userRole = :roleToRemove") //
                .setParameter("roleToRemove", roleToRemove) //
                .executeUpdate();
    }

}
