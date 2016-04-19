package uk.co.alumeni.prism.workflow.notification.property;

import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_VALUE_NOT_PROVIDED;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.comment.CommentInterviewInstruction;
import uk.co.alumeni.prism.dto.NotificationDefinitionDTO;
import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

import com.google.common.base.Objects;

@Component
public class ApplicationInterviewerInstructionsBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        NotificationDefinitionDTO notificationDefinitionDTO = propertyLoader.getNotificationDefinitionDTO();
        Comment comment = notificationDefinitionDTO.getComment();

        if (!Objects.equal(notificationDefinitionDTO.getRecipient(), comment.getApplication().getUser())) {
            CommentInterviewInstruction interviewInstruction = comment.getInterviewInstruction();
            String instructions = interviewInstruction == null ? null : interviewInstruction.getInterviewerInstructions();
            return instructions == null ? propertyLoader.getPropertyLoader().loadLazy(SYSTEM_VALUE_NOT_PROVIDED) : instructions;
        }

        return null;
    }

}
