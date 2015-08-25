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
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.resource.department.Department;

@Entity
@Table(name = "state_duration_configuration", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "system_id", "opportunity_type", "state_duration_definition_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "opportunity_type", "state_duration_definition_id" }),
        @UniqueConstraint(columnNames = { "department_id", "opportunity_type", "state_duration_definition_id" }),
        @UniqueConstraint(columnNames = { "program_id", "state_duration_definition_id" }),
        @UniqueConstraint(columnNames = { "project_id", "state_duration_definition_id" })})
public class StateDurationConfiguration extends WorkflowConfiguration<StateDurationDefinition> {

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
    @JoinColumn(name = "state_duration_definition_id", nullable = false)
    private StateDurationDefinition definition;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "system_default", nullable = false)
    private Boolean systemDefault;

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
    public final PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    @Override
    public final void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public final StateDurationDefinition getDefinition() {
        return definition;
    }

    public final void setDefinition(StateDurationDefinition definition) {
        this.definition = definition;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @Override
    public final Boolean getSystemDefault() {
        return systemDefault;
    }

    @Override
    public final void setSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
    }
    
    public StateDurationConfiguration withResource(Resource<?> resource) {
        setResource(resource);
        return this;
    }

    public StateDurationConfiguration withOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
        return this;
    }

    public StateDurationConfiguration withDefinition(StateDurationDefinition definition) {
        this.definition = definition;
        return this;
    }

    public StateDurationConfiguration withDuration(Integer duration) {
        this.duration = duration;
        return this;
    }

    public StateDurationConfiguration withSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
        return this;
    }
    
}
