package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismAdvertType.getSystemAdvertType;
import static com.zuehlke.pgadmissions.domain.definitions.PrismLocale.getSystemLocale;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.CustomizationDAO;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertType;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfigurationVersioned;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.exceptions.CustomizationException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.dto.WorkflowConfigurationDTO;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowConfigurationRepresentation;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

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

    public WorkflowDefinition getDefinitionById(PrismConfiguration configurationType, Enum<?> id) {
        return entityService.getById(configurationType.getDefinitionClass(), id);
    }

    public WorkflowConfiguration getConfiguration(PrismConfiguration configurationType, Resource resource, User user, WorkflowDefinition definition) {
        PrismScope resourceScope = resource.getResourceScope();
        PrismLocale locale = resourceScope == SYSTEM ? user.getLocale() : resource.getLocale();
        PrismAdvertType advertType = resourceScope.ordinal() > INSTITUTION.ordinal() ? resource.getProgram().getAdvert().getAdvertType().getPrismAdvertType()
                : null;
        return getConfiguration(configurationType, resource, locale, advertType, definition);
    }

    public Integer getActiveConfigurationVersion(PrismConfiguration configurationType, Resource resource, PrismScope scope) {
        PrismScope resourceScope = resource.getResourceScope();
        PrismLocale locale = userService.getCurrentUser() != null ? userService.getCurrentUser().getLocale() : resource.getLocale();
        PrismAdvertType advertType = resourceScope.ordinal() > INSTITUTION.ordinal() ? resource.getProgram().getAdvert().getAdvertType()
                .getPrismAdvertType() : null;
        return customizationDAO.getActiveConfigurationVersion(configurationType, resource, locale, advertType, scope);
    }

    public WorkflowConfiguration getConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismAdvertType advertType,
            WorkflowDefinition definition) {
        return customizationDAO.getConfiguration(configurationType, resource, locale, advertType, definition, false);
    }

    public WorkflowConfiguration getConfigurationWithVersion(PrismConfiguration configurationType, Enum<?> definitionId, Integer version) {
        WorkflowDefinition definition = getDefinitionById(configurationType, definitionId);
        return customizationDAO.getConfigurationWithVersion(configurationType, definition, version);
    }

    public WorkflowConfiguration getConfigurationWithVersion(PrismConfiguration configurationType, WorkflowDefinition definition, Integer version) {
        return customizationDAO.getConfigurationWithVersion(configurationType, definition, version);
    }

    public WorkflowConfigurationRepresentation getConfigurationRepresentation(PrismConfiguration configurationType, Resource resource, PrismLocale locale,
            PrismAdvertType advertType, WorkflowDefinition definition) throws Exception {
        Resource configuredResource = getConfiguredResource(resource);
        PrismAdvertType configuredadvertType = getConfiguredadvertType(resource, advertType);

        WorkflowConfiguration configuration = getConfiguration(configurationType, configuredResource, locale, configuredadvertType, definition);
        WorkflowConfigurationRepresentation representation = mapper.map(configuration, configurationType.getConfigurationRepresentationClass());

        return representation;
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource resource, User user)
            throws Exception {
        PrismScope resourceScope = resource.getResourceScope();
        PrismLocale locale = resourceScope == SYSTEM ? user.getLocale() : resource.getLocale();
        PrismAdvertType advertType = resourceScope.ordinal() > INSTITUTION.ordinal() ? resource.getProgram().getAdvert().getAdvertType()
                .getPrismAdvertType() : null;
        return getConfigurationRepresentations(configurationType, resource, resource.getResourceScope(), locale, advertType);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource resource,
            PrismLocale locale, PrismAdvertType advertType, WorkflowDefinition definition) throws Exception {
        Resource configuredResource = getConfiguredResource(resource);
        PrismAdvertType configuredadvertType = getConfiguredadvertType(resource, advertType);

        List<WorkflowConfiguration> configurations = customizationDAO.getConfigurations(configurationType, configuredResource, locale, configuredadvertType,
                definition, false);
        return parseRepresentations(resource, configurationType, configurations);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource resource, PrismScope scope,
            PrismLocale locale, PrismAdvertType advertType) throws Exception {
        Resource configuredResource = getConfiguredResource(resource);
        PrismAdvertType configuredadvertType = getConfiguredadvertType(resource, advertType);
        List<WorkflowConfiguration> configurations = customizationDAO.getConfigurations(configurationType, configuredResource, scope, locale,
                configuredadvertType, false);
        return parseRepresentations(resource, configurationType, configurations);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource resource, PrismScope scope,
            PrismLocale locale, PrismAdvertType advertType, Enum<?> category) throws Exception {
        return getConfigurationRepresentations(configurationType, resource, scope, locale, advertType, category, false);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentationsTranslationMode(PrismConfiguration configurationType, Resource resource,
            PrismScope scope,
            PrismLocale locale, PrismAdvertType advertType, Enum<?> category) throws Exception {
        return getConfigurationRepresentations(configurationType, resource, scope, locale, advertType, category, true);
    }

    public List<WorkflowConfiguration> getConfigurationsWithVersion(PrismConfiguration configurationType, Integer version) {
        return customizationDAO.getConfigurationsWithVersion(configurationType, version);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentationsWithVersion(Resource resource, PrismConfiguration configurationType,
            Integer version) throws Exception {
        List<WorkflowConfiguration> configurations = getConfigurationsWithVersion(configurationType, version);
        return parseRepresentations(resource, configurationType, configurations);
    }

    public WorkflowConfiguration getConfigurationWithOrWithoutVersion(PrismConfiguration configurationType, Resource resource, User user, Enum<?> definitionId,
            Integer configurationVersion) {
        WorkflowPropertyDefinition definition = (WorkflowPropertyDefinition) getDefinitionById(configurationType, definitionId);

        WorkflowPropertyConfiguration configuration;
        if (configurationVersion == null) {
            configuration = (WorkflowPropertyConfiguration) getConfiguration(configurationType, resource, user, definition);
        } else {
            configuration = (WorkflowPropertyConfiguration) getConfigurationWithVersion(configurationType, definition, configurationVersion);
        }

        return configuration;
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentationsWithOrWithoutVersion(PrismConfiguration configurationType,
            Resource resource, Integer configurationVersion) throws Exception {
        if (configurationVersion == null) {
            return getConfigurationRepresentations(configurationType, resource, userService.getCurrentUser());
        } else {
            return getConfigurationRepresentationsWithVersion(resource, configurationType, configurationVersion);
        }
    }

    public List<WorkflowDefinition> getDefinitions(PrismConfiguration configurationType, PrismScope scope) {
        return customizationDAO.listDefinitions(configurationType, scope);
    }

    public void restoreDefaultConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismAdvertType advertType,
            Enum<?> definitionId) {
        customizationDAO.restoreDefaultConfiguration(configurationType, resource, locale, advertType, definitionId);
    }

    public void restoreDefaultConfiguration(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismAdvertType advertType) {
        customizationDAO.restoreDefaultConfiguration(configurationType, resource, scope, locale, advertType);
    }

    public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismAdvertType advertType,
            Enum<?> definitionId) throws Exception {
        customizationDAO.restoreGlobalConfiguration(configurationType, resource, locale, advertType, definitionId);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismAdvertType advertType) throws Exception {
        customizationDAO.restoreGlobalConfiguration(configurationType, resource, scope, locale, advertType);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void createOrUpdateConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismAdvertType advertType, List<? extends WorkflowConfigurationDTO> workflowConfigurationGroupDTO) throws Exception {
        List<WorkflowDefinition> definitions = getDefinitions(configurationType, scope);

        if (configurationType.isValidateResponseSize()
                && (workflowConfigurationGroupDTO.isEmpty() || workflowConfigurationGroupDTO.size() != definitions.size())) {
            throw new Error();
        }

        createConfigurationGroup(configurationType, resource, scope, locale, advertType, workflowConfigurationGroupDTO);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void createOrUpdateConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismAdvertType advertType,
            Enum<?> definitionId, List<? extends WorkflowConfigurationDTO> workflowConfigurationGroupDTO) throws Exception {

        createConfigurationGroup(configurationType, resource, locale, advertType, definitionId, workflowConfigurationGroupDTO);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
    }

    public void createConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismAdvertType advertType, List<? extends WorkflowConfigurationDTO> valueDTOs) throws CustomizationException, DeduplicationException,
            InstantiationException, IllegalAccessException {
        if (configurationType.isVersioned()) {
            createOrUpdateConfigurationGroupVersion(configurationType, resource, scope, locale, advertType, valueDTOs);
        } else {
            createOrUpdateConfigurationGroup(configurationType, resource, locale, advertType, valueDTOs);
        }
    }

    public void createConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismAdvertType advertType,
            Enum<?> definitionId, List<? extends WorkflowConfigurationDTO> workflowConfigurationGroupDTO) throws Exception {
        if (configurationType.isVersioned()) {
            createOrUpdateConfigurationGroupVersion(configurationType, resource, locale, advertType, definitionId, workflowConfigurationGroupDTO);
        } else {
            createOrUpdateConfigurationGroup(configurationType, resource, locale, advertType, definitionId, workflowConfigurationGroupDTO);
        }
    }

    public boolean isSystemDefault(WorkflowDefinition definition, PrismLocale locale, PrismAdvertType advertType) {
        if (locale == getSystemLocale()) {
            Integer precedence = definition.getScope().getOrdinal();
            if (precedence > INSTITUTION.ordinal() && advertType == getSystemAdvertType()) {
                return true;
            } else if (precedence < PROGRAM.ordinal() && advertType == null) {
                return true;
            }
        }
        return false;
    }

    public void validateRestoreDefaultConfiguration(Resource resource, PrismLocale locale, PrismAdvertType advertType) throws CustomizationException {
        if (!Arrays.asList(INSTITUTION, PROGRAM).contains(resource.getResourceScope())) {
            throw new CustomizationException("Tried to restore default configurations as a system level entity");
        }
    }

    public void validateRestoreGlobalConfiguration(Resource resource, PrismLocale locale, PrismAdvertType advertType) throws CustomizationException {
        if (!Arrays.asList(SYSTEM, INSTITUTION).contains(resource.getResourceScope())) {
            throw new CustomizationException("Tried to restore global configurations as a program level entity");
        }
    }

    public List<DisplayPropertyConfiguration> getAllLocalizedProperties() {
        return entityService.list(DisplayPropertyConfiguration.class);
    }

    public void createOrUpdateConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismAdvertType advertType,
            WorkflowConfigurationDTO workflowConfigurationDTO) throws CustomizationException {
        WorkflowConfiguration configuration = createConfiguration(configurationType, resource, locale, advertType, workflowConfigurationDTO);
        entityService.createOrUpdate(configuration);
    }

    public WorkflowConfiguration createOrUpdateConfigurationUser(PrismConfiguration configurationType, Resource resource, PrismLocale locale,
            PrismAdvertType advertType, WorkflowConfigurationDTO workflowConfigurationDTO) throws Exception {
        WorkflowConfiguration configuration = createConfiguration(configurationType, resource, locale, advertType, workflowConfigurationDTO);
        resourceService.executeUpdate(resource,
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + configurationType.getUpdateCommentProperty()));
        return entityService.createOrUpdate(configuration);
    }

    public boolean isConfigurationEnabled(PrismConfiguration configurationType, Resource resource, Enum<?> definitionId) {
        Class<?> configurationClass = configurationType.getConfigurationClass();
        if (WorkflowConfigurationVersioned.class.isAssignableFrom(configurationClass)) {
            WorkflowConfiguration configuration = getConfigurationWithVersion(configurationType, definitionId,
                    resource.getWorkflowPropertyConfigurationVersion());
            return configuration != null && BooleanUtils.isTrue((Boolean) PrismReflectionUtils.getProperty(configuration, "enabled"));
        }
        throw new UnsupportedOperationException();
    }

    private List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource resource,
            PrismScope scope,
            PrismLocale locale, PrismAdvertType advertType, Enum<?> category, boolean translationMode) throws Exception {
        Resource configuredResource = getConfiguredResource(resource);
        PrismAdvertType configuredadvertType = getConfiguredadvertType(resource, advertType);
        if (configurationType.isCategorizable()) {
            List<WorkflowConfiguration> configurations = customizationDAO.getConfigurations(configurationType, configuredResource, scope, locale,
                    configuredadvertType, category, translationMode);
            return parseRepresentations(resource, configurationType, configurations);
        }
        return getConfigurationRepresentations(configurationType, configuredResource, scope, locale, configuredadvertType);
    }

    private WorkflowConfiguration createConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale,
            PrismAdvertType advertType, WorkflowConfigurationDTO workflowConfigurationDTO) throws CustomizationException {
        WorkflowDefinition definition = entityService.getById(configurationType.getDefinitionClass(), workflowConfigurationDTO.getDefinitionId());
        WorkflowConfiguration configuration = mapper.map(workflowConfigurationDTO, configurationType.getConfigurationClass());
        configuration.setResource(resource);
        configuration.setLocale(locale);
        configuration.setAdvertType(advertType);
        PrismReflectionUtils.setProperty(configuration, configurationType.getDefinitionPropertyName(), definition);
        configuration.setSystemDefault(isSystemDefault(definition, locale, advertType));
        return configuration;
    }

    private Resource getConfiguredResource(Resource resource) {
        return resource.getResourceScope().ordinal() > PROGRAM.ordinal() ? resource.getProgram() : resource;
    }

    private List<WorkflowConfigurationRepresentation> parseRepresentations(Resource resource, PrismConfiguration configurationType,
            List<WorkflowConfiguration> configurations) throws Exception {
        List<WorkflowConfigurationRepresentation> representations = Lists.newLinkedList();

        if (configurations.isEmpty()) {
            return representations;
        } else {
            WorkflowConfiguration stereotype = configurations.get(0);

            Resource stereotypeResource = stereotype.getResource();
            PrismLocale stereotypeLocale = stereotype.getLocale();
            PrismAdvertType stereotypeadvertType = stereotype.getAdvertType();

            for (WorkflowConfiguration configuration : configurations) {
                if (Objects.equal(configuration.getResource(), stereotypeResource) && Objects.equal(configuration.getLocale(), stereotypeLocale)
                        && Objects.equal(configuration.getAdvertType(), stereotypeadvertType)) {
                    WorkflowConfigurationRepresentation representation = mapper.map(configuration, configurationType.getConfigurationRepresentationClass());
                    representations.add(representation);
                }
            }

            return representations;
        }
    }

    private void createOrUpdateConfigurationGroup(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismAdvertType advertType,
            List<? extends WorkflowConfigurationDTO> valueDTOs) throws CustomizationException, DeduplicationException, InstantiationException,
            IllegalAccessException {
        for (WorkflowConfigurationDTO valueDTO : valueDTOs) {
            createOrUpdateConfiguration(configurationType, resource, locale, advertType, valueDTO);
        }
    }

    private void createOrUpdateConfigurationGroupVersion(PrismConfiguration configurationType, Resource resource, PrismLocale locale,
            PrismAdvertType advertType, Enum<?> definitionId, List<? extends WorkflowConfigurationDTO> valueDTOs) throws CustomizationException {
        restoreDefaultConfiguration(configurationType, resource, locale, advertType, definitionId);
        createOrUpdateConfigurationGroupVersion(configurationType, resource, locale, advertType, valueDTOs);
    }

    private void createOrUpdateConfigurationGroupVersion(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismAdvertType advertType, List<? extends WorkflowConfigurationDTO> valueDTOs) throws CustomizationException {
        restoreDefaultConfiguration(configurationType, resource, scope, locale, advertType);
        createOrUpdateConfigurationGroupVersion(configurationType, resource, locale, advertType, valueDTOs);
    }

    private void createOrUpdateConfigurationGroupVersion(PrismConfiguration configurationType, Resource resource, PrismLocale locale,
            PrismAdvertType advertType, List<? extends WorkflowConfigurationDTO> valueDTOs) throws CustomizationException {
        Integer version = null;
        for (WorkflowConfigurationDTO valueDTO : valueDTOs) {
            WorkflowConfiguration transientConfiguration = createConfiguration(configurationType, resource, locale, advertType, valueDTO);
            PrismReflectionUtils.setProperty(transientConfiguration, "active", true);

            WorkflowConfiguration persistentConfiguration;
            if (version == null) {
                entityService.save(transientConfiguration);
                persistentConfiguration = transientConfiguration;
            } else {
                PrismReflectionUtils.setProperty(transientConfiguration, "version", version);
                persistentConfiguration = entityService.createOrUpdate(transientConfiguration);
            }

            version = version == null ? persistentConfiguration.getId() : version;
            PrismReflectionUtils.setProperty(persistentConfiguration, "version", version);
        }
    }

    private PrismAdvertType getConfiguredadvertType(Resource resource, PrismAdvertType advertType) {
        return resource.getResourceScope() == PrismScope.PROGRAM ? resource.getProgram().getAdvert().getAdvertType().getPrismAdvertType() : advertType;
    }

}
