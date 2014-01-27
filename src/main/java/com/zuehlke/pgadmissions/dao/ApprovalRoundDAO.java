package com.zuehlke.pgadmissions.dao;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Supervisor;

@Repository
public class ApprovalRoundDAO {

	private final SessionFactory sessionFactory;

	public ApprovalRoundDAO(){
		this(null);
	}
	
	@Autowired
	public ApprovalRoundDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public ApprovalRound getApprovalRoundById(Integer id) {
		return (ApprovalRound) sessionFactory.getCurrentSession().get(ApprovalRound.class, id);
	}

	public void save(ApprovalRound approvalRound) {
		sessionFactory.getCurrentSession().saveOrUpdate(approvalRound);
	}
	
	public void saveAndInitialise(ApprovalRound approvalRound)
	{
	    save(approvalRound);
	    for (Supervisor supervisor : approvalRound.getSupervisors()) {
	        Hibernate.initialize(supervisor);
	        Hibernate.initialize(supervisor.getUser());
	    }
	}

}