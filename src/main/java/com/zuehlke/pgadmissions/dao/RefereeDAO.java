package com.zuehlke.pgadmissions.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ReminderType;

@Repository
@SuppressWarnings("unchecked")
public class RefereeDAO {

    private final SessionFactory sessionFactory;

    public RefereeDAO() {
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

    public List<Integer> getRefereesDueReminder() {
        // TODO use ApplicationFormUserRole
        Date today = Calendar.getInstance().getTime();
        ReminderInterval reminderInterval = (ReminderInterval) sessionFactory.getCurrentSession().createCriteria(ReminderInterval.class)
                .add(Restrictions.eq("reminderType", ReminderType.REFERENCE)).uniqueResult();
        Date dateWithSubtractedInterval = DateUtils.addMinutes(today, -reminderInterval.getDurationInMinutes());
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Referee.class)
                .setProjection(Projections.groupProperty("id"))
                .createAlias("application", "application", JoinType.INNER_JOIN)
                .createAlias("reference", "referenceComment", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.isNull("referenceComment.id"))
                .add(Restrictions.eq("declined", false))
                .add(Restrictions.isNotNull("user"))
                .add(Restrictions.not(Restrictions.in("application.status", new ApplicationFormStatus[]{ApplicationFormStatus.WITHDRAWN,
                        ApplicationFormStatus.APPROVED, ApplicationFormStatus.REJECTED, ApplicationFormStatus.UNSUBMITTED})))
                .add(Restrictions.le("lastNotified", dateWithSubtractedInterval)).list();
    }

    public List<Referee> getRefereesWhoDidntProvideReferenceYet(ApplicationForm form) {
        return (List<Referee>) sessionFactory.getCurrentSession().createCriteria(Referee.class)
                .createAlias("reference", "referenceComment", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.isNull("referenceComment.id"))
                .add(Restrictions.eq("declined", false))
                .add(Restrictions.eq("application", form)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public Referee getRefereeByUser(User user) {
        return (Referee) sessionFactory.getCurrentSession().createCriteria(Referee.class).add(Restrictions.eq("user", user)).uniqueResult();
    }

    public void refresh(Referee referee) {
        sessionFactory.getCurrentSession().refresh(referee);
    }
    
}