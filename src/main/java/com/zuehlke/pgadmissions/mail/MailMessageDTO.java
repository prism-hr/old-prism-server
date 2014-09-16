package com.zuehlke.pgadmissions.mail;

import java.util.List;
import java.util.Map;

import com.google.common.base.MoreObjects;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSource;

public final class MailMessageDTO {

    private String replyToAddress;

    private User to;

    private Map<String, Object> model;

    private NotificationTemplateVersion template;

    private List<PdfAttachmentInputSource> attachments;

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public void setModel(final Map<String, Object> model) {
        this.model = model;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public NotificationTemplateVersion getTemplate() {
        return template;
    }

    public void setTemplate(NotificationTemplateVersion template) {
        this.template = template;
    }

    public List<PdfAttachmentInputSource> getAttachments() {
        return attachments;
    }

    public void setAttachments(final List<PdfAttachmentInputSource> attachments) {
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
        return MoreObjects.toStringHelper(this)
                .add("to", to.getEmail())
                .add("actionUrl", model.get("actionUrl"))
                .add("resourceId", model.get("resourceId"))
                .add("template", template.getNotificationTemplate().getId())
                .toString();
    }

}
