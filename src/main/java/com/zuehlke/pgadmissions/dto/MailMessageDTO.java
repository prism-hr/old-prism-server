package com.zuehlke.pgadmissions.dto;

import java.util.List;

import com.google.common.base.MoreObjects;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.services.builders.pdf.mail.AttachmentInputSource;

public final class MailMessageDTO {

    private NotificationTemplateModelDTO modelDTO;

    private NotificationConfiguration configuration;

    private List<AttachmentInputSource> attachments;

    public NotificationTemplateModelDTO getModelDTO() {
        return modelDTO;
    }

    public void setModelDTO(NotificationTemplateModelDTO modelDTO) {
        this.modelDTO = modelDTO;
    }

    public final NotificationConfiguration getConfiguration() {
        return configuration;
    }

    public final void setConfiguration(NotificationConfiguration configuration) {
        this.configuration = configuration;
    }

    public List<AttachmentInputSource> getAttachments() {
        return attachments;
    }

    public void setAttachments(final List<AttachmentInputSource> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("to", modelDTO.getUser().getEmail())
                .add("resourceScope", modelDTO.getResource().getResourceScope())
                .add("resourceId", modelDTO.getResource().getId())
                .add("template", configuration.getNotificationTemplate().getId())
                .toString();
    }

}
