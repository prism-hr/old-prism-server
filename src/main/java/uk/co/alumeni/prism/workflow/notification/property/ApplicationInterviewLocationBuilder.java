package uk.co.alumeni.prism.workflow.notification.property;

import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_DIRECTIONS;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_DIRECTIONS_NOT_PROVIDED;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.comment.CommentInterviewInstruction;
import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

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
