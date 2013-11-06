package com.zuehlke.pgadmissions.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;

@Repository
@SuppressWarnings("unchecked")
public class NotificationRecordDAO {
	
	private final SessionFactory sessionFactory;
	
	public NotificationRecordDAO() {
		this(null);
	}
	
	@Autowired
	public NotificationRecordDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public List<NotificationRecord> getNotificationsWithTimeStampGreaterThan(Date timestamp, NotificationType notificationType) {
		return (List<NotificationRecord>) sessionFactory.getCurrentSession()
				.createCriteria(NotificationRecord.class)
				.add(Restrictions.ge("date", timestamp))
				.add(Restrictions.eq("notificationType", notificationType)).list();
	}
}
