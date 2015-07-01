package com.zuehlke.pgadmissions.workflow.notification.property;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_VALUE_NOT_PROVIDED;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.CommentInterviewInstruction;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class ApplicationInterviewerInstructionsBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) throws Exception {
        CommentInterviewInstruction interviewInstruction = propertyLoader.getNotificationDefinitionModelDTO().getComment().getInterviewInstruction();
        String instructions = interviewInstruction == null ? null : interviewInstruction.getInterviewerInstructions();
        return instructions == null ? propertyLoader.getPropertyLoader().load(SYSTEM_VALUE_NOT_PROVIDED) : instructions;
    }

}
