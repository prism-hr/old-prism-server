package uk.co.alumeni.prism.rest.controller;

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

import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyCategory;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismConfiguration;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.workflow.WorkflowDefinition;
import uk.co.alumeni.prism.rest.PrismRestUtils;
import uk.co.alumeni.prism.rest.ResourceDescriptor;
import uk.co.alumeni.prism.rest.dto.DisplayPropertyConfigurationDTO;
import uk.co.alumeni.prism.rest.dto.NotificationConfigurationDTO;
import uk.co.alumeni.prism.rest.dto.StateDurationConfigurationDTO;
import uk.co.alumeni.prism.rest.representation.configuration.DisplayPropertyConfigurationRepresentation;
import uk.co.alumeni.prism.rest.representation.configuration.NotificationConfigurationRepresentation;
import uk.co.alumeni.prism.rest.representation.configuration.WorkflowConfigurationRepresentation;
import uk.co.alumeni.prism.services.CustomizationService;
import uk.co.alumeni.prism.services.EntityService;
import uk.co.alumeni.prism.services.NotificationService;
import uk.co.alumeni.prism.utils.PrismWordUtils;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RestController
@RequestMapping("api/{resourceScope:projects|programs|departments|institutions|systems}/{resourceId}/configuration")
public class CustomizationController {

    @Inject
    private EntityService entityService;

    @Inject
    private CustomizationService customizationService;

    @Inject
    private NotificationService notificationService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:notifications}/{id}", method = RequestMethod.GET)
    public NotificationConfigurationRepresentation getConfiguration(@ModelAttribute PrismConfiguration configurationType,
            @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
            @RequestParam(required = false) PrismOpportunityType opportunityType,
            @PathVariable PrismNotificationDefinition id) {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        WorkflowDefinition definition = entityService.getById(configurationType.getDefinitionClass(), id);
        return (NotificationConfigurationRepresentation) customizationService.getConfigurationRepresentation(configurationType, resource, opportunityType,
                definition);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:displayProperties}/categories/{category}", method = RequestMethod.GET)
    public List<DisplayPropertyConfigurationRepresentation> getDisplayPropertyConfigurations(@ModelAttribute PrismConfiguration configurationType,
            @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @PathVariable PrismDisplayPropertyCategory category,
            @RequestParam PrismScope scope, @RequestParam(required = false) PrismOpportunityType opportunityType,
            @RequestParam(required = false) Boolean fetchReference) {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        if (BooleanUtils.isTrue(fetchReference)) {
            List<WorkflowConfigurationRepresentation> translations = customizationService.getConfigurationRepresentations(configurationType, resource, scope,
                    opportunityType,
                    category);
            return sparsifyDisplayPropertyConfigurations(category, translations);
        }
        List<WorkflowConfigurationRepresentation> translations = customizationService.getConfigurationRepresentationsConfigurationMode(configurationType,
                resource, scope, opportunityType, category);
        return sparsifyDisplayPropertyConfigurations(category, translations);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:stateDurations}", method = RequestMethod.GET)
    public List<WorkflowConfigurationRepresentation> getConfigurations(@ModelAttribute PrismConfiguration configurationType,
            @ModelAttribute ResourceDescriptor resourceDescriptor,
            @PathVariable Integer resourceId, @RequestParam PrismScope scope, @RequestParam(required = false) PrismOpportunityType opportunityType) {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        return customizationService.getConfigurationRepresentations(configurationType, resource, scope, opportunityType);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:customQuestions}", params = "version", method = RequestMethod.GET)
    public List<WorkflowConfigurationRepresentation> getConfigurationsWithVersion(@ModelAttribute PrismConfiguration configurationType,
            @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @RequestParam Integer version) {
        return customizationService.getConfigurationRepresentationsWithVersion(configurationType, version);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:notifications}/{id}", method = RequestMethod.DELETE, headers = "Restore-Type")
    public void restoreConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor, @ModelAttribute PrismConfiguration configurationType,
            @PathVariable Integer resourceId,
            @RequestParam(required = false) PrismOpportunityType opportunityType, @RequestHeader(value = "Restore-Type") String restoreType,
            @PathVariable PrismNotificationDefinition id) {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        if (restoreType.equals("global")) {
            customizationService.restoreGlobalConfiguration(configurationType, resource, opportunityType, id);
        } else {
            customizationService.restoreDefaultConfiguration(configurationType, resource, opportunityType, id);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:displayProperties|stateDurations}", method = RequestMethod.DELETE, headers = "Restore-Type")
    public void restoreConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor, @ModelAttribute PrismConfiguration configurationType,
            @PathVariable Integer resourceId, @RequestParam PrismScope scope, @RequestParam(required = false) PrismOpportunityType opportunityType,
            @RequestHeader(value = "Restore-Type") String restoreType) {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        if (restoreType.equals("global")) {
            customizationService.restoreGlobalConfiguration(configurationType, resource, scope, opportunityType);
        } else {
            customizationService.restoreDefaultConfiguration(configurationType, resource, scope, opportunityType);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:notifications}/{id}", method = RequestMethod.PUT)
    public void updateNotificationConfiguration(@ModelAttribute PrismConfiguration configurationType, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @PathVariable Integer resourceId, @RequestParam(required = false) PrismOpportunityType opportunityType,
            @PathVariable PrismNotificationDefinition id,
            @Valid @RequestBody NotificationConfigurationDTO notificationConfigurationDTO) {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        notificationService.createOrUpdateNotificationConfiguration(configurationType, resource, opportunityType, notificationConfigurationDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:stateDurations}", method = RequestMethod.PUT)
    public void updateStateDurationConfiguration(@ModelAttribute PrismConfiguration configurationType, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @PathVariable Integer resourceId, @RequestParam PrismScope scope, @RequestParam(required = false) PrismOpportunityType opportunityType,
            @Valid @RequestBody StateDurationConfigurationDTO stateDurationConfigurationDTO) {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        customizationService.createOrUpdateConfigurationGroup(configurationType, resource, scope, opportunityType, stateDurationConfigurationDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{configurationType:displayProperties}/{id}", method = RequestMethod.PUT)
    public void updateDisplayPropertyConfiguration(@ModelAttribute PrismConfiguration configurationType, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @PathVariable Integer resourceId, @RequestParam(required = false) PrismOpportunityType opportunityType,
            @PathVariable PrismDisplayPropertyDefinition id,
            @Valid @RequestBody DisplayPropertyConfigurationDTO displayPropertyConfigurationDTO) {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        displayPropertyConfigurationDTO.setDefinitionId(id);
        customizationService.createOrUpdateConfiguration(configurationType, resource, opportunityType, displayPropertyConfigurationDTO);
    }

    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceScope) {
        return PrismRestUtils.getResourceDescriptor(resourceScope);
    }

    @ModelAttribute
    private PrismConfiguration getConfigurationType(@PathVariable String configurationType) {
        String singleForm = PrismWordUtils.singularize(configurationType);
        String typeName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, singleForm);
        return PrismConfiguration.valueOf(typeName);
    }

    private List<DisplayPropertyConfigurationRepresentation> sparsifyDisplayPropertyConfigurations(
            PrismDisplayPropertyCategory category, List<WorkflowConfigurationRepresentation> translations) {
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
