package com.zuehlke.pgadmissions.dao;

import java.util.List;

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
	
}
