package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismWorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.rest.ResourceDescriptor;
import com.zuehlke.pgadmissions.rest.RestApiUtils;
import com.zuehlke.pgadmissions.rest.representation.configuration.AbstractConfigurationRepresentation;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.WordUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

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
//
//    @RequestMapping(value = "/{resourceId}/notificationTemplates/{notificationTemplateId}", method = RequestMethod.PUT)
//    public void updateNotificationConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
//                                                @PathVariable String notificationTemplateId, @RequestParam(required = false) PrismLocale locale,
//                                                @RequestParam(required = false) PrismProgramType programType, @Valid @RequestBody NotificationConfigurationDTO notificationConfigurationDTO,
//                                                BindingResult validationErrors) throws Exception {
//        NotificationDefinition template = notificationService.getById(PrismNotificationDefinition.valueOf(notificationTemplateId));
//        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
//
//        validateTemplate(resource, template, notificationConfigurationDTO, validationErrors);
//        if (validationErrors.hasErrors()) {
//            throw new PrismValidationException("Invalid notification configuration", validationErrors);
//        }
//
//        notificationService.updateNotificatonConfiguration(resource, locale, programType, template, notificationConfigurationDTO);
//    }
//
//    @RequestMapping(value = "/{resourceId}/notificationTemplates/{notificationTemplateId}", method = RequestMethod.DELETE)
//    public void restoreDefaultConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
//                                            @PathVariable String notificationTemplateId, @RequestParam(required = false) PrismLocale locale,
//                                            @RequestParam(required = false) PrismProgramType programType) throws Exception {
//        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
//        NotificationDefinition template = notificationService.getById(PrismNotificationDefinition.valueOf(notificationTemplateId));
//        notificationService.restoreDefaultNotificationConfiguration(resource, locale, programType, template);
//    }
//
//    @RequestMapping(value = "/{resourceId}/notificationTemplates/{notificationTemplateId}/descendants", method = RequestMethod.DELETE)
//    public void restoreGlobalConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
//                                           @PathVariable String notificationTemplateId, @RequestParam(required = false) PrismLocale locale,
//                                           @RequestParam(required = false) PrismProgramType programType) throws Exception {
//        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
//        NotificationDefinition template = notificationService.getById(PrismNotificationDefinition.valueOf(notificationTemplateId));
//        notificationService.restoreGlobalNotificationConfiguration(resource, locale, programType, template);
//    }
//
//    private Map<String, String> validateTemplate(Resource resource, NotificationDefinition notificationTemplate,
//                                                 NotificationConfigurationDTO notificationConfigurationDTO, BindingResult errors) {
//        if (notificationTemplate.getId().getReminderDefinition() == null && notificationConfigurationDTO.getReminderInterval() != null) {
//            errors.rejectValue("reminderInterval", "forbidden");
//        } else if (notificationTemplate.getId().getReminderDefinition() != null && notificationConfigurationDTO.getReminderInterval() == null) {
//            errors.rejectValue("reminderInterval", "notNull");
//        }
//
//        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localize(resource, userService.getCurrentUser());
//        MailSender mailSender = applicationContext.getBean(MailSender.class).localize(propertyLoader);
//
//        Map<String, Object> model = mailSender.createNotificationModelForValidation(notificationTemplate);
//
//        String subject = null, content = null;
//        try {
//            subject = mailSender.processHeader(notificationTemplate.getId(), notificationConfigurationDTO.getSubject(), model);
//        } catch (Exception e) {
//            errors.rejectValue("subject", "invalid");
//        }
//
//        try {
//            content = mailSender.processContent(notificationTemplate.getId(), notificationConfigurationDTO.getContent(), model, subject);
//        } catch (Exception e) {
//            errors.rejectValue("content", "invalid");
//        }
//
//        if (subject == null || content == null) {
//            throw new PrismValidationException("Invalid template", errors);
//        }
//        return ImmutableMap.of("subject", subject, "content", content);
//    }

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
