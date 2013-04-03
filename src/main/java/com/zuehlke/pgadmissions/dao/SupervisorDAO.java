package com.zuehlke.pgadmissions.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Repository
public class SupervisorDAO {

	private final SessionFactory sessionFactory;

	public SupervisorDAO() {
		this(null);
	}

	@Autowired
	public SupervisorDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Supervisor getSupervisorWithId(Integer id) {
		return (Supervisor) sessionFactory.getCurrentSession().get(Supervisor.class, id);
	}

	public void save(Supervisor supervisor) {
		sessionFactory.getCurrentSession().saveOrUpdate(supervisor);
	}

	/**
	 * @deprecated This method is now being replaced by the {@link #getPrimarySupervisorsDueNotification() getPrimarySupervisorsDueNotification} method.
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public List<Supervisor> getSupervisorsDueNotification() {
		return sessionFactory.getCurrentSession()
		        .createCriteria(Supervisor.class, "supervisor")
		        .add(Restrictions.isNull("lastNotified"))
				.createAlias("approvalRound.application", "application")
				.add(Restrictions.eq("application.status", ApplicationFormStatus.APPROVAL))
				.add(Restrictions.eqProperty("approvalRound", "application.latestApprovalRound"))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
	}

    public Supervisor getSupervisorByUser(RegisteredUser user) {
        return (Supervisor) sessionFactory.getCurrentSession().createCriteria(Supervisor.class).add(Restrictions.eq("user", user)).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Supervisor> getPrimarySupervisorsDueNotification() {
        return sessionFactory.getCurrentSession()
                .createCriteria(Supervisor.class, "supervisor")
                .add(Restrictions.isNull("lastNotified"))
                .add(Restrictions.isNull("confirmedSupervision"))
                .add(Restrictions.eq("isPrimary", true))
                .createAlias("approvalRound.application", "application")
                .add(Restrictions.eq("application.status", ApplicationFormStatus.APPROVAL))
                .add(Restrictions.eqProperty("approvalRound", "application.latestApprovalRound"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<Supervisor> getPrimarySupervisorsDueReminder() {
        Date today = Calendar.getInstance().getTime();
        ReminderInterval reminderInterval = (ReminderInterval) sessionFactory.getCurrentSession().createCriteria(ReminderInterval.class).uniqueResult();
        Date dateWithSubtractedInterval = DateUtils.addMinutes(today, -reminderInterval.getDurationInMinutes());
        return sessionFactory.getCurrentSession()
                .createCriteria(Supervisor.class, "supervisor")
                .add(Restrictions.le("lastNotified", dateWithSubtractedInterval))
                .add(Restrictions.eq("isPrimary", true))
                .add(Restrictions.isNull("confirmedSupervision"))
                .createAlias("approvalRound.application", "application")
                .add(Restrictions.eq("application.status", ApplicationFormStatus.APPROVAL))
                .add(Restrictions.eqProperty("approvalRound", "application.latestApprovalRound"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
        }
}
