package com.zuehlke.pgadmissions.rest.resource;

import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplateProperty;
import com.zuehlke.pgadmissions.dto.NotificationTemplateModelDTO;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import com.zuehlke.pgadmissions.mail.MailSender;
import com.zuehlke.pgadmissions.rest.ResourceDescriptor;
import com.zuehlke.pgadmissions.rest.RestApiUtils;
import com.zuehlke.pgadmissions.rest.dto.NotificationConfigurationDTO;
import com.zuehlke.pgadmissions.rest.representation.NotificationConfigurationRepresentation;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.NotificationService;
import freemarker.template.TemplateException;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
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
            @RequestBody NotificationConfigurationDTO notificationConfigurationDTO) throws Exception {

        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);

        // TODO save notification version
    }

    @RequestMapping(value = "/notificationTemplates/{notificationTemplateId}/preview", method = RequestMethod.POST)
    public NotificationConfigurationRepresentation getNotificationTemplatePreview(
            @ModelAttribute ResourceDescriptor resourceDescriptor,
            @PathVariable String notificationTemplateId,
            @RequestBody NotificationConfigurationDTO notificationConfigurationDTO,
            BindingResult validationErrors) throws Exception {
        NotificationTemplate template = notificationService.getById(PrismNotificationTemplate.valueOf(notificationTemplateId));
        Map<String, Object> model = mailSender.createNotificationModel(template, createSampleModelDTO());
        String subject = null, content = null;
        try {
            subject = mailSender.processTemplate(template.getId(), notificationConfigurationDTO.getSubject(), model);
        } catch (Exception e) {
            validationErrors.rejectValue("subject", "invalid");
        }

        try {
            content = mailSender.processTemplate(template.getId(), notificationConfigurationDTO.getContent(), model);
        } catch (Exception e) {
            validationErrors.rejectValue("content", "invalid");
        }

        if (subject == null || content == null) {
            throw new PrismValidationException("Invalid template", validationErrors);
        }
        return new NotificationConfigurationRepresentation(subject, content);
    }

    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceScope) {
        return RestApiUtils.getResourceDescriptor(resourceScope);
    }

    @PostConstruct
    public NotificationTemplateModelDTO createSampleModelDTO() {
        User user = new User().withFirstName("Franciszek").withLastName("Pieczka").withEmail("franek@pieczka.pl");
        User sender = new User().withFirstName("Jozef").withLastName("Oleksy").withEmail("jozef@oleksy.pl");

        System system = new System().withTitle("PRiSM");
        Program program = new Program().withTitle("Sample program").withCode("PROGRAM1");
        Project project = new Project().withTitle("Sample Project").withCode("PROJECT1");
        Application application = new Application().withCode("APP1").withProject(project).withProgram(program).withSystem(system).withUser(user);
        NotificationTemplateModelDTO modelDTO = new NotificationTemplateModelDTO(user, application, sender);
        modelDTO.withTransitionAction(PrismAction.APPLICATION_ASSIGN_REVIEWERS).withErrorMessage("An error occurred")
                .withNewPassword("s3cr3t").withRecommendations("recommendations1");
        return modelDTO;
    }
}
