package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Repository
public class RefereeDAO {

	private final SessionFactory sessionFactory;

	RefereeDAO() {
		this(null);
	}

	@Autowired
	public RefereeDAO(SessionFactory sessionFactory) {
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

	@SuppressWarnings("unchecked")
	public List<Referee> getRefereesDueAReminder() {
        List<Referee> refereesDueReminder = new ArrayList<Referee>();
        Date today = Calendar.getInstance().getTime();
        ReminderInterval reminderInterval = (ReminderInterval) sessionFactory.getCurrentSession()
                .createCriteria(ReminderInterval.class).uniqueResult();
        Date dateWithSubtractedInterval = DateUtils.addMinutes(today, -reminderInterval.getDurationInMinutes());
        List<Referee> referees = (List<Referee>) sessionFactory
                .getCurrentSession()
                .createCriteria(Referee.class)
                .createAlias("application", "application")
                .add(Restrictions.eq("declined", false))
                .add(Restrictions.isNotNull("user"))
                .add(Restrictions.not(Restrictions.in("application.status", new ApplicationFormStatus[] {
                        ApplicationFormStatus.WITHDRAWN, ApplicationFormStatus.APPROVED,
                        ApplicationFormStatus.REJECTED, ApplicationFormStatus.UNSUBMITTED })))
                .add(Restrictions.le("lastNotified", dateWithSubtractedInterval))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        for (Referee referee : referees) {
            if (!referee.hasProvidedReference()) {
                refereesDueReminder.add(referee);
            }
        }
        return refereesDueReminder;
	}
	
	@SuppressWarnings("unchecked")
	public List<Referee> getRefereesWhoDidntProvideReferenceYet(ApplicationForm form) {
        List<Referee> refereesDueReminder = new ArrayList<Referee>();
        List<Referee> referees = (List<Referee>) sessionFactory.getCurrentSession().createCriteria(Referee.class)
                .add(Restrictions.eq("declined", false)).add(Restrictions.eq("application", form))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        for (Referee referee : referees) {
            if (!referee.hasProvidedReference()) {
                refereesDueReminder.add(referee);
            }
        }
		return refereesDueReminder;
	}
	
	@SuppressWarnings("unchecked")
	public List<Referee> getRefereesDueNotification() {		
        List<Referee> refereesDueReminder = new ArrayList<Referee>();
        List<Referee> referees = sessionFactory
                .getCurrentSession()
                .createCriteria(Referee.class)
                .add(Restrictions.isNull("lastNotified"))
                .add(Restrictions.eq("declined", false))
                .createAlias("application", "application")
                .add(Restrictions.not(Restrictions.in("application.status", new ApplicationFormStatus[] {
                        ApplicationFormStatus.WITHDRAWN, ApplicationFormStatus.APPROVED,
                        ApplicationFormStatus.REJECTED, ApplicationFormStatus.VALIDATION,
                        ApplicationFormStatus.UNSUBMITTED }))).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
        for (Referee referee : referees) {
            if (!referee.hasProvidedReference()) {
                refereesDueReminder.add(referee);
            }
        }
		return refereesDueReminder;
	}

    public Referee getRefereeByUser(RegisteredUser user) {
        return (Referee) sessionFactory.getCurrentSession().createCriteria(Referee.class).add(Restrictions.eq("user", user)).uniqueResult();
    }
    
    public void refresh(Referee referee){
        sessionFactory.getCurrentSession().refresh(referee);
    }
}
