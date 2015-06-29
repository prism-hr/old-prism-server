package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.PrismPerformanceIndicator.getColumns;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_COMPLETED_PURGED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REJECTED_COMPLETED_PURGED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED_PURGED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup.APPLICATION_RESERVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup.APPLICATION_VALIDATION;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.amazonaws.util.StringUtils;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.application.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.dto.ApplicationProcessingSummaryDTO;
import com.zuehlke.pgadmissions.dto.ApplicationRatingSummaryDTO;
import com.zuehlke.pgadmissions.dto.ApplicationReferenceDTO;
import com.zuehlke.pgadmissions.dto.ApplicationReportListRowDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceReportFilterDTO.ResourceReportFilterPropertyDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationSummaryRepresentation.OtherApplicationSummaryRepresentation;

import freemarker.template.Template;

@Repository
@SuppressWarnings("unchecked")
public class ApplicationDAO {

    @Inject
    private SessionFactory sessionFactory;

    @Inject
    private FreeMarkerConfig freemarkerConfig;

    public Application getPreviousSubmittedApplication(Application application) {
        return (Application) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .add(Restrictions.eq("user", application.getUser())) //
                .add(Restrictions.eq("institution", application.getInstitution())) //
                .add(Restrictions.isNotNull("submittedTimestamp")) //
                .add(Restrictions.not( //
                        Restrictions.in("state.id", Arrays.asList( //
                                APPLICATION_APPROVED_COMPLETED_PURGED, //
                                APPLICATION_REJECTED_COMPLETED_PURGED, //
                                APPLICATION_WITHDRAWN_COMPLETED_PURGED)))) //
                .addOrder(Order.desc("submittedTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public String getApplicationExportReference(Application application) {
        return (String) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.property("applicationExport.exportReference")) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.isNotNull("applicationExport.exportReference")) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
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

    public List<ApplicationReferenceDTO> getApplicationRefereesResponded(Application application) {
        return (List<ApplicationReferenceDTO>) sessionFactory.getCurrentSession().createCriteria(ApplicationReferee.class, "applicationReferee") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("user"), "user") //
                        .add(Projections.property("jobTitle"), "jobTitle") //
                        .add(Projections.property("address.addressLine1"), "addressLine1") //
                        .add(Projections.property("address.addressLine2"), "addressLine2") //
                        .add(Projections.property("address.addressTown"), "addressTown") //
                        .add(Projections.property("address.addressRegion"), "addressRegion") //
                        .add(Projections.property("address.addressCode"), "addressCode") //
                        .add(Projections.property("domicile.code"), "addressDomicile") //
                        .add(Projections.property("comment"), "comment")) //
                .createAlias("address", "address", JoinType.INNER_JOIN) //
                .createAlias("address.domicile", "domicile", JoinType.INNER_JOIN) //
                .createAlias("comment", "comment", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("application", application)) //
                .addOrder(Order.desc("comment.applicationRating")) //
                .addOrder(Order.asc("comment.createdTimestamp")) //
                .addOrder(Order.asc("comment.id")) //
                .setResultTransformer(Transformers.aliasToBean(ApplicationReferenceDTO.class)) //
                .list();
    }

    public List<ApplicationQualification> getApplicationExportQualifications(Application application) {
        return (List<ApplicationQualification>) sessionFactory.getCurrentSession().createCriteria(ApplicationQualification.class) //
                .add(Restrictions.eq("application", application)) //
                .addOrder(Order.desc("awardDate")) //
                .addOrder(Order.desc("startDate")) //
                .list();
    }

    public ApplicationReferee getApplicationReferee(Application application, User user) {
        return (ApplicationReferee) sessionFactory.getCurrentSession().createCriteria(ApplicationReferee.class) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.eq("user", user)) //
                .uniqueResult();
    }

    public List<User> getUnassignedApplicationReferees(Application application) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(ApplicationReferee.class) //
                .setProjection(Projections.property("user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userRoles", "userRole", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.eq("userRole.role.id", APPLICATION_REFEREE))
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.isNull("comment")) //
                .add(Restrictions.isNull("userRole.id")) //
                .list();
    }

    public List<Integer> getApplicationsForExport() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("application.id")) //
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
                        .add(Projections.property("code"), "code") //
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
                        .add(Restrictions.between("stateGroup.ordinal", APPLICATION_VALIDATION.ordinal(), APPLICATION_RESERVED.ordinal())) //
                        .add(Restrictions.in("state.id", Arrays.asList(APPLICATION_APPROVAL, APPLICATION_REJECTED)))) //
                .addOrder(Order.desc("sequenceIdentifier")) //
                .setResultTransformer(Transformers.aliasToBean(OtherApplicationSummaryRepresentation.class)) //
                .list();
    }

    public List<PrismWorkflowPropertyDefinition> getApplicationWorkflowPropertyDefinitions(Set<Integer> applicationIds) {
        return (List<PrismWorkflowPropertyDefinition>) sessionFactory.getCurrentSession().createCriteria(WorkflowPropertyConfiguration.class) //
                .setProjection(Projections.groupProperty("workflowPropertyDefinition.id")) //
                .add(Subqueries.propertyIn("version", //
                        DetachedCriteria.forClass(Application.class) //
                                .setProjection(Projections.groupProperty("workflowPropertyConfigurationVersion")) //
                                .add(Restrictions.in("id", applicationIds)))) //
                .add(Restrictions.eq("enabled", true)) //
                .list();
    }

    public List<ApplicationReportListRowDTO> getApplicationReport(Set<Integer> assignedApplications, String columns) {
        return (List<ApplicationReportListRowDTO>) sessionFactory.getCurrentSession().createQuery( //
                "select " + columns + " " //
                        + "from Application as application " + "join application.user as user " //
                        + "left join application.personalDetail as personalDetail " //
                        + "left join personalDetail.firstNationality as nationality " //
                        + "left join personalDetail.domicile as domicile " //
                        + "left join personalDetail.country as country " //
                        + "left join personalDetail.gender as gender " //
                        + "join application.institution as institution " //
                        + "join application.program as program " //
                        + "left join program.department as department " //
                        + "left join application.project as project " //
                        + "left join application.programDetail as programDetail " //
                        + "left join programDetail.studyOption as studyOption " //
                        + "left join programDetail.referralSource as referralSource " //
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
                .setParameterList("assignedApplications", assignedApplications) //
                .setParameter("provideReferenceAction", PrismAction.APPLICATION_PROVIDE_REFERENCE) //
                .setResultTransformer(Transformers.aliasToBean(ApplicationReportListRowDTO.class)) //
                .list();
    }

    public <T extends Resource> List<Application> getUserAdministratorApplications(HashMultimap<PrismScope, T> userAdministratorResources) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("application")) //
                .createAlias("application", "application", JoinType.INNER_JOIN) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .add(Restrictions.isNull("role.scopeCreator"));

        Disjunction disjunction = Restrictions.disjunction();
        for (PrismScope scope : userAdministratorResources.keySet()) {
            disjunction.add(Restrictions.in("application." + scope.getLowerCamelName(), userAdministratorResources.get(scope)));
        }

        return (List<Application>) criteria.add(disjunction) //
                .list();
    }

    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByYear(ResourceParent resource,
            List<ResourceReportFilterPropertyDTO> constraints) {
        return (List<ApplicationProcessingSummaryDTO>) getApplicationProcessingSummaryQuery(resource, constraints,
                "sql/application_processing_summary_year.ftl")
                .setResultTransformer(Transformers.aliasToBean(ApplicationProcessingSummaryDTO.class))
                .list();
    }

    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByMonth(ResourceParent resource,
            List<ResourceReportFilterPropertyDTO> constraints) {
        return (List<ApplicationProcessingSummaryDTO>) getApplicationProcessingSummaryQuery(resource, constraints,
                "sql/application_processing_summary_month.ftl")
                .addScalar("applicationMonth", IntegerType.INSTANCE) //
                .setResultTransformer(Transformers.aliasToBean(ApplicationProcessingSummaryDTO.class))
                .list();
    }

    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByWeek(ResourceParent resource,
            List<ResourceReportFilterPropertyDTO> constraints) {
        return (List<ApplicationProcessingSummaryDTO>) getApplicationProcessingSummaryQuery(resource, constraints,
                "sql/application_processing_summary_week.ftl")
                .addScalar("applicationMonth", IntegerType.INSTANCE) //
                .addScalar("applicationWeek", IntegerType.INSTANCE) //
                .setResultTransformer(Transformers.aliasToBean(ApplicationProcessingSummaryDTO.class))
                .list();
    }

    public ApplicationRatingSummaryDTO getApplicationRatingSummary(Application application) {
        return (ApplicationRatingSummaryDTO) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("application"), "parent") //
                        .add(Projections.countDistinct("id"), "applicationRatingCount") //
                        .add(Projections.avg("applicationRating"), "applicationRatingAverage")) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.isNotNull("applicationRating")) //
                .setResultTransformer(Transformers.aliasToBean(ApplicationRatingSummaryDTO.class)) //
                .uniqueResult();
    }

    public ApplicationRatingSummaryDTO getApplicationRatingSummary(ResourceParent resource) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (ApplicationRatingSummaryDTO) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty(resourceReference), "parent") //
                        .add(Projections.sum("applicationRatingCount"), "applicationRatingCount") //
                        .add(Projections.countDistinct("id"), "applicationRatingApplications") //
                        .add(Projections.avg("applicationRatingAverage"), "applicationRatingAverage")) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(Restrictions.isNotNull("applicationRatingCount")) //
                .setResultTransformer(Transformers.aliasToBean(ApplicationRatingSummaryDTO.class)) //
                .uniqueResult();
    }

    public List<Integer> getApplicationsByMatchingSuggestedSupervisor(String searchTerm) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ApplicationSupervisor.class) //
                .setProjection(Projections.property("application.id")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.ilike("user.fullName", searchTerm, MatchMode.ANYWHERE)) //
                        .add(Restrictions.ilike("user.email", searchTerm, MatchMode.ANYWHERE))) //
                .list();
    }

    private SQLQuery getApplicationProcessingSummaryQuery(ResourceParent resource, List<ResourceReportFilterPropertyDTO> constraints, String templateLocation) {
        String columnExpression = Joiner.on(",\n\t").join(getColumns());

        List<String> filterConstraintExpressions = Lists.newLinkedList();
        if (constraints != null) {
            HashMultimap<PrismImportedEntity, Integer> flattenedConstraints = HashMultimap.create();
            for (ResourceReportFilterPropertyDTO constraint : constraints) {
                flattenedConstraints.put(constraint.getEntityType(), constraint.getEntityId());
            }

            for (PrismImportedEntity entity : flattenedConstraints.keySet()) {
                String columnConstraintExpression = "(";
                List<String> columnConstraint = Lists.newArrayList();
                for (String column : entity.getReportDefinition()) {
                    columnConstraint.add(column + " in (" + Joiner.on(", ").join(flattenedConstraints.get(entity)) + ")");
                }
                filterConstraintExpressions.add(columnConstraintExpression + Joiner.on("\n\t\tand ").join(columnConstraint) + ")");
            }
        }

        String constraintExpression = "where application." + resource.getResourceScope().getLowerCamelName() + "_id = " + resource.getId();
        String filterConstraintExpression = Joiner.on("\n\tand ").join(filterConstraintExpressions);
        if (!StringUtils.isNullOrEmpty(filterConstraintExpression)) {
            constraintExpression = constraintExpression + "\n\tand " + filterConstraintExpression;
        }

        ImmutableMap<String, Object> model = ImmutableMap.of("columnExpression", (Object) columnExpression, "constraintExpression", constraintExpression);

        String queryString;
        try {
            String statement = Resources.toString(Resources.getResource(templateLocation), Charsets.UTF_8);
            Template template = new Template("statement", statement, freemarkerConfig.getConfiguration());
            queryString = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (Exception e) {
            throw new Error(e);
        }
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(queryString);

        return query.addScalar("advertCount", LongType.INSTANCE) //
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
