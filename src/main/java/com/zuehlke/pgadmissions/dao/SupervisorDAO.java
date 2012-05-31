package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Repository
public class SupervisorDAO {

	private final SessionFactory sessionFactory;

	SupervisorDAO() {
		this(null);
	}

	@Autowired
	public SupervisorDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Supervisor getSupervisorWithId(Integer id) {
		return (Supervisor) sessionFactory.getCurrentSession().get(Supervisor.class, id);
	}

	public void save(Supervisor supervisor) {
		sessionFactory.getCurrentSession().saveOrUpdate(supervisor);
	}

	@SuppressWarnings("unchecked")
	public List<Supervisor> getSupervisorsDueNotification() {
		return sessionFactory.getCurrentSession().createCriteria(Supervisor.class).add(Restrictions.isNull("lastNotified"))
				.createAlias("supervisor.application", "application")
				.add(Restrictions.eq("application.status", ApplicationFormStatus.APPROVAL))
				.add(Restrictions.eqProperty("approvalRound", "application.latestApprovalRound"))
				.list();
	}

}
