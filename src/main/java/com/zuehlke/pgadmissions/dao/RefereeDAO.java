package com.zuehlke.pgadmissions.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

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
	@SuppressWarnings("unchecked")
	public List<Referee> getRefereesDueAReminder() {
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		Date oneWeekAgo = DateUtils.addDays(today, -6);
		return (List<Referee>) sessionFactory.getCurrentSession()
					.createCriteria(Referee.class)
					.createAlias("application", "application")
					.add(Restrictions.eq("declined", false))
					.add(Restrictions.isNull("reference"))
					.add(Restrictions.le("lastNotified", oneWeekAgo))
					.add(Restrictions.not(Restrictions.in("application.submissionStatus", new SubmissionStatus[]{SubmissionStatus.UNSUBMITTED})))
					.add(Restrictions.isNull("application.approvalStatus"))
				.list();
	}

}
