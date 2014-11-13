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
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowResourceConfiguration;
import com.zuehlke.pgadmissions.exceptions.CustomizationException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;

@Service
@Transactional
public class CustomizationService {

    @Autowired
    private CustomizationDAO customizationDAO;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private SystemService systemService;

    public DisplayPropertyDefinition getDisplayPropertyDefinitionById(PrismDisplayProperty id) {
        return entityService.getById(DisplayPropertyDefinition.class, id);
    }

    public void updateDisplayPropertyConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType,
            DisplayPropertyDefinition displayProperty, String value) throws DeduplicationException, CustomizationException {
        createOrUpdateDisplayPropertyConfiguration(resource, locale, programType, displayProperty, value);
        resourceService.executeUpdate(resource, PrismDisplayProperty.valueOf(resource.getResourceScope().name() + "_COMMENT_UPDATED_DISPLAY_PROPERTY"));
    }

    public void createOrUpdateDisplayPropertyConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType,
            DisplayPropertyDefinition displayProperty, String value) throws DeduplicationException, CustomizationException {
        validateConfiguration(resource, displayProperty, locale, programType);
        DisplayPropertyConfiguration transientConfiguration = new DisplayPropertyConfiguration().withResource(resource).withProgramType(programType)
                .withLocale(locale).withDisplayPropertyDefinition(displayProperty).withValue(value)
                .withSystemDefault(isSystemDefault(displayProperty, locale, programType));
        entityService.createOrUpdate(transientConfiguration);
    }

    public void restoreDefaultDisplayPropertyConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType,
            DisplayPropertyDefinition definition) throws DeduplicationException {
        restoreDefaultConfiguration(DisplayPropertyConfiguration.class, resource, locale, programType, "displayPropertyDefinition", definition);
        resourceService
                .executeUpdate(resource, PrismDisplayProperty.valueOf(resource.getResourceScope().name() + "_COMMENT_RESTORED_DISPLAY_PROPERTY_DEFAULT"));
    }

    public void restoreGlobalDisplayPropertyConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType,
            DisplayPropertyDefinition definition) throws DeduplicationException {
        restoreGlobalConfiguration(DisplayPropertyConfiguration.class, resource, locale, programType, "displayPropertyDefinition", definition);
        resourceService.executeUpdate(resource, PrismDisplayProperty.valueOf(resource.getResourceScope().name() + "_COMMENT_RESTORED_DISPLAY_PROPERTY_GLOBAL"));
    }

    public <T extends WorkflowResourceConfiguration> T getConfiguration(Class<T> entityClass, Resource resource, PrismLocale locale,
            PrismProgramType programType, String keyIndex, WorkflowDefinition keyValue) {
        return customizationDAO.getConfiguration(entityClass, resource, locale, programType, keyIndex, keyValue);
    }

    public <T extends WorkflowResourceConfiguration> T getConfiguration(Class<T> entityClass, Resource resource, User user, String keyIndex,
            WorkflowDefinition keyValue) {
        PrismScope resourceScope = resource.getResourceScope();
        PrismLocale locale = resourceScope == SYSTEM ? user.getLocale() : resource.getLocale();
        PrismProgramType programType = resourceScope.getPrecedence() > INSTITUTION.getPrecedence() ? resource.getProgram().getProgramType()
                .getPrismProgramType() : null;
        return customizationDAO.getConfiguration(entityClass, resource, locale, programType, keyIndex, keyValue);
    }

    public <T extends WorkflowResourceConfiguration> T getConfigurationStrict(Class<T> entityClass, Resource resource, PrismLocale locale,
            PrismProgramType programType, String keyIndex, WorkflowDefinition keyValue) {
        return customizationDAO.getConfigurationStrict(entityClass, resource, locale, programType, keyIndex, keyValue);
    }

    public <T extends WorkflowResourceConfiguration> void restoreDefaultConfiguration(Class<T> entityClass, Resource resource, PrismLocale locale,
            PrismProgramType programType, String keyIndex, WorkflowDefinition keyValue) {
        if (Arrays.asList(INSTITUTION, PROGRAM).contains(resource.getResourceScope())) {
            T configuration = getConfigurationStrict(entityClass, resource, locale, programType, keyIndex, keyValue);
            if (configuration != null) {
                entityService.delete(configuration);
            }
        } else {
            throw new Error();
        }
    }

    public <T extends WorkflowResourceConfiguration> void restoreGlobalConfiguration(Class<T> entityClass, Resource resource, PrismLocale locale,
            PrismProgramType programType, String keyIndex, WorkflowDefinition keyValue) {
        if (Arrays.asList(SYSTEM, INSTITUTION).contains(resource.getResourceScope())) {
            customizationDAO.restoreGlobalConfiguration(entityClass, resource, locale, programType, keyIndex, keyValue);
        } else {
            throw new Error();
        }
    }

    public HashMap<PrismDisplayProperty, String> getDisplayProperties(Resource resource, PrismLocale locale, PrismProgramType programType,
            PrismDisplayPropertyCategory category, PrismScope propertyScope) {
        List<DisplayPropertyConfiguration> displayValues = customizationDAO.getDisplayProperties(resource, locale, programType, category, propertyScope);
        HashMap<PrismDisplayProperty, String> displayProperties = Maps.newHashMap();
        for (DisplayPropertyConfiguration displayValue : displayValues) {
            PrismDisplayProperty displayPropertyId = (PrismDisplayProperty) displayValue.getDisplayPropertyDefinition().getId();
            if (!displayProperties.containsKey(displayPropertyId)) {
                displayProperties.put(displayPropertyId, displayValue.getValue());
            }
        }
        return displayProperties;
    }

    public boolean isSystemDefault(WorkflowDefinition definition, PrismLocale locale, PrismProgramType programType) {
        if (locale == getSystemLocale()) {
            Integer precedence = definition.getScope().getPrecedence();
            if (precedence > INSTITUTION.getPrecedence() && programType == getSystemProgramType()) {
                return true;
            } else if (precedence < PROGRAM.getPrecedence() && programType == null) {
                return true;
            }
        }
        return false;
    }

    public void validateConfiguration(Resource resource, WorkflowDefinition definition, PrismLocale locale, PrismProgramType programType)
            throws CustomizationException {
        Integer resourcePrecedence = resource.getResourceScope().getPrecedence();
        Integer definitionPrecedence = definition.getScope().getPrecedence();
        if (resourcePrecedence == SYSTEM.getPrecedence() && locale == null) {
            throw new CustomizationException("Tried to configure " + definition.getClass().getSimpleName() + ": " + definition.getId().toString()
                    + " with no locale. System scope configurations must specify locale.");
        } else if (resourcePrecedence > SYSTEM.getPrecedence() && locale != null) {
            throw new CustomizationException("Tried to configure " + definition.getClass().getSimpleName() + ": " + definition.getId().toString()
                    + " with locale. On system scope configurations may specify locale.");
        } else if (definitionPrecedence > INSTITUTION.getPrecedence() && programType == null) {
            throw new CustomizationException("Tried to configure " + definition.getClass().getSimpleName() + ": " + definition.getId().toString()
                    + " with no program type. Scopes within program must specify program type.");
        } else if (definitionPrecedence < PROGRAM.getPrecedence() && programType != null) {
            throw new CustomizationException("Tried to configure " + definition.getClass().getSimpleName() + ": " + definition.getId().toString()
                    + " with program type. Only scopes within program may specify program type.");
        }
    }

    public List<DisplayPropertyConfiguration> getAllLocalizedProperties() {
        return entityService.list(DisplayPropertyConfiguration.class);
    }

}
