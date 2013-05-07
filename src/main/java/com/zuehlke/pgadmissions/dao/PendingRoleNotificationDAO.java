package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.PendingRoleNotification;

@Repository
@SuppressWarnings("unchecked")
public class PendingRoleNotificationDAO {

	private final SessionFactory sessionFactory;

	public PendingRoleNotificationDAO(){
		this(null);		
	}
	
	@Autowired
	public PendingRoleNotificationDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public List<PendingRoleNotification> getAllPendingRoleNotifications() {
		return sessionFactory.getCurrentSession().createCriteria(PendingRoleNotification.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	public void deletePendingRoleNotifcation(PendingRoleNotification pendingNotification) {
		sessionFactory.getCurrentSession().delete(pendingNotification);
	}
}
