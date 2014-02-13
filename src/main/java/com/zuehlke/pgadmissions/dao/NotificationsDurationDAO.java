package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.NotificationsDuration;

@Repository
public class NotificationsDurationDAO {

    private final SessionFactory sessionFactory;

    public NotificationsDurationDAO() {
        this(null);
    }

    @Autowired
    public NotificationsDurationDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(NotificationsDuration notificationsDuration) {
        sessionFactory.getCurrentSession().update(notificationsDuration);
    }

    public NotificationsDuration getNotificationsDuration() {
        return (NotificationsDuration) sessionFactory.getCurrentSession().createCriteria(NotificationsDuration.class).uniqueResult();
    }

}
