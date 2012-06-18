package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.zuehlke.pgadmissions.domain.ReminderInterval;

@Repository
public class ReminderIntervalDAO {

	private final SessionFactory sessionFactory;

	ReminderIntervalDAO() {
		this(null);
	}

	@Autowired
	public ReminderIntervalDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Transactional
	public void save(ReminderInterval reminderInterval) {
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
	}
	
	@Transactional
	public ReminderInterval getReminderInterval() {
		return (ReminderInterval)sessionFactory.getCurrentSession().createCriteria(ReminderInterval.class).uniqueResult();
	}

}
