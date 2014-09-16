package com.zuehlke.pgadmissions.dao;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.ParentResource;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.FilterSortOrder;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.services.builders.ResourceListConstraintBuilder;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

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

    public List<Integer> getResourcesToPropagate(PrismScope propagatingResourceScope, Integer propagatingResourceId, PrismScope propagatedResourceScope,
                                                 PrismAction actionId) {
        String propagatedAlias = propagatedResourceScope.getLowerCaseName();
        String propagatedReference = propagatingResourceScope.getPrecedence() > propagatedResourceScope.getPrecedence() ? propagatedAlias : propagatedAlias
                + "s";

        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(propagatingResourceScope.getResourceClass()) //
                .setProjection(Projections.property(propagatedAlias + ".id")) //
                .createAlias(propagatedReference, propagatedAlias, JoinType.INNER_JOIN) //
                .createAlias(propagatedAlias + ".state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", propagatingResourceId)) //
                .add(Restrictions.eq("stateAction.action.id", actionId)) //
                .list();
    }

    public String getLastSequenceIdentifier(Resource resource, DateTime rangeStart, DateTime rangeClose) {
        return (String) sessionFactory.getCurrentSession().createCriteria(resource.getClass()) //
                .setProjection(Projections.max("sequenceIdentifier")) //
                .add(Restrictions.between("updatedTimestamp", rangeStart, rangeClose)) //
                .uniqueResult();
    }

    public <T extends Resource> List<Integer> getResourcesRequiringAttention(Class<T> resourceClass) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceClass) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.raisesUrgentFlag", true)) //
                .list();
    }

    public <T extends Resource> List<Integer> getRecentlyUpdatedResources(Class<T> resourceClass, DateTime rangeStart, DateTime rangeClose) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceClass) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.between("updatedTimestamp", rangeStart, rangeClose)) //
                .list();
    }

    public List<ResourceConsoleListRowDTO> getResourceConsoleList(User user, PrismScope scopeId, List<PrismScope> parentScopeIds,
                                                                  TreeSet<String> assignedResources, FilterSortOrder sortOrder, Integer recordsToReturn) {
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
                .add(Projections.property("user.firstName"), "creatorFirstName") //
                .add(Projections.property("user.firstName2"), "creatorFirstName2") //
                .add(Projections.property("user.firstName3"), "creatorFirstName3") //
                .add(Projections.property("user.lastName"), "creatorLastName").add(Projections.property("code"), "code");

        addResourceListCustomColumns(scopeId, projectionList);

        projectionList.add(Projections.property("applicationRatingAverage"), "applicationRatingAverage") //
                .add(Projections.property("state.id"), "stateId") //
                .add(Projections.property("state.stateGroup.id"), "stateGroupId") //
                .add(Projections.property("user.email"), "creatorEmail") //
                .add(Projections.property("updatedTimestamp"), "updatedTimestamp"); //

        criteria.setProjection(projectionList) //
                .createAlias("user", "user", JoinType.INNER_JOIN);

        addResourceListCustomJoins(scopeId, resourceReference, criteria);

        criteria.createAlias("state", "state", JoinType.INNER_JOIN);

        Disjunction conditions = Restrictions.disjunction();

        Iterator<String> iterator;
        if (sortOrder == FilterSortOrder.ASCENDING) {
            iterator = assignedResources.iterator();
        } else {
            iterator = assignedResources.descendingIterator();
        }

        int rowCursor = 0;
        while (iterator.hasNext() && (recordsToReturn == null || rowCursor < recordsToReturn)) {
            conditions.add(Restrictions.eq(ResourceListConstraintBuilder.SEQUENCE_IDENTIFIER, iterator.next()));
            rowCursor++;
        }

        return criteria.add(conditions) //
                .addOrder(FilterSortOrder.getOrderExpression(ResourceListConstraintBuilder.SEQUENCE_IDENTIFIER, sortOrder)) //
                .setResultTransformer(Transformers.aliasToBean(ResourceConsoleListRowDTO.class)) //
                .list();
    }

    public List<String> getAssignedResources(User user, PrismScope scopeId, ResourceListFilterDTO filter, Junction conditions, String lastIdentifier,
                                             Integer recordsToRetrieve) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(scopeId.getResourceClass()) //
                .setProjection(Projections.groupProperty(ResourceListConstraintBuilder.SEQUENCE_IDENTIFIER)) //
                .createAlias("userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.eqProperty("stateAction.state", "state"));

        ResourceListConstraintBuilder.appendFilterCriterion(criteria, filter, conditions, lastIdentifier, recordsToRetrieve);
        return (List<String>) criteria.list();
    }

    public List<String> getAssignedResources(User user, PrismScope scopeId, PrismScope parentScopeId, ResourceListFilterDTO filter, Junction conditions,
                                             String lastIdentifier, Integer recordsToRetrieve, TreeSet<String> exclusions) {
        String parentResourceReference = parentScopeId.getLowerCaseName();

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(scopeId.getResourceClass()) //
                .setProjection(Projections.groupProperty(ResourceListConstraintBuilder.SEQUENCE_IDENTIFIER)) //
                .createAlias(parentResourceReference, parentResourceReference, JoinType.INNER_JOIN) //
                .createAlias(parentResourceReference + ".userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("userRole.user", user)); //

        if (!exclusions.isEmpty()) {
            criteria.add(Restrictions.not(Restrictions.in(ResourceListConstraintBuilder.SEQUENCE_IDENTIFIER, exclusions)));
        }

        ResourceListConstraintBuilder.appendFilterCriterion(criteria, filter, conditions, lastIdentifier, recordsToRetrieve);
        return (List<String>) criteria.list();
    }

    public <T extends ParentResource> List<Integer> getMatchingParentResources(PrismScope parentScopeId, String searchTerm) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(parentScopeId.getResourceClass()) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.ilike("code", searchTerm, MatchMode.ANYWHERE)) //
                        .add(Restrictions.ilike("title", searchTerm, MatchMode.ANYWHERE))) //
                .list();
    }

    public List<Integer> getByMatchingUsersInRoles(PrismScope scopeId, String searchTerm, List<PrismRole> roleIds) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.property(scopeId.getLowerCaseName())) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.ilike("user.fullName", searchTerm, MatchMode.ANYWHERE)) //
                        .add(Restrictions.ilike("user.email", searchTerm, MatchMode.ANYWHERE))) //
                .add(Restrictions.in("role.id", roleIds)) //
                .list();
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
