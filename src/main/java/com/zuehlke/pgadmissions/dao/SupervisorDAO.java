package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;

@Repository
public class SupervisorDAO {

	private final SessionFactory sessionFactory;

	public SupervisorDAO() {
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

    public Supervisor getSupervisorByUserAndApprovalRound(RegisteredUser user, ApprovalRound approvalRound) {
        return (Supervisor) sessionFactory.getCurrentSession().createCriteria(Supervisor.class)
        		.add(Restrictions.eq("user", user))
        		.add(Restrictions.eq("approvalRound", approvalRound)).uniqueResult();
    }

}