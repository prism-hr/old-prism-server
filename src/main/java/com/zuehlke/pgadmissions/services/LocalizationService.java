package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.LocalizationDAO;
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
public class LocalizationService {

    @Autowired
    private LocalizationDAO localizationDAO;

    @Autowired
    private EntityService entityService;

    @Autowired
    private SystemService systemService;

    public void createOrUpdateDisplayProperty(Resource resource, DisplayCategory category, PrismDisplayProperty propertyIndex, String propertyValue)
            throws DeduplicationException {
        createOrUpdateDisplayProperty(resource, null, category, propertyIndex, propertyValue);
    }

    public void createOrUpdateDisplayProperty(Resource resource, PrismLocale locale, DisplayCategory category, PrismDisplayProperty propertyIndex,
            String propertyValue) throws DeduplicationException {
        boolean resourceIsSystem = resource.getResourceScope() == PrismScope.SYSTEM;
        PrismLocale defaultLocale = resourceIsSystem ? resource.getSystem().getLocale() : null;
        DisplayProperty transientProperty = new DisplayProperty().withResource(resource).withLocale(locale == null ? defaultLocale : locale)
                .withDisplayCategory(category).withPropertyIndex(propertyIndex).withPropertyValue(propertyValue)
                .withPropertyDefault(resourceIsSystem && locale == null || locale == defaultLocale ? true : false);
        entityService.createOrUpdate(transientProperty);
    }

    public List<DisplayProperty> getAllLocalizedProperties() {
        return entityService.list(DisplayProperty.class);
    }

    public <T extends WorkflowResource> T getConfiguration(Class<T> entityClass, Resource resource, String keyIndex, WorkflowDefinition keyValue) {
        return localizationDAO.getConfiguration(entityClass, resource, keyIndex, keyValue);
    }

    public <T extends WorkflowResource> T getConfigurationStrict(Class<NotificationConfiguration> entityClass, Resource resource, String keyIndex,
            WorkflowDefinition keyValue) {
        return localizationDAO.getConfigurationStrict(entityClass, resource, keyIndex, keyValue);
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
            localizationDAO.restoreGlobalizedConfiguration(entityClass, keyIndex, keyValue, globalizedResource, globalizedResourceScope);
        }
    }

    public String getLocalizedProperty(Resource resource, PrismDisplayProperty propertyIndex) {
        return getLocalizedProperty(resource, resource.getLocale(), propertyIndex);
    }

    public String getLocalizedProperty(Resource resource, PrismLocale locale, PrismDisplayProperty propertyIndex) {
        return getLocalizedProperties(resource, locale, propertyIndex).get(propertyIndex);
    }

    public HashMap<PrismDisplayProperty, String> getLocalizedProperties(Resource resource, PrismLocale locale, PrismDisplayProperty... propertyIndices) {
        List<DisplayProperty> properties = localizationDAO.getDisplayProperties(resource, locale, propertyIndices);
        return filterProperties(properties);
    }

    public HashMap<PrismDisplayProperty, String> getLocalizedProperties(Resource resource, PrismLocale locale, PrismDisplayCategory category) {
        return getLocalizedProperties(resource, locale, category);
    }

    public HashMap<PrismDisplayProperty, String> getLocalizedProperties(Resource resource, PrismLocale locale, PrismDisplayCategory... categories) {
        List<DisplayProperty> properties = localizationDAO.getDisplayProperties(resource, locale, categories);
        return filterProperties(properties);
    }

    private HashMap<PrismDisplayProperty, String> filterProperties(List<DisplayProperty> properties) {
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