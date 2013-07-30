package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.enums.ReminderType;

@Repository
public class ReminderIntervalDAO {

    private final SessionFactory sessionFactory;

    public ReminderIntervalDAO() {
        this(null);
    }

    @Autowired
    public ReminderIntervalDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(ReminderInterval reminderInterval) {
        sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
    }

    public ReminderInterval getReminderInterval(ReminderType reminderType) {
        return (ReminderInterval) sessionFactory.getCurrentSession().createCriteria(ReminderInterval.class).add(Restrictions.eq("reminderType", reminderType))
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<ReminderInterval> getReminderIntervals() {
        return (List<ReminderInterval>) sessionFactory.getCurrentSession().createCriteria(ReminderInterval.class).list();
    }
    
    

}
