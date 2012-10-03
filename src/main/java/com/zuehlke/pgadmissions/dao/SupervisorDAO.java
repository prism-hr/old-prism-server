package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
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
		return sessionFactory.getCurrentSession().createCriteria(Supervisor.class, "supervisor").add(Restrictions.isNull("lastNotified"))
				.createAlias("approvalRound.application", "application")
				.add(Restrictions.eq("application.status", ApplicationFormStatus.APPROVAL))
				.add(Restrictions.eqProperty("approvalRound", "application.latestApprovalRound"))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
	}

    public Supervisor getSupervisorByUser(RegisteredUser user) {
        return (Supervisor) sessionFactory.getCurrentSession().createCriteria(Supervisor.class).add(Restrictions.eq("user", user)).uniqueResult();
    }
}
