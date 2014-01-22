package com.zuehlke.pgadmissions.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramFeed;
import com.zuehlke.pgadmissions.domain.ProgramInstance;

@Repository
@SuppressWarnings("unchecked")
public class ProgramInstanceDAO {

    private final SessionFactory sessionFactory;

    ProgramInstanceDAO() {
        this(null);
    }

    @Autowired
    public ProgramInstanceDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<ProgramInstance> getActiveProgramInstances(Program program) {
        Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
        return (List<ProgramInstance>) sessionFactory.getCurrentSession().createCriteria(ProgramInstance.class).add(Restrictions.eq("program", program))
                .add(Restrictions.eq("enabled", true)).add(Restrictions.ge("applicationDeadline", today)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    public List<ProgramInstance> getActiveProgramInstancesOrderedByApplicationStartDate(Program program, String studyOption) {
        Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
        return (List<ProgramInstance>) sessionFactory.getCurrentSession().createCriteria(ProgramInstance.class).add(Restrictions.eq("program", program))
                .add(Restrictions.eq("enabled", true)).add(Restrictions.eq("studyOption", studyOption)).add(Restrictions.ge("applicationDeadline", today))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).addOrder(Order.asc("applicationStartDate")).list();
    }

    public List<ProgramInstance> getProgramInstancesWithStudyOptionAndDeadlineNotInPast(Program program, String studyOption) {
        Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
        return (List<ProgramInstance>) sessionFactory.getCurrentSession().createCriteria(ProgramInstance.class).add(Restrictions.eq("program", program))
                .add(Restrictions.eq("enabled", true)).add(Restrictions.eq("studyOption", studyOption)).add(Restrictions.ge("applicationDeadline", today))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<ProgramInstance> getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(Program program, String studyOption) {
        Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
        return (List<ProgramInstance>) sessionFactory.getCurrentSession().createCriteria(ProgramInstance.class).add(Restrictions.eq("program", program))
                .add(Restrictions.eq("enabled", true)).add(Restrictions.eq("studyOption", studyOption)).add(Restrictions.ge("applicationDeadline", today))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).addOrder(Order.asc("applicationDeadline")).list();
    }

    public ProgramInstance getCurrentProgramInstanceForStudyOption(Program program, String studyOption) {
        Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
        List<ProgramInstance> futureInstances = sessionFactory.getCurrentSession().createCriteria(ProgramInstance.class)
                .add(Restrictions.eq("program", program)).add(Restrictions.eq("enabled", true)).add(Restrictions.eq("studyOption", studyOption))
                .add(Restrictions.ge("applicationDeadline", today)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .addOrder(Order.asc("applicationDeadline")).list();
        return futureInstances.get(0);
    }

    public ProgramInstance getById(Integer id) {
        return (ProgramInstance) sessionFactory.getCurrentSession().createCriteria(ProgramInstance.class).add(Restrictions.eq("id", id)).uniqueResult();
    }

    public List<ProgramInstance> getAllProgramInstances(ProgramFeed programFeed) {
        return sessionFactory.getCurrentSession().createCriteria(ProgramInstance.class).createAlias("program", "p")
                .add(Restrictions.eq("p.programFeed", programFeed)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<Object[]> getDistinctStudyOptions() {
        return sessionFactory
                .getCurrentSession()
                .createCriteria(ProgramInstance.class)
                .setProjection(
                        Projections.distinct(Projections.projectionList().add(Projections.property("studyOptionCode")).add(Projections.property("studyOption"))))
                .list();
    }

    public void save(ProgramInstance programInstance) {
        sessionFactory.getCurrentSession().saveOrUpdate(programInstance);
    }

}
