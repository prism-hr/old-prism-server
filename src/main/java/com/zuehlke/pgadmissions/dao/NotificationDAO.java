package com.zuehlke.pgadmissions.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

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
                .add(Restrictions.eq("notificationTemplate.id", templateId))
                .add(Restrictions.disjunction()
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