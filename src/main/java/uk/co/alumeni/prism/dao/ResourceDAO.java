package uk.co.alumeni.prism.dao;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.hibernate.transform.Transformers.aliasToBean;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getLikeConstraint;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getMatchMode;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getMatchingUserConstraint;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getReadOrUnreadMessageConstraint;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getResourceParentConnectableConstraint;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getResourceParentManageableStateConstraint;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getUnreadMessageConstraint;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getVisibleMessageConstraint;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getVisibleResourceConstraint;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PROVIDED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROGRAM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROJECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory.ORGANIZATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.valueOf;
import static uk.co.alumeni.prism.utils.PrismEnumUtils.values;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import uk.co.alumeni.prism.domain.advert.AdvertTarget;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.PrismResourceListFilterExpression;
import uk.co.alumeni.prism.domain.definitions.PrismStudyOption;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateGroup;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceCondition;
import uk.co.alumeni.prism.domain.resource.ResourceOpportunity;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.resource.ResourceState;
import uk.co.alumeni.prism.domain.resource.ResourceStudyOption;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserRole;
import uk.co.alumeni.prism.domain.workflow.State;
import uk.co.alumeni.prism.dto.ActivityMessageCountDTO;
import uk.co.alumeni.prism.dto.EntityLocationDTO;
import uk.co.alumeni.prism.dto.ResourceConnectionDTO;
import uk.co.alumeni.prism.dto.ResourceFlatToNestedDTO;
import uk.co.alumeni.prism.dto.ResourceIdentityDTO;
import uk.co.alumeni.prism.dto.ResourceListRowDTO;
import uk.co.alumeni.prism.dto.ResourceRatingSummaryDTO;
import uk.co.alumeni.prism.dto.ResourceRoleDTO;
import uk.co.alumeni.prism.dto.ResourceSimpleDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceListFilterDTO;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationIdentity;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRobotMetadata;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSitemap;
import uk.co.alumeni.prism.utils.PrismEnumUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;

@Repository
@SuppressWarnings("unchecked")
public class ResourceDAO {

    @Inject
    private WorkflowDAO workflowDAO;

    @Inject
    private SessionFactory sessionFactory;

    public List<Integer> getResourcesToEscalate(PrismScope resourceScope, PrismAction actionId, LocalDate baseline) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.action.id", actionId)) //
                .add(Restrictions.lt("dueDate", baseline)) //
                .list();
    }

    public List<Integer> getResourcesToPropagate(PrismScope propagatingScope, Integer propagatingId, PrismScope propagatedScope, PrismAction actionId) {
        String propagatedAlias = propagatedScope.getLowerCamelName();
        String propagatedReference = propagatingScope.ordinal() > propagatedScope.ordinal() ? propagatedAlias : propagatedAlias + "s";

        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(propagatingScope.getResourceClass()) //
                .setProjection(Projections.property(propagatedAlias + ".id")) //
                .createAlias(propagatedReference, propagatedAlias, JoinType.INNER_JOIN) //
                .createAlias(propagatedAlias + ".state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", propagatingId)) //
                .add(Restrictions.eq("stateAction.action.id", actionId)) //
                .list();
    }

    public void deleteResourceState(Resource resource, State state) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete ResourceState " //
                        + "where " + resource.getResourceScope().getLowerCamelName() + " = :resource " //
                        + "and state = :state") //
                .setParameter("resource", resource) //
                .setParameter("state", state) //
                .executeUpdate();
    }

    public void deleteSecondaryResourceState(Resource resource, State state) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete ResourceState " //
                        + "where " + resource.getResourceScope().getLowerCamelName() + " = :resource " //
                        + "and state = :state " + "and primaryState is false") //
                .setParameter("resource", resource) //
                .setParameter("state", state) //
                .executeUpdate();
    }

    public List<ResourceListRowDTO> getResourceList(User user, PrismScope scope, List<PrismScope> parentScopes, Collection<Integer> resourceIds,
            ResourceListFilterDTO filter,
            boolean hasRedactions) {
        if (isNotEmpty(resourceIds)) {
            String scopeName = scope.getLowerCamelName();
            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(scope.getResourceClass(), scopeName);

            ProjectionList projectionList = Projections.projectionList();

            List<String> parentScopeNames = Lists.newLinkedList();
            for (PrismScope parentScopeId : parentScopes) {
                String parentScopeName = parentScopeId.getLowerCamelName();
                projectionList.add(Projections.property(parentScopeName + ".id"), parentScopeName + "Id");

                if (!parentScopeId.equals(SYSTEM)) {
                    projectionList.add(Projections.property(parentScopeName + ".name"), parentScopeName + "Name");
                }

                if (scope.equals(INSTITUTION)) {
                    projectionList.add(Projections.property(scopeName + ".logoImage.id"), "logoImageId");
                }

                parentScopeNames.add(parentScopeName);
            }

            boolean parentScope = !scope.equals(APPLICATION);
            projectionList.add(Projections.groupProperty("id"), scopeName + "Id");
            if (parentScope) {
                projectionList.add(Projections.property("name"), scopeName + "Name");
            }

            projectionList.add(Projections.property("advert.applyHomepage"), "applyHomepage") //
                    .add(Projections.property("code"), "code") //
                    .add(Projections.property("user.id"), "userId") //
                    .add(Projections.property("user.firstName"), "userFirstName") //
                    .add(Projections.property("user.firstName2"), "userFirstName2") //
                    .add(Projections.property("user.firstName3"), "userFirstName3") //
                    .add(Projections.property("user.lastName"), "userLastName") //
                    .add(Projections.property("user.email"), "userEmail") //
                    .add(Projections.property("userAccount.linkedinImageUrl"), "userAccountImageUrl");

            if (!hasRedactions) {
                projectionList.add(Projections.property("applicationRatingAverage"), "applicationRatingAverage");
            }

            projectionList.add(Projections.property("state.id"), "stateId") //
                    .add(Projections.property("createdTimestamp"), "createdTimestamp") //
                    .add(Projections.property("updatedTimestamp"), "updatedTimestamp") //
                    .add(Projections.property("sequenceIdentifier"), "sequenceIdentifier"); //

            if (parentScope) {
                projectionList.add(Projections.property("advertIncompleteSection"), "advertIncompleteSection");
            }

            criteria.setProjection(projectionList //
                    .add(Projections.countDistinct("stateActionPending.id"), "stateActionPendingCount"));
            for (String parentScopeName : parentScopeNames) {
                criteria.createAlias(parentScopeName, parentScopeName, JoinType.LEFT_OUTER_JOIN);
            }

            return (List<ResourceListRowDTO>) criteria.setProjection(projectionList) //
                    .createAlias("user", "user", JoinType.INNER_JOIN) //
                    .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                    .createAlias("state", "state", JoinType.INNER_JOIN) //
                    .createAlias("stateActionPendings", "stateActionPending", JoinType.LEFT_OUTER_JOIN,
                            Restrictions.isNotNull("stateActionPending.templateComment")) //
                    .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN)
                    .add(Restrictions.in("id", resourceIds)) //
                    .setResultTransformer(aliasToBean(ResourceListRowDTO.class)) //
                    .list();
        }

        return Collections.emptyList();
    }

    public <T> List<T> getResources(User user, PrismScope scope, ResourceListFilterDTO filter, ProjectionList columns, Junction conditions,
            Class<T> responseClass, DateTime updateBaseline) {
        Criteria criteria = workflowDAO.getWorkflowCriteriaList(scope, columns) //
                .add(Restrictions.eq("userRole.user", user));
        appendResourceListFilterCriteria(criteria, conditions, filter, updateBaseline);
        return (List<T>) criteria //
                .setResultTransformer(aliasToBean(responseClass)) //
                .list();
    }

    public <T> List<T> getResources(User user, PrismScope scope, PrismScope parentScope, ResourceListFilterDTO filter, ProjectionList columns,
            Junction conditions, Class<T> responseClass, DateTime updateBaseline) {
        Criteria criteria = workflowDAO.getWorkflowCriteriaList(scope, parentScope, columns) //
                .add(Restrictions.eq("userRole.user", user));
        appendResourceListFilterCriteria(criteria, conditions, filter, updateBaseline);
        return (List<T>) criteria //
                .setResultTransformer(aliasToBean(responseClass)) //
                .list();
    }

    public <T> List<T> getResources(User user, PrismScope scope, PrismScope targeterScope, PrismScope targetScope, List<Integer> targeterEntities,
            ResourceListFilterDTO filter, ProjectionList columns, Junction conditions, Class<T> responseClass, DateTime updateBaseline) {
        Criteria criteria = workflowDAO.getWorkflowCriteriaList(scope, targeterScope, targetScope, targeterEntities, columns)
                .add(Restrictions.eq("userRole.user", user));
        appendResourceListFilterCriteria(criteria, conditions, filter, updateBaseline);
        return (List<T>) criteria //
                .setResultTransformer(aliasToBean(responseClass)) //
                .list();
    }

    public List<Integer> getSimilarResources(PrismScope scope, String searchTerm) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(scope.getResourceClass()) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.like("code", searchTerm, MatchMode.ANYWHERE)) //
                        .add(Restrictions.like("name", searchTerm, MatchMode.ANYWHERE))) //
                .list();
    }

    public List<Resource> getResourcesByUser(PrismScope scope, User user) {
        return (List<Resource>) sessionFactory.getCurrentSession().createCriteria(scope.getResourceClass()) //
                .add(Restrictions.eq("user", user)) //
                .list();
    }

    public List<Integer> getResourcesByUserAndRole(PrismScope scope, String searchTerm, List<PrismRole> prismRoles) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.property(scope.getLowerCamelName() + ".id")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .add(getMatchingUserConstraint("user", searchTerm)) //
                .add(Restrictions.in("role.id", prismRoles)) //
                .list();
    }

    public List<Integer> getResourcesWithUnreadMessages(PrismScope scope, User user) {
        String resourceIdReference = scope.getLowerCamelName() + ".id";
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.groupProperty(resourceIdReference)) //
                .createAlias("thread", "thread", JoinType.INNER_JOIN) //
                .createAlias("thread.participants", "participant", JoinType.INNER_JOIN) //
                .createAlias("thread.messages", "message", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("participant.user", user)) //
                .add(getVisibleMessageConstraint("message")) //
                .add(getUnreadMessageConstraint()) //
                .list();
    }

    public List<ActivityMessageCountDTO> getResourceReadMessageCounts(PrismScope scope, Collection<Integer> resourceIds, User user) {
        return getResourceMessageCounts(scope, resourceIds, user, true);
    }

    public List<ActivityMessageCountDTO> getResourceUnreadMessageCounts(PrismScope scope, Collection<Integer> resourceIds, User user) {
        return getResourceMessageCounts(scope, resourceIds, user, false);
    }

    public ResourceStudyOption getResourceStudyOption(ResourceOpportunity resource, PrismStudyOption studyOption) {
        return (ResourceStudyOption) sessionFactory.getCurrentSession().createCriteria(ResourceStudyOption.class) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("project", resource.getProject())) //
                        .add(Restrictions.eq("program", resource.getProgram()))) //
                .add(Restrictions.eq("studyOption", studyOption)) //
                .addOrder(Order.desc("project"))
                .addOrder(Order.desc("program")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<ResourceStudyOption> getResourceStudyOptions(ResourceOpportunity resource) {
        return (List<ResourceStudyOption>) sessionFactory.getCurrentSession().createCriteria(ResourceStudyOption.class) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("project", resource.getProject())) //
                        .add(Restrictions.eq("program", resource.getProgram()))) //
                .addOrder(Order.desc("project")) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.asc("studyOption")) //
                .list();
    }

    public List<ResourceCondition> getResourceConditions(ResourceParent resource) {
        return (List<ResourceCondition>) sessionFactory.getCurrentSession().createCriteria(ResourceCondition.class) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .addOrder(Order.asc("actionCondition")) //
                .list();
    }

    public DateTime getLatestUpdatedTimestampSitemap(PrismScope resourceScope, List<PrismState> scopeStates, HashMultimap<PrismScope, PrismState> enclosedScopes) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.property("updatedTimestampSitemap"));

        Junction enclosedScopeExclusion = getResourceActiveEnclosedScopeRestriction(criteria, enclosedScopes);

        return (DateTime) criteria.add(getResourceActiveScopeExclusion(scopeStates, enclosedScopeExclusion)) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<ResourceRepresentationSitemap> getResourceSitemapRepresentations(PrismScope resourceScope, List<PrismState> scopeStates,
            HashMultimap<PrismScope, PrismState> enclosedScopes) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id"), "id") //
                        .add(Projections.property("updatedTimestampSitemap"), "updatedTimestampSitemap"));

        Junction enclosedScopeExclusion = getResourceActiveEnclosedScopeRestriction(criteria, enclosedScopes);

        return (List<ResourceRepresentationSitemap>) criteria
                .add(getResourceActiveScopeExclusion(scopeStates, enclosedScopeExclusion)) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setMaxResults(50000) //
                .setResultTransformer(aliasToBean(ResourceRepresentationSitemap.class)) //
                .list();
    }

    public ResourceRepresentationRobotMetadata getResourceRobotMetadataRepresentation(Resource resource, List<PrismState> scopeStates,
            HashMultimap<PrismScope, PrismState> enclosedScopes) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(resource.getResourceScope().getResourceClass())
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("id"), "id")
                        .add(Projections.property("advert.name"), "name")
                        .add(Projections.property("advert.summary"), "summary")
                        .add(Projections.property("advert.description"), "description")
                        .add(Projections.property("advert.homepage"), "homepage"))
                .createAlias("advert", "advert", JoinType.INNER_JOIN);

        Junction enclosedScopeExclusion = getResourceActiveEnclosedScopeRestriction(criteria, enclosedScopes);

        return (ResourceRepresentationRobotMetadata) criteria
                .add(Restrictions.eq("id", resource.getId()))
                .add(getResourceActiveScopeExclusion(scopeStates, enclosedScopeExclusion))
                .addOrder(Order.desc("updatedTimestampSitemap"))
                .setResultTransformer(aliasToBean(ResourceRepresentationRobotMetadata.class))
                .uniqueResult();
    }

    public List<ResourceRepresentationIdentity> getResourceRobotRelatedRepresentations(Resource resource, PrismScope relatedScope,
            List<PrismState> relatedScopeStates,
            HashMultimap<PrismScope, PrismState> enclosedScopes) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(relatedScope.getResourceClass())
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("id"), "id") //
                        .add(Projections.property("name"), "name"));

        Junction enclosedScopeExclusion = getResourceActiveEnclosedScopeRestriction(criteria, enclosedScopes);

        return (List<ResourceRepresentationIdentity>) criteria //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource))
                .add(getResourceActiveScopeExclusion(relatedScopeStates, enclosedScopeExclusion)) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setResultTransformer(aliasToBean(ResourceRepresentationIdentity.class)).list();
    }

    public <T extends ResourceParent> Long getActiveChildResourceCount(T resource, PrismScope childResourceScope) {
        return (Long) sessionFactory.getCurrentSession().createCriteria(childResourceScope.getResourceClass()) //
                .setProjection(Projections.countDistinct("id")) //
                .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("resourceState.state.id", PrismState.valueOf(childResourceScope.name() + "_APPROVED")))
                .uniqueResult();
    }

    public void deleteResourceStudyOptions(ResourceOpportunity resourceOpportunity) {
        String propertyName = resourceOpportunity.getResourceScope().getLowerCamelName();
        sessionFactory.getCurrentSession()
                .createQuery("delete ResourceStudyOption " //
                        + "where " + propertyName + " = :resourceOpportunity")
                .setParameter("resourceOpportunity", resourceOpportunity) //
                .executeUpdate();
    }

    public ResourceFlatToNestedDTO getResourceWithParentResources(Resource resource, List<PrismScope> parentScopes) {
        PrismScope resourceScope = resource.getResourceScope();
        ProjectionList projections = Projections.projectionList();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(projections);

        appendResourceProjections(resourceScope, projections, true);
        parentScopes.forEach(parentScope -> {
            appendResourceProjections(parentScope, projections, false);
        });

        if (resourceScope.equals(INSTITUTION)) {
            criteria.createAlias("logoImage", "logoImage", JoinType.LEFT_OUTER_JOIN);
        }

        parentScopes.forEach(parentScope -> {
            String parentResourceReference = parentScope.getLowerCamelName();
            criteria.createAlias(parentResourceReference, parentResourceReference, JoinType.LEFT_OUTER_JOIN);
            if (parentScope.equals(INSTITUTION)) {
                criteria.createAlias(parentResourceReference + ".logoImage", "logoImage", JoinType.LEFT_OUTER_JOIN);
            }
        });

        return (ResourceFlatToNestedDTO) criteria.add(Restrictions.eq("id", resource.getId()))
                .setResultTransformer(aliasToBean(ResourceFlatToNestedDTO.class))
                .uniqueResult();
    }

    public Integer getResourceForWhichUserCanConnect(User user, ResourceParent resource) {
        PrismScope resourceScope = resource.getResourceScope();
        String resourceReference = resourceScope.getLowerCamelName();
        return (Integer) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("resource.id"))
                .createAlias(resourceReference, "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(getResourceParentConnectableConstraint(resourceScope, user)) //
                .uniqueResult();
    }

    public List<ResourceConnectionDTO> getResourcesForWhichUserCanConnect(User user, PrismScope resourceScope, String searchTerm) {
        ProjectionList projections = Projections.projectionList() //
                .add(Projections.groupProperty("institution.id").as("institutionId")) //
                .add(Projections.property("institution.name").as("institutionName")) //
                .add(Projections.property("institution.logoImage.id").as("logoImageId"));

        boolean isDepartment = resourceScope.equals(DEPARTMENT);
        if (isDepartment) {
            projections.add(Projections.groupProperty("department.id").as("departmentId")) //
                    .add(Projections.property("department.name").as("departmentName"));

        }

        String resourceReference = resourceScope.getLowerCamelName();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(projections //
                        .add(Projections.property(resourceReference + ".opportunityCategories").as("opportunityCategories"))) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN);

        if (isDepartment) {
            criteria.createAlias(resourceReference + ".institution", "institution", JoinType.INNER_JOIN);
        }

        criteria.createAlias(resourceReference + ".userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .add(getResourceParentConnectableConstraint(resourceScope, user));

        if (!isNullOrEmpty(searchTerm)) {
            criteria.add(Restrictions.like(resourceReference + ".name", searchTerm, MatchMode.ANYWHERE));
        }

        return (List<ResourceConnectionDTO>) criteria //
                .setResultTransformer(aliasToBean(ResourceConnectionDTO.class)) //
                .list();
    }

    public List<Integer> getResourceForWhichUserHasRoles(User user, PrismRole... roles) {
        String resourceReference = roles[0].getScope().getLowerCamelName();
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.property(resourceReference + ".id"))
                .add(Restrictions.isNotNull(resourceReference))
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.in("role.id", roles)) //
                .list();
    }

    public List<PrismStateGroup> getResourceStateGroups(Resource resource) {
        return (List<PrismStateGroup>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("state.stateGroup.id")) //
                .createAlias("state", "state") //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .list();
    }

    public ResourceRatingSummaryDTO getResourceRatingSummary(ResourceParent resource) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (ResourceRatingSummaryDTO) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty(resourceReference), "resource") //
                        .add(Projections.countDistinct("id"), "ratingCount") //
                        .add(Projections.avg("rating"), "ratingAverage")) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(Restrictions.isNotNull("rating")) //
                .setResultTransformer(aliasToBean(ResourceRatingSummaryDTO.class)) //
                .uniqueResult();
    }

    public ResourceRatingSummaryDTO getResourceRatingSummary(ResourceParent resource, ResourceParent parent) {
        String parentReference = parent.getResourceScope().getLowerCamelName();
        return (ResourceRatingSummaryDTO) sessionFactory.getCurrentSession().createCriteria(resource.getResourceScope().getResourceClass()) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty(parentReference), "resource") //
                        .add(Projections.sum("opportunityRatingCount"), "ratingCount") //
                        .add(Projections.avg("opportunityRatingAverage"), "ratingAverage")) //
                .add(Restrictions.eq(parentReference, parent)) //
                .add(Restrictions.isNotNull("opportunityRatingCount")) //
                .setResultTransformer(aliasToBean(ResourceRatingSummaryDTO.class)) //
                .uniqueResult();
    }

    public List<Integer> getResourceIds(PrismScope resourceScope) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.property("id")) //
                .list();
    }

    public List<Integer> getResourceIds(Resource enclosingResource, PrismScope resourceScope) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq(enclosingResource.getResourceScope().getLowerCamelName(), enclosingResource)) //
                .list();
    }

    public List<Integer> getResourceIds(Resource enclosingResource, PrismScope resourceScope, String query) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.property("resource.id")) //
                .createAlias(resourceScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN)
                .createAlias("resource.state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("resource." + enclosingResource.getResourceScope().getLowerCamelName(), enclosingResource));

        if (!isNullOrEmpty(query)) {
            criteria.add(Restrictions.like("resource.name", query, MatchMode.ANYWHERE));
        }

        return (List<Integer>) criteria.add(getResourceParentManageableStateConstraint(resourceScope))
                .list();
    }

    public List<ResourceSimpleDTO> getResources(Resource enclosingResource, PrismScope resourceScope, Optional<String> query) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("state.scope.id").as("scope")) //
                        .add(Projections.property("resource.id").as("id")) //
                        .add(Projections.property("resource.name").as("name")) //
                        .add(Projections.property("state.id").as("stateId"))) //
                .createAlias(resourceScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("resource." + enclosingResource.getResourceScope().getLowerCamelName(), enclosingResource));

        if (query.isPresent()) {
            criteria.add(getLikeConstraint("resource.name", query.get()));
        }

        return (List<ResourceSimpleDTO>) criteria.add(Restrictions.ne("state.id", valueOf(resourceScope.name() + "_DISABLED_COMPLETED")))
                .addOrder(Order.asc("resource.name")) //
                .addOrder(Order.asc("resource.id")) //
                .setResultTransformer(aliasToBean(ResourceSimpleDTO.class)) //
                .list();
    }

    public List<Integer> getResourcesWithUsersToVerify(PrismScope resourceScope) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty(resourceScope.getLowerCamelName() + ".id")) //
                .add(Restrictions.in("role.id", values(PrismRole.class, resourceScope, "STUDENT_UNVERIFIED", "VIEWER_UNVERIFIED"))) //
                .list();
    }

    public List<Integer> getEnclosedResources(PrismScope resourceScope, Integer resourceId, PrismScope enclosedScope) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.property("resource.id")) //
                .createAlias(enclosedScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("resource." + resourceScope.getLowerCamelName() + ".id", resourceId)) //
                .list();
    }

    public List<Integer> getResourcesByTheme(PrismScope resourceScope, PrismResourceListFilterExpression expression, String theme) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN)
                .createAlias("advert.categories.themes", "advertTheme", JoinType.INNER_JOIN) //
                .createAlias("advertTheme.theme", "theme", JoinType.INNER_JOIN) //
                .add(Restrictions.like("theme.name", theme, getMatchMode(expression))) //
                .list();
    }

    public List<Integer> getResourcesByLocation(PrismScope resourceScope, PrismResourceListFilterExpression expression, String location) {
        MatchMode matchMode = getMatchMode(expression);
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.categories.locations", "location", JoinType.INNER_JOIN) //
                .createAlias("location.locationAdvert", "locationAdvert", JoinType.INNER_JOIN) //
                .createAlias("locationAdvert.institution", "locationInstitution", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("locationAdvert.department", "locationDepartment", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("locationAdvert.program", "locationProgram", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("locationAdvert.project", "locationProject", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.like("locationInstitution.name", location, matchMode)) //
                        .add(Restrictions.like("locationDepartment.name", location, matchMode)) //
                        .add(Restrictions.like("locationProgram.name", location, matchMode)) //
                        .add(Restrictions.like("locationProject.name", location, matchMode))) //
                .list();
    }

    public List<Integer> getResourcesWithStateActionsPending(PrismScope scope, List<PrismAction> actions) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(scope.getResourceClass()) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("stateActionPendings", "stateActionPending", JoinType.INNER_JOIN,
                        Restrictions.isNotNull("stateActionPending.templateComment")) //
                .add(Restrictions.in("stateActionPending.action.id", actions)) //
                .list();
    }

    public List<Integer> getResourcesByAdvertTheme(PrismScope scope, List<Integer> advertThemes) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(scope.getResourceClass()) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.categories.themes", "theme", JoinType.INNER_JOIN) //
                .add(Restrictions.in("theme.theme.id", advertThemes)) //
                .list();
    }

    public List<Integer> getResourcesByAdvertLocation(PrismScope scope, List<Integer> advertLocations) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(scope.getResourceClass()) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.categories.locations", "location", JoinType.INNER_JOIN) //
                .add(Restrictions.in("location.locationAdvert.id", advertLocations)) //
                .list();
    }

    public List<ResourceRoleDTO> getResourceRoles(User user, PrismScope resourceScope) {
        String resourceReference = resourceScope.getLowerCamelName();
        return (List<ResourceRoleDTO>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("role.scope.id").as("scope")) //
                        .add(Projections.property(resourceReference + ".id").as("id")) //
                        .add(Projections.property("role.id").as("role")) //
                        .add(Projections.property("role.verified").as("verified")) //
                        .add(Projections.property("role.directlyAssignable").as("directlyAssignable"))) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .add(Restrictions.isNotNull(resourceReference)) //
                .add(Restrictions.eq("user", user)) //
                .setResultTransformer(aliasToBean(ResourceRoleDTO.class)) //
                .list();
    }

    public Set<Integer> getResourcesWithActivitiesToCache(PrismScope scope, DateTime baseline) {
        Session session = sessionFactory.getCurrentSession();
        Class<? extends Resource> resourceClass = scope.getResourceClass();
        Set<Integer> resources = newHashSet(session.createCriteria(resourceClass) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("comments", "comment", JoinType.INNER_JOIN) //
                .add(Restrictions.le("comment.submittedTimestamp", baseline)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("activityCachedTimestamp")) //
                        .add(Restrictions.gtProperty("comment.submittedTimestamp", "activityCachedTimestamp"))) //
                .list());

        resources.addAll(session.createCriteria(resourceClass) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("comments", "comment", JoinType.INNER_JOIN) //
                .createAlias("comment.thread", "thread", JoinType.INNER_JOIN) //
                .createAlias("thread.messages", "message", JoinType.INNER_JOIN) //
                .add(Restrictions.le("message.createdTimestamp", baseline)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("activityCachedTimestamp")) //
                        .add(Restrictions.gtProperty("message.createdTimestamp", "activityCachedTimestamp"))) //
                .list());

        if (scope.getScopeCategory().equals(ORGANIZATION)) {
            String scopeReference = scope.getLowerCamelName();
            resources.addAll(session.createCriteria(AdvertTarget.class) //
                    .setProjection(Projections.groupProperty("resource.id")) //
                    .createAlias("acceptAdvert", "acceptAdvert", JoinType.INNER_JOIN) //
                    .createAlias("acceptAdvert." + scopeReference, "resource", JoinType.INNER_JOIN) //
                    .add(Restrictions.le("createdTimestamp", baseline)) //
                    .add(Restrictions.disjunction() //
                            .add(Restrictions.isNull("resource.activityCachedTimestamp"))
                            .add(Restrictions.gtProperty("createdTimestamp", "resource.activityCachedTimestamp"))) //
                    .list());

            resources.addAll(session.createCriteria(UserRole.class) //
                    .setProjection(Projections.groupProperty("resource.id")) //
                    .createAlias(scopeReference, "resource", JoinType.INNER_JOIN) //
                    .createAlias("role", "role", JoinType.INNER_JOIN) //
                    .add(Restrictions.le("assignedTimestamp", baseline)) //
                    .add(Restrictions.disjunction() //
                            .add(Restrictions.isNull("resource.activityCachedTimestamp"))
                            .add(Restrictions.gtProperty("assignedTimestamp", "resource.activityCachedTimestamp"))) //
                    .add(Restrictions.in("role.id", PrismEnumUtils.values(PrismRole.class, scope, "VIEWER_UNVERIFIED", "STUDENT_UNVERIFIED"))) //
                    .list());
        }

        return resources;
    }

    public void setResourceActivityCachedTimestamp(PrismScope scope, Collection<Integer> resources, DateTime baseline) {
        sessionFactory.getCurrentSession().createQuery( //
                "update " + scope.getUpperCamelName() + " " //
                        + "set activityCachedTimestamp = :baseline " //
                        + "where id in (:resources)") //
                .setParameter("baseline", baseline) //
                .setParameterList("resources", resources) //
                .executeUpdate();
    }

    public List<Integer> getResourceTargets(PrismScope targeterScope, Collection<Integer> targeterResources, PrismScope targetScope) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(targeterScope.getResourceClass()) //
                .setProjection(Projections.groupProperty("targetAdvert." + targetScope.getLowerCamelName() + ".id")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.targets", "target", JoinType.INNER_JOIN) //
                .createAlias("target.targetAdvert", "targetAdvert", JoinType.INNER_JOIN) //
                .add(Restrictions.in("id", targeterResources)) //
                .add(Restrictions.eq("target.partnershipState", ENDORSEMENT_PROVIDED)) //
                .list();
    }

    public List<EntityLocationDTO> getResourceOrganizationLocations(PrismScope resourceScope, Collection<Integer> resourceIds) {
        return (List<EntityLocationDTO>) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id").as("id")) //
                        .add(Projections.groupProperty("locationPart.name").as("location"))) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.address", "address", JoinType.INNER_JOIN) //
                .createAlias("address.locations", "location", JoinType.INNER_JOIN)
                .createAlias("location.locationPart", "locationPart", JoinType.INNER_JOIN) //
                .add(Restrictions.in("id", resourceIds)) //
                .addOrder(Order.asc("id")) //
                .addOrder(Order.asc("location.id")) //
                .setResultTransformer(Transformers.aliasToBean(EntityLocationDTO.class)) //
                .list();
    }

    public List<Integer> getVisibleResources(PrismScope scope) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("resource.id")) //
                .createAlias(scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.resourceConditions", "resourceCondition", JoinType.INNER_JOIN)
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("role.verified", true)) //
                .add(getVisibleResourceConstraint(scope)) //
                .list();
    }

    public List<Integer> getVisibleResources(User user, PrismScope scope) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("resource.id")) //
                .createAlias(scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("resource.resourceConditions", "resourceCondition", JoinType.INNER_JOIN)
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.eq("role.verified", true)) //
                .add(getVisibleResourceConstraint(scope)) //
                .list();
    }

    private static void appendResourceListFilterCriteria(Criteria criteria, Junction constraints, ResourceListFilterDTO filter, DateTime updateBaseline) {
        List<Integer> resourceIds = filter.getResourceIds();
        if (isNotEmpty(resourceIds)) {
            criteria.add(Restrictions.in("resource.id", resourceIds));
        }

        List<Integer> excludedResourceIds = filter.getExcludedResourceIds();
        if (isNotEmpty(excludedResourceIds)) {
            criteria.add(Restrictions.not(
                    Restrictions.in("resource.id", excludedResourceIds)));
        }

        ResourceIdentityDTO parentResource = filter.getParentResource();
        if (parentResource != null) {
            criteria.add(Restrictions.eq("resource." + parentResource.getScope().getLowerCamelName() + ".id", parentResource.getId()));
        }

        PrismRoleCategory[] roleCategories = filter.getRoleCategories();
        if (roleCategories != null) {
            criteria.add(Restrictions.in("role.roleCategory", roleCategories));
        }

        List<PrismAction> actionIds = filter.getActionIds();
        if (isNotEmpty(actionIds)) {
            criteria.add(Restrictions.in("stateAction.action.id", actionIds));
        }

        PrismActionEnhancement[] actionEnhancements = filter.getActionEnhancements();
        if (actionEnhancements != null) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.in("stateAction.actionEnhancement", actionEnhancements))
                    .add(Restrictions.in("stateActionAssignment.actionEnhancement", actionEnhancements)));
        }

        boolean urgentOnly = BooleanUtils.isTrue(filter.getUrgentOnly());
        boolean updateOnly = BooleanUtils.isTrue(filter.getUpdateOnly());

        if (urgentOnly && updateOnly) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.eq("stateAction.raisesUrgentFlag", true)) //
                    .add(Restrictions.ge("resource.updatedTimestamp", updateBaseline)));
        } else if (urgentOnly) {
            criteria.add(Restrictions.eq("stateAction.raisesUrgentFlag", true));
        } else if (updateOnly) {
            criteria.add(Restrictions.ge("resource.updatedTimestamp", updateBaseline));
        }

        if (constraints != null) {
            criteria.add(constraints);
        }
    }

    private static Junction getResourceActiveScopeExclusion(List<PrismState> relatedScopeStates, Junction enclosedScopeExclusion) {
        return Restrictions.disjunction() //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.in("state.id", relatedScopeStates)) //
                        .add(Restrictions.not(enclosedScopeExclusion)));
    }

    private static Junction getResourceActiveEnclosedScopeRestriction(Criteria criteria, HashMultimap<PrismScope, PrismState> enclosedScopes) {
        Junction enclosedScopeExclusion = Restrictions.conjunction();
        for (PrismScope enclosedScope : enclosedScopes.keySet()) {
            String enclosedScopeReference = enclosedScope.getLowerCamelName();
            criteria.createAlias(enclosedScopeReference + "s", enclosedScopeReference, JoinType.LEFT_OUTER_JOIN, //
                    Restrictions.in(enclosedScopeReference + ".state.id", enclosedScopes.get(enclosedScope)));

            enclosedScopeExclusion.add(Restrictions.isNull(enclosedScopeReference + ".id"));
        }
        return enclosedScopeExclusion;
    }

    private void appendResourceProjections(PrismScope resourceScope, ProjectionList projections, boolean rootScope) {
        String resourceReference = resourceScope.getLowerCamelName();
        String accessorPrefix = rootScope ? "" : resourceReference + ".";
        projections.add(Projections.property(accessorPrefix + "id").as(resourceReference + "Id"));

        if (asList(PROJECT, PROGRAM, DEPARTMENT, INSTITUTION).contains(resourceScope)) {
            projections.add(Projections.property(accessorPrefix + "name").as(resourceReference + "Name"));

            if (resourceScope.equals(INSTITUTION)) {
                projections.add(Projections.property("logoImage.id").as("logoImageId"));
                projections.add(Projections.property("logoImage.fileName").as("logoImageFileName"));
            }
        }
    }

    private List<ActivityMessageCountDTO> getResourceMessageCounts(PrismScope scope, Collection<Integer> resourceIds, User user, boolean read) {
        String resourceIdReference = scope.getLowerCamelName() + ".id";
        return (List<ActivityMessageCountDTO>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty(resourceIdReference).as("id")) //
                        .add(Projections.countDistinct("message.id").as("messageCount"))) //
                .createAlias("thread", "thread", JoinType.INNER_JOIN) //
                .createAlias("thread.participants", "participant", JoinType.INNER_JOIN) //
                .createAlias("thread.messages", "message") //
                .add(Restrictions.in(resourceIdReference, resourceIds)) //
                .add(Restrictions.eq("participant.user", user)) //
                .add(getVisibleMessageConstraint("message"))
                .add(getReadOrUnreadMessageConstraint(read)) //
                .setResultTransformer(aliasToBean(ActivityMessageCountDTO.class)) //
                .list();
    }

}
