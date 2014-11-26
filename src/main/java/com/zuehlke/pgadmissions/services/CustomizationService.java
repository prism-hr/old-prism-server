package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismLocale.getSystemLocale;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramType.getSystemProgramType;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Arrays;
import java.util.List;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.CustomizationDAO;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.exceptions.CustomizationException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.dto.WorkflowConfigurationDTO;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowConfigurationRepresentation;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

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
    private ApplicationContext applicationContext;

    @Autowired
    private Mapper mapper;

    public WorkflowConfiguration getConfiguration(PrismConfiguration configurationType, Resource resource, User user, WorkflowDefinition definition) {
        PrismScope resourceScope = resource.getResourceScope();
        PrismLocale locale = resourceScope == SYSTEM ? user.getLocale() : resource.getLocale();
        PrismProgramType programType = resourceScope.getPrecedence() > INSTITUTION.getPrecedence() ? resource.getProgram().getProgramType()
                .getPrismProgramType() : null;
        return getConfiguration(configurationType, resource, locale, programType, definition);
    }

    public WorkflowConfiguration getConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismProgramType programType,
            WorkflowDefinition definition) {
        return customizationDAO.getConfiguration(configurationType, resource, locale, programType, definition);
    }

    public List<DisplayPropertyConfiguration> getDisplayPropertyConfiguration(Resource resource, PrismScope scope,
            PrismDisplayPropertyCategory displayPropertyCategory, PrismLocale locale, PrismProgramType programType) {
        return customizationDAO.getDisplayPropertyConfiguration(resource, scope, displayPropertyCategory, locale, programType);
    }

    public WorkflowConfiguration getConfigurationWithVersion(PrismConfiguration configurationType, WorkflowDefinition definition, Integer version) {
        return customizationDAO.getConfigurationWithVersion(configurationType, definition, version);
    }

    public WorkflowConfigurationRepresentation getConfigurationRepresentation(PrismConfiguration configurationType, Resource resource, PrismLocale locale,
            PrismProgramType programType, WorkflowDefinition definition) {
        resource = getConfiguredResource(resource);
        WorkflowConfiguration configuration = getConfiguration(configurationType, resource, locale, programType, definition);
        WorkflowConfigurationRepresentation representation = mapper.map(configuration, configurationType.getConfigurationRepresentationClass());

        if (configurationType.isLocalizable()) {
            PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(resource, userService.getCurrentUser());
            localizeConfiguration(representation, definition, loader);
        }

        return representation;
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource resource, User user) {
        PrismScope resourceScope = resource.getResourceScope();
        PrismLocale locale = resourceScope == SYSTEM ? user.getLocale() : resource.getLocale();
        PrismProgramType programType = resourceScope.getPrecedence() > INSTITUTION.getPrecedence() ? resource.getProgram().getProgramType()
                .getPrismProgramType() : null;
        return getConfigurationRepresentations(configurationType, resource, resource.getResourceScope(), locale, programType);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource resource,
            PrismLocale locale, PrismProgramType programType, WorkflowDefinition definition) {
        resource = getConfiguredResource(resource);
        List<WorkflowConfiguration> configurations = customizationDAO.getConfigurations(configurationType, resource, locale, programType, definition);
        return parseRepresentations(resource, configurationType, configurations);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource resource, PrismScope scope,
            PrismLocale locale, PrismProgramType programType) {
        resource = getConfiguredResource(resource);
        List<WorkflowConfiguration> configurations = customizationDAO.getConfigurations(configurationType, resource, scope, locale, programType);
        return parseRepresentations(resource, configurationType, configurations);
    }

    public List<WorkflowConfiguration> getConfigurationsWithVersion(PrismConfiguration configurationType, Integer version) {
        return customizationDAO.getConfigurationsWithVersion(configurationType, version);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentationsWithVersion(Resource resource, PrismConfiguration configurationType,
            Integer version) {
        List<WorkflowConfiguration> configurations = getConfigurationsWithVersion(configurationType, version);
        return parseRepresentations(resource, configurationType, configurations);
    }

    public void restoreDefaultConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismProgramType programType,
            WorkflowDefinition definition) {
        customizationDAO.restoreDefaultConfiguration(configurationType, resource, locale, programType, definition);
    }

    public void restoreDefaultConfiguration(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismProgramType programType) {
        customizationDAO.restoreDefaultConfiguration(configurationType, resource, scope, locale, programType);
    }

    public List<WorkflowDefinition> getDefinitions(PrismConfiguration configurationType, PrismScope scope) {
        return (List<WorkflowDefinition>) customizationDAO.listDefinitions(configurationType, scope);
    }

    public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismProgramType programType,
            WorkflowDefinition definition) {
        customizationDAO.restoreGlobalConfiguration(configurationType, resource, locale, programType, definition);
    }

    public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismProgramType programType) {
        customizationDAO.restoreGlobalConfiguration(configurationType, resource, scope, locale, programType);
    }

    public void updateConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismProgramType programType,
            WorkflowDefinition definition, WorkflowConfigurationDTO workflowConfigurationDTO) throws CustomizationException, DeduplicationException,
            InstantiationException, IllegalAccessException {
        createOrUpdateConfiguration(configurationType, resource, locale, programType, workflowConfigurationDTO);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void createConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismProgramType programType, List<? extends WorkflowConfigurationDTO> valueDTOs) throws CustomizationException {
        if (configurationType.isVersioned()) {
            createOrUpdateConfigurationGroupVersion(configurationType, resource, scope, locale, programType, valueDTOs);
        } else {
            createOrUpdateConfigurationGroup(configurationType, resource, locale, programType, valueDTOs);
        }
    }

    public void createOrUpdateConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismProgramType programType, List<? extends WorkflowConfigurationDTO> workflowConfigurationGroupDTO) throws CustomizationException,
            DeduplicationException, InstantiationException, IllegalAccessException {
        List<WorkflowDefinition> definitions = getDefinitions(configurationType, scope);
        List<? extends WorkflowConfigurationDTO> valueDTOs = workflowConfigurationGroupDTO;

        if (configurationType.isValidateResponseSize() && (valueDTOs.isEmpty() || valueDTOs.size() != definitions.size())) {
            throw new Error();
        } else {
            createConfigurationGroup(configurationType, resource, scope, locale, programType, valueDTOs);
            resourceService.executeUpdate(resource,
                    PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
        }
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
        if (resourcePrecedence.equals(SYSTEM.getPrecedence()) && locale == null) {
            throw new CustomizationException("Tried to configure " + definition.getClass().getSimpleName() + ": " + definition.getId().toString()
                    + " with no locale. System scope configurations must specify locale.");
        } else if (resourcePrecedence > SYSTEM.getPrecedence() && locale != null) {
            throw new CustomizationException("Tried to configure " + definition.getClass().getSimpleName() + ": " + definition.getId().toString()
                    + " with locale. Only system scope configurations may specify locale.");
        } else if (resourcePrecedence < PROGRAM.getPrecedence() && definitionPrecedence > INSTITUTION.getPrecedence() && programType == null) {
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

    public WorkflowConfiguration createOrUpdateConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale,
            PrismProgramType programType, WorkflowConfigurationDTO workflowConfigurationDTO) throws CustomizationException {
        WorkflowConfiguration configuration = createConfiguration(configurationType, resource, locale, programType, workflowConfigurationDTO);
        return entityService.createOrUpdate(configuration);
    }

    private WorkflowConfiguration createConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale,
            PrismProgramType programType, WorkflowConfigurationDTO workflowConfigurationDTO) throws CustomizationException {
        WorkflowDefinition definition = entityService.getById(configurationType.getDefinitionClass(), workflowConfigurationDTO.getDefinitionId());
        validateConfiguration(resource, definition, locale, programType);
        WorkflowConfiguration configuration = mapper.map(workflowConfigurationDTO, configurationType.getConfigurationClass());
        configuration.setResource(resource);
        configuration.setLocale(locale);
        configuration.setProgramType(programType);
        ReflectionUtils.setProperty(configuration, configurationType.getDefinitionPropertyName(), definition);
        configuration.setSystemDefault(isSystemDefault(definition, locale, programType));
        return configuration;
    }

    private Resource getConfiguredResource(Resource resource) {
        return resource.getResourceScope().getPrecedence() > PrismScope.PROGRAM.getPrecedence() ? resource.getProgram() : resource;
    }

    private List<WorkflowConfigurationRepresentation> parseRepresentations(Resource resource, PrismConfiguration configurationType,
            List<WorkflowConfiguration> configurations) {
        List<WorkflowConfigurationRepresentation> representations = Lists.newLinkedList();
        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(resource, userService.getCurrentUser());

        if (configurations.isEmpty()) {
            return representations;
        } else {
            WorkflowConfiguration stereotype = configurations.get(0);

            Resource stereotypeResource = stereotype.getResource();
            PrismLocale stereotypeLocale = stereotype.getLocale();
            PrismProgramType stereotypeProgramType = stereotype.getProgramType();

            for (WorkflowConfiguration configuration : configurations) {
                if (Objects.equal(configuration.getResource(), stereotypeResource) && Objects.equal(configuration.getLocale(), stereotypeLocale)
                        && Objects.equal(configuration.getProgramType(), stereotypeProgramType)) {
                    WorkflowConfigurationRepresentation representation = mapper.map(configuration, configurationType.getConfigurationRepresentationClass());
                    if (configurationType.isLocalizable()) {
                        localizeConfiguration(representation, configuration.getDefinition(), loader);
                    }
                    representations.add(representation);
                }
            }

            return representations;
        }
    }

    private void createOrUpdateConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismProgramType programType,
            List<? extends WorkflowConfigurationDTO> valueDTOs) throws CustomizationException {
        for (WorkflowConfigurationDTO valueDTO : valueDTOs) {
            createOrUpdateConfiguration(configurationType, resource, locale, programType, valueDTO);
        }
    }

    private void createOrUpdateConfigurationGroupVersion(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismProgramType programType, List<? extends WorkflowConfigurationDTO> valueDTOs) throws CustomizationException {
        Integer version = null;
        restoreDefaultConfiguration(configurationType, resource, scope, locale, programType);
        for (WorkflowConfigurationDTO valueDTO : valueDTOs) {
            WorkflowConfiguration transientConfiguration = createConfiguration(configurationType, resource, locale, programType, valueDTO);
            ReflectionUtils.setProperty(transientConfiguration, "active", true);

            WorkflowConfiguration persistentConfiguration;
            if (version == null) {
                entityService.save(transientConfiguration);
                persistentConfiguration = transientConfiguration;
            } else {
                ReflectionUtils.setProperty(transientConfiguration, "version", version);
                persistentConfiguration = entityService.createOrUpdate(transientConfiguration);
            }

            version = version == null ? persistentConfiguration.getId() : version;
            ReflectionUtils.setProperty(persistentConfiguration, "version", version);
        }
    }

    private void localizeConfiguration(WorkflowConfigurationRepresentation representation, WorkflowDefinition definition, PropertyLoader loader) {
        if (ReflectionUtils.hasProperty(representation, "category")) {
            ReflectionUtils.setProperty(representation, "category", ReflectionUtils.getProperty(definition, "category"));
        }

        ReflectionUtils.setProperty(representation, "label",
                loader.load((PrismDisplayPropertyDefinition) ReflectionUtils.getProperty(definition.getId(), "label")));
        ReflectionUtils.setProperty(representation, "tooltip",
                loader.load((PrismDisplayPropertyDefinition) ReflectionUtils.getProperty(definition.getId(), "tooltip")));
    }

}
