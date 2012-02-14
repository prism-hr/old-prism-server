package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Repository
public class ApplicationFormDAO {

	private final SessionFactory sessionFactory;

	ApplicationFormDAO(){
		this(null);
	}
	
	public ApplicationFormDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void save(ApplicationForm application) {
		sessionFactory.getCurrentSession().saveOrUpdate(application);
	}

	public ApplicationForm get(Integer id) {
		return (ApplicationForm) sessionFactory.getCurrentSession().get(
				ApplicationForm.class, id);
	}
	
	public ApplicationForm getApplicationByUser(RegisteredUser user) {
		return (ApplicationForm) sessionFactory.getCurrentSession()
				.createCriteria(ApplicationForm.class)
				.add(Restrictions.eq("registered_user_id", user.getId())).uniqueResult();
	}

}
