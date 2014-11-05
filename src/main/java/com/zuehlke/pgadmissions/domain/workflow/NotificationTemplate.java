package com.zuehlke.pgadmissions.domain.workflow;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationPurpose;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;

@Entity
@Table(name = "NOTIFICATION_TEMPLATE")
public class NotificationTemplate extends WorkflowDefinition {

    @Id
    @Column(name = "id")
    @Enumerated(EnumType.STRING)
    private PrismNotificationTemplate id;

    @Column(name = "notification_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismNotificationType notificationType;
    
    @Column(name = "notification_purpose", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismNotificationPurpose notificationPurpose;
    
    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @OneToOne
    @JoinColumn(name = "reminder_notification_template_id")
    private NotificationTemplate reminderTemplate;
    
    @OneToMany(mappedBy = "notificationTemplate")
    private Set<NotificationConfiguration> reminderIntervals = Sets.newHashSet();

    @Override
    public PrismNotificationTemplate getId() {
        return id;
    }

    public void setId(PrismNotificationTemplate id) {
        this.id = id;
    }

    public PrismNotificationType getNotificationType() {
        return notificationType;
    }
    
    public void setNotificationType(PrismNotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public PrismNotificationPurpose getNotificationPurpose() {
        return notificationPurpose;
    }

    public void setNotificationPurpose(PrismNotificationPurpose notificationPurpose) {
        this.notificationPurpose = notificationPurpose;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }
    
    public NotificationTemplate getReminderTemplate() {
        return reminderTemplate;
    }

    public void setReminderTemplate(NotificationTemplate reminderTemplate) {
        this.reminderTemplate = reminderTemplate;
    }

    public Set<NotificationConfiguration> getReminderIntervals() {
        return reminderIntervals;
    }

    public NotificationTemplate withId(PrismNotificationTemplate id) {
        this.id = id;
        return this;
    }

    public NotificationTemplate withNotificationType(PrismNotificationType notificationType) {
        this.notificationType = notificationType;
        return this;
    }
    
    public NotificationTemplate withNotificationPurpose(PrismNotificationPurpose notificationPurpose) {
        this.notificationPurpose = notificationPurpose;
        return this;
    }
    
    public NotificationTemplate withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

    public NotificationTemplate withReminderTemplate(NotificationTemplate reminderTemplate) {
        this.reminderTemplate = reminderTemplate;
        return this;
    }
    
}
