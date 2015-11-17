package com.zuehlke.pgadmissions.dao;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismFilterSortOrder;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.*;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import com.zuehlke.pgadmissions.domain.resource.*;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.*;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationIdentity;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationRobotMetadata;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSitemap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.zuehlke.pgadmissions.PrismConstants.SEQUENCE_IDENTIFIER;
import static com.zuehlke.pgadmissions.dao.WorkflowDAO.*;
import static com.zuehlke.pgadmissions.domain.definitions.PrismFilterSortOrder.getOrderExpression;
import static com.zuehlke.pgadmissions.domain.definitions.PrismFilterSortOrder.getPagingRestriction;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.PrismActionEnhancementGroup.RESOURCE_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory.ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.valueOf;
import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

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

    public List<ResourceListRowDTO> getResourceList(User user, PrismScope scope, List<PrismScope> parentScopes, Collection<Integer> resourceIds, ResourceListFilterDTO filter,
            String lastSequenceIdentifier, Integer maxRecords, boolean hasRedactions) {
        if (CollectionUtils.isNotEmpty(resourceIds)) {
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
            projectionList.add(Projections.property("id"), scopeName + "Id");
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

            criteria.setProjection(projectionList);
            for (String parentScopeName : parentScopeNames) {
                criteria.createAlias(parentScopeName, parentScopeName, JoinType.LEFT_OUTER_JOIN);
            }

            criteria.setProjection(projectionList) //
                    .createAlias("user", "user", JoinType.INNER_JOIN) //
                    .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                    .createAlias("state", "state", JoinType.INNER_JOIN)
                    .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN);

            criteria.add(Restrictions.in("id", resourceIds));

            PrismOpportunityCategory opportunityCategory = filter.getOpportunityCategory();
            if (opportunityCategory != null) {
                criteria.add(getOpportunityCategoryConstraint(opportunityCategory));
            }

            return appendResourceListLimitCriteria(criteria, filter, lastSequenceIdentifier, maxRecords)
                    .setResultTransformer(Transformers.aliasToBean(ResourceListRowDTO.class)) //
                    .list();
        }

        return Collections.emptyList();
    }

    public <T> List<T> getResources(User user, PrismScope scope, ResourceListFilterDTO filter, ProjectionList columns, Junction conditions, Class<T> responseClass,
            DateTime updateBaseline) {
        Criteria criteria = workflowDAO.getWorkflowCriteriaList(scope, columns) //
                .add(Restrictions.eq("userRole.user", user));
        appendResourceListFilterCriteria(criteria, conditions, filter, updateBaseline);
        return (List<T>) criteria //
                .setResultTransformer(Transformers.aliasToBean(responseClass)) //
                .list();
    }

    public <T> List<T> getResources(User user, PrismScope scope, PrismScope parentScope, ResourceListFilterDTO filter, ProjectionList columns, Junction conditions,
            Class<T> responseClass, DateTime updateBaseline) {
        Criteria criteria = workflowDAO.getWorkflowCriteriaList(scope, parentScope, columns) //
                .add(Restrictions.eq("userRole.user", user));
        appendResourceListFilterCriteria(criteria, conditions, filter, updateBaseline);
        return (List<T>) criteria //
                .setResultTransformer(Transformers.aliasToBean(responseClass)) //
                .list();
    }

    public <T> List<T> getResources(User user, PrismScope scope, PrismScope targeterScope, PrismScope targetScope, List<Integer> targeterEntities, ResourceListFilterDTO filter,
            ProjectionList columns, Junction conditions, Class<T> responseClass, DateTime updateBaseline) {
        Criteria criteria = workflowDAO.getWorkflowCriteriaList(scope, targeterScope, targetScope, targeterEntities, columns)
                .add(Restrictions.eq("userRole.user", user));
        appendResourceListFilterCriteria(criteria, conditions, filter, updateBaseline);
        return (List<T>) criteria //
                .setResultTransformer(Transformers.aliasToBean(responseClass)) //
                .list();
    }

    public List<Integer> getSimilarResources(PrismScope resourceScope, String searchTerm) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.like("code", searchTerm, MatchMode.ANYWHERE)) //
                        .add(Restrictions.like("name", searchTerm, MatchMode.ANYWHERE))) //
                .list();
    }

    public List<Resource> getResourcesByUser(PrismScope prismScope, User user) {
        return (List<Resource>) sessionFactory.getCurrentSession().createCriteria(prismScope.getResourceClass()) //
                .add(Restrictions.eq("user", user)) //
                .list();
    }

    public List<Integer> getResourcesByMatchingUsersAndRole(PrismScope prismScope, String searchTerm, List<PrismRole> prismRoles) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.property(prismScope.getLowerCamelName() + ".id")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .add(getSimilarUserConstraint("user", searchTerm)) //
                .add(Restrictions.in("role.id", prismRoles)) //
                .list();
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
                .setResultTransformer(Transformers.aliasToBean(ResourceRepresentationSitemap.class)) //
                .list();
    }

    public ResourceRepresentationRobotMetadata getResourceRobotMetadataRepresentation(Resource resource, List<PrismState> scopeStates,
            HashMultimap<PrismScope, PrismState> enclosedScopes) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(resource.getClass()) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id"), "id") //
                        .add(Projections.property("advert.name"), "name") //
                        .add(Projections.property("advert.summary"), "summary") //
                        .add(Projections.property("advert.description"), "description") //
                        .add(Projections.property("advert.homepage"), "homepage")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN);

        Junction enclosedScopeExclusion = getResourceActiveEnclosedScopeRestriction(criteria, enclosedScopes);

        return (ResourceRepresentationRobotMetadata) criteria //
                .add(Restrictions.eq("id", resource.getId()))
                .add(getResourceActiveScopeExclusion(scopeStates, enclosedScopeExclusion)) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceRepresentationRobotMetadata.class))
                .uniqueResult();
    }

    public List<ResourceRepresentationIdentity> getResourceRobotRelatedRepresentations(Resource resource, PrismScope relatedScope, List<PrismState> relatedScopeStates,
            HashMultimap<PrismScope, PrismState> enclosedScopes) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(relatedScope.getResourceClass()) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id"), "id") //
                        .add(Projections.property("name"), "name"));

        Junction enclosedScopeExclusion = getResourceActiveEnclosedScopeRestriction(criteria, enclosedScopes);

        return (List<ResourceRepresentationIdentity>) criteria //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource))
                .add(getResourceActiveScopeExclusion(relatedScopeStates, enclosedScopeExclusion)) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceRepresentationIdentity.class)).list();
    }

    public <T extends ResourceParent> Long getActiveChildResourceCount(T resource, PrismScope childResourceScope) {
        return (Long) sessionFactory.getCurrentSession().createCriteria(childResourceScope.getResourceClass()) //
                .setProjection(Projections.countDistinct("id")) //
                .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("resourceState.state", "state", JoinType.INNER_JOIN) //
                .createAlias("resourceConditions", "resourceCondition", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN, //
                        Restrictions.eqProperty("resourceCondition.actionCondition", "stateAction.actionCondition")) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
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
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(resource.getClass()) //
                .setProjection(projections);

        appendResourceProjections(resourceScope, projections, true);
        parentScopes.forEach(parentScope -> {
            appendResourceProjections(parentScope, projections, false);
        });

        parentScopes.forEach(parentScope -> {
            String parentResourceReference = parentScope.getLowerCamelName();
            criteria.createAlias(parentResourceReference, parentResourceReference, JoinType.LEFT_OUTER_JOIN);
        });

        return (ResourceFlatToNestedDTO) criteria.add(Restrictions.eq("id", resource.getId())) //
                .setResultTransformer(Transformers.aliasToBean(ResourceFlatToNestedDTO.class)) //
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
                .add(WorkflowDAO.getResourceParentConnectableConstraint(resourceScope, user)) //
                .uniqueResult();
    }

    public List<ResourceConnectionDTO> getResourcesForWhichUserCanConnect(User user, PrismScope resourceScope, String searchTerm) {
        ProjectionList projections = Projections.projectionList() //
                .add(Projections.groupProperty("institution.id").as("institutionId")) //
                .add(Projections.property("institution.name").as("institutionName")) //
                .add(Projections.property("institution.logoImage.id").as("institutionLogoImageId"));

        boolean isDepartment = resourceScope.equals(DEPARTMENT);
        if (isDepartment) {
            projections.add(Projections.groupProperty("department.id").as("departmentId")) //
                    .add(Projections.property("department.name").as("departmentName"));

        }

        String resourceReference = resourceScope.getLowerCamelName();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(projections
                        .add(Projections.property(resourceReference + ".opportunityCategories").as("opportunityCategories")))
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

        return (List<ResourceConnectionDTO>) criteria
                .setResultTransformer(Transformers.aliasToBean(ResourceConnectionDTO.class))
                .list();
    }

    public List<ResourceIdentityDTO> getUserAdministratorResources(User user, PrismScope resourceScope) {
        String resourceReference = resourceScope.getLowerCamelName();
        PrismActionEnhancement[] administratorEnhancements = RESOURCE_ADMINISTRATOR.getActionEnhancements();

        return (List<ResourceIdentityDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("resource.id").as("id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias(resourceReference, "resource", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.eq("role.roleCategory", ADMINISTRATOR)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.in("stateActionAssignment.actionEnhancement", administratorEnhancements)) //
                        .add(Restrictions.in("stateAction.actionEnhancement", administratorEnhancements))) //
                .add(Restrictions.isNotNull(resourceReference)) //
                .setResultTransformer(Transformers.aliasToBean(ResourceIdentityDTO.class)) //
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

    public ResourceParent getActiveResourceByName(Resource parentResource, PrismScope resourceScope, String name) {
        String resourceReference = resourceScope.getLowerCamelName();
        return (ResourceParent) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.property(resourceReference)) //
                .createAlias(resourceReference, "resource", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("resource." + parentResource.getResourceScope().getLowerCamelName(), parentResource))
                .add(Restrictions.eq("resource.name", name)) //
                .add(getResourceParentManageableStateConstraint(resourceScope)) //
                .addOrder(Order.asc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
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
                .setResultTransformer(Transformers.aliasToBean(ResourceRatingSummaryDTO.class)) //
                .uniqueResult();
    }

    public ResourceRatingSummaryDTO getResourceRatingSummary(ResourceParent resource, ResourceParent parent) {
        String parentReference = parent.getResourceScope().getLowerCamelName();
        return (ResourceRatingSummaryDTO) sessionFactory.getCurrentSession().createCriteria(resource.getClass()) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty(parentReference), "resource") //
                        .add(Projections.sum("opportunityRatingCount"), "ratingCount") //
                        .add(Projections.avg("opportunityRatingAverage"), "ratingAverage")) //
                .add(Restrictions.eq(parentReference, parent)) //
                .add(Restrictions.isNotNull("opportunityRatingCount")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceRatingSummaryDTO.class)) //
                .uniqueResult();
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
                .setResultTransformer(Transformers.aliasToBean(ResourceSimpleDTO.class)) //
                .list();
    }

    public List<Integer> getResourcesWithUsersToVerify(PrismScope resourceScope) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty(resourceScope.getLowerCamelName() + ".id")) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("role.verified", false)) //
                .list();
    }

    public List<Integer> getResourcesWithNewOpportunities(PrismScope resourceScope, PrismScope targeterScope, PrismScope targetScope, DateTime createdBaseline) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("targetResource.id")) //
                .createAlias(resourceScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource." + targeterScope.getLowerCamelName(), "targeterResource", JoinType.INNER_JOIN) //
                .createAlias("targeterResource.advert", "targeterAdvert", JoinType.INNER_JOIN) //
                .createAlias("targeterAdvert.targets", "target", JoinType.INNER_JOIN) //
                .createAlias("target.targetAdvert", "targetAdvert", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert." + targetScope.getLowerCamelName(), "targetResource", JoinType.INNER_JOIN,
                        Restrictions.eqProperty("targetAdvert.id", "targetResource.advert.id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.ge("resource.createdTimestamp", createdBaseline)) //
                .add(Restrictions.eq("stateAction.action.id", PrismAction.valueOf(resourceScope.name() + "_CREATE_APPLICATION")))
                .list();
    }

    private static void appendResourceListFilterCriteria(Criteria criteria, Junction constraints, ResourceListFilterDTO filter, DateTime updateBaseline) {
        List<Integer> resourceIds = filter.getResourceIds();
        if (isNotEmpty(resourceIds)) {
            criteria.add(Restrictions.in("resource.id", resourceIds));
        }

        PrismRoleCategory roleCategory = filter.getRoleCategory();
        if (roleCategory != null) {
            criteria.add(Restrictions.eq("role.roleCategory", roleCategory));
        }

        PrismAction actionId = filter.getActionId();
        if (actionId != null) {
            criteria.add(Restrictions.eq("stateAction.action.id", actionId));
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

    private static Criteria appendResourceListLimitCriteria(Criteria criteria, ResourceListFilterDTO filter, String lastSequenceIdentifier, Integer recordsToRetrieve) {
        PrismFilterSortOrder sortOrder = filter.getSortOrder();

        if (lastSequenceIdentifier != null) {
            criteria.add(getPagingRestriction(SEQUENCE_IDENTIFIER, sortOrder, lastSequenceIdentifier));
        }

        criteria.addOrder(getOrderExpression(SEQUENCE_IDENTIFIER, sortOrder));

        if (recordsToRetrieve != null) {
            criteria.setMaxResults(recordsToRetrieve);
        }

        return criteria;
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
                projections.add(Projections.property(accessorPrefix + "logoImage.id").as("logoImageId"));
            }
        }
    }

}
