package com.zuehlke.pgadmissions.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.zuehlke.pgadmissions.domain.ProgramInstance;

public class ProgramInstanceDAO {

	private final SessionFactory sessionFactory;

	public ProgramInstanceDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;	
	}

	@SuppressWarnings("unchecked")
	public List<ProgramInstance> getActiveProgramInstances() {
		Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
		return (List<ProgramInstance>) sessionFactory.getCurrentSession()
				.createCriteria(ProgramInstance.class)
				.add(Restrictions.ge("applicationDeadline", today))
				.list();
	}

}
