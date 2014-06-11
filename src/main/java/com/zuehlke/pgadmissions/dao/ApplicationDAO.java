package com.zuehlke.pgadmissions.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

@Repository
@SuppressWarnings("unchecked")
public class ApplicationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public void save(Application application) {
        sessionFactory.getCurrentSession().saveOrUpdate(application);
    }

    public void refresh(Application applicationForm) {
        sessionFactory.getCurrentSession().refresh(applicationForm);
    }

    public Application getById(int id) {
        return (Application) sessionFactory.getCurrentSession().get(Application.class, id);
    }

    public List<Application> getAllApplications() {
        return sessionFactory.getCurrentSession().createCriteria(Application.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public Long getApplicationsInProgramThisYear(Program program, String year) {
        DateTime startYear = new DateTime(Integer.parseInt(year), 1, 1, 0, 0);
        DateTime endYear = startYear.plusYears(1);

        return (Long) sessionFactory.getCurrentSession().createCriteria(Application.class).setProjection(Projections.rowCount())
                .add(Restrictions.eq("program", program)).add(Restrictions.between("createdTimestamp", startYear, endYear)).uniqueResult();
    }

    public Application getByApplicationNumber(String applicationNumber) {
        return (Application) sessionFactory.getCurrentSession().createCriteria(Application.class).add(Restrictions.eq("applicationNumber", applicationNumber))
                .uniqueResult();

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

    public Application getApplicationByDocument(Document document) {
        return (Application) sessionFactory
                .getCurrentSession()
                .createCriteria(Application.class)
                .createAlias("personalDetails", "personalDetails")
                .createAlias("personalDetails.languageQualifications", "languageQualification")
                .createAlias("qualifications", "qualification")
                .createAlias("fundings", "funding")
                .createAlias("applicationComments", "comment")
                .add(Restrictions.disjunction().add(Restrictions.eq("languageQualication.languageQualificationDocument", document))
                        .add(Restrictions.eq("qualification.proofOfAward", document)).add(Restrictions.eq("funding.document", document))
                        .add(Restrictions.eq("comment.documents", document)).add(Restrictions.eq("personalStatement", document))
                        .add(Restrictions.eq("cv", document))).uniqueResult();
    }

    public Application getPreviousApplication(final Application application) {
        Boolean copySubmittedApplication = true;
        Integer applicationFormId = application.getId();
        User creator = application.getUser();

        Date copyOnDate = (Date) sessionFactory.getCurrentSession().createCriteria(Application.class).setProjection(Projections.max("submittedTimestamp"))
                .add(Restrictions.eq("user", creator)).add(Restrictions.isNotNull("submittedTimestamp")).add(Restrictions.ne("id", applicationFormId))
                .uniqueResult();

        if (copyOnDate == null) {
            copySubmittedApplication = false;
            copyOnDate = (Date) sessionFactory.getCurrentSession().createCriteria(Application.class).setProjection(Projections.min("createdTimestamp"))
                    .add(Restrictions.eq("user", creator)).add(Restrictions.ne("id", applicationFormId)).uniqueResult();
        }

        if (copyOnDate != null) {
            Criteria getPreviousApplication = sessionFactory.getCurrentSession().createCriteria(Application.class).setProjection(Projections.max("id"))
                    .add(Restrictions.eq("user", creator)).add(Restrictions.ne("id", applicationFormId));

            if (BooleanUtils.isTrue(copySubmittedApplication)) {
                getPreviousApplication.add(Restrictions.ge("submittedTimestamp", copyOnDate));
            } else {
                getPreviousApplication.add(Restrictions.ge("createdTimestamp", copyOnDate));
            }

            return getById((Integer) getPreviousApplication.uniqueResult());
        }

        return null;
    }

    public Comment getLatestStateChangeComment(Application applicationForm) {
        return (Comment) sessionFactory.getCurrentSession().createCriteria(Comment.class).uniqueResult();
    }

}
