package com.zuehlke.pgadmissions.dao;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
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

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.FilterFetchMode;
import com.zuehlke.pgadmissions.domain.definitions.FilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.FilterSortOrder;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.DateFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.RatingFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.StateFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.StringFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.UserRoleFilterDTO;
import com.zuehlke.pgadmissions.services.helpers.FilterHelper;
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
                .createAlias("stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.raisesUrgentFlag", true)) //
                .list();
    }

    public <T extends Resource> List<Integer> getRecentlyUpdatedResources(Class<T> resourceClass, DateTime rangeStart, DateTime rangeClose) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceClass) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.between("updatedTimestamp", rangeStart, rangeClose)) //
                .list();
    }

    public <T extends Resource> List<Integer> getResourceListFilter(User user, Class<T> resourceClass, List<PrismScope> parentScopeIds,
            ResourceListFilterDTO filterDTO, FilterFetchMode fetchMode, String lastSequenceIdentifier) {
        if (resourceClass.equals(System.class)) {
            throw new Error("System is not a listable resource type");
        }

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(resourceClass) //
                .setProjection(Projections.groupProperty("id"));

        if (filterDTO.hasFilter("userRole")) {
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

        HashMap<String, Object> filters = filterDTO.getFilters();
        for (String filterProperty : filters.keySet()) {
            Object filter = filters.get(filterProperty);

            if (filterProperty.equals("creator")) {
                for (StringFilterDTO filterTerm : (List<StringFilterDTO>) filter) {
                    FilterHelper.appendUserFilterCriterion(filterConditions, filterProperty, filterTerm);
                }
            } else if (filterProperty.equals("code")) {
                for (StringFilterDTO filterTerm : (List<StringFilterDTO>) filters) {
                    FilterHelper.appendStringFilterCriterion(filterConditions, filterProperty, filterTerm);
                }
            } else if (Arrays.asList("institution", "program", "project").contains(filterProperty)) {
                for (StringFilterDTO filterTerm : (List<StringFilterDTO>) filter) {
                    FilterHelper.appendParentResourceFilterCriterion(filterConditions, filterProperty, filterTerm);
                }
            } else if (Arrays.asList("createdTimestamp", "updatedTimestamp").contains(filterProperty)) {
                for (DateFilterDTO filterRange : (List<DateFilterDTO>) filter) {
                    FilterHelper.appendDateTimeFilterCriterion(filterConditions, filterProperty, filterRange);
                }
            } else if (filterProperty.equals("submittedTimestamp")) {
                if (resourceClass.equals(Application.class)) {
                    for (DateFilterDTO filterRange : (List<DateFilterDTO>) filter) {
                        FilterHelper.appendDateTimeFilterCriterion(filterConditions, filterProperty, filterRange);
                    }
                } else {
                    throwResourceFilterListMissingPropertyError(resourceClass, filterProperty);
                }
            } else if (filterProperty.equals("dueDate")) {
                for (DateFilterDTO filterRange : (List<DateFilterDTO>) filters) {
                    FilterHelper.appendDateFilterCriterion(filterConditions, filterProperty, filterRange);
                }
            } else if (filterProperty.equals("closingDate")) {
                if (resourceClass.equals(Application.class)) {
                    for (DateFilterDTO filterRange : (List<DateFilterDTO>) filters) {
                        Junction closingDateRestriction = Restrictions.disjunction();
                        FilterHelper.appendDateFilterCriterion(closingDateRestriction, filterProperty, filterRange);
                        FilterHelper.appendDateFilterCriterion(closingDateRestriction, "previous" + WordUtils.capitalize(filterProperty), filterRange);
                        filterConditions.add(closingDateRestriction);
                    }
                } else {
                    throwResourceFilterListMissingPropertyError(resourceClass, filterProperty);
                }
            } else if (filterProperty.equals("state")) {
                for (StateFilterDTO filterTerm : (List<StateFilterDTO>) filters) {
                    FilterHelper.appendStateFilterCriterion(filterConditions, filterProperty, filterTerm);
                }
            } else if (filterProperty.equals("referrer")) {
                for (StringFilterDTO filterTerm : (List<StringFilterDTO>) filters) {
                    FilterHelper.appendStringFilterCriterion(filterConditions, filterProperty, filterTerm);
                }
            } else if (filterProperty.equals("userRole")) {
                if (resourceClass.equals(Application.class)) {
                    for (UserRoleFilterDTO filterTerm : (List<UserRoleFilterDTO>) filters) {
                        FilterHelper.appendUserRoleFilterCriterion(filterConditions, "project.user.id", filterTerm);
                    }
                } else {
                    throwResourceFilterListMissingPropertyError(resourceClass, filterProperty);
                }
            } else if (filterProperty.equals("rating")) {
                String fieldProperty = resourceClass.equals(Application.class) ? "ratingAverage" : "applicationRatingAverage";
                for (RatingFilterDTO filterRange : (List<RatingFilterDTO>) filters) {
                    FilterHelper.appendRatingFilterCriterion(filterConditions, fieldProperty, filterRange);
                }
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

    private <T extends Resource> void throwResourceFilterListMissingPropertyError(Class<T> resourceClass, String type) {
        throw new Error(resourceClass.getSimpleName() + " does not have a " + type + " property");
    }

}
