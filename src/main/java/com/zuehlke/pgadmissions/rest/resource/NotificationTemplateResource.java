package com.zuehlke.pgadmissions.rest.resource;

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
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

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
    public NotificationConfigurationRepresentation getNotificationTemplateVersion(@ModelAttribute ResourceDescriptor resourceDescriptor,
                                                                                  @PathVariable Integer resourceId, @PathVariable String notificationTemplateId) throws Exception {

        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        NotificationTemplate template = notificationService.getById(PrismNotificationTemplate.valueOf(notificationTemplateId));
        return dozerBeanMapper.map(notificationService.getConfiguration(resource, template), NotificationConfigurationRepresentation.class);
    }

    @RequestMapping(value = "/{resourceId}/notificationTemplates/{notificationTemplateId}", method = RequestMethod.PUT)
    public void updateNotificationConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
                                                @PathVariable String notificationTemplateId, @Valid @RequestBody NotificationConfigurationDTO notificationConfigurationDTO,
                                                BindingResult validationErrors) throws Exception {
        NotificationTemplate template = notificationService.getById(PrismNotificationTemplate.valueOf(notificationTemplateId));

        validate(template, notificationConfigurationDTO, validationErrors);
        if (validationErrors.hasErrors()) {
            throw new PrismValidationException("Invalid notification configuration", validationErrors);
        }

        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        notificationService.saveConfiguration(resource, template, notificationConfigurationDTO);
    }

    @RequestMapping(value = "/{resourceId}/notificationTemplates/{notificationTemplateId}", method = RequestMethod.DELETE)
    public void removeNotificationConfiguration(@ModelAttribute ResourceDescriptor resourceDescriptor,
                                                @PathVariable Integer resourceId,
                                                @PathVariable String notificationTemplateId) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        NotificationTemplate template = notificationService.getById(PrismNotificationTemplate.valueOf(notificationTemplateId));
        notificationService.removeLocalizedConfiguration(resource, template);
    }

    private Map<String, String> validate(NotificationTemplate template, NotificationConfigurationDTO notificationConfigurationDTO, BindingResult errors) {
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
