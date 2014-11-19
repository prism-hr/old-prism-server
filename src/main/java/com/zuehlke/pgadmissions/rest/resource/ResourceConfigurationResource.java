package com.zuehlke.pgadmissions.rest.resource;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.CaseFormat;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.exceptions.CustomizationException;
import com.zuehlke.pgadmissions.rest.ResourceDescriptor;
import com.zuehlke.pgadmissions.rest.RestApiUtils;
import com.zuehlke.pgadmissions.rest.dto.ActionCustomQuestionConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.DisplayPropertyConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.StateDurationConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.WorkflowPropertyConfigurationDTO;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowConfigurationRepresentation;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.utils.WordUtils;

@RestController
@RequestMapping("api/{resourceScope:programs|institutions|systems}/{resourceId}/configuration")
public class ResourceConfigurationResource {

    @Autowired
    private EntityService entityService;

    @Autowired
    private CustomizationService customizationService;

    @RequestMapping(value = "{configurationType:notifications/{id}", method = RequestMethod.GET)
    public WorkflowConfigurationRepresentation getConfiguration(@ModelAttribute PrismConfiguration configurationType,
            @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @RequestParam(required = false) PrismLocale locale,
            @RequestParam(required = false) PrismProgramType programType, @PathVariable String id) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        WorkflowDefinition definition = entityService.getById(configurationType.getDefinitionClass(), id);
        return customizationService.getConfigurationRepresentation(configurationType, resource, definition, locale, programType);
    }

    @RequestMapping(value = "{configurationType:customQuestions|displayProperties|stateDurations|workflowProperties}", method = RequestMethod.GET)
    public List<WorkflowConfigurationRepresentation> getConfigurations(@ModelAttribute PrismConfiguration configurationType,
            @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @RequestParam PrismScope scope,
            @RequestParam(required = false) PrismLocale locale, @RequestParam(required = false) PrismProgramType programType) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        return customizationService.getConfigurationRepresentations(configurationType, resource, scope, locale, programType);
    }

    @RequestMapping(value = "{configurationType:customQuestions|workflowProperties}/{version}", method = RequestMethod.GET)
    public List<WorkflowConfigurationRepresentation> getConfigurationsWithVersion(@ModelAttribute PrismConfiguration configurationType,
            @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @PathVariable Integer version) throws Exception {
        return customizationService.getConfigurationRepresentationsWithVersion(configurationType, version);
    }

    @RequestMapping(value = "{configurationType:notifications/restoreDefault/{id}", method = RequestMethod.DELETE)
    public void restoreDefaultConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor, PrismConfiguration configurationType,
            @PathVariable Integer resourceId, @RequestParam(required = false) PrismLocale locale, @RequestParam(required = false) PrismProgramType programType,
            @PathVariable String id) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        WorkflowDefinition definition = entityService.getById(configurationType.getDefinitionClass(), id);
        customizationService.restoreDefaultConfiguration(configurationType, resource, locale, programType, definition);
    }

    @RequestMapping(value = "{configurationType:customQuestions|displayProperties|stateDurations|workflowProperties}/restoreDefault", method = RequestMethod.DELETE)
    public void restoreDefaultConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor, PrismConfiguration configurationType,
            @PathVariable Integer resourceId, @RequestParam PrismScope scope, @RequestParam(required = false) PrismLocale locale,
            @RequestParam(required = false) PrismProgramType programType) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        customizationService.restoreGlobalConfiguration(configurationType, resource, scope, locale, programType);
    }

    @RequestMapping(value = "{configurationType:notifications/restoreGlobal/{id}", method = RequestMethod.DELETE)
    public void restoreGlobalConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor, PrismConfiguration configurationType,
            @PathVariable Integer resourceId, @RequestParam(required = false) PrismLocale locale, @RequestParam(required = false) PrismProgramType programType,
            @PathVariable String id) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        WorkflowDefinition definition = entityService.getById(configurationType.getDefinitionClass(), id);
        customizationService.restoreGlobalConfiguration(configurationType, resource, locale, programType, definition);
    }

    @RequestMapping(value = "{configurationType:customQuestions|displayProperties|stateDurations|workflowProperties}/restoreGlobal", method = RequestMethod.DELETE)
    public void restoreGlobalConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor, PrismConfiguration configurationType,
            @PathVariable Integer resourceId, @RequestParam PrismScope scope, @RequestParam(required = false) PrismLocale locale,
            @RequestParam(required = false) PrismProgramType programType) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        customizationService.restoreDefaultConfiguration(configurationType, resource, scope, locale, programType);
    }

    @RequestMapping(value = "{configurationType:customQuestions", method = RequestMethod.PUT)
    public void updateActionCustomQuestionConfiguration(@ModelAttribute PrismConfiguration configurationType,
            @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @RequestParam PrismScope scope,
            @RequestParam(required = false) PrismLocale locale, @RequestParam(required = false) PrismProgramType programType,
            @Valid @RequestBody ActionCustomQuestionConfigurationDTO actionCustomQuestionConfigurationDTO) throws CustomizationException {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        customizationService.createOrUpdateConfigurationGroup(configurationType, resource, scope, locale, programType, actionCustomQuestionConfigurationDTO);
    }

    @RequestMapping(value = "{configurationType:stateDurations}", method = RequestMethod.PUT)
    public void updateStateDurationConfiguration(@ModelAttribute PrismConfiguration configurationType, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @PathVariable Integer resourceId, @RequestParam PrismScope scope, @RequestParam(required = false) PrismLocale locale,
            @RequestParam(required = false) PrismProgramType programType, @Valid @RequestBody StateDurationConfigurationDTO stateDurationConfigurationDTO)
            throws CustomizationException {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        customizationService.createOrUpdateConfigurationGroup(configurationType, resource, scope, locale, programType, stateDurationConfigurationDTO);
    }

    @RequestMapping(value = "{configurationType:displayProperties}", method = RequestMethod.PUT)
    public void updateDisplayPropertyConfiguration(@ModelAttribute PrismConfiguration configurationType, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @PathVariable Integer resourceId, @RequestParam PrismScope scope, @RequestParam(required = false) PrismLocale locale,
            @RequestParam(required = false) PrismProgramType programType, @Valid @RequestBody DisplayPropertyConfigurationDTO displayPropertyConfigurationDTO)
            throws CustomizationException {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        customizationService.createOrUpdateConfigurationGroup(configurationType, resource, scope, locale, programType, displayPropertyConfigurationDTO);
    }

    @RequestMapping(value = "{configurationType:workflowProperties}", method = RequestMethod.PUT)
    public void updateWorkflowPropertyConfiguration(@ModelAttribute PrismConfiguration configurationType,
            @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @RequestParam PrismScope scope,
            @RequestParam(required = false) PrismLocale locale, @RequestParam(required = false) PrismProgramType programType,
            @Valid @RequestBody WorkflowPropertyConfigurationDTO workflowPropertyConfigurationDTO) throws CustomizationException {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        customizationService.createOrUpdateConfigurationGroup(configurationType, resource, scope, locale, programType, workflowPropertyConfigurationDTO);
    }

    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceScope) {
        return RestApiUtils.getResourceDescriptor(resourceScope);
    }

    @ModelAttribute
    private PrismConfiguration getConfigurationType(@PathVariable String configurationType) {
        String singleForm = WordUtils.singularize(configurationType);
        String typeName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, singleForm);
        return PrismConfiguration.valueOf(typeName);
    }

}
