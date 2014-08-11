package com.zuehlke.pgadmissions.dao;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

import org.hibernate.SessionFactory;
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
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.DurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;

import freemarker.template.Template;

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
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private SessionFactory sessionFactory;

    public <T extends Resource> List<ResourceConsoleListRowDTO> getConsoleListBlock(User user, Class<T> resourceClass, List<Scope> parentScopes, int loadIndex) {
        return (List<ResourceConsoleListRowDTO>) sessionFactory.getCurrentSession() //
                .createSQLQuery(getResourceListBlockSelect(user, resourceClass, parentScopes, loadIndex)) //
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
    
    public <T extends Resource> Integer getVisibleResourceWithUpdateCount(Class<T> resourceClass, Scope roleScope, User user, LocalDate baseline) {
        String resourceReference = PrismScope.getResourceScope(resourceClass).getLowerCaseName();
        DateTime rangeStart = baseline.minusDays(1).toDateTimeAtStartOfDay();
        DateTime rangeClose = rangeStart.plusDays(1).minusSeconds(1);
        
        return (Integer) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.countDistinct(resourceReference + ".id")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("userRole." + resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .add(Restrictions.eqProperty("state", resourceReference + ".state")) //
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.between(resourceReference + ".updatedTimestamp", rangeStart, rangeClose)) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .uniqueResult(); 
    }

    private <T extends Resource> String getResourceListBlockSelect(User user, Class<T> resourceClass, List<Scope> parentScopes, int loadIndex) {
        HashMap<String, Object> queryParameters = Maps.newHashMap();
        queryParameters.put("user", user);
        queryParameters.put("queryScope", PrismScope.getResourceScope(resourceClass).getLowerCaseName());
        queryParameters.put("parentScopes", parentScopes);
        queryParameters.put("queryRangeValue", resourceListYearsToRetrieve);
        queryParameters.put("queryRangeUnit", DurationUnit.YEARS.getSqlValue());
        queryParameters.put("rowIndex", loadIndex * resourceListRecordsToRetrieve);
        queryParameters.put("rowCount", resourceListRecordsToRetrieve);

        StringWriter writer = new StringWriter();

        try {
            Template resourceListSelect = freeMarkerConfigurer.getConfiguration().getTemplate(resourceListSqlLocation);
            resourceListSelect.process(queryParameters, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
