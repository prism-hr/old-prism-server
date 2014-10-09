package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.CustomizationDAO;
import com.zuehlke.pgadmissions.domain.DisplayCategory;
import com.zuehlke.pgadmissions.domain.DisplayProperty;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.WorkflowDefinition;
import com.zuehlke.pgadmissions.domain.WorkflowResource;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;

@Service
@Transactional
public class CustomizationService {

    @Autowired
    private CustomizationDAO customizationDAO;

    @Autowired
    private EntityService entityService;

    @Autowired
    private SystemService systemService;

    public DisplayCategory getDisplayCategoryById(PrismDisplayCategory id) {
        return entityService.getById(DisplayCategory.class, id);
    }

    public void createOrUpdateDisplayProperty(Resource resource, PrismDisplayProperty propertyIndex, String propertyValue) throws DeduplicationException {
        createOrUpdateDisplayProperty(resource, null, propertyIndex, propertyValue);
    }

    public void createOrUpdateDisplayProperty(Resource resource, PrismLocale locale, PrismDisplayProperty propertyIndex, String propertyValue)
            throws DeduplicationException {
        boolean resourceIsSystem = resource.getResourceScope() == PrismScope.SYSTEM;
        PrismLocale defaultLocale = resourceIsSystem ? resource.getSystem().getLocale() : null;
        DisplayCategory category = getDisplayCategoryById(propertyIndex.getCategory());
        DisplayProperty transientProperty = new DisplayProperty().withResource(resource).withLocale(locale == null ? defaultLocale : locale)
                .withDisplayCategory(category).withPropertyIndex(propertyIndex).withPropertyValue(propertyValue)
                .withPropertyDefault(resourceIsSystem && locale == null || locale == defaultLocale ? true : false);
        entityService.createOrUpdate(transientProperty);
    }

    public List<DisplayProperty> getAllLocalizedProperties() {
        return entityService.list(DisplayProperty.class);
    }

    public <T extends WorkflowResource> T getConfiguration(Class<T> entityClass, Resource resource, String keyIndex, WorkflowDefinition keyValue) {
        return customizationDAO.getConfiguration(entityClass, resource, keyIndex, keyValue);
    }

    public <T extends WorkflowResource> T getConfigurationStrict(Class<NotificationConfiguration> entityClass, Resource resource, String keyIndex,
            WorkflowDefinition keyValue) {
        return customizationDAO.getConfigurationStrict(entityClass, resource, keyIndex, keyValue);
    }

    public <T extends WorkflowResource> void removeLocalizedConfiguration(Class<NotificationConfiguration> entityClass, Resource resource, String keyIndex,
            WorkflowDefinition keyValue) {
        T localizedConfiguration = getConfigurationStrict(entityClass, resource, keyIndex, keyValue);
        if (localizedConfiguration != null) {
            entityService.delete(localizedConfiguration);
        }
    }

    public <T extends WorkflowResource> void restoreGlobalizedConfiguration(Class<NotificationConfiguration> entityClass, Resource resource, String keyIndex,
            WorkflowDefinition keyValue) {
        T globalizedConfiguration = getConfigurationStrict(entityClass, resource, keyIndex, keyValue);

        Resource globalizedResource = globalizedConfiguration.getResource();
        PrismScope globalizedResourceScope = globalizedResource.getResourceScope();

        if (globalizedResourceScope == PrismScope.SYSTEM || globalizedResourceScope == PrismScope.INSTITUTION) {
            customizationDAO.restoreGlobalizedConfiguration(entityClass, keyIndex, keyValue, globalizedResource, globalizedResourceScope);
        }
    }

    public HashMap<PrismDisplayProperty, String> getLocalizedProperties(Resource resource, PrismLocale locale, PrismDisplayCategory category) {
        List<DisplayProperty> properties = customizationDAO.getDisplayProperties(resource, locale, category);
        HashMap<PrismDisplayProperty, String> propertiesMerged = Maps.newHashMap();
        for (DisplayProperty property : properties) {
            PrismDisplayProperty index = property.getPropertyIndex();
            if (!propertiesMerged.containsKey(index)) {
                propertiesMerged.put(index, property.getPropertyValue());
            }
        }
        return propertiesMerged;
    }

}
