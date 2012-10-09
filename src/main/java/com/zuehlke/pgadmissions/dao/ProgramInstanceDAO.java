package com.zuehlke.pgadmissions.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
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
				.add(Restrictions.ge("applicationDeadline", today)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
	}
	
    @SuppressWarnings("unchecked")
    public List<ProgramInstance> getActiveProgramInstancesOrderedByApplicationStartDate(Program program, StudyOption studyOption) {
        Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
        return (List<ProgramInstance>) sessionFactory.getCurrentSession()
                .createCriteria(ProgramInstance.class)
                .add(Restrictions.eq("program", program))
                .add(Restrictions.eq("studyOption", studyOption))
                .add(Restrictions.ge("applicationStartDate", today))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .addOrder(Order.asc("applicationStartDate"))
                .list();
    }

	@SuppressWarnings("unchecked")
	public List<ProgramInstance> getProgramInstancesWithStudyOptionAndDeadlineNotInPast(Program program, StudyOption studyOption) {
		Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
		return (List<ProgramInstance>) sessionFactory.getCurrentSession()
				.createCriteria(ProgramInstance.class)
				.add(Restrictions.eq("program", program))
				.add(Restrictions.eq("studyOption", studyOption))
				.add(Restrictions.ge("applicationDeadline", today)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
	}
	
	@SuppressWarnings("unchecked")
    public List<ProgramInstance> getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(Program program, StudyOption studyOption) {
        Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
        return (List<ProgramInstance>) sessionFactory.getCurrentSession()
                .createCriteria(ProgramInstance.class)
                .add(Restrictions.eq("program", program))
                .add(Restrictions.eq("studyOption", studyOption))
                .add(Restrictions.ge("applicationDeadline", today)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .addOrder(Order.asc("applicationDeadline"))
                .list();
    }
	
	@SuppressWarnings("unchecked")
	public ProgramInstance getCurrentProgramInstanceForStudyOption(Program program, StudyOption studyOption) {
		Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
		List<ProgramInstance> futureInstances = sessionFactory.getCurrentSession()
				.createCriteria(ProgramInstance.class)
				.add(Restrictions.eq("program", program))
				.add(Restrictions.eq("studyOption", studyOption))
				.add(Restrictions.ge("applicationDeadline", today))		
				.addOrder(Order.asc("applicationDeadline")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
			return futureInstances.get(0);
	}

}
