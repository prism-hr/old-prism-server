package com.zuehlke.pgadmissions.domain.workflow;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;

@Entity
@Table(name = "WORKFLOW_PROPERTY_CONFIGURATION", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"system_id", "locale", "program_type", "workflow_property_definition_id", "version"}),
        @UniqueConstraint(columnNames = {"institution_id", "program_type", "workflow_property_definition_id", "version"}),
        @UniqueConstraint(columnNames = {"program_id", "locale", "workflow_property_definition_id", "version"}), 
        @UniqueConstraint(columnNames = {"project_id", "locale", "workflow_property_definition_id", "version"}) })
public class WorkflowPropertyConfiguration extends WorkflowConfigurationVersioned {

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
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "locale")
    @Enumerated(EnumType.STRING)
    private PrismLocale locale;

    @Column(name = "program_type")
    @Enumerated(EnumType.STRING)
    private PrismProgramType programType;

    @ManyToOne
    @JoinColumn(name = "workflow_property_definition_id", nullable = false)
    private WorkflowPropertyDefinition workflowPropertyDefinition;

    @Column(name = "version")
    private Integer version;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "required")
    private Boolean required;

    @Column(name = "minimum")
    private Integer minimum;

    @Column(name = "maximum")
    private Integer maximum;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "system_default", nullable = false)
    private Boolean systemDefault;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    @Override
    public final System getSystem() {
        return system;
    }

    @Override
    public final void setSystem(System system) {
        this.system = system;
    }

    @Override
    public final Institution getInstitution() {
        return institution;
    }

    @Override
    public final void setInstitution(Institution institution) {
        this.institution = institution;
    }

    @Override
    public final Program getProgram() {
        return program;
    }

    @Override
    public final void setProgram(Program program) {
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
    public final PrismLocale getLocale() {
        return locale;
    }

    @Override
    public final void setLocale(PrismLocale locale) {
        this.locale = locale;
    }

    @Override
    public final PrismProgramType getProgramType() {
        return programType;
    }

    @Override
    public final void setProgramType(PrismProgramType programType) {
        this.programType = programType;
    }

    public final WorkflowPropertyDefinition getWorkflowPropertyDefinition() {
        return workflowPropertyDefinition;
    }

    public final void setWorkflowPropertyDefinition(WorkflowPropertyDefinition workflowPropertyDefinition) {
        this.workflowPropertyDefinition = workflowPropertyDefinition;
    }

    @Override
    public final Integer getVersion() {
        return version;
    }

    @Override
    public final void setVersion(Integer version) {
        this.version = version;
    }

    public final Boolean getEnabled() {
        return enabled;
    }

    public final void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public final Boolean getRequired() {
        return required;
    }

    public final void setRequired(Boolean required) {
        this.required = required;
    }

    public final Integer getMinimum() {
        return minimum;
    }

    public final void setMinimum(Integer minimum) {
        this.minimum = minimum;
    }

    public final Integer getMaximum() {
        return maximum;
    }

    public final void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }

    @Override
    public final Boolean getActive() {
        return active;
    }

    @Override
    public final void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public final Boolean getSystemDefault() {
        return systemDefault;
    }

    @Override
    public final void setSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
    }

    @Override
    public WorkflowDefinition getDefinition() {
        return getWorkflowPropertyDefinition();
    }

    public WorkflowPropertyConfiguration withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public WorkflowPropertyConfiguration withLocale(PrismLocale locale) {
        this.locale = locale;
        return this;
    }

    public WorkflowPropertyConfiguration withProgramType(PrismProgramType programType) {
        this.programType = programType;
        return this;
    }

    public WorkflowPropertyConfiguration withWorkflowPropertyDefinition(WorkflowPropertyDefinition workflowPropertyDefinition) {
        this.workflowPropertyDefinition = workflowPropertyDefinition;
        return this;
    }

    public WorkflowPropertyConfiguration withVersion(Integer version) {
        this.version = version;
        return this;
    }

    public WorkflowPropertyConfiguration withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public WorkflowPropertyConfiguration withRequired(Boolean required) {
        this.required = required;
        return this;
    }

    public WorkflowPropertyConfiguration withMinimum(Integer minimum) {
        this.minimum = minimum;
        return this;
    }

    public WorkflowPropertyConfiguration withMaximum(Integer maximum) {
        this.maximum = maximum;
        return this;
    }

    public WorkflowPropertyConfiguration withActive(Boolean active) {
        this.active = active;
        return this;
    }

    public WorkflowPropertyConfiguration withSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("workflowPropertyDefinition", workflowPropertyDefinition).addProperty("version", version);
    }

}
