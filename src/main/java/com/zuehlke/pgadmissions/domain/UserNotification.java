package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Entity
@Table(name = "USER_NOTIFICATION", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "notification_template_id" }) })
public class UserNotification implements IUniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "notification_template_id", nullable = false)
    private NotificationTemplate notificationTemplate;

    @Column(name = "notification_last_sent_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate lastSentDate;
    
    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final User getUser() {
        return user;
    }

    public final void setUser(User user) {
        this.user = user;
    }

    public final NotificationTemplate getNotificationTemplate() {
        return notificationTemplate;
    }

    public final void setNotificationTemplate(NotificationTemplate notificationTemplate) {
        this.notificationTemplate = notificationTemplate;
    }

    public final LocalDate getLastSentDate() {
        return lastSentDate;
    }

    public final void setLastSentDate(LocalDate lastSentDate) {
        this.lastSentDate = lastSentDate;
    }
    
    public UserNotification withUser(User user) {
        this.user = user;
        return this;
    }
    
    public UserNotification withNotificationTemplate(NotificationTemplate notificationTemplate) {
        this.notificationTemplate = notificationTemplate;
        return this;
    }
    
    public UserNotification withLastSentDate(LocalDate lastSentDate) {
        this.lastSentDate = lastSentDate;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("user", user);
        properties.put("notificationTemplate", notificationTemplate);
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
    }

}
