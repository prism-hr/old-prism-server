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

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;

@Entity
@Table(name = "notification_template_version")
public class NotificationTemplateVersion extends WorkflowResourceVersion {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "notification_configuration_id", nullable = false)
    private NotificationConfiguration notificationConfiguration;
    
    @Column(name = "locale", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismLocale locale;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "content", nullable = false)
    private String content;
    
    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "created_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;
    
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

    @Override
    public final Boolean getActive() {
        return active;
    }

    @Override
    public final void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public final DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    @Override
    public final void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public NotificationTemplateVersion withNotificationConfiguration(NotificationConfiguration notificationConfiguration) {
        this.notificationConfiguration = notificationConfiguration;
        return this;
    }
    
    public NotificationTemplateVersion withLocale(PrismLocale locale) {
        this.locale = locale;
        return this;
    }
    
    public NotificationTemplateVersion withActive(Boolean active) {
        this.active = active;
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

    public NotificationTemplateVersion withCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

}
