package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "notification_template_version")
public class NotificationTemplateVersion {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "notification_template_id", nullable = false)
    private NotificationTemplate notificationTemplate;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_timestamp", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTimestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public NotificationTemplate getNotificationTemplate() {
        return notificationTemplate;
    }

    public void setNotificationTemplate(NotificationTemplate notificationTemplate) {
        this.notificationTemplate = notificationTemplate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
    
    public NotificationTemplateVersion withId(Integer id) {
        this.id = id;
        return this;
    }

    public NotificationTemplateVersion withNotificationTemplate(NotificationTemplate notificationTemplate) {
        this.notificationTemplate = notificationTemplate;
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

    public NotificationTemplateVersion withCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

}
