package com.zuehlke.pgadmissions.dao;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
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
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.DurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.ResourceActionDTO;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.dto.StateChangeDTO;
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

    public <T extends Resource> List<ResourceConsoleListRowDTO> getConsoleListBlock(User user, Class<T> resourceClass, List<Scope> parentScopes, int loadIndex) {
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

    public List<Resource> getResourcesToEscalate(Action action, LocalDate baseline) {
        return (List<Resource>) sessionFactory.getCurrentSession().createCriteria(action.getScope().getId().getResourceClass()) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.lt("dueDate", baseline)) //
                .list();
    }

    public List<Resource> getResourcesToPropagate(Resource propagator, Action action) {
        PrismScope propagatedScope = action.getScope().getId();
        String propagatedAlias = propagatedScope.getLowerCaseName();
        String propagatedReference = propagator.getResourceScope().getPrecedence() > propagatedScope.getPrecedence() ? propagatedAlias : propagatedAlias + "s";

        return (List<Resource>) sessionFactory.getCurrentSession().createCriteria(propagator.getClass()) //
                .createAlias(propagatedReference, propagatedAlias, JoinType.INNER_JOIN) //
                .createAlias(propagatedAlias + ".state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", propagator.getId())) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .list();
    }
    
    public List<ResourceActionDTO> getResourcesFlaggedAsUrgent(Scope scope) {
        PrismScope scopeId = scope.getId();
        return (List<ResourceActionDTO>) sessionFactory.getCurrentSession().createCriteria(scopeId.getClass(), scopeId.getLowerCaseName()) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id"), "resourceId") //
                        .add(Projections.groupProperty("stateAction.action"), "action") //
                        .add(Projections.property("stateAction.notificationTemplate"), "notificationTemplate")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.raisesUrgentFlag", true)) //
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .addOrder(Order.asc("id")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceActionDTO.class)) //
                .list();
    }
    
    public List<StateChangeDTO> getRecentStateChanges(Scope scope, LocalDate baseline) {
        DateTime rangeStart = baseline.minusDays(1).toDateTimeAtStartOfDay();
        DateTime rangeClose = rangeStart.plusDays(1).minusSeconds(1);
        
        String resourceName = scope.getId().getLowerCaseName();
        
        return (List<StateChangeDTO>) sessionFactory.getCurrentSession().createCriteria(Comment.class, "comment") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty(resourceName), "resource") //
                        .add(Projections.groupProperty("state"), "state") //
                        .add(Projections.groupProperty("action"), "action")) //
                .add(Restrictions.isNotNull(resourceName)) //
                .add(Restrictions.between("createdTimestamp", rangeStart, rangeClose)) //
                .addOrder(Order.asc(resourceName)) //
                .addOrder(Order.asc("updatedTimestamp")) //
                .setResultTransformer(Transformers.aliasToBean(StateChangeDTO.class)) //
                .list();
    }

    public String getLastSequenceIdentifier(Resource resource, LocalDate baseline) {
        DateTime rangeStart = baseline.toDateTimeAtStartOfDay();
        DateTime rangeClose = rangeStart.plusDays(1).minusSeconds(1);
        
        return (String) sessionFactory.getCurrentSession().createCriteria(resource.getClass()) //
                .setProjection(Projections.max("sequenceIdentifier")) //
                .add(Restrictions.between("updatedTimestamp", rangeStart, rangeClose)) //
                .uniqueResult();
    }
    
    public <T extends Resource> List<Resource> getResourcesRequiringAttention(Class<T> resourceClass) {
        return (List<Resource>) sessionFactory.getCurrentSession().createCriteria(resourceClass) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.raisesUrgentFlag", true)) //
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY) //
                .list();
    }
    
    public <T extends Resource> List<Resource> getRecentlyUpdatedResources(Class<T> resourceClass, DateTime rangeStart, DateTime rangeClose) {
        return (List<Resource>) sessionFactory.getCurrentSession().createCriteria(resourceClass) //
                .add(Restrictions.between("updatedTimestamp", rangeStart, rangeClose)) //
                .list();
    }

}
