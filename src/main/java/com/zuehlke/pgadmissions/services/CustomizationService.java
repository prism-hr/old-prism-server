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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.CustomizationDAO;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
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
    private Mapper mapper;

    public WorkflowConfiguration getConfiguration(PrismConfiguration configurationType, Resource resource, User user, WorkflowDefinition definition) {
        if (definition != null) {
            PrismScope resourceScope = resource.getResourceScope();
            PrismLocale locale = resourceScope == SYSTEM ? user.getLocale() : resource.getLocale();
            PrismProgramType programType = resourceScope.getPrecedence() > INSTITUTION.getPrecedence() ? resource.getProgram().getProgramType()
                    .getPrismProgramType() : null;
            return getConfiguration(configurationType, resource, definition, locale, programType);
        }
        return null;
    }

    public WorkflowConfiguration getConfiguration(PrismConfiguration configurationType, Resource resource, WorkflowDefinition definition, PrismLocale locale,
            PrismProgramType programType) {
        return customizationDAO.getConfiguration(configurationType, resource, definition, locale, programType);
    }

    public List<DisplayPropertyConfiguration> getDisplayPropertyConfiguration(Resource resource, PrismScope scope,
            PrismDisplayPropertyCategory displayPropertyCategory, PrismLocale locale, PrismProgramType programType) {
        return customizationDAO.getDisplayPropertyConfiguration(resource, scope, displayPropertyCategory, locale, programType);
    }

    public WorkflowConfigurationRepresentation getConfigurationRepresentation(PrismConfiguration configurationType, Resource resource,
            WorkflowDefinition definition, PrismLocale locale, PrismProgramType programType) {
        resource = getConfiguredResource(resource);
        WorkflowConfiguration configuration = getConfiguration(configurationType, resource, definition, locale, programType);
        return mapper.map(configuration, configurationType.getConfigurationRepresentationClass());
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource resource, User user) {
        PrismScope resourceScope = resource.getResourceScope();
        PrismLocale locale = resourceScope == SYSTEM ? user.getLocale() : resource.getLocale();
        PrismProgramType programType = resourceScope.getPrecedence() > INSTITUTION.getPrecedence() ? resource.getProgram().getProgramType()
                .getPrismProgramType() : null;
        return getConfigurationRepresentations(configurationType, resource, resource.getResourceScope(), locale, programType);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentations(PrismConfiguration configurationType, Resource resource, PrismScope scope,
            PrismLocale locale, PrismProgramType programType) {
        resource = getConfiguredResource(resource);
        List<WorkflowConfiguration> configurations = customizationDAO.getConfigurations(configurationType, resource, scope, locale, programType);
        return parseRepresentations(configurationType, configurations);
    }

    public List<WorkflowConfiguration> getConfigurationsWithVersion(PrismConfiguration configurationType, Integer version) {
        return customizationDAO.getConfigurationsWithVersion(configurationType, version);
    }

    public List<WorkflowConfigurationRepresentation> getConfigurationRepresentationsWithVersion(PrismConfiguration configurationType, Integer version) {
        List<WorkflowConfiguration> configurations = getConfigurationsWithVersion(configurationType, version);
        return parseRepresentations(configurationType, configurations);
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

    private Resource getConfiguredResource(Resource resource) {
        return resource.getResourceScope().getPrecedence() > PrismScope.PROGRAM.getPrecedence() ? resource.getProgram() : resource;
    }

    private List<WorkflowConfigurationRepresentation> parseRepresentations(PrismConfiguration configurationType, List<WorkflowConfiguration> configurations) {
        List<WorkflowConfigurationRepresentation> representations = Lists.newLinkedList();

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
                    representations.add(mapper.map(configuration, configurationType.getConfigurationRepresentationClass()));
                }
            }

            return representations;
        }
    }

}
