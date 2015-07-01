package com.zuehlke.pgadmissions.domain.workflow;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;

import javax.persistence.*;

@Entity
@Table(name = "state_duration_configuration", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "system_id", "opportunity_type", "state_duration_definition_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "opportunity_type", "state_duration_definition_id" }),
        @UniqueConstraint(columnNames = { "program_id", "state_duration_definition_id" }),
        @UniqueConstraint(columnNames = { "project_id", "state_duration_definition_id" })})
public class StateDurationConfiguration extends WorkflowConfiguration {

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

    @Column(name = "opportunity_type")
    @Enumerated(EnumType.STRING)
    private PrismOpportunityType opportunityType;

    @ManyToOne
    @JoinColumn(name = "state_duration_definition_id", nullable = false)
    private StateDurationDefinition stateDurationDefinition;

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

    public final StateDurationDefinition getStateDurationDefinition() {
        return stateDurationDefinition;
    }

    public final void setStateDurationDefinition(StateDurationDefinition stateDurationDefinition) {
        this.stateDurationDefinition = stateDurationDefinition;
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

    @Override
    public WorkflowDefinition getDefinition() {
        return getStateDurationDefinition();
    }

    public StateDurationConfiguration withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public StateDurationConfiguration withOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
        return this;
    }

    public StateDurationConfiguration withStateDurationDefinition(StateDurationDefinition stateDurationDefinition) {
        this.stateDurationDefinition = stateDurationDefinition;
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

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("stateDurationDefinition", stateDurationDefinition);
    }

}
