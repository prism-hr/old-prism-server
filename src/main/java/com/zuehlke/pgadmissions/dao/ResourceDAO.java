package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.definitions.FilterFetchMode;
import com.zuehlke.pgadmissions.domain.definitions.FilterSortOrder;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
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

    public void appendResourceListPagingExpression(Criteria criteria, FilterSortOrder sortOrder, String lastSequenceIdentifier) {
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

    public void appendResourceListOrderByExpression(Criteria criteria, FilterSortOrder sortOrder) {
        Order sortOrderExpression;
        if (sortOrder == FilterSortOrder.DESCENDING) {
            sortOrderExpression = Order.desc("sequenceIdentifier");
        } else {
            sortOrderExpression = Order.asc("sequenceIdentifier");
        }
        criteria.addOrder(sortOrderExpression);
    }

    public void appendResourceListLimitExpression(Criteria criteria, FilterFetchMode fetchMode) {
        Integer recordsToRetrieve = fetchMode == FilterFetchMode.LIST ? resourceListRecordsToRetrieve : null;
        if (recordsToRetrieve == null) {
            return;
        }
        criteria.setMaxResults(recordsToRetrieve);
    }

}
