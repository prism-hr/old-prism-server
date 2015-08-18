package com.zuehlke.pgadmissions.dao;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.definitions.PrismFilterSortOrder;
import com.zuehlke.pgadmissions.domain.definitions.workflow.*;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.resource.*;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.resource.ResourceChildCreationDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceListRowDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceStandardDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceTargetingDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationIdentity;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationRobotMetadata;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSitemap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.WordUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getResourceStateActionConstraint;
import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getSimilarUserRestriction;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.PrismActionEnhancementGroup.RESOURCE_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory.ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;
import static com.zuehlke.pgadmissions.utils.PrismConstants.SEQUENCE_IDENTIFIER;

@Repository
@SuppressWarnings("unchecked")
public class ResourceDAO {

    @Autowired
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

    public List<Integer> getResourcesToPropagate(PrismScope propagatingScope, Integer propagatingId,
            PrismScope propagatedScope, PrismAction actionId) {
        String propagatedAlias = propagatedScope.getLowerCamelName();
        String propagatedReference = propagatingScope.ordinal() > propagatedScope.ordinal() ? propagatedAlias
                : propagatedAlias + "s";

        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(propagatingScope.getResourceClass()) //
                .setProjection(Projections.property(propagatedAlias + ".id")) //
                .createAlias(propagatedReference, propagatedAlias, JoinType.INNER_JOIN) //
                .createAlias(propagatedAlias + ".state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", propagatingId)) //
                .add(Restrictions.eq("stateAction.action.id", actionId)) //
                .list();
    }

    public List<Integer> getResourcesRequiringIndividualReminders(PrismScope resourceScope, LocalDate baseline) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("resourceState.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("lastRemindedRequestIndividual")) //
                        .add(Restrictions.lt("lastRemindedRequestIndividual", baseline))) //
                .add(Restrictions.eq("stateAction.raisesUrgentFlag", true)) //
                .list();
    }

    public List<Integer> getResourcesRequiringSyndicatedReminders(PrismScope resourceScope, LocalDate baseline) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("resourceState.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("lastRemindedRequestSyndicated")) //
                        .add(Restrictions.lt("lastRemindedRequestSyndicated", baseline))) //
                .add(Restrictions.eq("stateAction.raisesUrgentFlag", true)) //
                .list();
    }

    public List<Integer> getResourceRequiringSyndicatedUpdates(PrismScope resourceScope, LocalDate baseline,
            DateTime rangeStart, DateTime rangeClose) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("lastNotifiedUpdateSyndicated")) //
                        .add(Restrictions.lt("lastNotifiedUpdateSyndicated", baseline))) //
                .add(Restrictions.between("updatedTimestamp", rangeStart, rangeClose)) //
                .list();
    }

    public void deleteResourceState(Resource<?> resource, State state) {
        sessionFactory.getCurrentSession()
                .createQuery( //
                        "delete ResourceState " //
                                + "where " + resource.getResourceScope().getLowerCamelName() + " = :resource " //
                                + "and state = :state") //
                .setParameter("resource", resource) //
                .setParameter("state", state) //
                .executeUpdate();
    }

    public void deleteSecondaryResourceState(Resource<?> resource, State state) {
        sessionFactory.getCurrentSession()
                .createQuery( //
                        "delete ResourceState " //
                                + "where " + resource.getResourceScope().getLowerCamelName() + " = :resource " //
                                + "and state = :state " + "and primaryState is false") //
                .setParameter("resource", resource) //
                .setParameter("state", state) //
                .executeUpdate();
    }

    public List<ResourceListRowDTO> getResourceList(User user, PrismScope scopeId, List<PrismScope> parentScopeIds,
            Set<Integer> assignedResources, ResourceListFilterDTO filter, String lastSequenceIdentifier,
            Integer maxRecords, boolean hasRedactions) {
        if (assignedResources.isEmpty()) {
            return Collections.emptyList();
        }

        String scopeName = scopeId.getLowerCamelName();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(scopeId.getResourceClass(), scopeName);

        ProjectionList projectionList = Projections.projectionList();

        List<String> parentScopeNames = Lists.newLinkedList();
        for (PrismScope parentScopeId : parentScopeIds) {
            String parentScopeName = parentScopeId.getLowerCamelName();
            projectionList.add(Projections.property(parentScopeName + ".id"), parentScopeName + "Id");

            if (!parentScopeId.equals(SYSTEM)) {
                projectionList.add(Projections.property(parentScopeName + ".name"), parentScopeName + "Name");
            }

            if (parentScopeId.equals(INSTITUTION)) {
                projectionList.add(Projections.property(parentScopeName + ".logoImage.id"),
                        parentScopeName + "LogoImageId");
            }

            parentScopeNames.add(parentScopeName);
        }

        projectionList.add(Projections.property("id"), scopeName + "Id");
        if (!scopeId.equals(APPLICATION)) {
            projectionList.add(Projections.property("name"), scopeName + "Name");
        }

        projectionList.add(Projections.property("code"), "code") //
                .add(Projections.property("user.id"), "userId") //
                .add(Projections.property("user.firstName"), "userFirstName") //
                .add(Projections.property("user.firstName2"), "userFirstName2") //
                .add(Projections.property("user.firstName3"), "userFirstName3") //
                .add(Projections.property("user.lastName"), "userLastName") //
                .add(Projections.property("user.email"), "userEmail") //
                .add(Projections.property("primaryExternalAccount.accountImageUrl"), "userAccountImageUrl");

        if (!hasRedactions) {
            projectionList.add(Projections.property("applicationRatingAverage"), "applicationRatingAverage");
        }

        projectionList.add(Projections.property("state.id"), "stateId") //
                .add(Projections.property("createdTimestamp"), "createdTimestamp") //
                .add(Projections.property("updatedTimestamp"), "updatedTimestamp") //
                .add(Projections.property("sequenceIdentifier"), "sequenceIdentifier"); //

        if (!scopeId.equals(APPLICATION)) {
            projectionList.add(Projections.property("advertIncompleteSection"), "advertIncompleteSection");
        }

        criteria.setProjection(projectionList);
        for (String parentScopeName : parentScopeNames) {
            criteria.createAlias(parentScopeName, parentScopeName, JoinType.LEFT_OUTER_JOIN);
        }

        criteria.setProjection(projectionList) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN)
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN)
                .createAlias("userAccount.primaryExternalAccount", "primaryExternalAccount", JoinType.LEFT_OUTER_JOIN);

        criteria.add(Restrictions.in("id", assignedResources));

        return appendResourceListLimitCriterion(criteria, filter, lastSequenceIdentifier, maxRecords)
                .setResultTransformer(Transformers.aliasToBean(ResourceListRowDTO.class)) //
                .list();
    }

    public List<Integer> getAssignedResources(User user, PrismScope scopeId, ResourceListFilterDTO filter,
            Junction conditions, String lastSequenceIdentifier, Integer recordsToRetrieve) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(scopeId.getResourceClass()) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN,
                        Restrictions.eq("stateActionAssignment.partnerMode", false)) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.eqProperty("stateAction.state", "resourceState.state")) //
                .add(getResourceStateActionConstraint()) //
                .add(Restrictions.isNull("state.hidden"));

        appendResourceListFilterCriterion(criteria, conditions, filter);
        appendResourceListLimitCriterion(criteria, filter, lastSequenceIdentifier, recordsToRetrieve);
        return (List<Integer>) criteria.list();
    }

    public List<Integer> getAssignedResources(User user, PrismScope scopeId, PrismScope parentScopeId,
            ResourceListFilterDTO filter, Junction conditions, String lastSequenceIdentifier,
            Integer recordsToRetrieve) {
        String parentResourceReference = parentScopeId.getLowerCamelName();

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(scopeId.getResourceClass()) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(parentResourceReference, parentResourceReference, JoinType.INNER_JOIN) //
                .createAlias(parentResourceReference + ".userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN,
                        Restrictions.eq("stateActionAssignment.partnerMode", false)) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(getResourceStateActionConstraint()) //
                .add(Restrictions.eqProperty("stateAction.state", "resourceState.state")) //
                .add(Restrictions.isNull("state.hidden"));

        appendResourceListFilterCriterion(criteria, conditions, filter);
        appendResourceListLimitCriterion(criteria, filter, lastSequenceIdentifier, recordsToRetrieve);
        return (List<Integer>) criteria.list();
    }

    public List<Integer> getResourcesByMatchingEnclosingResources(PrismScope parentResourceScope, String searchTerm) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(parentResourceScope.getResourceClass()) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.ilike("code", searchTerm, MatchMode.ANYWHERE)) //
                        .add(Restrictions.ilike("name", searchTerm, MatchMode.ANYWHERE))) //
                .list();
    }

    public List<Integer> getResourcesByMatchingUsersAndRole(PrismScope prismScope, String searchTerm,
            List<PrismRole> prismRoles) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.property(prismScope.getLowerCamelName() + ".id")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .add(getSimilarUserRestriction("user", searchTerm)) //
                .add(Restrictions.in("role.id", prismRoles)) //
                .list();
    }

    public <T> T getResourceAttribute(ResourceOpportunity<?> resource, Class<T> attributeClass, String attributeName,
            Object attributeValue) {
        return (T) sessionFactory.getCurrentSession().createCriteria(attributeClass)
                .add(Restrictions.disjunction().add(Restrictions.eq("project", resource.getProject()))
                        .add(Restrictions.eq("program", resource.getProgram())))
                .addOrder(Order.desc("project")).addOrder(Order.desc("program"))
                .add(Restrictions.eq(attributeName, attributeValue)).setMaxResults(1).uniqueResult();
    }

    public <T> T getResourceAttributeStrict(ResourceOpportunity<?> resource, Class<T> attributeClass,
            String attributeName, Object attributeValue) {
        return (T) sessionFactory.getCurrentSession().createCriteria(attributeClass) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq(attributeName, attributeValue)) //
                .uniqueResult();
    }

    public <T> List<T> getResourceAttributes(ResourceOpportunity<?> resource, Class<T> attributeClass,
            String attributeName, String orderAttributeName) {
        return (List<T>) sessionFactory.getCurrentSession().createCriteria(attributeClass) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("project", resource.getProject())) //
                        .add(Restrictions.eq("program", resource.getProgram()))) //
                .addOrder(Order.desc("project")) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.asc(Joiner.on(".").skipNulls().join(attributeName, orderAttributeName))) //
                .list();
    }

    public List<ResourceCondition> getResourceConditions(ResourceParent<?> resource) {
        return (List<ResourceCondition>) sessionFactory.getCurrentSession().createCriteria(ResourceCondition.class) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("project", resource.getProject())) //
                        .add(Restrictions.eq("program", resource.getProgram()))
                        .add(Restrictions.eq("department", resource.getDepartment())) //
                        .add(Restrictions.eq("institution", resource.getInstitution()))) //
                .addOrder(Order.desc("project")) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("department")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.asc("actionCondition")) //
                .list();
    }

    public <T> List<T> getResourceAttributesStrict(ResourceOpportunity<?> resource, Class<T> attributeClass,
            String attributeName, String orderAttributeName) {
        return (List<T>) sessionFactory.getCurrentSession().createCriteria(attributeClass) //
                .createAlias(attributeName, attributeName, JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .addOrder(Order.asc(Joiner.on(".").skipNulls().join(attributeName, orderAttributeName))) //
                .list();
    }

    public ResourceStudyOptionInstance getFirstStudyOptionInstance(ResourceOpportunity<?> resource,
            ImportedEntitySimple studyOption) {
        return (ResourceStudyOptionInstance) sessionFactory.getCurrentSession()
                .createCriteria(ResourceStudyOptionInstance.class) //
                .createAlias("studyOption", "studyOption", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("studyOption." + resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("studyOption.studyOption", studyOption)) //
                .addOrder(Order.asc("applicationStartDate")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public void deleteElapsedStudyOptions(LocalDate baseline) {
        sessionFactory.getCurrentSession()
                .createQuery( //
                        "delete ResourceStudyOption " //
                                + "where applicationCloseDate < :baseline") //
                .setParameter("baseline", baseline) //
                .executeUpdate();
    }

    public void deleteElapsedStudyOptionInstances(LocalDate baseline) {
        sessionFactory.getCurrentSession()
                .createQuery( //
                        "delete ResourceStudyOptionInstance " //
                                + "where applicationCloseDate < :baseline") //
                .setParameter("baseline", baseline) //
                .executeUpdate();
    }

    public LocalDate getResourceEndDate(ResourceOpportunity<?> resource) {
        return (LocalDate) sessionFactory.getCurrentSession().createCriteria(ResourceStudyOption.class) //
                .setProjection(Projections.property("applicationCloseDate")) //
                .addOrder(Order.desc("applicationCloseDate")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public DateTime getLatestUpdatedTimestampSitemap(PrismScope resourceScope, List<PrismState> scopeStates,
            HashMultimap<PrismScope, PrismState> enclosedScopes) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.property("updatedTimestampSitemap"));

        Junction enclosedScopeExclusion = getResourceActiveEnclosedScopeRestriction(criteria, enclosedScopes);

        return (DateTime) criteria.add(getResourceActiveScopeExclusion(scopeStates, enclosedScopeExclusion)) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<ResourceRepresentationSitemap> getResourceSitemapRepresentations(PrismScope resourceScope,
            List<PrismState> scopeStates, HashMultimap<PrismScope, PrismState> enclosedScopes) {
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

    public ResourceRepresentationRobotMetadata getResourceRobotMetadataRepresentation(Resource<?> resource,
            List<PrismState> scopeStates, HashMultimap<PrismScope, PrismState> enclosedScopes) {
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

    public List<ResourceRepresentationIdentity> getResourceRobotRelatedRepresentations(Resource<?> resource,
            PrismScope relatedScope, List<PrismState> relatedScopeStates,
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

    public <T extends ResourceParent<?>> Long getActiveChildResourceCount(T resource, PrismScope childResourceScope) {
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

    public void disableImportedResourceStudyOptions(ResourceOpportunity<?> resourceOpportunity) {
        String propertyName = resourceOpportunity.getResourceScope().getLowerCamelName();
        sessionFactory.getCurrentSession()
                .createQuery("delete ResourceStudyOption " + "where " + propertyName + " = :resourceOpportunity")
                .setParameter("resourceOpportunity", resourceOpportunity).executeUpdate();
    }

    public List<ResourceTargetingDTO> getResourcesWhichPermitTargeting(PrismScope filterScope, Integer filterResourceId,
            PrismScope resourceScope, List<PrismScope> parentScopes, String searchTerm) {
        return (List<ResourceTargetingDTO>) getResourcesCriteria(filterScope, Lists.newArrayList(filterResourceId),
                resourceScope, parentScopes, Projections.property("domicile.name").as("addressDomicileName"), Projections.property("address.addressLine1").as("addressLine1"),
                Projections.property("address.addressLine2").as("addressLine2"), Projections.property("address.addressTown").as("addressTown"),
                Projections.property("address.addressRegion").as("addressRegion"), Projections.property("address.addressCode").as("addressCode"),
                Projections.property("address.googleId").as("addressGoogleId"), Projections.property("address.addressCoordinates.latitude").as("addressCoordinateLatitude"),
                Projections.property("address.addressCoordinates.longitude").as("addressCoordinateLongitude"))
                        .add(Restrictions.ilike("name", searchTerm, MatchMode.ANYWHERE)) //
                        .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                        .createAlias("advert.address", "address", JoinType.INNER_JOIN) //
                        .createAlias("address.domicile", "domicile", JoinType.INNER_JOIN) //
                        .addOrder(Order.desc("name")) //
                        .setResultTransformer(Transformers.aliasToBean(ResourceTargetingDTO.class)) //
                        .list();
    }

    public List<ResourceChildCreationDTO> getResourcesWhichPermitChildResourceCreation(PrismScope filterScope,
            Integer filterResourceId, PrismScope resourceScope, List<PrismScope> parentScopes, PrismScope creationScope,
            String searchTerm, boolean userLoggedIn) {
        Criteria criteria = getResourcesCriteria(filterScope, Lists.newArrayList(filterResourceId), resourceScope,
                parentScopes, Projections.property("resourceCondition.partnerMode").as("partnerMode"))
                        .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
                        .createAlias("resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                        .createAlias("resourceState.state", "state", JoinType.INNER_JOIN) //
                        .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                        .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                        .add(Restrictions.eq("action.creationScope.id", creationScope));

        if (searchTerm != null) {
            criteria.add(Restrictions.ilike("name", searchTerm, MatchMode.ANYWHERE)); //
        }

        criteria.add(Restrictions.disjunction() //
                .add(Restrictions.isNull("resourceCondition.id")) //
                .add(Restrictions.eqProperty("resourceCondition.actionCondition", "stateAction.actionCondition")));

        if (!userLoggedIn) {
            criteria.add(Restrictions.eq("resourceCondition.partnerMode", true));
        }

        return (List<ResourceChildCreationDTO>) criteria.addOrder(Order.desc("name")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceChildCreationDTO.class)).list();
    }

    public void disableImportedResourceStudyOptionInstances(ResourceOpportunity<?> resourceOpportunity) {
        String propertyName = resourceOpportunity.getResourceScope().getLowerCamelName();
        sessionFactory.getCurrentSession()
                .createQuery("delete ResourceStudyOptionInstance " + "where studyOption in ("
                        + "select resourceStudyOption.id " + "from ResourceStudyOption as resourceStudyOption "
                        + "join resourceStudyOption." + propertyName + " as program "
                        + "where program = :resourceOpportunity)")
                .setParameter("resourceOpportunity", resourceOpportunity).executeUpdate();
    }

    public List<ResourceTargetingDTO> getResourceTargets(Advert advert, PrismScope[] resourceScopes, Collection<Integer> resourceIds, Collection<PrismState> activeStates,
            Collection<Integer> subjectAreas) {
        ProjectionList projectionList = Projections.projectionList();

        PrismScope resourceScope = null;
        int resourceScopesLength = resourceScopes.length;
        for (int i = 0; i < resourceScopesLength; i++) {
            PrismScope currentScope = resourceScopes[i];
            String currentScopeReference = currentScope.getLowerCamelName();
            if (i == 0) {
                resourceScope = currentScope;
                addResourceProjections(projectionList, currentScope, "");
            } else {
                addResourceProjections(projectionList, currentScope, currentScopeReference);
            }
        }

        projectionList.add(Projections.property("domicile.name"), "addressDomicileName") //
                .add(Projections.property("address.addressLine1"), "addressLine1") //
                .add(Projections.property("address.addressLine2"), "addressLine2") //
                .add(Projections.property("address.addressTown"), "addressTown") //
                .add(Projections.property("address.addressRegion"), "addressRegion") //
                .add(Projections.property("address.addressCode"), "addressCode") //
                .add(Projections.property("address.googleId"), "addressGoogleId") //
                .add(Projections.property("address.addressCoordinates.latitude"), "addressCoordinateLatitude") //
                .add(Projections.property("address.addressCoordinates.longitude"), "addressCoordinateLongitude") //
                .add(Projections.property("advertSelectedResource.id"), "selectedId") //
                .add(Projections.property("advertSelectedResource.endorsed"), "endorsed");

        boolean doSubjectAreaFilter = CollectionUtils.isNotEmpty(subjectAreas);
        if (doSubjectAreaFilter) {
            projectionList.add(Projections.sum("institutionSubjectArea.relationStrength").as("targetingRelevance"));
        }

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass())
                .setProjection(projectionList);

        for (int i = 1; i < resourceScopesLength; i++) {
            String joinScopeReference = resourceScopes[i].getLowerCamelName();
            criteria.createAlias(joinScopeReference, joinScopeReference, JoinType.INNER_JOIN);
        }

        String importedInstitutionJoinPath = resourceScope.equals(INSTITUTION) ? "importedInstitution" : "institution.importedInstitution";

        criteria.createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.address", "address", JoinType.INNER_JOIN) //
                .createAlias("address.domicile", "domicile", JoinType.INNER_JOIN) //
                .createAlias("advertSelectedResources", "advertSelectedResource", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.eq("advertSelectedResource.advert", advert))
                .createAlias("resourceStates", "resourceState") //
                .createAlias(importedInstitutionJoinPath, "importedInstitution", JoinType.INNER_JOIN,
                        Restrictions.isNotNull("importedInstitution.ucasId"));

        if (doSubjectAreaFilter) {
            criteria.createAlias("importedInstitution.institutionSubjectAreas", "institutionSubjectArea", JoinType.INNER_JOIN) //
                    .add(Restrictions.in("institutionSubjectArea.id", subjectAreas));
        }

        if (!CollectionUtils.isEmpty(resourceIds)) {
            criteria.add(Restrictions.in("id", resourceIds));
        }

        criteria.add(Restrictions.in("resourceState.state.id", activeStates));

        if (doSubjectAreaFilter) {
            criteria.addOrder(Order.desc("targetingRelevance"));
        }

        return (List<ResourceTargetingDTO>) criteria.addOrder(Order.asc("name"))
                .addOrder(Order.asc("id"))
                .setResultTransformer(Transformers.aliasToBean(ResourceTargetingDTO.class))
                .list();
    }

    public List<ResourceStandardDTO> getUserAdministratorResources(PrismScope resourceScope, User user) {
        String resourceReference = resourceScope.getLowerCamelName();
        PrismActionEnhancement[] administratorEnhancements = RESOURCE_ADMINISTRATOR.getActionEnhancements();

        return (List<ResourceStandardDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty(resourceReference + ".id").as(resourceReference + "Id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.eq("role.roleCategory", ADMINISTRATOR)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.in("stateActionAssignment.actionEnhancement", administratorEnhancements)) //
                        .add(Restrictions.in("stateAction.actionEnhancement", administratorEnhancements))) //
                .add(Restrictions.isNotNull(resourceReference)) //
                .setResultTransformer(Transformers.aliasToBean(ResourceStandardDTO.class)) //
                .list();
    }

    public List<PrismStateGroup> getResourceStateGroups(Resource<?> resource) {
        return (List<PrismStateGroup>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("state.stateGroup.id")) //
                .createAlias("state", "state") //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .list();
    }

    private Criteria getResourcesCriteria(PrismScope filterScope, List<Integer> filerResourceIds,
            PrismScope resourceScope, List<PrismScope> parentScopes, Projection... customColumns) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass());
        ProjectionList projectionList = Projections.projectionList();
        criteria.setProjection(projectionList);

        addResourceProjections(projectionList, resourceScope, "");
        for (PrismScope parentScope : parentScopes) {
            String parentScopeReference = parentScope.getLowerCamelName();
            addResourceProjections(projectionList, parentScope, parentScopeReference);
            criteria.createAlias(parentScopeReference, parentScopeReference, JoinType.LEFT_OUTER_JOIN);
        }

        if (customColumns != null) {
            for (Projection customColumn : customColumns) {
                projectionList.add(customColumn);
            }
        }

        if (!(filterScope == null || filerResourceIds == null || filerResourceIds.size() == 0)) {
            criteria.add(Restrictions.in(filterScope.getLowerCamelName() + ".id", filerResourceIds));
        }

        return criteria;
    }

    private void addResourceProjections(ProjectionList projectionList, PrismScope resourceScope, String prefix) {
        String resourceReference = resourceScope.getLowerCamelName();
        projectionList.add(getResourceProjection(resourceReference, prefix, "id"));
        if (resourceScope.isResourceParentScope()) {
            projectionList.add(getResourceProjection(resourceReference, prefix, "name"));
        }

        if (resourceScope.equals(INSTITUTION)) {
            projectionList.add(getResourceProjection(resourceReference, prefix, "logoImage.id"));
        }
    }

    private Projection getResourceProjection(String resourceReference, String prefix, String column) {
        return Projections.property(Joiner.on(".").skipNulls().join(Strings.emptyToNull(prefix), column))
                .as(resourceReference + Joiner.on("").join(Arrays.asList(column.split("\\.")).stream().map(part -> WordUtils.capitalize(part)).collect(Collectors.toList())));
    }

    private static void appendResourceListFilterCriterion(Criteria criteria, Junction conditions,
            ResourceListFilterDTO filter) {
        if (filter.isUrgentOnly()) {
            criteria.add(Restrictions.eq("stateAction.raisesUrgentFlag", true));
        }

        if (conditions != null) {
            criteria.add(conditions);
        }
    }

    private static Criteria appendResourceListLimitCriterion(Criteria criteria, ResourceListFilterDTO filter,
            String lastSequenceIdentifier, Integer recordsToRetrieve) {
        PrismFilterSortOrder sortOrder = filter.getSortOrder();

        if (lastSequenceIdentifier != null) {
            criteria.add(
                    PrismFilterSortOrder.getPagingRestriction(SEQUENCE_IDENTIFIER, sortOrder, lastSequenceIdentifier));
        }

        criteria.addOrder(PrismFilterSortOrder.getOrderExpression(SEQUENCE_IDENTIFIER, sortOrder));

        if (recordsToRetrieve != null) {
            criteria.setMaxResults(recordsToRetrieve);
        }

        return criteria;
    }

    private Junction getResourceActiveScopeExclusion(List<PrismState> relatedScopeStates,
            Junction enclosedScopeExclusion) {
        return Restrictions.disjunction() //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.in("state.id", relatedScopeStates)) //
                        .add(Restrictions.not(enclosedScopeExclusion)));
    }

    private Junction getResourceActiveEnclosedScopeRestriction(Criteria criteria,
            HashMultimap<PrismScope, PrismState> enclosedScopes) {
        Junction enclosedScopeExclusion = Restrictions.conjunction();
        for (PrismScope enclosedScope : enclosedScopes.keySet()) {
            String enclosedScopeReference = enclosedScope.getLowerCamelName();
            criteria.createAlias(enclosedScopeReference + "s", enclosedScopeReference, JoinType.LEFT_OUTER_JOIN, //
                    Restrictions.in(enclosedScopeReference + ".state.id", enclosedScopes.get(enclosedScope)));

            enclosedScopeExclusion.add(Restrictions.isNull(enclosedScopeReference + ".id"));
        }
        return enclosedScopeExclusion;
    }

}
