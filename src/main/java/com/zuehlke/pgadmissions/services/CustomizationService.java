package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismLocale.getSystemLocale;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramType.getSystemProgramType;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.CustomizationDAO;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.exceptions.CustomizationException;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowConfigurationRepresentation;

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

    @Autowired
    private UserService userService;

    @Autowired
    private Mapper dozerBeanMapper;
    
    public List<DisplayPropertyConfiguration> getConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType,
            PrismDisplayPropertyCategory displayPropertyCategory, PrismScope propertyScope) {
        return customizationDAO.getConfiguration(resource, locale, programType, displayPropertyCategory, propertyScope);
    }
    
    public <T extends WorkflowConfiguration, U extends WorkflowDefinition> T getConfiguration(Resource resource, User user, Class<T> configurationClass,
            U definition) {
        if (definition != null) {
            PrismScope resourceScope = resource.getResourceScope();
            PrismLocale locale = resourceScope == SYSTEM ? user.getLocale() : resource.getLocale();
            PrismProgramType programType = resourceScope.getPrecedence() > INSTITUTION.getPrecedence() ? resource.getProgram().getProgramType()
                    .getPrismProgramType() : null;
            return customizationDAO.getConfiguration(resource, locale, programType, configurationClass, definition);
        }
        return null;
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition> T getConfiguration(Resource resource, PrismLocale locale,
            PrismProgramType programType, Class<T> configurationClass, U definition) {
        return customizationDAO.getConfiguration(resource, locale, programType, configurationClass, definition);
    }

    public <T extends WorkflowConfiguration> List<T> getConfigurationVersion(Class<T> configurationClass, Integer version) {
        return customizationDAO.getConfigurationVersion(configurationClass, version);
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition> void restoreDefaultConfiguration(Resource resource, PrismLocale locale,
            PrismProgramType programType, Class<T> configurationClass, Class<U> definitionClass, PrismScope definitionScope) {
        customizationDAO.restoreDefaultConfiguration(resource, locale, programType, configurationClass, definitionClass, definitionScope);
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition> void restoreDefaultConfiguration(Resource resource, PrismLocale locale,
            PrismProgramType programType, Class<T> configurationClass, U definition) {
        customizationDAO.restoreDefaultConfiguration(resource, locale, programType, configurationClass, definition);
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition> void restoreDefaultConfigurationVersion(Resource resource, PrismLocale locale,
            PrismProgramType programType, Class<T> configurationClass, Class<U> definitionClass, PrismScope definitionScope) {
        customizationDAO.restoreDefaultConfigurationVersion(resource, locale, programType, configurationClass, definitionClass, definitionScope);
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition> void restoreDefaultConfigurationVersion(Resource resource, PrismLocale locale,
            PrismProgramType programType, Class<T> entityClass, U definition) {
        customizationDAO.restoreDefaultConfigurationVersion(resource, locale, programType, entityClass, definition);
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition> void restoreGlobalConfiguration(Resource resource, PrismLocale locale,
            PrismProgramType programType, Class<T> configurationClass, Class<U> definitionClass, PrismScope definitionScope) {
        customizationDAO.restoreGlobalConfiguration(resource, locale, programType, configurationClass, definitionClass, definitionScope);
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition> void restoreGlobalConfiguration(Resource resource, PrismLocale locale,
            PrismProgramType programType, Class<T> configurationClass, U definition) {
        customizationDAO.restoreGlobalConfiguration(resource, locale, programType, configurationClass, definition);
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition> void restoreGlobalConfigurationVersion(Resource resource, PrismLocale locale,
            PrismProgramType programType, Class<T> configurationClass, Class<U> definitionClass, PrismScope definitionScope) {
        customizationDAO.restoreGlobalConfigurationVersion(resource, locale, programType, configurationClass, definitionClass, definitionScope);
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition> void restoreGlobalConfigurationVersion(Resource resource, PrismLocale locale,
            PrismProgramType programType, Class<T> configurationClass, U definition) {
        customizationDAO.restoreGlobalConfigurationVersion(resource, locale, programType, configurationClass, definition);
    }

    public <T extends WorkflowDefinition> List<T> listDefinitions(Class<T> entityClass, PrismScope scope) {
        return (List<T>) customizationDAO.listDefinitions(entityClass, scope);
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition, V extends WorkflowConfigurationRepresentation> List<WorkflowConfigurationRepresentation> //
    getConfigurationRepresentations(Resource resource, Class<T> configurationClass, Class<U> definitionClass, Class<V> representationClass)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PrismScope resourceScope = resource.getResourceScope();
        PrismLocale locale = resourceScope == SYSTEM ? userService.getCurrentUser().getLocale() : resource.getLocale();
        PrismProgramType programType = resourceScope.getPrecedence() > INSTITUTION.getPrecedence() ? resource.getProgram().getProgramType()
                .getPrismProgramType() : null;
        return getConfigurationRepresentations(resource, locale, programType, configurationClass, definitionClass, representationClass,
                resource.getResourceScope());
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition, V extends WorkflowConfigurationRepresentation> List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(
            Resource resource, PrismLocale locale, PrismProgramType programType, Class<T> configurationClass, Class<U> definitionClass,
            Class<V> representationClass, PrismScope definitionScope) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        resource = resource.getResourceScope().getPrecedence() > PrismScope.PROGRAM.getPrecedence() ? resource.getProgram() : resource;
        List<T> configurations = listConfigurations(resource, locale, programType, configurationClass, definitionClass, definitionScope);
        return parseRepresentations(configurations, definitionClass, representationClass);
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition, V extends WorkflowConfigurationRepresentation> List<WorkflowConfigurationRepresentation> getVersionedConfigurationRepresentations(
            Resource resource, PrismLocale locale, PrismProgramType programType, Class<T> configurationClass, Class<U> definitionClass,
            Class<V> representationClass, PrismScope definitionScope) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        resource = resource.getResourceScope().getPrecedence() > PrismScope.PROGRAM.getPrecedence() ? resource.getProgram() : resource;
        List<T> configurations = listVersionedConfigurations(resource, locale, programType, configurationClass, definitionClass, definitionScope);
        return parseRepresentations(configurations, definitionClass, representationClass);
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

    private <T extends WorkflowConfiguration, U extends WorkflowDefinition> List<T> listConfigurations(Resource resource, PrismLocale locale,
            PrismProgramType programType, Class<T> configurationClass, Class<U> definitionClass, PrismScope definitionScope) {
        List<T> configurations = customizationDAO.listConfigurations(resource, locale, programType, configurationClass, definitionClass, definitionScope);
        return parseConfigurations(configurations);
    }

    private <T extends WorkflowConfiguration, U extends WorkflowDefinition> List<T> listVersionedConfigurations(Resource resource, PrismLocale locale,
            PrismProgramType programType, Class<T> configurationClass, Class<U> definitionClass, PrismScope definitionScope) {
        List<T> configurations = customizationDAO.listVersionedConfigurations(resource, locale, programType, configurationClass, definitionClass,
                definitionScope);
        return parseConfigurations(configurations);
    }

    private <T extends WorkflowConfiguration> List<T> parseConfigurations(List<T> configurations) {
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

    private <T extends WorkflowConfiguration, U extends WorkflowDefinition, V extends WorkflowConfigurationRepresentation> List<WorkflowConfigurationRepresentation> parseRepresentations(
            List<T> configurations, Class<U> definitionClass, Class<V> representationClass) throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        List<WorkflowConfigurationRepresentation> representations = Lists.newArrayListWithCapacity(configurations.size());
        String definitionPropertyName = WordUtils.uncapitalize(definitionClass.getSimpleName());
        for (WorkflowConfiguration configuration : configurations) {
            WorkflowDefinition workflowDefinition = (WorkflowDefinition) PropertyUtils.getSimpleProperty(configuration, definitionPropertyName);
            WorkflowConfigurationRepresentation representation = dozerBeanMapper.map(configuration, representationClass);
            representation.setDefinitionId(workflowDefinition.getId());
            representations.add(representation);
        }
        return representations;
    }

}
