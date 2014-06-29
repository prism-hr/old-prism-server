package com.zuehlke.pgadmissions.dao;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.ResourceDynamic;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.DurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;

import freemarker.template.Template;

@Repository
public class ResourceDAO {

    private static final int RESOURCE_LIST_YEAR_RANGE = 2;

    private static final String RESOURCE_LIST_SELECT = "/resource/console_list.ftl";

    @Autowired
    private ScopeDAO scopeDAO;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public <T extends ResourceDynamic> List<ResourceConsoleListRowDTO> getConsoleListBlock(User user, Class<T> resourceType, int page, int perPage) {
        return (List<ResourceConsoleListRowDTO>) sessionFactory.getCurrentSession().createSQLQuery(getResourceListBlockSelect(user, resourceType, page, perPage))
                .addScalar("id", IntegerType.INSTANCE)
                .addScalar("code", StringType.INSTANCE)
                .addScalar("raisesUrgentFlag", BooleanType.INSTANCE)
                .addScalar("state", StringType.INSTANCE)
                .addScalar("creatorFirstName", StringType.INSTANCE)
                .addScalar("creatorFirstName2", StringType.INSTANCE)
                .addScalar("creatorFirstName3", StringType.INSTANCE)
                .addScalar("creatorLastName", StringType.INSTANCE)
                .addScalar("institutionTitle", StringType.INSTANCE)
                .addScalar("programTitle", StringType.INSTANCE)
                .addScalar("projectTitle", StringType.INSTANCE)
                .addScalar("displayTimestamp", DateType.INSTANCE)
                .addScalar("actions", StringType.INSTANCE)
                .addScalar("averageRating", BigDecimalType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ResourceConsoleListRowDTO.class)) //
                .list();
    }

    private <T extends ResourceDynamic> String getResourceListBlockSelect(User user, Class<T> resourceType, int page, int perPage) {
        String resourceTypeString = resourceType.getSimpleName();

        HashMap<String, Object> queryParameters = Maps.newHashMap();
        queryParameters.put("user", user);
        queryParameters.put("queryScope", resourceTypeString);
        queryParameters.put("parentScopes", scopeDAO.getParentScopesByType(resourceType));
        queryParameters.put("queryRangeValue", RESOURCE_LIST_YEAR_RANGE);
        queryParameters.put("queryRangeUnit", DurationUnit.YEARS.getSqlValue());
        queryParameters.put("rowIndex", page * perPage);
        queryParameters.put("rowCount", perPage);

        StringWriter writer = new StringWriter();

        try {
            Template resourceListSelect = freeMarkerConfigurer.getConfiguration().getTemplate(ResourceDAO.RESOURCE_LIST_SELECT);
            resourceListSelect.process(queryParameters, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new Error("Could not build " + resourceTypeString + " list", e);
        }
    }

    public <T extends Resource> void reassignState(Class<T> resourceClass, State state, State degradationState) {
        sessionFactory.getCurrentSession().createQuery( //
                "update " + PrismScope.getResourceScope(resourceClass).getLowerCaseName() + " " //
                    + "set state = :degradationState " //
                    + "where state = : state") //
                .setParameter("degradationState", degradationState) //
                .setParameter("state", state) // 
                .executeUpdate();
    }

}
