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

import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.enums.InterviewStage;
import com.zuehlke.pgadmissions.domain.enums.ReminderType;

@Repository
@SuppressWarnings("unchecked")
public class InterviewParticipantDAO {

    private final SessionFactory sessionFactory;

    public InterviewParticipantDAO() {
        this(null);
    }

    @Autowired
    public InterviewParticipantDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<InterviewParticipant> getInterviewParticipantsDueReminder() {
        Date today = Calendar.getInstance().getTime();
        ReminderInterval reminderInterval = (ReminderInterval) sessionFactory.getCurrentSession().createCriteria(ReminderInterval.class)
                .add(Restrictions.eq("reminderType", ReminderType.INTERVIEW_SCHEDULE)).uniqueResult();
        Date dateWithSubtractedInterval = DateUtils.addMinutes(today, -reminderInterval.getDurationInMinutes());
        return (List<InterviewParticipant>) sessionFactory.getCurrentSession().createCriteria(InterviewParticipant.class)
                .createAlias("interview", "interview")
                .createAlias("interview.application", "application")
                .add(Restrictions.eqProperty("application.latestInterview", "interview"))
                .add(Restrictions.eq("interview.stage", InterviewStage.SCHEDULING))
                .add(Restrictions.eq("responded", false))
                .add(Restrictions.le("lastNotified", dateWithSubtractedInterval)) 
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public InterviewParticipant getParticipantById(Integer id) {
        return (InterviewParticipant) sessionFactory.getCurrentSession().get(InterviewParticipant.class, id);
    }

    public void save(InterviewParticipant participant) {
        sessionFactory.getCurrentSession().saveOrUpdate(participant);
    }

    public InterviewParticipant initialise(InterviewParticipant proxyParticipant) {
        return getParticipantById(proxyParticipant.getId());
    }
}
