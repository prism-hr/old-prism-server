package com.zuehlke.pgadmissions.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ApplicationReportListRowDTO;
import com.zuehlke.pgadmissions.rest.representation.ApplicationSummaryRepresentation.OtherApplicationSummaryRepresentation;

@Repository
@SuppressWarnings("unchecked")
public class ApplicationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Application getPreviousSubmittedApplication(Application application) {
        return (Application) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("user", application.getUser())) //
                .add(Restrictions.eq("institution.locale", application.getLocale())) //
                .add(Restrictions.isNotNull("submittedTimestamp")) //
                .addOrder(Order.desc("submittedTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public Application getPreviousUnsubmittedApplication(Application application) {
        return (Application) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("user", application.getUser())) //
                .add(Restrictions.eq("institution.locale", application.getLocale())) //
                .add(Restrictions.isNull("submittedTimestamp")) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public String getApplicationExportReference(Application application) {
        return (String) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.property("exportReference")) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.isNotNull("exportReference")) //
                .uniqueResult();
    }

    public String getApplicationCreatorIpAddress(Application application) {
        return (String) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.property("creatorIpAddress")) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.isNotNull("creatorIpAddress")) //
                .uniqueResult();
    }

    public User getPrimarySupervisor(Comment offerRecommendationComment) {
        return (User) sessionFactory.getCurrentSession().createCriteria(CommentAssignedUser.class) //
                .setProjection(Projections.property("user")) //
                .add(Restrictions.eq("comment", offerRecommendationComment)) //
                .add(Restrictions.eq("role.id", PrismRole.APPLICATION_PRIMARY_SUPERVISOR)) //
                .uniqueResult();
    }

    public List<ApplicationReferee> getApplicationRefereesResponded(Application application) {
        return (List<ApplicationReferee>) sessionFactory.getCurrentSession().createCriteria(ApplicationReferee.class) //
                .createAlias("comment", "comment", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("application", application)) //
                .addOrder(Order.desc("comment.applicationRating")) //
                .addOrder(Order.asc("comment.createdTimestamp")) //
                .addOrder(Order.asc("comment.id")) //
                .list();
    }

    public List<ApplicationReferee> getApplicationRefereesNotResponded(Application application) {
        return (List<ApplicationReferee>) sessionFactory.getCurrentSession().createCriteria(ApplicationReferee.class) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.isNull("comment")) //
                .addOrder(Order.asc("id")) //
                .list();
    }

    public List<ApplicationQualification> getApplicationExportQualifications(Application application) {
        return (List<ApplicationQualification>) sessionFactory.getCurrentSession().createCriteria(ApplicationQualification.class) //
                .add(Restrictions.eq("application", application)) //
                .addOrder(Order.desc("awardDate")) //
                .addOrder(Order.desc("startDate")) //
                .list();
    }

    public ApplicationReferee getRefereeByUser(Application application, User user) {
        return (ApplicationReferee) sessionFactory.getCurrentSession().createCriteria(ApplicationReferee.class) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.eq("user", user)) //
                .uniqueResult();
    }

    public List<Integer> getApplicationsForExport() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("action.actionCategory", PrismActionCategory.EXPORT_RESOURCE)) //
                .list();
    }

    public ApplicationQualification getLatestApplicationQualification(Application application) {
        return (ApplicationQualification) sessionFactory.getCurrentSession().createCriteria(ApplicationQualification.class) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.eq("completed", true)) //
                .addOrder(Order.desc("awardDate")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public ApplicationEmploymentPosition getLatestApplicationEmploymentPosition(Application application) {
        return (ApplicationEmploymentPosition) sessionFactory.getCurrentSession().createCriteria(ApplicationEmploymentPosition.class) //
                .add(Restrictions.eq("application", application)) //
                .addOrder(Order.desc("current")) //
                .addOrder(Order.desc("endDate")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public Long getProvidedReferenceCount(Application application) {
        return (Long) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.rowCount()) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.eq("action.id", PrismAction.APPLICATION_PROVIDE_REFERENCE)) //
                .add(Restrictions.eq("declinedResponse", false)) //
                .uniqueResult();
    }

    public Long getDeclinedReferenceCount(Application application) {
        return (Long) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.rowCount()) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.eq("action.id", PrismAction.APPLICATION_PROVIDE_REFERENCE)) //
                .add(Restrictions.eq("declinedResponse", true)) //
                .uniqueResult();
    }

    public List<OtherApplicationSummaryRepresentation> getOtherLiveApplications(Application application) {
        return (List<OtherApplicationSummaryRepresentation>) sessionFactory.getCurrentSession().createCriteria(Application.class, "application") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property("program.title"), "program") //
                        .add(Projections.property("project.title"), "project") //
                        .add(Projections.property("applicationRatingCount"), "ratingCount") //
                        .add(Projections.property("applicationRatingAverage"), "ratingAverage") //
                        .add(Projections.property("stateGroup.id"), "stateGroup")) //
                .createAlias("program", "program", JoinType.INNER_JOIN) //
                .createAlias("project", "project", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("institution", application.getInstitution())) //
                .add(Restrictions.eq("user", application.getUser())) //
                .add(Restrictions.ne("id", application.getId())) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.between("stateGroup.sequenceOrder", 2, 7)) //
                        .add(Restrictions.in("state.id", Arrays.asList(PrismState.APPLICATION_APPROVAL, PrismState.APPLICATION_REJECTED)))) //
                .addOrder(Order.desc("sequenceIdentifier")) //
                .setResultTransformer(Transformers.aliasToBean(OtherApplicationSummaryRepresentation.class)) //
                .list();
    }

    public List<ApplicationReportListRowDTO> getApplicationReport(Set<Integer> assignedApplications) {
        return (List<ApplicationReportListRowDTO>) sessionFactory.getCurrentSession().createCriteria(Application.class, "application") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id"), "id") //
                        .add(Projections.property("user.fullName"), "fullName") //
                        .add(Projections.property("user.email"), "email") //
                        .add(Projections.property("nationality.name"), "nationality") //
                        .add(Projections.property("domicile.name"), "domicile") //
                        .add(Projections.property("country.name"), "country") //
                        .add(Projections.property("personalDetail.dateOfBirth"), "dateOfBirth") //
                        .add(Projections.property("gender.name"), "gender") //
                        .add(Projections.property("institution.title"), "institution") //
                        .add(Projections.property("program.title"), "program") //
                        .add(Projections.property("project.title"), "project") //
                        .add(Projections.property("studyOption.code"), "studyOption") //
                        .add(Projections.property("referralSource.name"), "referralSource") //
                        .add(Projections.property("referrer"), "referrer") //
                        .add(Projections.property("createdTimestamp"), "createdDate") //
                        .add(Projections.property("closingDate"), "closingDate") //
                        .add(Projections.property("submittedTimestamp"), "submittedDate") //
                        .add(Projections.property("updatedTimestamp"), "updatedDate") //
                        .add(Projections.property("applicationRatingCount"), "ratingCount") //
                        .add(Projections.property("applicationRatingAverage"), "ratingAverage") //
                        .add(Projections.count("provideReferenceComment.id"), "providedReferences") //
                        .add(Projections.count("declineReferenceComment.id"), "declinedReferences") //
                        .add(Projections.property("verificationProcessing.instanceCount"), "verificationInstanceCount") //
                        .add(Projections.property("verificationProcessing.dayDurationAverage"), "verificationInstanceDurationAverage") //
                        .add(Projections.property("referenceProcessing.instanceCount"), "referenceInstanceCount") //
                        .add(Projections.property("referenceProcessing.dayDurationAverage"), "referenceInstanceDurationAverage") //
                        .add(Projections.property("reviewProcessing.instanceCount"), "reviewInstanceCount") //
                        .add(Projections.property("reviewProcessing.dayDurationAverage"), "reviewInstanceDurationAverage") //
                        .add(Projections.property("interviewProcessing.instanceCount"), "interviewInstanceCount") //
                        .add(Projections.property("interviewProcessing.dayDurationAverage"), "interviewInstanceDurationAverage") //
                        .add(Projections.property("approvalProcessing.instanceCount"), "approvalInstanceCount") //
                        .add(Projections.property("approvalProcessing.dayDurationAverage"), "approvalInstanceDurationAverage") //
                        .add(Projections.property("confirmedStartDate"), "confirmedStartDate") //
                        .add(Projections.property("confirmedOfferType"), "confirmedOfferType")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("personalDetail", "personalDetail", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("personalDetail.firstNationality", "nationality", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("personalDetail.domicile", "domicile", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("personalDetail.country", "country", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("personalDetail.gender", "gender", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("program", "program", JoinType.INNER_JOIN) //
                .createAlias("project", "project", JoinType.INNER_JOIN) //
                .createAlias("programDetail", "programDetail", JoinType.INNER_JOIN) //
                .createAlias("programDetail.studyOption", "studyOption", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("programDetail.referralSource", "referralSource", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("comments", "provideReferenceComment", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.eq("provideReferenceComment.action.id", PrismAction.APPLICATION_PROVIDE_REFERENCE)) //
                                .add(Restrictions.eq("provideReferenceComment.declinedResponse", false))) //
                .createAlias("comments", "declineReferenceComment", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.eq("declineReferenceComment.action.id", PrismAction.APPLICATION_PROVIDE_REFERENCE)) //
                                .add(Restrictions.eq("declineReferenceComment.declinedResponse", true))) //
                .createAlias("processings", "verificationProcessing", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.eq("verificationProcessing.stateGroup.id", PrismStateGroup.APPLICATION_VERIFICATION)) //
                .createAlias("processings", "referenceProcessing", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.eq("referenceProcessing.stateGroup.id", PrismStateGroup.APPLICATION_REFERENCE)) //
                .createAlias("processings", "reviewProcessing", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.eq("reviewProcessing.stateGroup.id", PrismStateGroup.APPLICATION_REVIEW)) //
                .createAlias("processings", "interviewProcessing", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.eq("interviewProcessing.stateGroup.id", PrismStateGroup.APPLICATION_INTERVIEW)) //
                .createAlias("processings", "approvalProcessing", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.eq("approvalProcessing.stateGroup.id", PrismStateGroup.APPLICATION_APPROVAL)) //
                .setResultTransformer(Transformers.aliasToBean(ApplicationReportListRowDTO.class)) //
                .list();
    }
}
