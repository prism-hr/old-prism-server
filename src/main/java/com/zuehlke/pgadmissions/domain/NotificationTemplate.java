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
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationType;

@Entity
@Table(name = "NOTIFICATION_TEMPLATE")
public class NotificationTemplate implements Serializable {

    private static final long serialVersionUID = -3640707667534813533L;

    @Id
    @Column(name = "id")
    @Enumerated(EnumType.STRING)
    private PrismNotificationTemplate id;

    @Column(name = "notification_type_id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismNotificationType notificationType;

    @OneToOne
    @JoinColumn(name = "notification_template_version_id")
    private NotificationTemplateVersion version;

    @OneToOne
    @JoinColumn(name = "reminder_notification_template_id")
    private NotificationTemplate reminderTemplate;
    
    @OneToMany(mappedBy = "notificationTemplate")
    @OrderBy("createdTimestamp DESC")
    private Set<NotificationTemplateVersion> versions = Sets.newLinkedHashSet();
    
    @OneToMany(mappedBy = "notificationTemplate")
    private Set<NotificationReminderInterval> reminderIntervals = Sets.newHashSet();

    public PrismNotificationTemplate getId() {
        return id;
    }

    public void setId(PrismNotificationTemplate id) {
        this.id = id;
    }

    public PrismNotificationType getNotificationType() {
        return notificationType;
    }

    public void setType(PrismNotificationType notificationType) {
        this.notificationType = notificationType;
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

    public Set<NotificationTemplateVersion> getVersions() {
        return versions;
    }

    public Set<NotificationReminderInterval> getReminderIntervals() {
        return reminderIntervals;
    }

    public NotificationTemplate withId(PrismNotificationTemplate id) {
        this.id = id;
        return this;
    }

    public NotificationTemplate withType(PrismNotificationType notificationType) {
        this.notificationType = notificationType;
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

}
