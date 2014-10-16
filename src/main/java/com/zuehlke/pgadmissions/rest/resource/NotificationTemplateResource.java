package com.zuehlke.pgadmissions.rest.resource;

import java.util.Map;

import javax.validation.Valid;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.NotificationTemplate;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import com.zuehlke.pgadmissions.mail.MailSender;
import com.zuehlke.pgadmissions.rest.ResourceDescriptor;
import com.zuehlke.pgadmissions.rest.RestApiUtils;
import com.zuehlke.pgadmissions.rest.dto.NotificationConfigurationDTO;
import com.zuehlke.pgadmissions.rest.representation.NotificationConfigurationRepresentation;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.NotificationService;

@RestController
@RequestMapping("api/{resourceScope:programs|institutions|systems}")
public class NotificationTemplateResource {

    @Autowired
    private EntityService entityService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private Mapper dozerBeanMapper;

    @RequestMapping(value = "/{resourceId}/notificationTemplates/{notificationTemplateId}", method = RequestMethod.GET)
    public NotificationConfigurationRepresentation getNotificationTemplateVersion(
            @ModelAttribute ResourceDescriptor resourceDescriptor,
            @PathVariable Integer resourceId,
            @PathVariable String notificationTemplateId) throws Exception {

        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        NotificationTemplate template = notificationService.getById(PrismNotificationTemplate.valueOf(notificationTemplateId));
        return dozerBeanMapper.map(notificationService.getConfiguration(resource, template), NotificationConfigurationRepresentation.class);
    }

    @RequestMapping(value = "/{resourceId}/notificationTemplates/{notificationTemplateId}", method = RequestMethod.PUT)
    public void updateNotificationTemplateVersion(
            @ModelAttribute ResourceDescriptor resourceDescriptor,
            @PathVariable Integer resourceId,
            @PathVariable String notificationTemplateId,
            @Valid @RequestBody NotificationConfigurationDTO notificationConfigurationDTO,
            BindingResult validationErrors) throws Exception {
        NotificationTemplate template = notificationService.getById(PrismNotificationTemplate.valueOf(notificationTemplateId));

        validateAndCreatePreview(template, notificationConfigurationDTO, validationErrors);
        if (validationErrors.hasErrors()) {
            throw new PrismValidationException("Invalid notification configuration", validationErrors);
        }

        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        notificationService.saveConfiguration(resource, template, notificationConfigurationDTO);

        // TODO save notification version
    }

    @RequestMapping(value = "/notificationTemplates/{notificationTemplateId}/preview", method = RequestMethod.POST)
    public Map<String, String> getNotificationTemplatePreview(
            @ModelAttribute ResourceDescriptor resourceDescriptor,
            @PathVariable String notificationTemplateId,
            @Valid @RequestBody NotificationConfigurationDTO notificationConfigurationDTO,
            BindingResult validationErrors) throws Exception {
        NotificationTemplate template = notificationService.getById(PrismNotificationTemplate.valueOf(notificationTemplateId));

        Map<String, String> preview = validateAndCreatePreview(template, notificationConfigurationDTO, validationErrors);
        if (validationErrors.hasErrors()) {
            throw new PrismValidationException("Invalid notification configuration", validationErrors);
        }
        return preview;
    }

    private Map<String, String> validateAndCreatePreview(NotificationTemplate template, NotificationConfigurationDTO notificationConfigurationDTO, BindingResult errors) {
        if (template.getId().getReminderTemplate() == null && notificationConfigurationDTO.getReminderInterval() != null) {
            errors.rejectValue("reminderInterval", "forbidden");
        } else if (template.getId().getReminderTemplate() != null && notificationConfigurationDTO.getReminderInterval() == null) {
            errors.rejectValue("reminderInterval", "notNull");
        }

        Map<String, Object> model = mailSender.createNotificationModelForValidation(template);
        
        String subject = null, content = null;
        try {
            subject = mailSender.processHeader(template.getId(), notificationConfigurationDTO.getSubject(), model);
        } catch (Exception e) {
            errors.rejectValue("subject", "invalid");
        }

        try {
            content = mailSender.processContent(template.getId(), notificationConfigurationDTO.getContent(), model, subject);
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
