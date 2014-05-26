package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;

@Entity
@Table(name = "NOTIFICATION_TEMPLATE")
public class NotificationTemplate implements Serializable {

    private static final long serialVersionUID = -3640707667534813533L;

    @Id
    @Column(name = "id")
    @Enumerated(EnumType.STRING)
    private NotificationTemplateId id;

    @Column(name = "notification_type_id")
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @OneToOne
    @JoinColumn(name = "notification_template_version_id")
    private NotificationTemplateVersion version;

    @OneToOne
    @JoinColumn(name = "reminder_notification_template_id")
    private NotificationTemplate reminderTemplate;

    @Column(name = "reminder_interval")
    private Integer reminderInterval;

    @OneToMany(mappedBy = "notificationTemplate")
    @OrderBy("createdTimestamp")
    private Set<NotificationTemplateVersion> versions = Sets.newLinkedHashSet();

    public NotificationTemplateId getId() {
        return id;
    }

    public void setId(NotificationTemplateId id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationTemplateVersion getVersion() {
        return version;
    }

    public void setVersion(NotificationTemplateVersion version) {
        this.version = version;
    }

    public NotificationTemplate getReminderTemplate() {
        return reminderTemplate;
    }

    public void setReminderTemplate(NotificationTemplate reminderTemplate) {
        this.reminderTemplate = reminderTemplate;
    }

    public Integer getReminderInterval() {
        return reminderInterval;
    }

    public void setReminderInterval(Integer reminderInterval) {
        this.reminderInterval = reminderInterval;
    }

    public Set<NotificationTemplateVersion> getVersions() {
        return versions;
    }

    public NotificationTemplate withId(NotificationTemplateId id) {
        this.id = id;
        return this;
    }

    public NotificationTemplate withType(NotificationType type) {
        this.type = type;
        return this;
    }

    public NotificationTemplate withVersion(NotificationTemplateVersion version) {
        this.version = version;
        return this;
    }

    public NotificationTemplate withReminderTemplate(NotificationTemplate reminderTemplate) {
        this.reminderTemplate = reminderTemplate;
        return this;
    }

    public NotificationTemplate withReminderInterval(Integer reminderInterval) {
        this.reminderInterval = reminderInterval;
        return this;
    }

}
