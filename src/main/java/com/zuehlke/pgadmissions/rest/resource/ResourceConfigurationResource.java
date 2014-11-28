package com.zuehlke.pgadmissions.rest.resource;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.CaseFormat;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.exceptions.CustomizationException;
import com.zuehlke.pgadmissions.rest.ResourceDescriptor;
import com.zuehlke.pgadmissions.rest.RestApiUtils;
import com.zuehlke.pgadmissions.rest.dto.ActionCustomQuestionConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.DisplayPropertyConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.NotificationConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.StateDurationConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.WorkflowPropertyConfigurationDTO;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.validation.validator.ActionCustomQuestionValidator;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.utils.WordUtils;

@RestController
@RequestMapping("api/{resourceScope:projects|programs|institutions|systems}/{resourceId}/configuration")
public class ResourceConfigurationResource {

    @Autowired
    private EntityService entityService;

    @Autowired
    private CustomizationService customizationService;

    @Autowired
    private ActionCustomQuestionValidator actionCustomQuestionValidator;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:notifications}/{id}", method = RequestMethod.GET)
    public WorkflowConfigurationRepresentation getConfiguration(@ModelAttribute PrismConfiguration configurationType,
                                                                @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @RequestParam(required = false) PrismLocale locale,
                                                                @RequestParam(required = false) PrismProgramType programType, @PathVariable PrismNotificationDefinition id) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        WorkflowDefinition definition = entityService.getById(configurationType.getDefinitionClass(), id);
        return customizationService.getConfigurationRepresentation(configurationType, resource, locale, programType, definition);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:customQuestions}/{id}", method = RequestMethod.GET)
    public List<WorkflowConfigurationRepresentation> getConfiguration(@ModelAttribute PrismConfiguration configurationType,
                                                                      @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @RequestParam(required = false) PrismLocale locale,
                                                                      @RequestParam(required = false) PrismProgramType programType, @PathVariable PrismActionCustomQuestionDefinition id) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        WorkflowDefinition definition = entityService.getById(configurationType.getDefinitionClass(), id);
        return customizationService.getConfigurationRepresentations(configurationType, resource, locale, programType, definition);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:displayProperties|stateDurations|workflowProperties}", method = RequestMethod.GET)
    public List<WorkflowConfigurationRepresentation> getConfigurations(@ModelAttribute PrismConfiguration configurationType,
                                                                       @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @RequestParam PrismScope scope,
                                                                       @RequestParam(required = false) PrismLocale locale, @RequestParam(required = false) PrismProgramType programType) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        return customizationService.getConfigurationRepresentations(configurationType, resource, scope, locale, programType);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:customQuestions|workflowProperties}", params = "version", method = RequestMethod.GET)
    public List<WorkflowConfigurationRepresentation> getConfigurationsWithVersion(@ModelAttribute PrismConfiguration configurationType,
                                                                                  @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @RequestParam Integer version) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        return customizationService.getConfigurationRepresentationsWithVersion(resource, configurationType, version);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:notifications}/{id}", method = RequestMethod.DELETE, headers = "Restore-Type")
    public void restoreConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor, @ModelAttribute PrismConfiguration configurationType,
                                     @PathVariable Integer resourceId, @RequestParam(required = false) PrismLocale locale, @RequestParam(required = false) PrismProgramType programType,
                                     @RequestHeader(value = "Restore-Type") String restoreType, @PathVariable PrismNotificationDefinition id) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        WorkflowDefinition definition = entityService.getById(configurationType.getDefinitionClass(), id);
        if (restoreType.equals("global")) {
            customizationService.restoreGlobalConfiguration(configurationType, resource, locale, programType, definition);
        } else {
            customizationService.restoreDefaultConfiguration(configurationType, resource, locale, programType, definition);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:customQuestions}/{id}", method = RequestMethod.DELETE, headers = "Restore-Type")
    public void restoreConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor, @ModelAttribute PrismConfiguration configurationType,
                                     @PathVariable Integer resourceId, @RequestParam(required = false) PrismLocale locale, @RequestParam(required = false) PrismProgramType programType,
                                     @RequestHeader(value = "Restore-Type") String restoreType, @PathVariable PrismActionCustomQuestionDefinition id) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        WorkflowDefinition definition = entityService.getById(configurationType.getDefinitionClass(), id);
        if (restoreType.equals("global")) {
            customizationService.restoreGlobalConfiguration(configurationType, resource, locale, programType, definition);
        } else {
            customizationService.restoreDefaultConfiguration(configurationType, resource, locale, programType, definition);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:displayProperties|stateDurations|workflowProperties}", method = RequestMethod.DELETE, headers = "Restore-Type")
    public void restoreConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor, @ModelAttribute PrismConfiguration configurationType,
                                     @PathVariable Integer resourceId, @RequestParam PrismScope scope, @RequestParam(required = false) PrismLocale locale,
                                     @RequestParam(required = false) PrismProgramType programType, @RequestHeader(value = "Restore-Type") String restoreType) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        if (restoreType.equals("global")) {
            customizationService.restoreGlobalConfiguration(configurationType, resource, scope, locale, programType);
        } else {
            customizationService.restoreDefaultConfiguration(configurationType, resource, scope, locale, programType);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:notifications}/{id}", method = RequestMethod.PUT)
    public void updateNotificationConfiguration(@ModelAttribute PrismConfiguration configurationType, @ModelAttribute ResourceDescriptor resourceDescriptor,
                                                @PathVariable Integer resourceId, @RequestParam PrismScope scope, @RequestParam(required = false) PrismLocale locale,
                                                @RequestParam(required = false) PrismProgramType programType, @PathVariable PrismNotificationDefinition id,
                                                @Valid @RequestBody NotificationConfigurationDTO notificationConfigurationDTO) throws CustomizationException {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        customizationService.createOrUpdateConfiguration(configurationType, resource, locale, programType, notificationConfigurationDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:customQuestions}/{id}", method = RequestMethod.PUT)
    public void updateActionCustomQuestionConfiguration(@ModelAttribute PrismConfiguration configurationType,
                                                        @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @RequestParam PrismScope scope,
                                                        @RequestParam(required = false) PrismLocale locale, @RequestParam(required = false) PrismProgramType programType,
                                                        @PathVariable PrismActionCustomQuestionDefinition id, @Valid @RequestBody ActionCustomQuestionConfigurationDTO actionCustomQuestionConfigurationDTO)
            throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        customizationService.createOrUpdateConfigurationGroup(configurationType, resource, scope, locale, programType, actionCustomQuestionConfigurationDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:stateDurations}", method = RequestMethod.PUT)
    public void updateStateDurationConfiguration(@ModelAttribute PrismConfiguration configurationType, @ModelAttribute ResourceDescriptor resourceDescriptor,
                                                 @PathVariable Integer resourceId, @RequestParam PrismScope scope, @RequestParam(required = false) PrismLocale locale,
                                                 @RequestParam(required = false) PrismProgramType programType, @Valid @RequestBody StateDurationConfigurationDTO stateDurationConfigurationDTO)
            throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        customizationService.createOrUpdateConfigurationGroup(configurationType, resource, scope, locale, programType, stateDurationConfigurationDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:displayProperties}", method = RequestMethod.PUT)
    public void updateDisplayPropertyConfiguration(@ModelAttribute PrismConfiguration configurationType, @ModelAttribute ResourceDescriptor resourceDescriptor,
                                                   @PathVariable Integer resourceId, @RequestParam PrismScope scope, @RequestParam(required = false) PrismLocale locale,
                                                   @RequestParam(required = false) PrismProgramType programType, @Valid @RequestBody DisplayPropertyConfigurationDTO displayPropertyConfigurationDTO)
            throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        customizationService.createOrUpdateConfigurationGroup(configurationType, resource, scope, locale, programType, displayPropertyConfigurationDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:workflowProperties}", method = RequestMethod.PUT)
    public void updateWorkflowPropertyConfiguration(@ModelAttribute PrismConfiguration configurationType,
                                                    @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @RequestParam PrismScope scope,
                                                    @RequestParam(required = false) PrismLocale locale, @RequestParam(required = false) PrismProgramType programType,
                                                    @Valid @RequestBody WorkflowPropertyConfigurationDTO workflowPropertyConfigurationDTO) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        customizationService.createOrUpdateConfigurationGroup(configurationType, resource, scope, locale, programType, workflowPropertyConfigurationDTO);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "{configurationType:workflowProperties}/version", method = RequestMethod.GET)
    public Integer getWorkflowPropertyConfigurationVersion(
            @ModelAttribute PrismConfiguration configurationType,
            @ModelAttribute ResourceDescriptor resourceDescriptor,
            @PathVariable Integer resourceId,
            @RequestParam PrismScope targetScope) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        return customizationService.getActiveConfigurationVersion(configurationType, resource, targetScope);
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

    @InitBinder(value = "actionCustomQuestionConfigurationDTO")
    public void registerActionCustomQuestionConfigurationDTOValidator(WebDataBinder binder) {
        binder.setValidator(actionCustomQuestionValidator);
    }

}
