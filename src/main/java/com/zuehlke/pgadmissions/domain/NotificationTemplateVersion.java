package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;

@Entity
@Table(name = "notification_template_version")
public class NotificationTemplateVersion extends WorkflowResourceVersion {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "notification_configuration_id", insertable = false, updatable = false, nullable = false)
    private NotificationConfiguration notificationConfiguration;

    @Column(name = "locale", insertable = false, updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismLocale locale;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "content", nullable = false)
    private String content;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final NotificationConfiguration getNotificationConfiguration() {
        return notificationConfiguration;
    }

    public final void setNotificationConfiguration(NotificationConfiguration notificationConfiguration) {
        this.notificationConfiguration = notificationConfiguration;
    }

    @Override
    public final PrismLocale getLocale() {
        return locale;
    }

    @Override
    public final void setLocale(PrismLocale locale) {
        this.locale = locale;
    }

    public final String getSubject() {
        return subject;
    }

    public final void setSubject(String subject) {
        this.subject = subject;
    }

    public final String getContent() {
        return content;
    }

    public final void setContent(String content) {
        this.content = content;
    }
    
    public NotificationTemplateVersion withNotificationConfiguration(NotificationConfiguration notificationConfiguration) {
        this.notificationConfiguration = notificationConfiguration;
        return this;
    }

    public NotificationTemplateVersion withLocale(PrismLocale locale) {
        this.locale = locale;
        return this;
    }

    public NotificationTemplateVersion withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public NotificationTemplateVersion withContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("notificationConfiguration", notificationConfiguration);
    }

}
