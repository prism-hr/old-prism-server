package com.zuehlke.pgadmissions.dao;

import java.util.HashMap;
import java.util.List;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.hibernate.SessionFactory;
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

	@RemoteMethod
	public ApplicationForm get(Integer id) {
		return (ApplicationForm) sessionFactory.getCurrentSession().get(
				ApplicationForm.class, id);
	}
	
	public List<ApplicationForm> getApplicationsByUser(RegisteredUser user) {
		@SuppressWarnings("unchecked")
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

	public List<ApplicationForm> checkIfApplicationIsAlreadyApproved(
			Integer id) {
		 @SuppressWarnings("unchecked")
		List<ApplicationForm> list = sessionFactory.getCurrentSession()
				.createCriteria(ApplicationForm.class)
				.add(Restrictions.eq("approved", "1"))
		 		.add(Restrictions.eq("id", id)).list();
		 return list;
	}


}
