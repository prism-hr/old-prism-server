package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Entity
@Table(name = "NOTIFICATION_CONFIGURATION", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "notification_template_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "notification_template_id" }),
        @UniqueConstraint(columnNames = { "program_id", "notification_template_id" }) })
public class NotificationConfiguration implements IUniqueResource {

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
    
    @OneToOne
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

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
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
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties1 = Maps.newHashMap();
        properties1.put("system", system);
        properties1.put("notificationTemplate", notificationTemplate);
        propertiesWrapper.add(properties1);
        HashMap<String, Object> properties2 = Maps.newHashMap();
        properties2.put("institution", institution);
        properties2.put("notificationTemplate", notificationTemplate);
        propertiesWrapper.add(properties2);
        HashMap<String, Object> properties3 = Maps.newHashMap();
        properties3.put("program", program);
        properties3.put("notificationTemplate", notificationTemplate);
        propertiesWrapper.add(properties3);
        return new ResourceSignature(propertiesWrapper);
    }

}
