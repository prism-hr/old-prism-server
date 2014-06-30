package com.zuehlke.pgadmissions.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateActionNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

@Repository
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
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource)) //
                .add(Restrictions.eq("notificationTemplate.id", templateId)) //
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

    @SuppressWarnings("unchecked")
    public List<NotificationTemplateVersion> getVersions(Resource resource, NotificationTemplate template) {
        return (List<NotificationTemplateVersion>) sessionFactory.getCurrentSession().createCriteria(NotificationTemplateVersion.class) //
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource)) //
                .add(Restrictions.eq("notificationTemplate", template)) //
                .addOrder(Order.desc("id")) //
                .list();
    }

    public NotificationConfiguration getConfiguration(Resource resource, NotificationTemplate template) {
        return (NotificationConfiguration) sessionFactory.getCurrentSession().createCriteria(NotificationConfiguration.class)
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource))
                .add(Restrictions.eq("notificationTemplate", template)).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<NotificationTemplate> getConfigurableNotificationTemplates() {
        Set<NotificationTemplate> templates = Sets.newHashSet(sessionFactory.getCurrentSession().createCriteria(StateAction.class)
                .setProjection(Projections.groupProperty("notificationTemplate")) //
                .list());

        templates.addAll(Sets.newHashSet(sessionFactory.getCurrentSession().createCriteria(StateActionNotification.class)
                .setProjection(Projections.groupProperty("notificationTemplate")) //
                .list()));

        return Lists.newArrayList(templates);
    }
    
    public void deleteObseleteNotificationConfigurations() {
        sessionFactory.getCurrentSession().createQuery( //
                "delete NotificationConfiguration " //
                    + "where notificationTemplate not in (:configurableTemplates)") //
                .setParameterList("configurableTemplates", getConfigurableNotificationTemplates()) //
                .executeUpdate();
    }

}
