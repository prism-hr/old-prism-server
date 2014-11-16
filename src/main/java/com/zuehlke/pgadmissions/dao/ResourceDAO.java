package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.hibernate.Criteria;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.resource.ResourceStateTransitionSummary;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.services.builders.ResourceListConstraintBuilder;

@Repository
@SuppressWarnings("unchecked")
public class ResourceDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public <T extends Resource> List<Integer> getResourcesToEscalate(Class<T> resourceClass, PrismAction actionId, LocalDate baseline) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceClass) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.action.id", actionId)) //
                .add(Restrictions.lt("dueDate", baseline)) //
                .list();
    }

    public List<Integer> getResourcesToPropagate(PrismScope propagatingScope, Integer propagatingId, PrismScope propagatedScope, PrismAction actionId) {
        String propagatedAlias = propagatedScope.getLowerCaseName();
        String propagatedReference = propagatingScope.getPrecedence() > propagatedScope.getPrecedence() ? propagatedAlias : propagatedAlias + "s";

        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(propagatingScope.getResourceClass()) //
                .setProjection(Projections.property(propagatedAlias + ".id")) //
                .createAlias(propagatedReference, propagatedAlias, JoinType.INNER_JOIN) //
                .createAlias(propagatedAlias + ".state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", propagatingId)) //
                .add(Restrictions.eq("stateAction.action.id", actionId)) //
                .list();
    }

    public <T extends Resource> List<Integer> getResourcesRequiringIndividualReminders(Class<T> resourceClass, LocalDate baseline) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceClass) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("lastRemindedRequestIndividual")) //
                        .add(Restrictions.lt("lastRemindedRequestIndividual", baseline))) //
                .add(Restrictions.eq("stateAction.raisesUrgentFlag", true)) //
                .list();
    }

    public <T extends Resource> List<Integer> getResourcesRequiringSyndicatedReminders(Class<T> resourceClass, LocalDate baseline) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceClass) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("lastRemindedRequestSyndicated")) //
                        .add(Restrictions.lt("lastRemindedRequestSyndicated", baseline))) //
                .add(Restrictions.eq("stateAction.raisesUrgentFlag", true)) //
                .list();
    }

    public <T extends Resource> List<Integer> getResourceRequiringSyndicatedUpdates(Class<T> resourceClass, LocalDate baseline, DateTime rangeStart,
            DateTime rangeClose) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceClass) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("lastNotifiedUpdateSyndicated")) //
                        .add(Restrictions.lt("lastNotifiedUpdateSyndicated", baseline))) //
                .add(Restrictions.between("updatedTimestamp", rangeStart, rangeClose)) //
                .list();
    }

    public void deleteResourceState(Resource resource, State state) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete ResourceState " //
                        + "where " + resource.getResourceScope().getLowerCaseName() + " = :resource " //
                        + "and state = :state") //
                .setParameter("resource", resource) //
                .setParameter("state", state) //
                .executeUpdate();
    }

    public void deleteSecondaryResourceState(Resource resource, State state) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete ResourceState " //
                        + "where " + resource.getResourceScope().getLowerCaseName() + " = :resource " //
                        + "and state = :state " + "and primaryState is false") //
                .setParameter("resource", resource) //
                .setParameter("state", state) //
                .executeUpdate();
    }

    public List<ResourceConsoleListRowDTO> getResourceConsoleList(User user, PrismScope scopeId, List<PrismScope> parentScopeIds,
            Set<Integer> assignedResources, ResourceListFilterDTO filter, String lastSequenceIdentifier, Integer maxRecords) {
        if (assignedResources.isEmpty()) {
            return new ArrayList<ResourceConsoleListRowDTO>(0);
        }

        Class<? extends Resource> resourceClass = scopeId.getResourceClass();
        String resourceReference = scopeId.getLowerCaseName();

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(resourceClass, resourceReference);

        ProjectionList projectionList = Projections.projectionList();

        for (PrismScope parentScopeId : parentScopeIds) {
            String parentScopeName = parentScopeId.getLowerCaseName();
            projectionList.add(Projections.property(parentScopeName + ".id"), parentScopeName + "Id");
        }

        projectionList.add(Projections.property("id"), scopeId.getLowerCaseName() + "Id") //
                .add(Projections.property("user.id"), "creatorId") //
                .add(Projections.property("user.firstName"), "creatorFirstName") //
                .add(Projections.property("user.firstName2"), "creatorFirstName2") //
                .add(Projections.property("user.firstName3"), "creatorFirstName3") //
                .add(Projections.property("user.lastName"), "creatorLastName") //
                .add(Projections.property("user.email"), "creatorEmail") //
                .add(Projections.property("code"), "code");

        addResourceListCustomColumns(scopeId, projectionList);

        projectionList.add(Projections.property("applicationRatingAverage"), "applicationRatingAverage") //
                .add(Projections.property("state.id"), "stateId") //
                .add(Projections.property("state.stateGroup.id"), "stateGroupId") //
                .add(Projections.property("user.email"), "creatorEmail") //
                .add(Projections.property("updatedTimestamp"), "updatedTimestamp") //
                .add(Projections.property("sequenceIdentifier"), "sequenceIdentifier"); //

        criteria.setProjection(projectionList) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN);

        addResourceListCustomJoins(scopeId, resourceReference, criteria);

        criteria.add(Restrictions.in("id", assignedResources));

        return ResourceListConstraintBuilder.appendLimitCriterion(criteria, filter, lastSequenceIdentifier, maxRecords)
                .setResultTransformer(Transformers.aliasToBean(ResourceConsoleListRowDTO.class)) //
                .list();
    }

    public List<Integer> getAssignedResources(User user, PrismScope scopeId, ResourceListFilterDTO filter, Junction conditions, String lastIdentifier,
            Integer recordsToRetrieve) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(scopeId.getResourceClass()) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.eqProperty("stateAction.state", "state"));

        ResourceListConstraintBuilder.appendFilterCriterion(criteria, conditions, filter);
        ResourceListConstraintBuilder.appendLimitCriterion(criteria, filter, lastIdentifier, recordsToRetrieve);

        return (List<Integer>) criteria.list();
    }

    public List<Integer> getAssignedResources(User user, PrismScope scopeId, PrismScope parentScopeId, ResourceListFilterDTO filter, Junction conditions,
            String lastIdentifier, Integer recordsToRetrieve) {
        String parentResourceReference = parentScopeId.getLowerCaseName();

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(scopeId.getResourceClass()) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias(parentResourceReference, parentResourceReference, JoinType.INNER_JOIN) //
                .createAlias(parentResourceReference + ".userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.eqProperty("stateAction.state", "state"));

        ResourceListConstraintBuilder.appendFilterCriterion(criteria, conditions, filter);
        ResourceListConstraintBuilder.appendLimitCriterion(criteria, filter, lastIdentifier, recordsToRetrieve);

        return (List<Integer>) criteria.list();
    }

    public <T extends ResourceParent> List<Integer> getMatchingParentResources(PrismScope parentScopeId, String searchTerm) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(parentScopeId.getResourceClass()) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.ilike("code", searchTerm, MatchMode.ANYWHERE)) //
                        .add(Restrictions.ilike("title", searchTerm, MatchMode.ANYWHERE))) //
                .list();
    }

    public List<Integer> getByMatchingUsersInRole(PrismScope scopeId, String searchTerm, PrismRole roleId) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.property(scopeId.getLowerCaseName() + ".id")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.ilike("user.fullName", searchTerm, MatchMode.ANYWHERE)) //
                        .add(Restrictions.ilike("user.email", searchTerm, MatchMode.ANYWHERE))) //
                .add(Restrictions.eq("role.id", roleId)) //
                .list();
    }

    public void deletePrimaryResourceState(Resource resource) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete ResourceState " + "where " + resource.getResourceScope().getLowerCaseName() + " = :resource " //
                        + "and primaryState is true") //
                .setParameter("resource", resource) //
                .executeUpdate();
    }

    public void deletePrimaryResourcePreviousState(Resource resource) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete ResourcePreviousState " + "where " + resource.getResourceScope().getLowerCaseName() + " = :resource " //
                        + "and primaryState is true") //
                .setParameter("resource", resource) //
                .executeUpdate();
    }

    public void deleteSecondaryResourcePreviousStates(Resource resource) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete ResourcePreviousState " + "where " + resource.getResourceScope().getLowerCaseName() + " = :resource " //
                        + "and primaryState is false") //
                .setParameter("resource", resource) //
                .executeUpdate();
    }

    public void insertSecondaryPreviousResourceStates(Resource resource) {
        String resourceReference = resource.getResourceScope().getLowerCaseName();
        sessionFactory.getCurrentSession().createQuery( //
                "insert into ResourcePreviousState(" + resourceReference + ", previousState, primaryState) " //
                        + "select " + resourceReference + ", state, primaryState " //
                        + "from ResourceState " + //
                        "where " + resourceReference + " = :resource " //
                        + "and primaryState is false") //
                .setParameter("resource", resource) //
                .executeUpdate();
    }

    public List<State> getCurrentStates(Resource resource) {
        return (List<State>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.property("state")) //
                .add(Restrictions.eq("resource", resource)) //
                .addOrder(Order.desc("primaryState")) //
                .list();
    }

    public String getRecommendedNextStates(Resource resource) {
        Resource parentResource = resource.getParentResource();
        return (String) sessionFactory.getCurrentSession().createCriteria(ResourceStateTransitionSummary.class) //
                .setProjection(Projections.property("transitionStateSelection")) //
                .add(Restrictions.eq(parentResource.getResourceScope().getLowerCaseName(), parentResource)) //
                .add(Restrictions.eq("stateGroup", resource.getState().getStateGroup())) //
                .addOrder(Order.desc("frequency")) //
                .addOrder(Order.desc("updatedTimestamp")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    private void addResourceListCustomColumns(PrismScope scopeId, ProjectionList projectionList) {
        HashMultimap<String, String> customColumns = scopeId.getConsoleListCustomColumns();
        for (String tableName : customColumns.keySet()) {
            boolean prefixColumnName = !tableName.equals(scopeId.getLowerCaseName());
            for (String columnName : customColumns.get(tableName)) {
                projectionList.add(Projections.property(prefixColumnName ? tableName + "." + columnName : columnName),
                        tableName + WordUtils.capitalize(columnName));
            }
        }
    }

    private void addResourceListCustomJoins(PrismScope scopeId, String resourceReference, Criteria criteria) {
        for (String tableName : scopeId.getConsoleListCustomColumns().keySet()) {
            if (!tableName.equals(resourceReference)) {
                criteria.createAlias(tableName, tableName, JoinType.LEFT_OUTER_JOIN); //
            }
        }
    }

}
