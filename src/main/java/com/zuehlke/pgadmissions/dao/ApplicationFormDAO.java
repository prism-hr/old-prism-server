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
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.enums.ActionType;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

@Repository
@SuppressWarnings("unchecked")
public class ApplicationFormDAO {

    private SessionFactory sessionFactory;

    public ApplicationFormDAO() {
    }

    @Autowired
    public ApplicationFormDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(ApplicationForm application) {
        sessionFactory.getCurrentSession().saveOrUpdate(application);
    }

    public void refresh(ApplicationForm applicationForm) {
        sessionFactory.getCurrentSession().refresh(applicationForm);
    }

    public ApplicationForm getById(Integer id) {
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

    public ApplicationForm getByApplicationNumber(String applicationNumber) {
        return (ApplicationForm) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                .add(Restrictions.eq("applicationNumber", applicationNumber)).uniqueResult();

    }

    public List<ApplicationForm> getAllApplicationsByStatus(PrismState status) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).add(Restrictions.eq("status", status))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<ApplicationForm> getApplicationsByApplicantAndProgram(User applicant, Program program) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).add(Restrictions.eq("user", applicant))
                .add(Restrictions.eq("program", program)).list();
    }

    public List<ApplicationForm> getApplicationsByApplicantAndProgramAndProject(User applicant, Program program, Project project) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).add(Restrictions.eq("user", applicant))
                .add(Restrictions.eq("program", program)).add(Restrictions.eq("project", project)).list();
    }

    public List<ApplicationForm> getApplicationsByProject(Project project) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).add(Restrictions.eq("project", project)).list();
    }

    public List<ApplicationForm> getActiveApplicationsByProgram(Program program) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).createAlias("status", "state", JoinType.INNER_JOIN)
                .add(Restrictions.eq("program", program)).add(Restrictions.eq("state.underConsideration", true)).list();
    }

    public List<ApplicationForm> getActiveApplicationsByProject(Project project) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).createAlias("status", "state", JoinType.INNER_JOIN)
                .add(Restrictions.eq("project", project)).add(Restrictions.eq("state.underConsideration", true)).list();
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

    public ApplicationForm getPreviousApplicationForApplicant(final ApplicationForm applicationForm, final User applicant) {
        Boolean copySubmittedApplication = true;
        Integer applicationFormId = applicationForm.getId();

        Date copyOnDate = (Date) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).setProjection(Projections.max("submittedTimestamp"))
                .add(Restrictions.eq("user", applicant)).add(Restrictions.isNotNull("submittedTimestamp")).add(Restrictions.ne("id", applicationFormId))
                .uniqueResult();

        if (copyOnDate == null) {
            copySubmittedApplication = false;
            copyOnDate = (Date) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).setProjection(Projections.min("applicationTimestamp"))
                    .add(Restrictions.eq("user", applicant)).add(Restrictions.ne("id", applicationFormId)).uniqueResult();
        }

        if (copyOnDate != null) {
            Criteria getPreviousApplication = sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).setProjection(Projections.max("id"))
                    .add(Restrictions.eq("user", applicant)).add(Restrictions.ne("id", applicationFormId));

            if (BooleanUtils.isTrue(copySubmittedApplication)) {
                getPreviousApplication.add(Restrictions.ge("submittedTimestamp", copyOnDate));
            } else {
                getPreviousApplication.add(Restrictions.ge("applicationTimestamp", copyOnDate));
            }

            return getById((Integer) getPreviousApplication.uniqueResult());
        }

        return null;
    }

    public ApplicationForm getInProgressApplication(final User applicant, final Advert advert) {
        return (ApplicationForm) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).createAlias("status", "state", JoinType.INNER_JOIN)
                .add(Restrictions.eq("applicant", applicant)).add(Restrictions.eq("advert", advert)).add(Restrictions.eq("state.completed", false))
                .addOrder(Order.desc("createdDate")).addOrder(Order.desc("id")).setMaxResults(1).uniqueResult();
    }

    public Boolean getRaisesUpdateFlagForUser(ApplicationForm application, User user) {
        return (Boolean) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUpdate.class).setProjection(Projections.property("raisesUpdateFlag"))
                .add(Restrictions.eq("id.applicationForm", application)).add(Restrictions.eq("id.registeredUser", user)).uniqueResult();
    }

    public Boolean getRaisesUrgentFlagForUser(ApplicationForm application, User user) {
        // FIXME: rewrite as HQL statement
        Boolean raisesUrgentFlag = (Boolean) sessionFactory.getCurrentSession().createCriteria(UserRole.class)
                .add(Restrictions.eq("applicationForm", application)).add(Restrictions.eq("user", user)).addOrder(Order.desc("raisesUrgentFlag"))
                .setProjection(Projections.projectionList().add(Projections.max("raisesUrgentFlag"))).uniqueResult();
        return BooleanUtils.toBoolean(raisesUrgentFlag);
    }

    public void deleteApplicationUpdate(ApplicationForm applicationForm, User user) {
        // FIXME: rewrite as HQL statement
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_APPLICATION_UPDATE(?, ?);").setInteger(0, applicationForm.getId())
                .setInteger(1, user.getId()).executeUpdate();
    }

    public void deleteApplicationRole(ApplicationForm application, User user, Authority authority) {
        // FIXME: rewrite as HQL statement
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_APPLICATION_ROLE(?, ?, ?);").setInteger(0, application.getId())
                .setInteger(1, user.getId()).setString(2, authority.toString()).executeUpdate();
    }

    public void deleteProgramRole(User user, Program program, Authority authority) {
        // FIXME: rewrite as HQL statement
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_PROGRAM_ROLE(?, ?, ?);").setInteger(0, user.getId())
                .setInteger(1, program.getId()).setString(2, authority.toString()).executeUpdate();
    }

    public void deleteUserRole(User user, Authority authority) {
        // FIXME: rewrite as HQL statement
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_USER_ROLE(?, ?);").setInteger(0, user.getId())
                .setString(1, authority.toString()).executeUpdate();
    }

    public void insertProgramRole(User user, Program program, Authority authority) {
        // FIXME: rewrite as HQL statement
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_INSERT_PROGRAM_ROLE(?, ?, ?);").setInteger(0, user.getId())
                .setInteger(1, program.getId()).setString(2, authority.toString()).executeUpdate();
    }

    public void insertUserRole(User user, Authority authority) {
        // FIXME: rewrite as HQL statement
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_INSERT_USER_ROLE(?, ?);").setInteger(0, user.getId())
                .setString(1, authority.toString()).executeUpdate();
    }

    public void updateApplicationDueDate(ApplicationForm applicationForm, Date deadlineTimestamp) {
        // FIXME: rewrite as HQL statement
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_UPDATE_APPLICATION_FORM_DUE_DATE(?, ?);").setInteger(0, applicationForm.getId())
                .setDate(1, deadlineTimestamp).executeUpdate();
    }

    public void updateApplicationInterest(ApplicationForm applicationForm, User user, Boolean interested) {
        // FIXME: rewrite as HQL statement
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_UPDATE_APPLICATION_INTEREST(?, ?, ?);").setInteger(0, applicationForm.getId())
                .setInteger(1, user.getId()).setBoolean(2, interested).executeUpdate();
    }

    public void updateUrgentApplications() {
        // FIXME: rewrite as HQL statement
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_UPDATE_URGENT_APPLICATIONS();").executeUpdate();
    }

    public Comment getLatestStateChangeComment(ApplicationForm applicationForm, ActionType applicationCompleteApprovalStage) {
        return (Comment) sessionFactory.getCurrentSession().createCriteria(Comment.class).uniqueResult();
    }

}
