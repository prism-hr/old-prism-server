package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
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

    public List<Integer> getRefereesIdsDueAReminder() {
        Date today = Calendar.getInstance().getTime();
        ReminderInterval reminderInterval = (ReminderInterval) sessionFactory.getCurrentSession().createCriteria(ReminderInterval.class)
                .add(Restrictions.eq("reminderType", ReminderType.REFERENCE)).uniqueResult();
        Date dateWithSubtractedInterval = DateUtils.addMinutes(today, -reminderInterval.getDurationInMinutes());
        List<Referee> referees = (List<Referee>) sessionFactory.getCurrentSession().createCriteria(Referee.class)
                .createAlias("application", "application")
                .add(Restrictions.isNotNull("user"))
                .add(Restrictions.not(Restrictions.in("application.status", new ApplicationFormStatus[] { ApplicationFormStatus.WITHDRAWN,
                        ApplicationFormStatus.APPROVED, ApplicationFormStatus.REJECTED, ApplicationFormStatus.UNSUBMITTED })))
                .add(Restrictions.le("lastNotified", dateWithSubtractedInterval)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        final List<Integer> result = new ArrayList<Integer>(referees.size());
        CollectionUtils.forAllDo(referees, new Closure() {
            @Override
            public void execute(Object input) {
                Referee referee = (Referee) input;
                if (!referee.hasProvidedReference()) {
                    result.add(referee.getId());
                }
            }
        });
        return result;
    }

    public List<Referee> getRefereesWhoDidntProvideReferenceYet(ApplicationForm form) {
        List<Referee> referees = (List<Referee>) sessionFactory.getCurrentSession().createCriteria(Referee.class)
                .add(Restrictions.eq("application", form))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        CollectionUtils.filter(referees, new Predicate() {
            @Override
            public boolean evaluate(final Object object) {
                Referee referee = (Referee) object;
                if (!referee.hasProvidedReference()) {
                    return true;
                }
                return false;
            }
        });
        return referees;
    }

    public Referee getRefereeByUser(RegisteredUser user) {
        return (Referee) sessionFactory.getCurrentSession().createCriteria(Referee.class)
        		.add(Restrictions.eq("user", user)).uniqueResult();
    }
    
    public Referee getRefereeByUserAndApplication(RegisteredUser user, ApplicationForm application) {
        return (Referee) sessionFactory.getCurrentSession().createCriteria(Referee.class)
        		.add(Restrictions.eq("user", user))
        		.add(Restrictions.eq("application", application)).uniqueResult();
    }

    public void refresh(Referee referee) {
        sessionFactory.getCurrentSession().refresh(referee);
    }
    
}