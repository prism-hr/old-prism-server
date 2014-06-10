package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.NotificationReminderInterval;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;

@Repository
public class NotificationTemplateDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Integer getDefaultReminderDuration(NotificationTemplate notificationTemplate) {
        return (Integer) sessionFactory.getCurrentSession().createCriteria(NotificationReminderInterval.class) //
                .setProjection(Projections.property("interval")) //
                .add(Restrictions.eq("notificationTemplate", notificationTemplate)) //
                .add(Restrictions.isNotNull("system")) //
                .uniqueResult();
    }

}
