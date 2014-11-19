package com.zuehlke.pgadmissions.rest.resource;

import java.util.Map;

import javax.validation.Valid;

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

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.NotificationDefinition;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import com.zuehlke.pgadmissions.mail.MailSender;
import com.zuehlke.pgadmissions.rest.ResourceDescriptor;
import com.zuehlke.pgadmissions.rest.RestApiUtils;
import com.zuehlke.pgadmissions.rest.dto.NotificationConfigurationDTO;
import com.zuehlke.pgadmissions.rest.representation.configuration.NotificationConfigurationRepresentation;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@RestController
@RequestMapping("api/{resourceScope:programs|institutions|systems}/{resourceId}/configuration/notifications/{notificationTemplateId}")
public class NotificationResource {

    @Autowired
    private EntityService entityService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private Mapper dozerBeanMapper;

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(method = RequestMethod.GET)
    public NotificationConfigurationRepresentation getNotificationConfiguration(
            @ModelAttribute ResourceDescriptor resourceDescriptor,
            @PathVariable Integer resourceId, @PathVariable String notificationTemplateId, @RequestParam(required = false) PrismLocale locale,
            @RequestParam(required = false) PrismProgramType programType) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        NotificationDefinition template = notificationService.getById(PrismNotificationDefinition.valueOf(notificationTemplateId));
        return dozerBeanMapper.map(notificationService.getNotificationConfiguration(resource, locale, programType, template), NotificationConfigurationRepresentation.class);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void updateNotificationConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
            @PathVariable String notificationTemplateId, @RequestParam(required = false) PrismLocale locale,
            @RequestParam(required = false) PrismProgramType programType, @Valid @RequestBody NotificationConfigurationDTO notificationConfigurationDTO,
            BindingResult validationErrors) throws Exception {
        NotificationDefinition template = notificationService.getById(PrismNotificationDefinition.valueOf(notificationTemplateId));
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);

        validateTemplate(resource, template, notificationConfigurationDTO, validationErrors);
        if (validationErrors.hasErrors()) {
            throw new PrismValidationException("Invalid notification configuration", validationErrors);
        }

        notificationService.updateNotificationConfiguration(resource, locale, programType, template, notificationConfigurationDTO);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void restoreDefaultConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
            @PathVariable String notificationTemplateId, @RequestParam(required = false) PrismLocale locale,
            @RequestParam(required = false) PrismProgramType programType) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        NotificationDefinition template = notificationService.getById(PrismNotificationDefinition.valueOf(notificationTemplateId));
        notificationService.restoreDefaultNotificationConfiguration(resource, locale, programType, template);
    }

    @RequestMapping(value = "/descendants", method = RequestMethod.DELETE)
    public void restoreGlobalConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
            @PathVariable String notificationTemplateId, @RequestParam(required = false) PrismLocale locale,
            @RequestParam(required = false) PrismProgramType programType) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        NotificationDefinition template = notificationService.getById(PrismNotificationDefinition.valueOf(notificationTemplateId));
        notificationService.restoreGlobalNotificationConfiguration(resource, locale, programType, template);
    }

    private Map<String, String> validateTemplate(Resource resource, NotificationDefinition notificationTemplate,
            NotificationConfigurationDTO notificationConfigurationDTO, BindingResult errors) {
        if (notificationTemplate.getId().getReminderDefinition() == null && notificationConfigurationDTO.getReminderInterval() != null) {
            errors.rejectValue("reminderInterval", "forbidden");
        } else if (notificationTemplate.getId().getReminderDefinition() != null && notificationConfigurationDTO.getReminderInterval() == null) {
            errors.rejectValue("reminderInterval", "notNull");
        }

        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localize(resource, userService.getCurrentUser());
        MailSender mailSender = applicationContext.getBean(MailSender.class).localize(propertyLoader);

        Map<String, Object> model = mailSender.createNotificationModelForValidation(notificationTemplate);

        String subject = null, content = null;
        try {
            subject = mailSender.processHeader(notificationTemplate.getId(), notificationConfigurationDTO.getSubject(), model);
        } catch (Exception e) {
            errors.rejectValue("subject", "invalid");
        }

        try {
            content = mailSender.processContent(notificationTemplate.getId(), notificationConfigurationDTO.getContent(), model, subject);
        } catch (Exception e) {
            errors.rejectValue("content", "invalid");
        }

        if (subject == null || content == null) {
            throw new PrismValidationException("Invalid template", errors);
        }
        return ImmutableMap.of("subject", subject, "content", content);
    }

    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceScope) {
        return RestApiUtils.getResourceDescriptor(resourceScope);
    }

}
