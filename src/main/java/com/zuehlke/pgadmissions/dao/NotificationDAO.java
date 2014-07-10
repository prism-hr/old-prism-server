package com.zuehlke.pgadmissions.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateActionNotification;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinition;
import com.zuehlke.pgadmissions.mail.MailDescriptor;

@Repository
@SuppressWarnings("unchecked")
public class NotificationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public NotificationTemplateVersion getActiveVersion(Resource resource, NotificationTemplate template) {
        return (NotificationTemplateVersion) sessionFactory.getCurrentSession().createCriteria(NotificationConfiguration.class) //
                .setProjection(Projections.property("notificationTemplateVersion")) //
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource)) //
                .add(Restrictions.eq("notificationTemplate", template)) //
                .uniqueResult();
    }

    public NotificationTemplateVersion getActiveVersion(Resource resource, PrismNotificationTemplate templateId) {
        return (NotificationTemplateVersion) sessionFactory.getCurrentSession().createCriteria(NotificationConfiguration.class) //
                .setProjection(Projections.property("notificationTemplateVersion")) //
                .add(Restrictions.eq("notificationTemplate.id", templateId)).add(Restrictions.disjunction().add(Restrictions.conjunction() //
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

    public NotificationTemplateVersion getLatestVersion(Resource resource, NotificationTemplate template) {
        return (NotificationTemplateVersion) sessionFactory.getCurrentSession().createCriteria(NotificationTemplateVersion.class) //
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource)) //
                .add(Restrictions.eq("notificationTemplate", template)) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
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

    public List<NotificationTemplate> getActiveNotificationTemplates() {
        Set<NotificationTemplate> templates = Sets.newHashSet(sessionFactory.getCurrentSession().createCriteria(StateAction.class)
                .setProjection(Projections.groupProperty("notificationTemplate")) //
                .list());

        templates.addAll(Sets.newHashSet(sessionFactory.getCurrentSession().createCriteria(StateActionNotification.class)
                .setProjection(Projections.groupProperty("notificationTemplate")) //
                .list()));

        return Lists.newArrayList(templates);
    }

    public void deleteObseleteNotificationConfigurations(List<NotificationTemplate> activeNotificationTemplates) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete NotificationConfiguration " //
                        + "where notificationTemplate not in (:configurableTemplates)") //
                .setParameterList("configurableTemplates", activeNotificationTemplates) //
                .executeUpdate();
    }
    
    public List<UserNotificationDefinition> getUpdateNotifications(StateAction stateAction, Resource resource) {
        return (List<UserNotificationDefinition>) sessionFactory.getCurrentSession().createCriteria(StateActionNotification.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("role.userRoles"), "userRole") //
                        .add(Projections.property("notificationTemplate"), "notificationTemplate")) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .createAlias("userRoles", "userRole", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction", stateAction)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication()))) //
                .setResultTransformer(Transformers.aliasToBean(UserNotificationDefinition.class)) //
                .list();
    }
    
    public List<MailDescriptor> getPendingUpdateNotifications(Scope scope, PrismNotificationType notificationType) {
        String scopeName = scope.toString().toLowerCase();
        return (List<MailDescriptor>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user"), "user") //
                        .add(Projections.groupProperty(scopeName), scopeName) //
                        .add(Projections.groupProperty("notificationTemplate"), "notificationTemplate")) //
                .createAlias("userNotifications", "userNotification", JoinType.INNER_JOIN) //
                .createAlias("userNotification.notificationTemplate", "notificationTemplate", JoinType.INNER_JOIN) //
                .add(Restrictions.isNotNull(scopeName)) //
                .add(Restrictions.eq("notificationTemplate.notificationType", notificationType)) //
                .list();
    }

    public void deleteSentUpdateNotifications(List<UserRole> userRoles) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete UserNotification " //
                        + "where userRole in (:userRoles)") //
                .setParameterList("userRoles", userRoles) //
                .executeUpdate();
    }
    
}
