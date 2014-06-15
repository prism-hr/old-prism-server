package com.zuehlke.pgadmissions.dao;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.PrismResourceDynamic;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.DurationUnit;
import com.zuehlke.pgadmissions.rest.domain.ResourceConsoleListRowRepresentation;

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
    public <T extends PrismResourceDynamic> List<ResourceConsoleListRowRepresentation> getConsoleListBlock(User user, Class<T> resourceType, int page, int perPage) {
        return (List<ResourceConsoleListRowRepresentation>) sessionFactory.getCurrentSession().createSQLQuery(getResourceListBlockSelect(user, resourceType, page, perPage))
                .setResultTransformer(Transformers.aliasToBean(ResourceConsoleListRowRepresentation.class)) //
                .list();
    }

    private <T extends PrismResourceDynamic> String getResourceListBlockSelect(User user, Class<T> resourceType, int page, int perPage) {
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

}
