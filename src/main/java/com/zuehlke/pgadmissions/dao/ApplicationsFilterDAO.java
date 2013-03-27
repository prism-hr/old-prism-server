package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Repository
public class ApplicationsFilterDAO {

	private final SessionFactory sessionFactory;

	ApplicationsFilterDAO() {
		this(null);
	}

	@Autowired
	public ApplicationsFilterDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;

	}

	public void save(ApplicationsFilter applicationsFilter) {
		sessionFactory.getCurrentSession().saveOrUpdate(applicationsFilter);

	}

	public ApplicationsFilter getApplicationsFilterById(Integer id) {
		return (ApplicationsFilter) sessionFactory.getCurrentSession().get(ApplicationsFilter.class, id);
	}

	public ApplicationsFilter getApplicationsFilterByUser(RegisteredUser user) {
		return (ApplicationsFilter) sessionFactory.getCurrentSession().createCriteria(ApplicationsFilter.class)
				.add(Restrictions.eq("user", user)).uniqueResult();
	}

	public void removeFilter(ApplicationsFilter filter) {
		sessionFactory.getCurrentSession().delete(filter);
	}

}
