package com.zuehlke.pgadmissions.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Repository
public class InterviewerDAO {

	private final SessionFactory sessionFactory;

	InterviewerDAO() {
		this(null);
	}

	@Autowired
	public InterviewerDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;

	}

	public void save(Interviewer interviewer) {
		sessionFactory.getCurrentSession().saveOrUpdate(interviewer);
		
	}
	
	public Interviewer getInterviewerById(Integer id) {
		return (Interviewer) sessionFactory.getCurrentSession().get(Interviewer.class, id);
	}

	public Interviewer getInterviewerByUser(RegisteredUser user) {
		return (Interviewer)sessionFactory.getCurrentSession().createCriteria(Interviewer.class)
				.add(Restrictions.eq("user", user)).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Interviewer> getInterviewersDueNotification() {
		return sessionFactory.getCurrentSession().createCriteria(Interviewer.class).add(Restrictions.isNull("lastNotified"))
				.createAlias("application", "application").add(Restrictions.eq("application.status", ApplicationFormStatus.INTERVIEW)).list();
		
	}
	@SuppressWarnings("unchecked")
	public List<Interviewer> getInterviewersDueReminder() {
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		Date sixDaysAgo = DateUtils.addDays(today,-6);
		return sessionFactory.getCurrentSession().createCriteria(Interviewer.class)
				.createAlias("application", "application")
				.add(Restrictions.eq("application.status", ApplicationFormStatus.INTERVIEW))
				.add(Restrictions.lt("application.dueDate", sixDaysAgo))
				.list();
	}
	
}
