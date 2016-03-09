package uk.co.alumeni.prism.dao;

import static com.amazonaws.util.StringUtils.isNullOrEmpty;
import static com.google.common.collect.Lists.newLinkedList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.hibernate.sql.JoinType.INNER_JOIN;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getMatchMode;
import static uk.co.alumeni.prism.domain.definitions.PrismPerformanceIndicator.getColumns;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_REFERENCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_CONFIRMED_INTERVIEW_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW;
import static uk.co.alumeni.prism.utils.PrismEnumUtils.values;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
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

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.advert.AdvertTarget;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.application.ApplicationEmploymentPosition;
import uk.co.alumeni.prism.domain.application.ApplicationLocation;
import uk.co.alumeni.prism.domain.application.ApplicationQualification;
import uk.co.alumeni.prism.domain.application.ApplicationReferee;
import uk.co.alumeni.prism.domain.application.ApplicationTagSection;
import uk.co.alumeni.prism.domain.application.ApplicationTheme;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.PrismFilterEntity;
import uk.co.alumeni.prism.domain.definitions.PrismRejectionReason;
import uk.co.alumeni.prism.domain.definitions.PrismResourceListFilterExpression;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserRole;
import uk.co.alumeni.prism.dto.ApplicationAppointmentDTO;
import uk.co.alumeni.prism.dto.ApplicationProcessingSummaryDTO;
import uk.co.alumeni.prism.dto.ApplicationReportListRowDTO;
import uk.co.alumeni.prism.dto.ResourceRatingSummaryDTO;
import uk.co.alumeni.prism.utils.PrismTemplateUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;

@Repository
@SuppressWarnings("unchecked")
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

    public List<User> getApplicationRefereesNotResponded(Application application) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(ApplicationReferee.class) //
                .setProjection(Projections.property("user")) //
                .add(Restrictions.eq("association", application)) //
                .add(Restrictions.isNull("comment")) //
                .list();
    }

    public List<ApplicationReportListRowDTO> getApplicationReport(Collection<Integer> applicationIds, String columns) {
        return (List<ApplicationReportListRowDTO>) sessionFactory.getCurrentSession().createQuery( //
                "select " + columns + " " //
                        + "from Application as application " + "join application.user as user " //
                        + "left join user.userAccount as userAccount " //
                        + "left join userAccount.personalDetail as userPersonalDetail " //
                        + "left join application.personalDetail as applicationPersonalDetail " //
                        + "left join application.institution as institution " //
                        + "left join application.department as department " //
                        + "left join application.program as program " //
                        + "left join application.project as project " //
                        + "left join application.themes as primaryTheme " //
                        + "with primaryTheme.preference is true " //
                        + "left join primaryTheme.tag as primaryThemeTag "
                        + "left join application.themes as secondaryTheme " //
                        + "with secondaryTheme.preference is false " //
                        + "left join secondaryTheme.tag as secondaryThemeTag "
                        + "left join application.locations as primaryLocation " //
                        + "with primaryLocation.preference is true " //
                        + "left join primaryLocation.tag as primaryLocationTag " //
                        + "left join primaryLocationTag.institution as primaryLocationInstitution " //
                        + "left join primaryLocationTag.department as primaryLocationDepartment " //
                        + "left join application.locations as secondaryLocation " //
                        + "with secondaryLocation.preference is false " //
                        + "left join secondaryLocation.tag as secondaryLocationTag " //
                        + "left join secondaryLocationTag.institution as secondaryLocationInstitution " //
                        + "left join secondaryLocationTag.department as secondaryLocationDepartment " //
                        + "left join application.state as state " //
                        + "left join application.referees as referee " //
                        + "left join application.comments as provideReferenceComment " //
                        + "with provideReferenceComment.action.id = :provideReferenceAction " //
                        + "and provideReferenceComment.declinedResponse is false " //
                        + "left join application.comments as declineReferenceComment " //
                        + "with declineReferenceComment.action.id = :provideReferenceAction " //
                        + "and declineReferenceComment.declinedResponse is true " //
                        + "where application.id in :assignedApplications " //
                        + "group by application.id, secondaryTheme.id, secondaryLocation.id " //
                        + "order by application.sequenceIdentifier desc") //
                .setParameterList("assignedApplications", applicationIds) //
                .setParameter("provideReferenceAction", APPLICATION_PROVIDE_REFERENCE) //
                .setResultTransformer(Transformers.aliasToBean(ApplicationReportListRowDTO.class)) //
                .list();
    }

    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByYear(ResourceParent resource,
            HashMultimap<PrismFilterEntity, String> constraints) {
        return (List<ApplicationProcessingSummaryDTO>) getApplicationProcessingSummaryQuery(resource, constraints,
                "sql/application_processing_summary_year.ftl")
                .setResultTransformer(Transformers.aliasToBean(ApplicationProcessingSummaryDTO.class))
                .list();
    }

    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByMonth(ResourceParent resource,
            HashMultimap<PrismFilterEntity, String> constraints) {
        return (List<ApplicationProcessingSummaryDTO>) getApplicationProcessingSummaryQuery(resource, constraints,
                "sql/application_processing_summary_month.ftl")
                .addScalar("applicationMonth", IntegerType.INSTANCE) //
                .setResultTransformer(Transformers.aliasToBean(ApplicationProcessingSummaryDTO.class))
                .list();
    }

    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByWeek(ResourceParent resource,
            HashMultimap<PrismFilterEntity, String> constraints) {
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

    public List<Integer> getApplicationsByRejectionReason(ResourceParent parent, Collection<String> rejectionReasons) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.groupProperty("application.id")) //
                .createAlias("application", "application", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("application." + parent.getResourceScope().getLowerCamelName(), parent)) //
                .add(Restrictions.in("rejectionReason.id", rejectionReasons.stream().map(PrismRejectionReason::valueOf).collect(toList()))) //
                .list();
    }

    public List<Integer> getApplicationsByQualifyingResourceScope(ResourceParent parent, PrismScope resourceScope, Collection<String> resources) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ApplicationQualification.class) //
                .setProjection(Projections.groupProperty("application.id")) //
                .createAlias("application", "application", JoinType.INNER_JOIN) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("application." + parent.getResourceScope().getLowerCamelName(), parent)) //
                .add(Restrictions.in("advert." + resourceScope.getLowerCamelName() + ".id", resources.stream().map(Integer::parseInt).collect(toList()))) //
                .list();
    }

    public List<Integer> getApplicationsByEmployingResourceScope(ResourceParent parent, PrismScope resourceScope, Collection<String> resources) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ApplicationEmploymentPosition.class) //
                .setProjection(Projections.groupProperty("application.id")) //
                .createAlias("application", "application", JoinType.INNER_JOIN) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("application." + parent.getResourceScope().getLowerCamelName(), parent)) //
                .add(Restrictions.in("advert." + resourceScope.getLowerCamelName() + ".id", resources.stream().map(Integer::parseInt).collect(toList()))) //
                .list();
    }

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

    public List<Integer> getApplicationsForTargets() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .setProjection(Projections.groupProperty("application.id")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.applications", "application", JoinType.INNER_JOIN) //
                .list();
    }

    public List<Integer> getApplicationsForTargets(User user, PrismScope targeterScope, PrismScope targetScope, Collection<Integer> students) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .setProjection(Projections.groupProperty("application.id")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert." + targeterScope.getLowerCamelName(), "targeterResource", INNER_JOIN,
                        Restrictions.eqProperty("advert.id", "targeterResource.advert.id"))
                .createAlias("targeterResource.applications", "application", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert", "targetAdvert", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert." + targetScope.getLowerCamelName(), "targetResource") //
                .createAlias("targetResource.userRoles", "targetUserRole", JoinType.INNER_JOIN) //
                .add(Restrictions.in("application.user.id", students)) //
                .add(Restrictions.eq("targetUserRole.user", user)) //
                .add(Restrictions.in("targetUserRole.role.id", values(PrismRole.class, targetScope, new String[] { "ADMINISTRATOR", "APPROVER" }))) //
                .add(Restrictions.eq("application.shared", true)) //
                .list();
    }

    public <T extends ApplicationTagSection<U>, U extends UniqueEntity> void togglePrimaryApplicationTag(Class<T> applicationTagClass, Application application,
            U tag) {
        sessionFactory.getCurrentSession().createQuery( //
                "update " + applicationTagClass.getSimpleName() + " " //
                        + "set preference = 0 " //
                        + "where association = :application " //
                        + "and tag != :tag") //
                .setParameter("application", application) //
                .setParameter("tag", tag) //
                .executeUpdate();
    }

    public <T extends ApplicationTagSection<?>> void deleteApplicationTag(Class<T> applicationTagClass, Integer tagId) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete " + applicationTagClass.getSimpleName() + " " //
                        + "where id = :tagId") //
                .setParameter("tagId", tagId) //
                .executeUpdate();
    }

    public List<Integer> getApplicationsByTheme(String theme, PrismResourceListFilterExpression expression, Boolean preference) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ApplicationTheme.class) //
                .setProjection(Projections.groupProperty("association.id")) //
                .createAlias("tag", "theme") //
                .add(Restrictions.like("theme.name", theme, getMatchMode(expression))) //
                .add(Restrictions.eq("preference", preference)) //
                .list();
    }

    public List<Integer> getApplicationsByLocation(String location, PrismResourceListFilterExpression expression, Boolean preference) {
        MatchMode matchMode = getMatchMode(expression);
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ApplicationLocation.class) //
                .setProjection(Projections.groupProperty("association.id")) //
                .createAlias("tag", "locationAdvert", JoinType.INNER_JOIN) //
                .createAlias("locationAdvert.institution", "locationInstitution", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("locationAdvert.department", "locationDepartment", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("locationAdvert.program", "locationProgram", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("locationAdvert.project", "locationProject", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.like("locationInstitution.name", location, matchMode)) //
                        .add(Restrictions.like("locationDepartment.name", location, matchMode)) //
                        .add(Restrictions.like("locationProgram.name", location, matchMode)) //
                        .add(Restrictions.like("locationProject.name", location, matchMode))) //
                .add(Restrictions.eq("preference", preference)) //
                .list();
    }

    public void updateApplicationOpportunityCategories(Advert advert) {
        sessionFactory.getCurrentSession().createQuery( //
                "update Application " //
                        + "set opportunityCategories = :opportunityCategories " //
                        + "where advert = :advert") //
                .setParameter("opportunityCategories", advert.getOpportunityCategories()) //
                .setParameter("advert", advert) //
                .executeUpdate();
    }

    public List<Integer> getApplicationsByApplicationTheme(List<Integer> themes, List<Integer> secondaryThemes) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("themes", "theme", JoinType.INNER_JOIN) //
                .add(getApplicationTagCriterion("theme", themes, secondaryThemes)) //
                .list();
    }

    public List<Integer> getApplicationsByApplicationLocation(List<Integer> locations, List<Integer> secondaryLocations) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("locations", "location", JoinType.INNER_JOIN) //
                .add(getApplicationTagCriterion("location", locations, secondaryLocations)) //
                .list();
    }

    public List<Integer> getApplicationsWithReferencesPending(Resource parentResource) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ApplicationReferee.class) //
                .setProjection(Projections.groupProperty("application.id")) //
                .createAlias("association", "application", JoinType.INNER_JOIN) //
                .add(Restrictions.isNull("comment")) //
                .add(Restrictions.eq("application." + parentResource.getResourceScope().getLowerCamelName(), parentResource)) //
                .list();
    }

    public List<Integer> getApplicationsWithReferencesProvided(Resource parentResource) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ApplicationReferee.class) //
                .setProjection(Projections.groupProperty("application.id")) //
                .createAlias("association", "application", JoinType.INNER_JOIN) //
                .add(Restrictions.isNotNull("comment")) //
                .add(Restrictions.eq("application." + parentResource.getResourceScope().getLowerCamelName(), parentResource)) //
                .list();
    }

    public void deleteApplicationHiringManagers(Application application) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete ApplicationHiringManager " //
                        + "where application = :application") //
                .setParameter("application", application) //
                .executeUpdate();
    }

    private Junction getApplicationTagCriterion(String tagAlias, List<Integer> primaryIds, List<Integer> secondaryIds) {
        Junction constraint = Restrictions.conjunction() //
                .add(Restrictions.in(tagAlias + ".tag.id", primaryIds)) //
                .add(Restrictions.eq(tagAlias + ".preference", true));
        if (isNotEmpty(secondaryIds)) {
            constraint = Restrictions.disjunction() //
                    .add(constraint) //
                    .add(Restrictions.conjunction() //
                            .add(Restrictions.in(tagAlias + ".tag.id", secondaryIds)) //
                            .add(Restrictions.ne(tagAlias + ".preference", true)));
        }
        return constraint;
    }

    private SQLQuery getApplicationProcessingSummaryQuery(ResourceParent resource, HashMultimap<PrismFilterEntity, String> constraints, String templateLocation) {
        String columnExpression = Joiner.on(",\n\t").join(getColumns());

        List<String> filterConstraintExpressions = newLinkedList();
        constraints.keySet().forEach(
                fe -> {
                    Set<String> constraintValues = constraints.get(fe);
                    if (CollectionUtils.isNotEmpty(constraintValues)) {
                        filterConstraintExpressions.add(fe.getFilterColumn() + " in ("
                                + constraintValues.stream().map(cv -> "'" + cv + "'").collect(joining(", ")) + ")");
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
