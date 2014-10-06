package com.zuehlke.pgadmissions.domain;

import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;

@Entity
@Table(name = "NOTIFICATION_CONFIGURATION", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "locale", "notification_template_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "locale", "notification_template_id" }),
        @UniqueConstraint(columnNames = { "program_id", "locale", "notification_template_id" }) })
public class NotificationConfiguration extends WorkflowResourceLocalized<NotificationTemplateVersion> {

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

    @Column(name = "day_reminder_interval")
    private Integer reminderInterval;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "notification_template_id", nullable = false)
    @MapKeyColumn(name = "locale", nullable = false)
    private Map<PrismLocale, NotificationTemplateVersion> notificationTemplateVersions = Maps.newHashMap();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public NotificationTemplate getNotificationTemplate() {
        return notificationTemplate;
    }

    public void setNotificationTemplate(NotificationTemplate notificationTemplate) {
        this.notificationTemplate = notificationTemplate;
    }

    public Integer getReminderInterval() {
        return reminderInterval;
    }

    public void setReminderInterval(Integer reminderInterval) {
        this.reminderInterval = reminderInterval;
    }
    
    @Override
    public final Map<PrismLocale, NotificationTemplateVersion> getVersions() {
        return notificationTemplateVersions;
    }

    public NotificationConfiguration withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public NotificationConfiguration withNotificationTemplate(NotificationTemplate notificationTemplate) {
        this.notificationTemplate = notificationTemplate;
        return this;
    }

    public NotificationConfiguration withReminderInterval(Integer reminderInterval) {
        this.reminderInterval = reminderInterval;
        return this;
    }
    
    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("notificationTemplate", notificationTemplate);
    }

}
