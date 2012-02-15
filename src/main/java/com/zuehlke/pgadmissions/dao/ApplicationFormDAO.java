package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Repository
public class ApplicationFormDAO {

	private final SessionFactory sessionFactory;

	
	ApplicationFormDAO(){
		this(null);
	}
	
	@Autowired
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
	
	public List<ApplicationForm> getApplicationsByUser(RegisteredUser user) {
		List<ApplicationForm> list = sessionFactory.getCurrentSession()
				.createCriteria(ApplicationForm.class)
				.add(Restrictions.eq("user", user)).list();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getAllApplications() {
		return  (List<ApplicationForm>)sessionFactory.getCurrentSession()
				.createCriteria(ApplicationForm.class).list();
				
	}


}
