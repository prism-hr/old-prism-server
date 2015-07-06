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

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.System;

@Entity
@Table(name = "workflow_property_configuration", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "system_id", "opportunity_type", "workflow_property_definition_id", "version" }),
        @UniqueConstraint(columnNames = { "institution_id", "opportunity_type", "workflow_property_definition_id", "version" }),
        @UniqueConstraint(columnNames = { "department_id", "opportunity_type", "workflow_property_definition_id", "version" }),
        @UniqueConstraint(columnNames = { "program_id", "workflow_property_definition_id", "version" }),
        @UniqueConstraint(columnNames = { "project_id", "workflow_property_definition_id", "version" })})
public class WorkflowPropertyConfiguration extends WorkflowConfigurationVersioned<WorkflowPropertyDefinition> {

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

    @Column(name = "opportunity_type")
    @Enumerated(EnumType.STRING)
    private PrismOpportunityType opportunityType;

    @ManyToOne
    @JoinColumn(name = "workflow_property_definition_id", nullable = false)
    private WorkflowPropertyDefinition definition;

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
    public Department getDepartment() {
        return department;
    }
    
    @Override
    public void setDepartment(Department department) {
        this.department = department;
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
    public final PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    @Override
    public final void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public final WorkflowPropertyDefinition getDefinition() {
        return definition;
    }

    public final void setDefinition(WorkflowPropertyDefinition definition) {
        this.definition = definition;
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

    public WorkflowPropertyConfiguration withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public WorkflowPropertyConfiguration withOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
        return this;
    }

    public WorkflowPropertyConfiguration withDefinition(WorkflowPropertyDefinition definition) {
        this.definition = definition;
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
        return super.getResourceSignature().addProperty("workflowPropertyDefinition", definition).addProperty("version", version);
    }

}
