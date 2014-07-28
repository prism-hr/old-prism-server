package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

@Repository
@SuppressWarnings("unchecked")
public class ApplicationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<Application> getAllApplications() {
        return sessionFactory.getCurrentSession().createCriteria(Application.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public Long getApplicationsInProgramThisYear(Program program, String year) {
        DateTime startYear = new DateTime(Integer.parseInt(year), 1, 1, 0, 0);
        DateTime endYear = startYear.plusYears(1);

        return (Long) sessionFactory.getCurrentSession().createCriteria(Application.class).setProjection(Projections.rowCount())
                .add(Restrictions.eq("program", program)).add(Restrictions.between("createdTimestamp", startYear, endYear)).uniqueResult();
    }

    public List<Application> getAllApplicationsByStatus(PrismState status) {
        return sessionFactory.getCurrentSession().createCriteria(Application.class).add(Restrictions.eq("status", status))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<Application> getApplicationsByApplicantAndProgram(User applicant, Program program) {
        return sessionFactory.getCurrentSession().createCriteria(Application.class).add(Restrictions.eq("user", applicant))
                .add(Restrictions.eq("program", program)).list();
    }

    public List<Application> getApplicationsByApplicantAndProgramAndProject(User applicant, Program program, Project project) {
        return sessionFactory.getCurrentSession().createCriteria(Application.class).add(Restrictions.eq("user", applicant))
                .add(Restrictions.eq("program", program)).add(Restrictions.eq("project", project)).list();
    }

    public List<Application> getApplicationsByProject(Project project) {
        return sessionFactory.getCurrentSession().createCriteria(Application.class).add(Restrictions.eq("project", project)).list();
    }

    public List<Application> getActiveApplicationsByProgram(Program program) {
        return sessionFactory.getCurrentSession().createCriteria(Application.class).createAlias("status", "state", JoinType.INNER_JOIN)
                .add(Restrictions.eq("program", program)).add(Restrictions.eq("state.underConsideration", true)).list();
    }

    public List<Application> getActiveApplicationsByProject(Project project) {
        return sessionFactory.getCurrentSession().createCriteria(Application.class).createAlias("status", "state", JoinType.INNER_JOIN)
                .add(Restrictions.eq("project", project)).add(Restrictions.eq("state.underConsideration", true)).list();
    }

    public Application getPreviousSubmittedApplication(Application application) {
        return (Application) sessionFactory.getCurrentSession().createCriteria(Application.class)
                .add(Restrictions.eq("user", application.getUser())) //
                .add(Restrictions.isNotNull("submittedTimestamp")) //
                .add(Restrictions.ne("id", application.getId())) //
                .addOrder(Order.desc("submittedTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }
    
    public Application getPreviousUnsubmittedApplication(Application application) {
        return (Application) sessionFactory.getCurrentSession().createCriteria(Application.class)
                .add(Restrictions.eq("user", application.getUser())) //
                .add(Restrictions.isNull("submittedTimestamp")) //
                .add(Restrictions.ne("id", application.getId())) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

}
