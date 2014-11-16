package com.zuehlke.pgadmissions.rest.resource;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.beanutils.PropertyUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismWorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.NotificationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import com.zuehlke.pgadmissions.mail.MailSender;
import com.zuehlke.pgadmissions.rest.ResourceDescriptor;
import com.zuehlke.pgadmissions.rest.RestApiUtils;
import com.zuehlke.pgadmissions.rest.dto.NotificationConfigurationDTO;
import com.zuehlke.pgadmissions.rest.representation.configuration.AbstractConfigurationRepresentation;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.WordUtils;

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

    @RequestMapping(method = RequestMethod.GET)
    public List<AbstractConfigurationRepresentation> getConfigurations(
            @ModelAttribute ResourceDescriptor resourceDescriptor,
            @PathVariable Integer resourceId,
            @ModelAttribute PrismWorkflowConfiguration configurationType,
            @RequestParam(required = false) PrismLocale locale,
            @RequestParam(required = false) PrismProgramType programType) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);

        List<WorkflowConfiguration> configurations = customizationService.listConfigurations(configurationType, resource, locale, programType);

        List<AbstractConfigurationRepresentation> representations = Lists.newArrayListWithCapacity(configurations.size());
        String definitionPropertyName = configurationType.getDefinitionPropertyName();
        for (WorkflowConfiguration configuration : configurations) {
            WorkflowDefinition workflowDefinition = (WorkflowDefinition) PropertyUtils.getSimpleProperty(configuration, definitionPropertyName);
            AbstractConfigurationRepresentation representation = dozerBeanMapper.map(configuration, configurationType.getRepresentationClass());
            representation.setDefinitionId(workflowDefinition.getId());
            representations.add(representation);
        }
        return representations;
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public List<AbstractConfigurationRepresentation> updateConfigurations(
            @ModelAttribute ResourceDescriptor resourceDescriptor,
            @PathVariable Integer resourceId,
            @ModelAttribute PrismWorkflowConfiguration configurationType,
            @RequestParam(required = false) PrismLocale locale,
            @RequestParam(required = false) PrismProgramType programType) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);

        List<WorkflowConfiguration> configurations = customizationService.listConfigurations(configurationType, resource, locale, programType);

        List<AbstractConfigurationRepresentation> representations = Lists.newArrayListWithCapacity(configurations.size());
        String definitionPropertyName = configurationType.getDefinitionPropertyName();
        for (WorkflowConfiguration configuration : configurations) {
            WorkflowDefinition workflowDefinition = (WorkflowDefinition) PropertyUtils.getSimpleProperty(configuration, definitionPropertyName);
            AbstractConfigurationRepresentation representation = dozerBeanMapper.map(configuration, configurationType.getRepresentationClass());
            representation.setDefinitionId(workflowDefinition.getId());
            representations.add(representation);
        }
        return representations;
    }

    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceScope) {
        return RestApiUtils.getResourceDescriptor(resourceScope);
    }

    @ModelAttribute
    private PrismWorkflowConfiguration getConfigurationType(@PathVariable String configurationType) {
        String singleForm = WordUtils.singularize(configurationType);
        String typeName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, singleForm);
        return PrismWorkflowConfiguration.valueOf(typeName);
    }

}
