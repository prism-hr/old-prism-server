package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismLocale.getSystemLocale;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramType.getSystemProgramType;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.CustomizationDAO;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.display.DisplayCategory;
import com.zuehlke.pgadmissions.domain.display.DisplayProperty;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowResourceConfiguration;
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

    @Autowired
    private UserService userService;

    public DisplayCategory getDisplayCategoryById(PrismDisplayCategory id) {
        return entityService.getById(DisplayCategory.class, id);
    }

    public void createOrUpdateDisplayProperty(Resource resource, DisplayCategory category, PrismProgramType programType, PrismLocale locale,
            PrismDisplayProperty propertyIndex, String propertyValue) throws DeduplicationException {
        boolean systemDefault = ((category.getScope().getId().isProgramTypeConfigurationOwner() && programType == null) || programType == getSystemProgramType())
                && locale == getSystemLocale();
        DisplayProperty transientProperty = new DisplayProperty().withResource(resource).withProgramType(programType).withLocale(locale)
                .withDisplayCategory(category).withPropertyIndex(propertyIndex).withPropertyValue(propertyValue).withSystemDefault(systemDefault);
        entityService.createOrUpdate(transientProperty);
    }

    public List<DisplayProperty> getAllLocalizedProperties() {
        return entityService.list(DisplayProperty.class);
    }

    public <T extends WorkflowResourceConfiguration> T getConfiguration(Class<T> entityClass, Resource resource, String keyIndex, WorkflowDefinition keyValue) {
        return customizationDAO.getConfiguration(entityClass, resource, userService.getCurrentUser().getLocale(), keyIndex, keyValue);
    }

    public <T extends WorkflowResourceConfiguration> void restoreDefaultConfiguration(Class<T> entityClass, Resource resource, PrismProgramType programType,
            PrismLocale locale, String keyIndex, WorkflowDefinition keyValue) {
        if (Arrays.asList(INSTITUTION, PROGRAM).contains(resource.getResourceScope())) {
            T configuration = customizationDAO.getConfigurationToEdit(entityClass, resource, programType, locale, keyIndex, keyValue);
            if (configuration != null) {
                entityService.delete(configuration);
            }
        } else {
            throw new Error();
        }
    }

    public <T extends WorkflowResourceConfiguration> void restoreGlobalConfiguration(Class<T> entityClass, Resource resource, PrismProgramType programType,
            PrismLocale locale, String keyIndex, WorkflowDefinition keyValue) {
        if (Arrays.asList(SYSTEM, INSTITUTION).contains(resource.getResourceScope())) {
            customizationDAO.restoreGlobalConfiguration(entityClass, resource, programType, locale, keyIndex, keyValue);
        } else {
            throw new Error();
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
