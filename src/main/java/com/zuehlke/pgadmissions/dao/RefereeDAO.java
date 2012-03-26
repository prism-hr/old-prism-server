package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Repository
public class RefereeDAO {

	private final SessionFactory sessionFactory;

	RefereeDAO(){
		this(null);
	}
	@Autowired
	public RefereeDAO(SessionFactory sessionFactory){
		this.sessionFactory = sessionFactory;
	}
	

	public void save(Referee referee) {
		sessionFactory.getCurrentSession().saveOrUpdate(referee);
	}
	
	public Referee getRefereeById(Integer id) {
		return (Referee) sessionFactory.getCurrentSession().get(Referee.class, id);
	}
	
	public void delete(Referee referee) {
		sessionFactory.getCurrentSession().delete(referee);
		
	}
	public Referee getRefereeByActivationCode(String activationCode) {
		return (Referee) sessionFactory.getCurrentSession().createCriteria(Referee.class).add(Restrictions.eq("activationCode", activationCode)).uniqueResult();
		
	}

}
