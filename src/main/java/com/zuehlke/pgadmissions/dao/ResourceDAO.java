package com.zuehlke.pgadmissions.dao;

import java.util.HashMap;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.DurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.FilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.utils.FreeMarkerHelper;

@Repository
@SuppressWarnings("unchecked")
public class ResourceDAO {

    @Value("${resource.list.sql.location}")
    private String resourceListSqlLocation;

    @Value("${resource.list.years.to.retrieve}")
    private Integer resourceListYearsToRetrieve;

    @Value("${resource.list.records.to.retrieve}")
    private Integer resourceListRecordsToRetrieve;

    @Autowired
    private FreeMarkerHelper freeMarkerHelper;

    @Autowired
    private SessionFactory sessionFactory;

    public <T extends Resource> List<ResourceConsoleListRowDTO> getConsoleListBlock(User user, Class<T> resourceClass, List<PrismScope> parentScopes,
            int loadIndex) {
        HashMap<String, Object> model = Maps.newHashMap();
        model.put("user", user);
        model.put("queryScope", PrismScope.getResourceScope(resourceClass).getLowerCaseName());
        model.put("parentScopes", parentScopes);
        model.put("queryRangeValue", resourceListYearsToRetrieve);
        model.put("queryRangeUnit", DurationUnit.YEAR.name());
        model.put("queryRangeValue", resourceListYearsToRetrieve);
        model.put("queryRangeUnit", DurationUnit.YEAR.name());
        model.put("rowIndex", loadIndex * resourceListRecordsToRetrieve);
        model.put("rowCount", resourceListRecordsToRetrieve);

        return (List<ResourceConsoleListRowDTO>) sessionFactory.getCurrentSession() //
                .createSQLQuery(freeMarkerHelper.buildString(resourceListSqlLocation, model)) //
                .addScalar("id", IntegerType.INSTANCE) //
                .addScalar("code", StringType.INSTANCE) //
                .addScalar("raisesUrgentFlag", BooleanType.INSTANCE) //
                .addScalar("state", StringType.INSTANCE) //
                .addScalar("creatorFirstName", StringType.INSTANCE) //
                .addScalar("creatorFirstName2", StringType.INSTANCE) //
                .addScalar("creatorFirstName3", StringType.INSTANCE) //
                .addScalar("creatorLastName", StringType.INSTANCE) //
                .addScalar("institutionTitle", StringType.INSTANCE) //
                .addScalar("programTitle", StringType.INSTANCE) //
                .addScalar("projectTitle", StringType.INSTANCE) //
                .addScalar("displayTimestamp", DateType.INSTANCE) //
                .addScalar("actions", StringType.INSTANCE) //
                .addScalar("averageRating", BigDecimalType.INSTANCE) //
                .setResultTransformer(Transformers.aliasToBean(ResourceConsoleListRowDTO.class)) //
                .list();
    }

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

    public <T extends Resource> DetachedCriteria getResourceListFilter(User user, Class<T> resourceClass, List<PrismScope> parentScopeIds,
            ResourceListFilterDTO filterDTO, String lastSequenceIdentifier) {
        DetachedCriteria criteria = DetachedCriteria.forClass(resourceClass) //
                .setProjection(Projections.groupProperty("id")) //
                .add(Restrictions.disjunction() //
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
            
            DetachedCriteria stateCriteria = DetachedCriteria.forClass(StateAction.class) //
                    .setProjection(Projections.groupProperty("state.id"))
                    .createAlias("stateActionAssignments", "stateActionAssigment", JoinType.INNER_JOIN)
                    .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN)
                    .add(Restrictions.eq("role.scope.id", parentScopeId));
            
            if (filterDTO.isUrgentOnly()) {
                stateCriteria.add(Restrictions.eq("stateAction.raisesUrgentFlag", true));
            }
            
            criteria.add(Restrictions.conjunction() //
                    .add(Subqueries.propertyIn(parentResourceReference, //
                            DetachedCriteria.forClass(UserRole.class) //
                                    .setProjection(Projections.groupProperty(parentResourceReference + ".id")) //
                                    .add(Restrictions.eq("user", user))
                                    .add(Restrictions.isNotNull(parentResourceReference)))) //
                    .add(Subqueries.propertyIn("state", //
                            stateCriteria)));
        }
        
        Junction filterConditions = Restrictions.conjunction();
        if (filterDTO.getMatchMode() == FilterMatchMode.ANY) {
            filterConditions = Restrictions.disjunction();
        }
        
        HashMap<String, Object> filters = filterDTO.getFilters();
        for (String type : filters.keySet()) {
            Object filter = filters.get(type);
        }
        
        return criteria.add(filterConditions);
    }

}
