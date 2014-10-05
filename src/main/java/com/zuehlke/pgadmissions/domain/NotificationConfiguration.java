package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "NOTIFICATION_CONFIGURATION", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "notification_template_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "notification_template_id" }),
        @UniqueConstraint(columnNames = { "program_id", "notification_template_id" }) })
public class NotificationConfiguration extends WorkflowResourceConfiguration {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "system_id")
    private System system;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne
    @JoinColumn(name = "notification_template_id")
    private NotificationTemplate notificationTemplate;
    
    @ManyToOne
    @JoinColumn(name = "notification_template_version_id")
    private NotificationTemplateVersion notificationTemplateVersion;

    @Column(name = "day_reminder_interval")
    private Integer reminderInterval;

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

    public NotificationTemplateVersion getNotificationTemplateVersion() {
        return notificationTemplateVersion;
    }

    public void setNotificationTemplateVersion(NotificationTemplateVersion notificationTemplateVersion) {
        this.notificationTemplateVersion = notificationTemplateVersion;
    }

    public Integer getReminderInterval() {
        return reminderInterval;
    }

    public void setReminderInterval(Integer reminderInterval) {
        this.reminderInterval = reminderInterval;
    }

    public NotificationConfiguration withSystem(System system) {
        this.system = system;
        return this;
    }
    
    public NotificationConfiguration withNotificationTemplate(NotificationTemplate notificationTemplate) {
        this.notificationTemplate = notificationTemplate;
        return this;
    }
    
    public NotificationConfiguration withNotificationTemplateVersion(NotificationTemplateVersion notificationTemplateVersion) {
        this.notificationTemplateVersion = notificationTemplateVersion;
        return this;
    }
    
    public NotificationConfiguration withReminderInterval(Integer reminderInterval) {
        this.reminderInterval = reminderInterval;
        return this;
    }
    
    @Override
    public System getSystem() {
        return system;
    }

    @Override
    public void setSystem(System system) {
        this.system = system;
    }

    @Override
    public Institution getInstitution() {
        return institution;
    }

    @Override
    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    @Override
    public Program getProgram() {
        return program;
    }

    @Override
    public void setProgram(Program program) {
        this.program = program;
    }
    
    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("notificationTemplate", notificationTemplate);
    }

}
