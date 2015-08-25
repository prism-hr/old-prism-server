package com.zuehlke.pgadmissions.rest.controller;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.rest.ResourceDescriptor;
import com.zuehlke.pgadmissions.rest.RestUtils;
import com.zuehlke.pgadmissions.rest.dto.DisplayPropertyConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.NotificationConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.StateDurationConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.WorkflowPropertyConfigurationDTO;
import com.zuehlke.pgadmissions.rest.representation.configuration.DisplayPropertyConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowConfigurationRepresentation;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.utils.PrismWordUtils;

@RestController
@RequestMapping("api/{resourceScope:projects|programs|institutions|systems}/{resourceId}/configuration")
public class CustomizationController {

    @Inject
    private EntityService entityService;

    @Inject
    private CustomizationService customizationService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:notifications}/{id}", method = RequestMethod.GET)
    public WorkflowConfigurationRepresentation getConfiguration(@ModelAttribute PrismConfiguration configurationType,
            @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
            @RequestParam(required = false) PrismOpportunityType opportunityType, @PathVariable PrismNotificationDefinition id) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        WorkflowDefinition definition = entityService.getById(configurationType.getDefinitionClass(), id);
        return customizationService.getConfigurationRepresentation(configurationType, resource, opportunityType, definition);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:displayProperties}/categories/{category}", method = RequestMethod.GET)
    public List<DisplayPropertyConfigurationRepresentation> getDisplayPropertyConfigurations(
            @ModelAttribute PrismConfiguration configurationType,
            @ModelAttribute ResourceDescriptor resourceDescriptor,
            @PathVariable Integer resourceId,
            @PathVariable PrismDisplayPropertyCategory category,
            @RequestParam PrismScope scope,
            @RequestParam(required = false) PrismOpportunityType opportunityType,
            @RequestParam(required = false) Boolean fetchReference) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        if (BooleanUtils.isTrue(fetchReference)) {
            List<WorkflowConfigurationRepresentation> translations = customizationService.getConfigurationRepresentations(configurationType, resource, scope, opportunityType, category);
            return sparsifyDisplayPropertyConfigurations(category, translations);
        }
        List<WorkflowConfigurationRepresentation> translations = customizationService.getConfigurationRepresentationsConfigurationMode(configurationType,
                resource, scope, opportunityType, category);
        return sparsifyDisplayPropertyConfigurations(category, translations);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:stateDurations|workflowProperties}", method = RequestMethod.GET)
    public List<WorkflowConfigurationRepresentation> getConfigurations(
            @ModelAttribute PrismConfiguration configurationType,
            @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @RequestParam PrismScope scope,
            @RequestParam(required = false) PrismOpportunityType opportunityType) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        return customizationService.getConfigurationRepresentations(configurationType, resource, scope, opportunityType);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:customQuestions|workflowProperties}", params = "version", method = RequestMethod.GET)
    public List<WorkflowConfigurationRepresentation> getConfigurationsWithVersion(
            @ModelAttribute PrismConfiguration configurationType,
            @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @RequestParam Integer version) throws Exception {
        return customizationService.getConfigurationRepresentationsWithVersion(configurationType, version);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:notifications}/{id}", method = RequestMethod.DELETE, headers = "Restore-Type")
    public void restoreConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor, @ModelAttribute PrismConfiguration configurationType,
            @PathVariable Integer resourceId, @RequestParam(required = false) PrismOpportunityType opportunityType,
            @RequestHeader(value = "Restore-Type") String restoreType, @PathVariable PrismNotificationDefinition id) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        if (restoreType.equals("global")) {
            customizationService.restoreGlobalConfiguration(configurationType, resource, opportunityType, id);
        } else {
            customizationService.restoreDefaultConfiguration(configurationType, resource, opportunityType, id);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:displayProperties|stateDurations|workflowProperties}", method = RequestMethod.DELETE, headers = "Restore-Type")
    public void restoreConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor, @ModelAttribute PrismConfiguration configurationType,
            @PathVariable Integer resourceId, @RequestParam PrismScope scope, @RequestParam(required = false) PrismOpportunityType opportunityType,
            @RequestHeader(value = "Restore-Type") String restoreType) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        if (restoreType.equals("global")) {
            customizationService.restoreGlobalConfiguration(configurationType, resource, scope, opportunityType);
        } else {
            customizationService.restoreDefaultConfiguration(configurationType, resource, scope, opportunityType);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:notifications}/{id}", method = RequestMethod.PUT)
    public void updateNotificationConfiguration(
            @ModelAttribute PrismConfiguration configurationType, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @PathVariable Integer resourceId, @RequestParam(required = false) PrismOpportunityType opportunityType,
            @PathVariable PrismNotificationDefinition id,
            @Valid @RequestBody NotificationConfigurationDTO notificationConfigurationDTO) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        customizationService.createOrUpdateConfigurationUser(configurationType, resource, opportunityType, notificationConfigurationDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:stateDurations}", method = RequestMethod.PUT)
    public void updateStateDurationConfiguration(@ModelAttribute PrismConfiguration configurationType, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @PathVariable Integer resourceId, @RequestParam PrismScope scope, @RequestParam(required = false) PrismOpportunityType opportunityType,
            @Valid @RequestBody StateDurationConfigurationDTO stateDurationConfigurationDTO)
            throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        customizationService.createOrUpdateConfigurationGroup(configurationType, resource, scope, opportunityType, stateDurationConfigurationDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:displayProperties}/{id}", method = RequestMethod.PUT)
    public void updateDisplayPropertyConfiguration(@ModelAttribute PrismConfiguration configurationType, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @PathVariable Integer resourceId, @RequestParam(required = false) PrismOpportunityType opportunityType,
            @PathVariable PrismDisplayPropertyDefinition id,
            @Valid @RequestBody DisplayPropertyConfigurationDTO displayPropertyConfigurationDTO) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        displayPropertyConfigurationDTO.setDefinitionId(id);
        customizationService.createOrUpdateConfiguration(configurationType, resource, opportunityType, displayPropertyConfigurationDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:workflowProperties}", method = RequestMethod.PUT)
    public void updateWorkflowPropertyConfiguration(@ModelAttribute PrismConfiguration configurationType,
            @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @RequestParam PrismScope scope,
            @RequestParam(required = false) PrismOpportunityType opportunityType,
            @Valid @RequestBody WorkflowPropertyConfigurationDTO workflowPropertyConfigurationDTO)
            throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        customizationService.createOrUpdateConfigurationGroup(configurationType, resource, scope, opportunityType, workflowPropertyConfigurationDTO);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "{configurationType:workflowProperties}/version", method = RequestMethod.GET)
    public Integer getWorkflowPropertyConfigurationVersion(@ModelAttribute PrismConfiguration configurationType,
            @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @RequestParam PrismScope targetScope) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        return customizationService.getActiveConfigurationVersion(configurationType, resource);
    }

    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceScope) {
        return RestUtils.getResourceDescriptor(resourceScope);
    }

    @ModelAttribute
    private PrismConfiguration getConfigurationType(@PathVariable String configurationType) {
        String singleForm = PrismWordUtils.singularize(configurationType);
        String typeName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, singleForm);
        return PrismConfiguration.valueOf(typeName);
    }

    private List<DisplayPropertyConfigurationRepresentation> sparsifyDisplayPropertyConfigurations(PrismDisplayPropertyCategory category,
            List<WorkflowConfigurationRepresentation> translations) {
        Map<PrismDisplayPropertyDefinition, String> index = Maps.newHashMap();
        for (WorkflowConfigurationRepresentation translation : translations) {
            DisplayPropertyConfigurationRepresentation translationRepresentation = (DisplayPropertyConfigurationRepresentation) translation;
            index.put((PrismDisplayPropertyDefinition) translation.getDefinitionId(), translationRepresentation.getValue());
        }
        List<DisplayPropertyConfigurationRepresentation> representations = Lists.newLinkedList();
        for (PrismDisplayPropertyDefinition definition : PrismDisplayPropertyDefinition.getProperties(category)) {
            representations.add(new DisplayPropertyConfigurationRepresentation().withProperty(definition).withValue(index.get(definition)));
        }
        return representations;
    }

}
