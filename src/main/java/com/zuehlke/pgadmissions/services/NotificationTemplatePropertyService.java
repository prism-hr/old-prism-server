package com.zuehlke.pgadmissions.services;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.NotificationTemplateModelDTO;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;
import freemarker.template.Template;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.util.Arrays;
import java.util.Map;

@Service
@Transactional
public class NotificationTemplatePropertyService {

    @Value("${application.host}")
    private String host;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FreeMarkerConfig freemarkerConfig;

    public String get(NotificationTemplateModelDTO modelDTO, String[] properties) {
        Object tempObject = modelDTO;
        int i = 0;
        for (String property : properties) {
            if (tempObject == null) {
                String[] subArray = Arrays.copyOf(properties, i);
                throw new NullPointerException("Value of given property (of NotificationTemplateModelDTO) is null: " + Joiner.on(".").join(subArray) + ", trying to get: " + Joiner.on(".").join(properties));
            }
            tempObject = ReflectionUtils.getProperty(tempObject, property);
            i++;
        }
        return (String) tempObject;
    }

    public String getCommentOutcome(NotificationTemplateModelDTO modelDTO) {
        return modelDTO.getComment().getTransitionState().getId().name();
    }

    public String getProjectOrProgramTitle(NotificationTemplateModelDTO modelDTO) {
        return modelDTO.getResource().getApplication().getProjectOrProgramTitle();
    }

    public String getPropertyOrProgramCode(NotificationTemplateModelDTO modelDTO) {
        return modelDTO.getResource().getApplication().getProjectOrProgramCode();
    }

    public String getInterviewDateTime(NotificationTemplateModelDTO modelDTO) {
        LocalDateTime dateTime = modelDTO.getComment().getInterviewDateTime();
        return dateTime.toString();
    }

    public String getInterviewTimeZone(NotificationTemplateModelDTO modelDTO) {
        return modelDTO.getComment().getInterviewTimeZone().getDisplayName();
    }

    public String getIntervieweeInstructions(NotificationTemplateModelDTO modelDTO) {
        String instructions = modelDTO.getComment().getIntervieweeInstructions();
        return instructions != null ? instructions : "Not provided";
    }

    public String getActionControl(NotificationTemplateModelDTO modelDTO) throws Exception {
        Resource resource = modelDTO.getResource();
        String url = host + "/#/" + resource.getResourceScope().getLowerCaseName();
        if (resource.getResourceScope() != PrismScope.SYSTEM) {
            url += "/" + resource.getId();
        }

        PrismAction action = modelDTO.getTransitionAction();
        url += "?action=" + action.name() + "&user=" + modelDTO.getUser().getEmail();
        String declineUrl = action.isDeclinableAction() ? url + "&decline=true" : null;

        return processControlTemplate(resource, url, PrismDisplayProperty.SYSTEM_PROCEED, declineUrl, PrismDisplayProperty.SYSTEM_DECLINE);
    }

    public String getActivateAccountControl(NotificationTemplateModelDTO modelDTO) throws Exception {
        Resource resource = modelDTO.getResource();
        User user = modelDTO.getUser();
        PrismAction action = modelDTO.getTransitionAction();
        String url = host + "/#/activate?activationCode=" + user.getActivationCode() + "&resourceId=" + resource.getId() + "&action=" + action.name();
        return processControlTemplate(resource, url, PrismDisplayProperty.SYSTEM_ACTIVATE_ACCOUNT);
    }

    public String getHomepageControl(NotificationTemplateModelDTO modelDTO) throws Exception {
        Resource resource = modelDTO.getResource();
        return processControlTemplate(resource, host, PrismDisplayProperty.SYSTEM_HOMEPAGE);
    }

    public String getViewEditControl(NotificationTemplateModelDTO modelDTO) throws Exception {
        Resource resource = modelDTO.getResource();
        String url;
        if (resource.getResourceScope() == PrismScope.SYSTEM) {
            url = host + "/#/system";
        } else {
            url = host + "/#/" + resource.getResourceScope().getLowerCaseName() + "s/" + resource.getId() + "/view";
        }

        return processControlTemplate(resource, url, PrismDisplayProperty.SYSTEM_VIEW_EDIT);
    }


    public String getInterviewDirectionsControl(NotificationTemplateModelDTO modelDTO) throws Exception {
        Resource resource = modelDTO.getResource();
        Comment comment = modelDTO.getComment();
        return processControlTemplate(resource, comment.getInterviewLocation(), PrismDisplayProperty.APPLICATION_COMMENT_DIRECTIONS);
    }

    public String getNewPasswordControl(NotificationTemplateModelDTO modelDTO) throws Exception {
        Resource resource = modelDTO.getResource();
        return processControlTemplate(resource, modelDTO.getNewPassword(), PrismDisplayProperty.SYSTEM_NEW_PASSWORD);
    }

    public String getHelpdeskControl(NotificationTemplateModelDTO modelDTO) throws Exception {
        Resource resource = modelDTO.getResource();
        return processControlTemplate(resource, resource.getHelpdesk(), PrismDisplayProperty.SYSTEM_HELPDESK);
    }

    public String getInstitutionHomepageLink(NotificationTemplateModelDTO modelDTO) throws Exception {
        Resource resource = modelDTO.getResource();
        return processControlTemplate(resource, resource.getHomepage(), PrismDisplayProperty.SYSTEM_HOMEPAGE);
    }


    public String getRejectionReason(NotificationTemplateModelDTO modelDTO) {
        Application application = (Application) modelDTO.getResource();
        Comment rejection = commentService.getRejectionComment(application);
        return rejection == null ? null : rejection.getRejectionReasonDisplay();
    }

    private String processControlTemplate(Resource resource, String linkUrl, PrismDisplayProperty linkLabel) throws Exception {
        return processControlTemplate(resource, linkUrl, linkLabel, null, null);
    }

    private String processControlTemplate(Resource resource, String linkUrl, PrismDisplayProperty linkLabel, String declineLinkUrl, PrismDisplayProperty declineLinkLabel) throws Exception {
        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).withResource(resource);
        Map<String, Object> model = Maps.newHashMap();
        ImmutableMap<String, String> link = ImmutableMap.of("url", linkUrl, "label", propertyLoader.load(linkLabel));
        model.put("link", link);

        if (declineLinkUrl != null) {
            ImmutableMap<String, String> declineLink = ImmutableMap.of("url", declineLinkUrl, "label", propertyLoader.load(declineLinkLabel));
            model.put("declineLink", declineLink);
        }

        String emailControlTemplate = Resources.toString(Resources.getResource("email/email_control_template.ftl"), Charsets.UTF_8);
        Template template = new Template("Email control template", emailControlTemplate, freemarkerConfig.getConfiguration());

        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }


}
