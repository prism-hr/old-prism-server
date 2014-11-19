package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.base.CaseFormat;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.rest.ResourceDescriptor;
import com.zuehlke.pgadmissions.rest.RestApiUtils;
import com.zuehlke.pgadmissions.rest.dto.WorkflowConfigurationDTO;
import com.zuehlke.pgadmissions.rest.dto.WorkflowConfigurationGroupDTO;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowConfigurationRepresentation;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.WordUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/{resourceScope:programs|institutions|systems}/{resourceId}/configuration/{configurationType:customQuestions|displayProperties|stateDurations|workflowProperties}")
public class ResourceConfigurationResource {

    @Autowired
    private EntityService entityService;

    @Autowired
    private CustomizationService customizationService;

    @Autowired
    private UserService userService;

    @Autowired
    private Mapper dozerBeanMapper;

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public WorkflowConfigurationRepresentation getConfiguration(@ModelAttribute PrismConfiguration configurationType,
                                                                @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @RequestParam(required = false) PrismLocale locale,
                                                                @RequestParam(required = false) PrismProgramType programType, @PathVariable String id) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        WorkflowDefinition definition = entityService.getById(configurationType.getDefinitionClass(), id);
        return customizationService.getConfigurationRepresentation(configurationType, resource, definition, locale, programType);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<WorkflowConfigurationRepresentation> getConfigurations(@ModelAttribute PrismConfiguration configurationType,
                                                                       @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @RequestParam PrismScope scope,
                                                                       @RequestParam(required = false) PrismLocale locale, @RequestParam(required = false) PrismProgramType programType) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        return customizationService.getConfigurationRepresentations(configurationType, resource, scope, locale, programType);
    }

    @RequestMapping(value = "/{version}", method = RequestMethod.GET)
    public List<WorkflowConfigurationRepresentation> getConfigurationsWithVersion(@ModelAttribute PrismConfiguration configurationType,
                                                                                  @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId, @PathVariable Integer version) throws Exception {
        return customizationService.getConfigurationRepresentationsWithVersion(configurationType, version);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void restoreDefaultConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor, PrismConfiguration configurationType,
                                            @PathVariable Integer resourceId, @RequestParam(required = false) PrismLocale locale, @RequestParam(required = false) PrismProgramType programType,
                                            @PathVariable String id) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        WorkflowDefinition definition = entityService.getById(configurationType.getDefinitionClass(), id);
        customizationService.restoreDefaultConfiguration(configurationType, resource, locale, programType, definition);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void restoreDefaultConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor, PrismConfiguration configurationType,
                                            @PathVariable Integer resourceId, @RequestParam PrismScope scope, @RequestParam(required = false) PrismLocale locale,
                                            @RequestParam(required = false) PrismProgramType programType) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        customizationService.restoreDefaultConfiguration(configurationType, resource, scope, locale, programType);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void updateConfiguration(@ModelAttribute PrismConfiguration configurationType,
                                    @ModelAttribute ResourceDescriptor resourceDescriptor,
                                    @PathVariable Integer resourceId,
                                    @RequestParam(required = false) PrismLocale locale,
                                    @RequestParam(required = false) PrismProgramType programType,
                                    @PathVariable String id,
                                    @Valid @RequestBody WorkflowConfigurationDTO workflowConfigurationDTO) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        WorkflowDefinition definition = entityService.getById(configurationType.getDefinitionClass(), id);
        customizationService.updateConfiguration(configurationType, resource, locale, programType, definition, workflowConfigurationDTO);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void updateConfiguration(@ModelAttribute PrismConfiguration configurationType,
                                    @ModelAttribute ResourceDescriptor resourceDescriptor,
                                    @PathVariable Integer resourceId,
                                    @RequestParam PrismScope scope,
                                    @RequestParam(required = false) PrismLocale locale,
                                    @RequestParam(required = false) PrismProgramType programType,
                                    @Valid @RequestBody WorkflowConfigurationGroupDTO workflowConfigurationGroupDTO) {
        // TODO: build the generic DTO objects
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
