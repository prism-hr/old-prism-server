package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationType;

@Entity
@Table(name = "NOTIFICATION_TEMPLATE")
public class NotificationTemplate implements IUniqueResource {

    @Id
    @Column(name = "id")
    @Enumerated(EnumType.STRING)
    private PrismNotificationTemplate id;

    @Column(name = "notification_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismNotificationType notificationType;

    @OneToOne
    @JoinColumn(name = "reminder_notification_template_id")
    private NotificationTemplate reminderTemplate;
    
    @OneToMany(mappedBy = "notificationTemplate")
    private Set<NotificationConfiguration> reminderIntervals = Sets.newHashSet();
    
    public NotificationTemplate() {
    }
    
    public NotificationTemplate(PrismNotificationTemplate id) {
        this.id = id;
    }

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

    public NotificationTemplate withType(PrismNotificationType notificationType) {
        this.notificationType = notificationType;
        return this;
    }

    public NotificationTemplate withReminderTemplate(NotificationTemplate reminderTemplate) {
        this.reminderTemplate = reminderTemplate;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("id", id);
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
    }
    
}
