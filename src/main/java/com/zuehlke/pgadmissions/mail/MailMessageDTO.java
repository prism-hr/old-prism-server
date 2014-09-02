package com.zuehlke.pgadmissions.mail;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSource;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.util.List;
import java.util.Map;

public final class MailMessageDTO {

    private Application application;

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

    public Application getApplication() {
        return application;
    }

    public void setApplication(final Application application) {
        this.application = application;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
