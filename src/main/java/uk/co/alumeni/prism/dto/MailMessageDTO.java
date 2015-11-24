package uk.co.alumeni.prism.dto;

import com.google.common.base.MoreObjects;
import uk.co.alumeni.prism.domain.workflow.NotificationConfiguration;

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
