package com.zuehlke.pgadmissions.workflow.notification.property;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_VALUE_NOT_PROVIDED;

import org.springframework.stereotype.Component;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentInterviewInstruction;
import com.zuehlke.pgadmissions.dto.NotificationDefinitionModelDTO;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class ApplicationInterviewerInstructionsBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) throws Exception {
        NotificationDefinitionModelDTO model = propertyLoader.getNotificationDefinitionModelDTO();
        Comment comment = model.getComment();

        if (!Objects.equal(model.getUser(), comment.getApplication().getUser())) {
            CommentInterviewInstruction interviewInstruction = comment.getInterviewInstruction();
            String instructions = interviewInstruction == null ? null : interviewInstruction.getInterviewerInstructions();
            return instructions == null ? propertyLoader.getPropertyLoader().loadLazy(SYSTEM_VALUE_NOT_PROVIDED) : instructions;
        }

        return null;
    }

}
