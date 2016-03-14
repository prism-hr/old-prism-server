package uk.co.alumeni.prism.dto;

import static com.google.common.collect.Lists.newLinkedList;
import static org.apache.commons.lang.BooleanUtils.isTrue;

import java.util.List;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.workflow.NotificationConfiguration;

import com.google.common.base.MoreObjects;

public final class MailMessageDTO {

    private NotificationDefinitionDTO notificationDefinitionDTO;

    private NotificationConfiguration notificationConfiguration;

    public NotificationDefinitionDTO getNotificationDefinitionDTO() {
        return notificationDefinitionDTO;
    }

    public void setNotificationDefinitionDTO(NotificationDefinitionDTO notificationDefinitionDTO) {
        this.notificationDefinitionDTO = notificationDefinitionDTO;
    }

    public NotificationConfiguration getNotificationConfiguration() {
        return notificationConfiguration;
    }

    public void setNotificationConfiguration(NotificationConfiguration notificationConfiguration) {
        this.notificationConfiguration = notificationConfiguration;
    }

    public List<Document> getDocumentAttachments() {
        List<Document> documentAttachments = newLinkedList();

        Comment comment = notificationDefinitionDTO.getComment();
        if (comment != null && isTrue(comment.getAction().getDocumentCirculationAction())) {
            comment.getDocuments().forEach(document -> documentAttachments.add(document));
        }

        notificationConfiguration.getDocuments().forEach(document -> documentAttachments.add(document.getDocument()));
        return documentAttachments;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("to", notificationDefinitionDTO.getRecipient().getEmail())
                .add("resourceScope", notificationDefinitionDTO.getResource().getResourceScope())
                .add("resourceId", notificationDefinitionDTO.getResource().getId())
                .add("configuration", notificationConfiguration.getDefinition().getId())
                .toString();
    }

}
