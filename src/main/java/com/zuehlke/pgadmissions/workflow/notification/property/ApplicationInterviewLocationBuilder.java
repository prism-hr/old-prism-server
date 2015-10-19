package com.zuehlke.pgadmissions.workflow.notification.property;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_DIRECTIONS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_DIRECTIONS_NOT_PROVIDED;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.CommentInterviewInstruction;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class ApplicationInterviewLocationBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        CommentInterviewInstruction interviewInstruction = propertyLoader.getNotificationDefinitionDTO().getComment().getInterviewInstruction();
        String interviewLocation = interviewInstruction == null ? null : interviewInstruction.getInterviewLocation();
        return interviewLocation == null ? "<p>" + propertyLoader.getPropertyLoader().loadLazy(APPLICATION_COMMENT_DIRECTIONS_NOT_PROVIDED) + "</p>"
                : propertyLoader.getRedirectionControl(interviewLocation, APPLICATION_COMMENT_DIRECTIONS);
    }

}
