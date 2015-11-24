package com.zuehlke.pgadmissions.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.workflow.NotificationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowResourceExecution;
import com.zuehlke.pgadmissions.workflow.user.UserNotificationReassignmentProcessor;

@Entity
@Table(name = "user_notification", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "user_id", "notification_definition_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "user_id", "notification_definition_id" }),
        @UniqueConstraint(columnNames = { "department_id", "user_id", "notification_definition_id" }),
        @UniqueConstraint(columnNames = { "program_id", "user_id", "notification_definition_id" }),
        @UniqueConstraint(columnNames = { "project_id", "user_id", "notification_definition_id" }),
        @UniqueConstraint(columnNames = { "application_id", "user_id", "notification_definition_id" }) })
public class UserNotification extends WorkflowResourceExecution implements UserAssignment<UserNotificationReassignmentProcessor> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_id")
    private System system;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "notification_definition_id", nullable = false)
    private NotificationDefinition notificationDefinition;

    @Column(name = "last_notified_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastNotifiedTimestamp;

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
    public Department getDepartment() {
        return department;
    }

    @Override
    public void setDepartment(Department department) {
        this.department = department;
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
    public Project getProject() {
        return project;
    }

    @Override
    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public void setApplication(Application application) {
        this.application = application;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public NotificationDefinition getNotificationDefinition() {
        return notificationDefinition;
    }

    public void setNotificationDefinition(NotificationDefinition notificationDefinition) {
        this.notificationDefinition = notificationDefinition;
    }

    public DateTime getLastNotifiedTimestamp() {
        return lastNotifiedTimestamp;
    }

    public void setLastNotifiedTimestamp(DateTime lastNotifiedTimestamp) {
        this.lastNotifiedTimestamp = lastNotifiedTimestamp;
    }

    public UserNotification withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public UserNotification withUser(User user) {
        this.user = user;
        return this;
    }

    public UserNotification withNotificationDefinition(NotificationDefinition notificationDefinition) {
        this.notificationDefinition = notificationDefinition;
        return this;
    }

    public UserNotification withLastNotifiedTimestamp(DateTime lastNotifiedTimestamp) {
        this.lastNotifiedTimestamp = lastNotifiedTimestamp;
        return this;
    }

    @Override
    public Class<UserNotificationReassignmentProcessor> getUserReassignmentProcessor() {
        return UserNotificationReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("user", user).addProperty("notificationDefinition", notificationDefinition);
    }

}
