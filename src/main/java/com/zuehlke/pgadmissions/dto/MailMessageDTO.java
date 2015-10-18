package com.zuehlke.pgadmissions.dto;

import com.google.common.base.MoreObjects;
import com.zuehlke.pgadmissions.domain.workflow.NotificationConfiguration;

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
                .add("to", notificationDefinitionDTO.getUser().getEmail())
                .add("resourceScope", notificationDefinitionDTO.getResource().getResourceScope())
                .add("resourceId", notificationDefinitionDTO.getResource().getId())
                .add("template", notificationConfiguration.getDefinition().getId())
                .toString();
    }

}
