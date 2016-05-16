package uk.co.alumeni.prism.workflow.notification.property;

import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_VALUE_NOT_PROVIDED;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.comment.CommentInterviewInstruction;
import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

@Component
public class ApplicationIntervieweeInstructionsBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        CommentInterviewInstruction interviewInstruction = propertyLoader.getNotificationDefinitionDTO().getComment().getInterviewInstruction();
        String instructions = interviewInstruction == null ? null : interviewInstruction.getIntervieweeInstructions();
        return instructions == null ? propertyLoader.getPropertyLoader().loadLazy(SYSTEM_VALUE_NOT_PROVIDED) : instructions;
    }

}
