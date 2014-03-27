package com.zuehlke.pgadmissions.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Repository
@SuppressWarnings("unchecked")
public class ApplicationFormDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private StateDAO stateDao;
    
    public void save(ApplicationForm application) {
        sessionFactory.getCurrentSession().saveOrUpdate(application);
    }

    public void refresh(ApplicationForm applicationForm) {
        sessionFactory.getCurrentSession().refresh(applicationForm);
    }

    public ApplicationForm get(Integer id) {
        return (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
    }

    public List<ApplicationForm> getAllApplications() {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

    }

    public List<Qualification> getQualificationsByApplication(ApplicationForm application) {
        return sessionFactory.getCurrentSession().createCriteria(Qualification.class).add(Restrictions.eq("application", application))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public Long getApplicationsInProgramThisYear(Program program, String year) {
        Date startYear = null;

        try {
            startYear = new SimpleDateFormat("yyyy").parse(year);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

        Date endYear = DateUtils.addYears(startYear, 1);

        return (Long) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).setProjection(Projections.rowCount())
                .add(Restrictions.eq("program", program)).add(Restrictions.between("applicationTimestamp", startYear, endYear)).uniqueResult();
    }

    public ApplicationForm getApplicationByApplicationNumber(String applicationNumber) {
        return (ApplicationForm) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                .add(Restrictions.eq("applicationNumber", applicationNumber)).uniqueResult();

    }

    public List<ApplicationForm> getAllApplicationsByStatus(ApplicationFormStatus status) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                .add(Restrictions.eq("status", status))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<ApplicationForm> getApplicationsByApplicantAndProgram(RegisteredUser applicant, Program program) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                .add(Restrictions.eq("applicant", applicant))
                .add(Restrictions.eq("program", program)).list();
    }

    public List<ApplicationForm> getApplicationsByApplicantAndProgramAndProject(RegisteredUser applicant, Program program, Project project) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                .add(Restrictions.eq("applicant", applicant))
                .add(Restrictions.eq("program", program))
                .add(Restrictions.eq("project", project)).list();
    }

    public List<ApplicationForm> getApplicationsByProject(Project project) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                .add(Restrictions.eq("project", project)).list();
    }

    public List<ApplicationForm> getActiveApplicationsByProgram(Program program) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                .createAlias("status", "state", JoinType.INNER_JOIN)
                .add(Restrictions.eq("program", program))
                .add(Restrictions.eq("state.underConsideration", true)).list();
    }

    public List<ApplicationForm> getActiveApplicationsByProject(Project project) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                .createAlias("status", "state", JoinType.INNER_JOIN)
                .add(Restrictions.eq("project", project))
                .add(Restrictions.eq("state.underConsideration", true)).list();
    }

    public ApplicationForm getApplicationByDocument(Document document) {
        return (ApplicationForm) sessionFactory
                .getCurrentSession()
                .createCriteria(ApplicationForm.class)
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

    public ApplicationForm getPreviousApplicationForApplicant(final ApplicationForm applicationForm, final RegisteredUser applicant) {
        Boolean copySubmittedApplication = true;
        Integer applicationFormId = applicationForm.getId();

        Date copyOnDate = (Date) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                .setProjection(Projections.max("submittedDate"))
                .add(Restrictions.eq("applicant", applicant))
                .add(Restrictions.isNotNull("submittedDate"))
                .add(Restrictions.ne("id", applicationFormId)).uniqueResult();

        if (copyOnDate == null) {
            copySubmittedApplication = false;
            copyOnDate = (Date) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                    .setProjection(Projections.min("applicationTimestamp"))
                    .add(Restrictions.eq("applicant", applicant))
                    .add(Restrictions.ne("id", applicationFormId)).uniqueResult();
        }

        if (copyOnDate != null) {
            Criteria getPreviousApplication = sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                    .setProjection(Projections.max("id"))
                    .add(Restrictions.eq("applicant", applicant))
                    .add(Restrictions.ne("id", applicationFormId));

            if (BooleanUtils.isTrue(copySubmittedApplication)) {
                getPreviousApplication.add(Restrictions.ge("submittedDate", copyOnDate));
            } else {
                getPreviousApplication.add(Restrictions.ge("applicationTimestamp", copyOnDate));
            }

            return get((Integer) getPreviousApplication.uniqueResult());
        }

        return null;
    }
    
    public ApplicationForm getInProgressApplication(final RegisteredUser applicant, final Advert advert) {
        return (ApplicationForm) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                .createAlias("status", "state", JoinType.INNER_JOIN)
                .add(Restrictions.eq("applicant", applicant))
                .add(Restrictions.eq("advert", advert))
                .add(Restrictions.eq("state.completed", false))
                .addOrder(Order.desc("createdDate"))
                .addOrder(Order.desc("id"))
                .setMaxResults(1).uniqueResult();
    }

}
