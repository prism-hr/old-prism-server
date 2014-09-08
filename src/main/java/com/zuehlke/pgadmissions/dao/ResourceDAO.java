package com.zuehlke.pgadmissions.dao;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.FilterFetchMode;
import com.zuehlke.pgadmissions.domain.definitions.FilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.FilterProperty;
import com.zuehlke.pgadmissions.domain.definitions.FilterSortOrder;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.DateFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.DecimalFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.StateGroupFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.StringFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.UserRoleFilterDTO;
import com.zuehlke.pgadmissions.services.helpers.ResourceListFilterHelper;
import com.zuehlke.pgadmissions.utils.FreeMarkerHelper;

@Repository
@SuppressWarnings("unchecked")
public class ResourceDAO {

    @Value("${resource.list.records.to.retrieve}")
    private Integer resourceListRecordsToRetrieve;

    @Value("${resource.report.records.to.retrieve}")
    private Integer resourceReportRecordsToRetrieve;

    @Autowired
    private FreeMarkerHelper freeMarkerHelper;

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

    public <T extends Resource> List<Integer> getVisibleResources(User user, Class<T> resourceClass, List<PrismScope> parentScopeIds,
            ResourceListFilterDTO filterDTO, FilterFetchMode fetchMode, String lastSequenceIdentifier) {
        if (resourceClass.equals(System.class)) {
            throw new Error("System is not a listable resource type");
        }

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(resourceClass) //
                .setProjection(Projections.groupProperty("id"));

        if (filterDTO.hasFilter(FilterProperty.STATE_GROUP)) {
            criteria.createAlias("state", "state", JoinType.INNER_JOIN);
        }
        
        if (filterDTO.hasFilter(FilterProperty.USER_ROLE)) {
            criteria.createAlias("userRoles", "userRole", JoinType.LEFT_OUTER_JOIN);
        }

        criteria.add(Restrictions.disjunction() //
                .add(Subqueries.propertyIn("id", //
                        DetachedCriteria.forClass(UserRole.class) //
                                .setProjection(Projections.groupProperty("application.id")) //
                                .createAlias("role", "role", JoinType.INNER_JOIN) //
                                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                                .createAlias("stateAction", "stateAction", JoinType.INNER_JOIN) //
                                .add(Restrictions.eq("user", user)) //
                                .add(Restrictions.isNotNull(PrismScope.getResourceScope(resourceClass).getLowerCaseName())))));

        boolean getUrgentOnly = filterDTO.isUrgentOnly();

        if (getUrgentOnly) {
            criteria.add(Restrictions.eq("stateAction.raisesUrgentFlag", true));
        }

        for (PrismScope parentScopeId : parentScopeIds) {
            String parentResourceReference = parentScopeId.getLowerCaseName();

            DetachedCriteria stateCriteria = DetachedCriteria.forClass(StateAction.class)
                    //
                    .setProjection(Projections.groupProperty("state.id")).createAlias("stateActionAssignments", "stateActionAssigment", JoinType.INNER_JOIN)
                    .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN).add(Restrictions.eq("role.scope.id", parentScopeId));

            if (filterDTO.isUrgentOnly()) {
                stateCriteria.add(Restrictions.eq("stateAction.raisesUrgentFlag", true));
            }

            criteria.add(Restrictions.conjunction() //
                    .add(Subqueries.propertyIn(parentResourceReference, //
                            DetachedCriteria.forClass(UserRole.class) //
                                    .setProjection(Projections.groupProperty(parentResourceReference + ".id")) //
                                    .add(Restrictions.eq("user", user)).add(Restrictions.isNotNull(parentResourceReference)))) //
                    .add(Subqueries.propertyIn("state", //
                            stateCriteria)));
        }

        appendResourceListFilterCriteria(resourceClass, criteria, filterDTO);

        FilterSortOrder sortOrder = filterDTO.getSortOrder();
        appendResourceListPagingExpression(criteria, filterDTO.getSortOrder(), lastSequenceIdentifier);
        appendResourceListOrderByExpression(criteria, sortOrder);

        appendResourceListLimitExpression(criteria, fetchMode);
        return criteria.list();
    }

    private <T extends Resource> void appendResourceListFilterCriteria(Class<T> resourceClass, Criteria criteria, ResourceListFilterDTO filterDTO) {
        Junction filterConditions = Restrictions.conjunction();
        if (filterDTO.getMatchMode() == FilterMatchMode.ANY) {
            filterConditions = Restrictions.disjunction();
        }

        HashMap<String, Object> filters = filterDTO.getFilterConstraints();
        for (String filterProperty : filters.keySet()) {
            Object filterConstraints = filters.get(filterProperty);

            switch (FilterProperty.getByPropertyName(filterProperty)) {
            case CLOSING_DATE:
                ResourceListFilterHelper.appendClosingDateFilterCriteria(resourceClass, filterConditions, filterProperty,
                        (List<DateFilterDTO>) filterConstraints);
                break;
            case CODE:
            case REFERRER:
                ResourceListFilterHelper.appendCodeFilterCriteria(filterConditions, filterProperty, (List<StringFilterDTO>) filterConstraints);
                break;
            case CONFIRMED_START_DATE:
                ResourceListFilterHelper.appendConfirmedStartDateFilterCriteria(resourceClass, filterConditions, filterProperty,
                        (List<DateFilterDTO>) filterConstraints);
                break;
            case CREATED_TIMESTAMP:
            case UPDATED_TIMESTAMP:
                ResourceListFilterHelper.appendTimestampFilterCriteria(filterConditions, filterProperty, (List<DateFilterDTO>) filterConstraints);
                break;
            case DUE_DATE:
                ResourceListFilterHelper.appendDateFilterCriteria(filterConditions, filterProperty, (List<DateFilterDTO>) filterConstraints);
                break;
            case INSTITUTION:
            case PROGRAM:
            case PROJECT:
                ResourceListFilterHelper.appendParentResourceFilterCriteria(resourceClass, filterConditions, filterProperty,
                        (List<StringFilterDTO>) filterConstraints);
                break;
            case RATING:
                ResourceListFilterHelper.appendRatingFilterCriteria(resourceClass, filterConditions, (List<DecimalFilterDTO>) filterConstraints);
                break;
            case STATE_GROUP:
                ResourceListFilterHelper.appendStateFilterCriteria(filterConditions, filterProperty, (List<StateGroupFilterDTO>) filterConstraints);
                break;
            case SUBMITTED_TIMESTAMP:
                ResourceListFilterHelper.appendSubmittedTimestampFilterCriteria(resourceClass, filterConditions, filterProperty,
                        (List<DateFilterDTO>) filterConstraints);
                break;
            case USER:
                ResourceListFilterHelper.appendUserFilterCriteria(filterConditions, filterProperty, (List<StringFilterDTO>) filterConstraints);
                break;
            case USER_ROLE:
                ResourceListFilterHelper.appendUserRoleFilterCriteria(filterConditions, filterProperty, (List<UserRoleFilterDTO>) filterConstraints);
                break;
            }
        }

        criteria.add(filterConditions);
    }

    private void appendResourceListPagingExpression(Criteria criteria, FilterSortOrder sortOrder, String lastSequenceIdentifier) {
        if (lastSequenceIdentifier == null) {
            return;
        }

        Criterion pagingCondition;
        if (sortOrder == FilterSortOrder.DESCENDING) {
            pagingCondition = Restrictions.lt("sequenceIdentifier", lastSequenceIdentifier);
        } else {
            pagingCondition = Restrictions.gt("sequenceIdentifier", lastSequenceIdentifier);
        }
        criteria.add(pagingCondition);
    }

    private void appendResourceListOrderByExpression(Criteria criteria, FilterSortOrder sortOrder) {
        Order sortOrderExpression;
        if (sortOrder == FilterSortOrder.DESCENDING) {
            sortOrderExpression = Order.desc("sequenceIdentifier");
        } else {
            sortOrderExpression = Order.asc("sequenceIdentifier");
        }
        criteria.addOrder(sortOrderExpression);
    }

    private void appendResourceListLimitExpression(Criteria criteria, FilterFetchMode fetchMode) {
        Integer recordsToRetrieve = fetchMode == FilterFetchMode.LIST ? resourceListRecordsToRetrieve : null;
        if (recordsToRetrieve == null) {
            return;
        }
        criteria.setMaxResults(recordsToRetrieve);
    }

}
