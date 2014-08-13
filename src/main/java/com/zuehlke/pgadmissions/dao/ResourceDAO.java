package com.zuehlke.pgadmissions.dao;

import java.util.HashMap;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.DurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
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
        model.put("queryRangeUnit", DurationUnit.YEARS.getSqlValue());
        model.put("queryRangeValue", resourceListYearsToRetrieve);
        model.put("queryRangeUnit", DurationUnit.YEARS.getSqlValue());
        model.put("rowIndex", loadIndex * resourceListRecordsToRetrieve);
        model.put("rowCount", resourceListRecordsToRetrieve);
        
        return (List<ResourceConsoleListRowDTO>) sessionFactory.getCurrentSession() //
                .createSQLQuery(freeMarkerHelper.buildString(resourceListSqlLocation, model)) //
                .addScalar("id", IntegerType.INSTANCE) //
                .addScalar("code", StringType.INSTANCE).addScalar("raisesUrgentFlag", BooleanType.INSTANCE) //
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
                .createAlias("stateAction.action", "action") //
                .add(Restrictions.eq("action", action)) //
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

}
