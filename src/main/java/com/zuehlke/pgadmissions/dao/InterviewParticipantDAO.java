package com.zuehlke.pgadmissions.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.enums.InterviewStage;

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

    public List<Integer> getInterviewParticipantsIdsDueAReminder() {
        Date today = Calendar.getInstance().getTime();
        ReminderInterval reminderInterval = (ReminderInterval) sessionFactory.getCurrentSession().createCriteria(ReminderInterval.class).uniqueResult();
        Date dateWithSubtractedInterval = DateUtils.addMinutes(today, -reminderInterval.getDurationInMinutes());
        List<Integer> participants = (List<Integer>) sessionFactory.getCurrentSession().createCriteria(InterviewParticipant.class)
                .createAlias("interview", "interview").add(Restrictions.eq("interview.stage", InterviewStage.SCHEDULING))
                .add(Restrictions.eq("responded", false)).add(Restrictions.le("lastNotified", dateWithSubtractedInterval))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).setProjection(Projections.property("id")).list();
        return participants;
    }

    public InterviewParticipant getParticipantById(Integer id) {
        return (InterviewParticipant) sessionFactory.getCurrentSession().get(InterviewParticipant.class, id);
    }
    
}
