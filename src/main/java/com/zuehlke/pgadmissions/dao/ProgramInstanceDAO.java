package com.zuehlke.pgadmissions.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;

@Repository
public class ProgramInstanceDAO {

	private final SessionFactory sessionFactory;
	
	 ProgramInstanceDAO() {
		this(null);
	}
	
	@Autowired
	public ProgramInstanceDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;	
	}

	@SuppressWarnings("unchecked")
	public List<ProgramInstance> getActiveProgramInstances(Program program) {
		Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
		return (List<ProgramInstance>) sessionFactory.getCurrentSession()
				.createCriteria(ProgramInstance.class)
				.add(Restrictions.eq("program", program))
				.add(Restrictions.ge("applicationDeadline", today))
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<ProgramInstance> getProgramInstancesWithStudyOptionAndDeadlineNotInPast(Program program, StudyOption studyOption) {
		Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
		return (List<ProgramInstance>) sessionFactory.getCurrentSession()
				.createCriteria(ProgramInstance.class)
				.add(Restrictions.eq("program", program))
				.add(Restrictions.eq("studyOption", studyOption))
				.add(Restrictions.ge("applicationDeadline", today))
				.list();
	}

}
