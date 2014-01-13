package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApprovalRound;

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

}