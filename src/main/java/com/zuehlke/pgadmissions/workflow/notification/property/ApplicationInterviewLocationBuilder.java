package com.zuehlke.pgadmissions.workflow.notification.property;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_DIRECTIONS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_DIRECTIONS_NOT_PROVIDED;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.CommentApplicationInterviewInstruction;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class ApplicationInterviewLocationBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) throws Exception {
        CommentApplicationInterviewInstruction interviewInstruction = propertyLoader.getNotificationDefinitionModelDTO().getComment().getInterviewInstruction();
        String interviewLocation = interviewInstruction == null ? null : interviewInstruction.getInterviewLocation();
        return interviewLocation == null ? "<p>" + propertyLoader.getPropertyLoader().load(APPLICATION_COMMENT_DIRECTIONS_NOT_PROVIDED) + "</p>"
                : propertyLoader.buildRedirectionControl(interviewLocation, APPLICATION_COMMENT_DIRECTIONS);
    }

}