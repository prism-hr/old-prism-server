package com.zuehlke.pgadmissions.services;

import com.google.common.base.Joiner;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.dto.NotificationTemplateModelDTO;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@Transactional
public class NotificationTemplatePropertyService {

    @Value("${application.host}")
    private String host;

    @Autowired
    private CommentService commentService;

    public String get(NotificationTemplateModelDTO modelDTO, String[] properties) {
        Object tempObject = modelDTO;
        int i = 0;
        for (String property : properties) {
            if (tempObject == null) {
                String[] subArray = Arrays.copyOf(properties, i);
                throw new NullPointerException("Value of given property (of NotificationTemplateModelDTO) is null: " + Joiner.on(".").join(subArray));
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

    public String getInterviewLocation(NotificationTemplateModelDTO modelDTO) {
        String location = modelDTO.getComment().getInterviewLocation();
        // TODO wrap with <a href>
        return location;
    }


    public String getActionControl(NotificationTemplateModelDTO modelDTO) {
        // TODO
        return host;
    }

    public String getHomepageControl(NotificationTemplateModelDTO modelDTO) {
        // TODO create <a href> with homepage
        return host;
    }

    public String getViewEditControl(NotificationTemplateModelDTO modelDTO) {
        Resource resource = modelDTO.getResource();
        String url;
        if (resource.getResourceScope() == PrismScope.SYSTEM) {
            url = host + "/#/system";
        } else {
            url = host + "/#/" + resource.getResourceScope().getLowerCaseName() + "s/" + resource.getId() + "/view";
        }
        return "<a href=\"" + url + "\">View</a>";
    }

    public String getDirectionsControl(NotificationTemplateModelDTO modelDTO) {
        // TODO
        return host;
    }

    public String getNewPasswordControl(NotificationTemplateModelDTO modelDTO) {
        // TODO
        return host;
    }

    public String getHelpDeskLink(NotificationTemplateModelDTO modelDTO) {
        // TODO
        return host;
    }

    public String getInstitutionHomepageLink(NotificationTemplateModelDTO modelDTO) {
        // TODO
        return host;
    }



    public String getRejectionReason(NotificationTemplateModelDTO modelDTO) {
        Application application = (Application) modelDTO.getResource();
        Comment rejection = commentService.getRejectionComment(application);
        return rejection == null ? null : rejection.getRejectionReasonDisplay();
    }

}
