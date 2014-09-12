package com.zuehlke.pgadmissions.dao;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.FilterSortOrder;
import org.apache.commons.lang.WordUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
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
            ResourceListFilterDTO filterDTO, String lastSequenceIdentifier) {
        Class<? extends Resource> resourceClass = scopeId.getResourceClass();
        String resourceReference = scopeId.getLowerCaseName();

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(resourceClass, resourceReference);

        ProjectionList projectionList = Projections.projectionList();

        for (PrismScope parentScopeId : parentScopeIds) {
            String parentScopeName = parentScopeId.getLowerCaseName();
            projectionList.add(Projections.property(parentScopeName + ".id"), parentScopeName + "Id");
        }

        projectionList.add(Projections.property("id"), scopeId.getLowerCaseName() + ".Id") //
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

        criteria.createAlias("state", "state", JoinType.INNER_JOIN) //
                .add(Subqueries.propertyIn("id", ResourceListConstraintBuilder.getVisibleResourcesCriteria(user, resourceClass, parentScopeIds, filterDTO)));

        return ResourceListConstraintBuilder
                .appendResourceListDisplayFilterExpression(Application.class, criteria, filterDTO == null ? FilterSortOrder.DESCENDING : filterDTO.getSortOrder(), lastSequenceIdentifier) //
                .setResultTransformer(Transformers.aliasToBean(ResourceConsoleListRowDTO.class)) //
                .list();
    }

    private void addResourceListCustomColumns(PrismScope scopeId, ProjectionList projectionList) {
        HashMultimap<String, String> customColumns = scopeId.getConsoleListCustomColumns();
        for (String tableName : customColumns.keySet()) {
            boolean prefixColumnName = !tableName.equals(scopeId.getLowerCaseName());
            for (String columnName : customColumns.get(tableName)) {
                projectionList.add(Projections.property(prefixColumnName ? tableName + "." + columnName : columnName),
                        prefixColumnName ? tableName + WordUtils.capitalize(columnName) : columnName);
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
