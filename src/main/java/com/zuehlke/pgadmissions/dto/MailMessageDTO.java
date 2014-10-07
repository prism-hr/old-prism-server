package com.zuehlke.pgadmissions.dto;

import java.util.List;
import java.util.Map;

import com.google.common.base.MoreObjects;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplateProperty;
import com.zuehlke.pgadmissions.services.builders.pdf.mail.AttachmentInputSource;

public final class MailMessageDTO {

    private String replyToAddress;

    private User to;

    private NotificationConfiguration configuration;

    private Map<PrismNotificationTemplateProperty, Object> model;

    private List<AttachmentInputSource> attachments;

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public final NotificationConfiguration getConfiguration() {
        return configuration;
    }

    public final void setConfiguration(NotificationConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setModel(final Map<PrismNotificationTemplateProperty, Object> model) {
        this.model = model;
    }

    public Map<PrismNotificationTemplateProperty, Object> getModel() {
        return model;
    }

    public List<AttachmentInputSource> getAttachments() {
        return attachments;
    }

    public void setAttachments(final List<AttachmentInputSource> attachments) {
        this.attachments = attachments;
    }

    public String getReplyToAddress() {
        return replyToAddress;
    }

    public void setReplyToAddress(final String replyToAddress) {
        this.replyToAddress = replyToAddress;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("to", to.getEmail()).add("actionUrl", model.get("actionUrl")).add("resourceId", model.get("resourceId"))
                .add("template", configuration.getNotificationTemplate().getId()).toString();
    }

}
