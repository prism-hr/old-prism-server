package com.zuehlke.pgadmissions.dto;

import com.google.common.base.MoreObjects;
import com.zuehlke.pgadmissions.domain.workflow.NotificationConfiguration;

public final class MailMessageDTO {

    private NotificationDefinitionModelDTO modelDTO;

    private NotificationConfiguration configuration;

    public NotificationDefinitionModelDTO getModelDTO() {
        return modelDTO;
    }

    public void setModelDTO(NotificationDefinitionModelDTO modelDTO) {
        this.modelDTO = modelDTO;
    }

    public NotificationConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(NotificationConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("to", modelDTO.getUser().getEmail())
                .add("resourceScope", modelDTO.getResource().getResourceScope())
                .add("resourceId", modelDTO.getResource().getId())
                .add("template", configuration.getNotificationDefinition().getId())
                .toString();
    }

}
