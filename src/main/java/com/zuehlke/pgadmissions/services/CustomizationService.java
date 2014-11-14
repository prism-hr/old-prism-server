package com.zuehlke.pgadmissions.services;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.CustomizationDAO;
import com.zuehlke.pgadmissions.domain.definitions.*;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.exceptions.CustomizationException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.zuehlke.pgadmissions.domain.definitions.PrismLocale.getSystemLocale;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramType.getSystemProgramType;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;

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

    public <T extends WorkflowConfiguration> T getConfiguration(Class<T> entityClass, Resource resource, PrismLocale locale, PrismProgramType programType,
                                                                String keyIndex, WorkflowDefinition keyValue) {
        return customizationDAO.getConfiguration(entityClass, resource, locale, programType, keyIndex, keyValue);
    }

    public <T extends WorkflowConfiguration> T getConfiguration(Class<T> entityClass, Resource resource, User user, String definitionReference,
                                                                WorkflowDefinition definition) {
        if (definition != null) {
            PrismScope resourceScope = resource.getResourceScope();
            PrismLocale locale = resourceScope == SYSTEM ? user.getLocale() : resource.getLocale();
            PrismProgramType programType = resourceScope.getPrecedence() > INSTITUTION.getPrecedence() ? resource.getProgram().getProgramType()
                    .getPrismProgramType() : null;
            return customizationDAO.getConfiguration(entityClass, resource, locale, programType, definitionReference, definition);
        }
        return null;
    }

    public <T extends WorkflowDefinition> List<Enum<?>> listDefinitions(Class<T> entityClass, PrismScope scope) {
        return customizationDAO.listDefinitions(entityClass, scope);
    }

    public <T extends WorkflowConfiguration> List<T> listConfigurations(WorkflowResourceConfigurationType configurationType, Resource resource, PrismLocale locale, PrismProgramType programType) {
        List<T> configurations = customizationDAO.listConfigurations(configurationType, resource, locale, programType);

        if (configurations.isEmpty()) {
            return configurations;
        } else {
            T stereotype = configurations.get(0);

            Resource stereotypeResource = stereotype.getResource();
            PrismLocale stereotypeLocale = stereotype.getLocale();
            PrismProgramType stereotypeProgramType = stereotype.getProgramType();

            List<T> filteredConfigurations = Lists.newLinkedList();

            for (T configuration : configurations) {
                if (Objects.equal(configuration.getResource(), stereotypeResource) && Objects.equal(configuration.getLocale(), stereotypeLocale)
                        && Objects.equal(configuration.getProgramType(), stereotypeProgramType)) {
                    filteredConfigurations.add(configuration);
                }
            }

            return filteredConfigurations;
        }
    }

    public <T extends WorkflowConfiguration> void restoreDefaultConfiguration(Class<T> entityClass, Resource resource, PrismLocale locale,
                                                                              PrismProgramType programType) throws CustomizationException {
        validateRestoreDefaultConfiguration(resource, locale, programType);
        customizationDAO.restoreDefaultConfiguration(entityClass, resource, locale, programType);
    }

    public <T extends WorkflowConfiguration> void restoreGlobalConfiguration(Class<T> entityClass, Resource resource, PrismLocale locale,
                                                                             PrismProgramType programType) throws CustomizationException {
        validateRestoreGlobalConfiguration(resource, locale, programType);
        customizationDAO.restoreGlobalConfiguration(entityClass, resource, locale, programType);
    }

    public HashMap<PrismDisplayProperty, String> getDisplayProperties(Resource resource, PrismLocale locale, PrismProgramType programType,
                                                                      PrismDisplayPropertyCategory displayPropertyCategory, PrismScope propertyScope) {
        List<DisplayPropertyConfiguration> displayValues = customizationDAO.getDisplayProperties(resource, locale, programType, displayPropertyCategory,
                propertyScope);
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

    public void validateRestoreDefaultConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType) throws CustomizationException {
        if (!Arrays.asList(INSTITUTION, PROGRAM).contains(resource.getResourceScope())) {
            throw new CustomizationException("Tried to restore default configurations as a system level entity");
        }
    }

    public void validateRestoreGlobalConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType) throws CustomizationException {
        if (!Arrays.asList(SYSTEM, INSTITUTION).contains(resource.getResourceScope())) {
            throw new CustomizationException("Tried to restore global configurations as a program level entity");
        }
    }

    public List<DisplayPropertyConfiguration> getAllLocalizedProperties() {
        return entityService.list(DisplayPropertyConfiguration.class);
    }

}
