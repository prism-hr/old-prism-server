package com.zuehlke.pgadmissions.domain.workflow;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.System;

@Entity
@Table(name = "notification_configuration", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "system_id", "opportunity_type_id", "notification_definition_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "opportunity_type_id", "notification_definition_id" }),
        @UniqueConstraint(columnNames = { "department_id", "opportunity_type_id", "notification_definition_id" }),
        @UniqueConstraint(columnNames = { "program_id", "notification_definition_id" }),
        @UniqueConstraint(columnNames = { "project_id", "notification_definition_id" }) })
public class NotificationConfiguration extends WorkflowConfiguration<NotificationDefinition> {

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
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "opportunity_type_id")
    private OpportunityType opportunityType;

    @ManyToOne
    @JoinColumn(name = "notification_definition_id", nullable = false)
    private NotificationDefinition definition;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "system_default", nullable = false)
    private Boolean systemDefault;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
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
    public final OpportunityType getOpportunityType() {
        return opportunityType;
    }

    @Override
    public final void setOpportunityType(OpportunityType opportunityType) {
        this.opportunityType = opportunityType;
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

    public NotificationDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(NotificationDefinition definition) {
        this.definition = definition;
    }

    public final String getSubject() {
        return subject;
    }

    public final void setSubject(String subject) {
        this.subject = subject;
    }

    public final String getContent() {
        return content;
    }

    public final void setContent(String content) {
        this.content = content;
    }

    @Override
    public final Boolean getSystemDefault() {
        return systemDefault;
    }

    @Override
    public final void setSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
    }

    public NotificationConfiguration withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public NotificationConfiguration withOpportunityType(OpportunityType opportunityType) {
        this.opportunityType = opportunityType;
        return this;
    }

    public NotificationConfiguration withDefinition(NotificationDefinition definition) {
        this.definition = definition;
        return this;
    }

    public NotificationConfiguration withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public NotificationConfiguration withContent(String content) {
        this.content = content;
        return this;
    }

    public NotificationConfiguration withSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
        return this;
    }

}
