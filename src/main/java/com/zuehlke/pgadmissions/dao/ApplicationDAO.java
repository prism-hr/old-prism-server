package com.zuehlke.pgadmissions.dao;

import static com.amazonaws.util.StringUtils.isNullOrEmpty;
import static com.zuehlke.pgadmissions.domain.definitions.PrismPerformanceIndicator.getColumns;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_CONFIRMED_INTERVIEW_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismFilterEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismRejectionReason;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.dto.ApplicationAppointmentDTO;
import com.zuehlke.pgadmissions.dto.ApplicationProcessingSummaryDTO;
import com.zuehlke.pgadmissions.dto.ApplicationReportListRowDTO;
import com.zuehlke.pgadmissions.dto.ResourceRatingSummaryDTO;
import com.zuehlke.pgadmissions.utils.PrismTemplateUtils;

@Repository
public class ApplicationDAO {

    @Inject
    private SessionFactory sessionFactory;

    @Inject
    private PrismTemplateUtils prismTemplateUtils;

    public ApplicationReferee getApplicationReferee(Application application, User user) {
        return (ApplicationReferee) sessionFactory.getCurrentSession().createCriteria(ApplicationReferee.class) //
                .add(Restrictions.eq("association", application)) //
                .add(Restrictions.eq("user", user)) //
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<User> getApplicationRefereesNotResponded(Application application) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(ApplicationReferee.class) //
                .setProjection(Projections.property("user")) //
                .add(Restrictions.eq("association", application)) //
                .add(Restrictions.isNull("comment")) //
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<ApplicationReportListRowDTO> getApplicationReport(Collection<Integer> applicationIds, String columns) {
        return (List<ApplicationReportListRowDTO>) sessionFactory.getCurrentSession().createQuery( //
                "select " + columns + " " //
                        + "from Application as application " + "join application.user as user " //
                        + "left join application.personalDetail as personalDetail " //
                        + "join application.institution as institution " //
                        + "join application.program as program " //
                        + "left join program.department as department " //
                        + "left join application.project as project " //
                        + "join application.state as state " //
                        + "left join application.referees as referee " //
                        + "left join application.comments as provideReferenceComment " //
                        + "with provideReferenceComment.action.id = :provideReferenceAction " //
                        + "and provideReferenceComment.declinedResponse is false " //
                        + "left join application.comments as declineReferenceComment " //
                        + "with declineReferenceComment.action.id = :provideReferenceAction " //
                        + "and declineReferenceComment.declinedResponse is true " //
                        + "where application.id in :assignedApplications " //
                        + "group by application.id " //
                        + "order by application.sequenceIdentifier desc") //
                .setParameterList("assignedApplications", applicationIds) //
                .setParameter("provideReferenceAction", PrismAction.APPLICATION_PROVIDE_REFERENCE) //
                .setResultTransformer(Transformers.aliasToBean(ApplicationReportListRowDTO.class)) //
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByYear(ResourceParent resource, HashMultimap<PrismFilterEntity, String> constraints) {
        return (List<ApplicationProcessingSummaryDTO>) getApplicationProcessingSummaryQuery(resource, constraints,
                "sql/application_processing_summary_year.ftl")
                        .setResultTransformer(Transformers.aliasToBean(ApplicationProcessingSummaryDTO.class))
                        .list();
    }

    @SuppressWarnings("unchecked")
    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByMonth(ResourceParent resource, HashMultimap<PrismFilterEntity, String> constraints) {
        return (List<ApplicationProcessingSummaryDTO>) getApplicationProcessingSummaryQuery(resource, constraints,
                "sql/application_processing_summary_month.ftl")
                        .addScalar("applicationMonth", IntegerType.INSTANCE) //
                        .setResultTransformer(Transformers.aliasToBean(ApplicationProcessingSummaryDTO.class))
                        .list();
    }

    @SuppressWarnings("unchecked")
    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByWeek(ResourceParent resource, HashMultimap<PrismFilterEntity, String> constraints) {
        return (List<ApplicationProcessingSummaryDTO>) getApplicationProcessingSummaryQuery(resource, constraints,
                "sql/application_processing_summary_week.ftl")
                        .addScalar("applicationMonth", IntegerType.INSTANCE) //
                        .addScalar("applicationWeek", IntegerType.INSTANCE) //
                        .setResultTransformer(Transformers.aliasToBean(ApplicationProcessingSummaryDTO.class))
                        .list();
    }

    public <T extends Application> ResourceRatingSummaryDTO getApplicationRatingSummary(T application) {
        return (ResourceRatingSummaryDTO) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("application"), "resource") //
                        .add(Projections.countDistinct("id"), "ratingCount") //
                        .add(Projections.avg("rating"), "ratingAverage")) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.isNotNull("rating")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceRatingSummaryDTO.class)) //
                .uniqueResult();
    }

    public ResourceRatingSummaryDTO getApplicationRatingSummary(ResourceParent resource) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (ResourceRatingSummaryDTO) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty(resourceReference), "resource") //
                        .add(Projections.sum("applicationRatingCount"), "ratingCount") //
                        .add(Projections.countDistinct("id"), "ratingResources") //
                        .add(Projections.avg("applicationRatingAverage"), "ratingAverage")) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(Restrictions.isNotNull("applicationRatingCount")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceRatingSummaryDTO.class)) //
                .uniqueResult();
    }

    public Boolean getApplicationOnCourse(Application application) {
        return (Boolean) sessionFactory.getCurrentSession().createCriteria(Comment.class)
                .setProjection(Projections.property("onCourse")) //
                .add(Restrictions.eq("application", application)) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getApplicationsByRejectionReason(ResourceParent parent, Collection<String> rejectionReasons) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.groupProperty("application.id")) //
                .createAlias("application", "application", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("application." + parent.getResourceScope().getLowerCamelName(), parent)) //
                .add(Restrictions.in("rejectionReason.id", rejectionReasons.stream().map(PrismRejectionReason::valueOf).collect(toList()))) //
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getApplicationsByQualifyingResourceScope(ResourceParent parent, PrismScope resourceScope, Collection<String> resources) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ApplicationQualification.class) //
                .setProjection(Projections.groupProperty("application.id")) //
                .createAlias("application", "application", JoinType.INNER_JOIN) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("application." + parent.getResourceScope().getLowerCamelName(), parent)) //
                .add(Restrictions.in("advert." + resourceScope.getLowerCamelName() + ".id", resources.stream().map(Integer::parseInt).collect(toList()))) //
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getApplicationsByEmployingResourceScope(ResourceParent parent, PrismScope resourceScope, Collection<String> resources) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ApplicationEmploymentPosition.class) //
                .setProjection(Projections.groupProperty("application.id")) //
                .createAlias("application", "application", JoinType.INNER_JOIN) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("application." + parent.getResourceScope().getLowerCamelName(), parent)) //
                .add(Restrictions.in("advert." + resourceScope.getLowerCamelName() + ".id", resources.stream().map(Integer::parseInt).collect(toList()))) //
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getUserWithAppointmentsForApplications() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user.id")) //
                .createAlias("application", "application", JoinType.INNER_JOIN) //
                .createAlias("application.institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("application.department", "department", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("application.program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("application.project", "project", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("application.resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("application.comments", "comment", JoinType.INNER_JOIN) //
                .add(Restrictions.in("role.id", APPLICATION_CONFIRMED_INTERVIEW_GROUP.getRoles())) //
                .add(Restrictions.eq("resourceState.state.id", APPLICATION_INTERVIEW_PENDING_INTERVIEW)) //
                .add(Restrictions.isNotNull("comment.interviewAppointment.interviewDateTime")) //
                .addOrder(Order.asc("comment.interviewAppointment.interviewDateTime")) //
                .addOrder(Order.asc("application.id")) //
                .setResultTransformer(Transformers.aliasToBean(ApplicationAppointmentDTO.class)) //
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<ApplicationAppointmentDTO> getApplicationAppointments(User user) {
        return (List<ApplicationAppointmentDTO>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("institution.id").as("institutionId")) //
                        .add(Projections.property("institution.name").as("institutionName")) //
                        .add(Projections.property("institution.logoImage.id").as("logoImageId")) //
                        .add(Projections.property("department.id").as("departmentId")) //
                        .add(Projections.property("department.name").as("departmentName")) //
                        .add(Projections.property("program.id").as("programId")) //
                        .add(Projections.property("program.name").as("programName")) //
                        .add(Projections.property("project.id").as("projectId")) //
                        .add(Projections.property("project.name").as("projectName")) //
                        .add(Projections.groupProperty("application.id").as("applicationId")) //
                        .add(Projections.property("application.code").as("applicationCode")) //
                        .add(Projections.groupProperty("comment.interviewAppointment.interviewDateTime").as("interviewDateTime")) //
                        .add(Projections.property("comment.interviewAppointment.interviewTimeZone").as("interviewTimeZone")) //
                        .add(Projections.property("comment.interviewAppointment.interviewDuration").as("interviewDuration")) //
                        .add(Projections.property("comment.interviewInstruction.interviewLocation").as("interviewLocation")))
                .createAlias("application", "application", JoinType.INNER_JOIN) //
                .createAlias("application.institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("application.department", "department", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("application.program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("application.project", "project", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("application.resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("application.comments", "comment", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.in("role.id", APPLICATION_CONFIRMED_INTERVIEW_GROUP.getRoles())) //
                .add(Restrictions.eq("resourceState.state.id", APPLICATION_INTERVIEW_PENDING_INTERVIEW)) //
                .add(Restrictions.isNotNull("comment.interviewAppointment.interviewDateTime")) //
                .addOrder(Order.asc("comment.interviewAppointment.interviewDateTime")) //
                .addOrder(Order.asc("application.id")) //
                .setResultTransformer(Transformers.aliasToBean(ApplicationAppointmentDTO.class)) //
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getSharedApplicationsForAdverts(List<Integer> adverts) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("department", "department", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("institution", "institution", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.in("department.advert.id", adverts))
                        .add(Restrictions.in("institution.advert.id", adverts))) //
                .add(Restrictions.eq("shared", true)) //
                .list();
    }

    private SQLQuery getApplicationProcessingSummaryQuery(ResourceParent resource, HashMultimap<PrismFilterEntity, String> constraints, String templateLocation) {
        String columnExpression = Joiner.on(",\n\t").join(getColumns());

        List<String> filterConstraintExpressions = Lists.newLinkedList();
        constraints.keySet().forEach(fe -> {
            Set<String> constraintValues = constraints.get(fe);
            if (CollectionUtils.isNotEmpty(constraintValues)) {
                filterConstraintExpressions.add(fe.getFilterColumn() + " in (" + constraintValues.stream().map(cv -> "'" + cv + "'").collect(joining(", ")) + ")");
            }
        });

        String constraintExpression = "where application." + resource.getResourceScope().getLowerCamelName() + "_id = '" + resource.getId() + "'";
        String filterConstraintExpression = Joiner.on("\n\tand ").join(filterConstraintExpressions);
        if (!isNullOrEmpty(filterConstraintExpression)) {
            constraintExpression = constraintExpression + "\n\tand " + filterConstraintExpression;
        }

        ImmutableMap<String, Object> model = ImmutableMap.of("columnExpression", (Object) columnExpression, "constraintExpression", constraintExpression);
        return sessionFactory.getCurrentSession().createSQLQuery(prismTemplateUtils.getContentFromLocation("statement", templateLocation, model))
                .addScalar("advertCount", LongType.INSTANCE) //
                .addScalar("applicationYear", StringType.INSTANCE) //
                .addScalar("submittedApplicationCount", LongType.INSTANCE) //
                .addScalar("approvedApplicationCount", LongType.INSTANCE) //
                .addScalar("rejectedApplicationCount", LongType.INSTANCE) //
                .addScalar("withdrawnApplicationCount", LongType.INSTANCE) //
                .addScalar("submittedApplicationRatio", DoubleType.INSTANCE) //
                .addScalar("approvedApplicationRatio", DoubleType.INSTANCE) //
                .addScalar("rejectedApplicationRatio", DoubleType.INSTANCE) //
                .addScalar("withdrawnApplicationRatio", DoubleType.INSTANCE) //
                .addScalar("averageRating", DoubleType.INSTANCE) //
                .addScalar("averageProcessingTime", DoubleType.INSTANCE);
    }

}
