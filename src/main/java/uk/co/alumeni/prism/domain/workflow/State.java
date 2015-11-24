package uk.co.alumeni.prism.domain.workflow;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.collect.Sets;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationEvaluation;
import uk.co.alumeni.prism.domain.resource.Department;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.Program;
import uk.co.alumeni.prism.domain.resource.Project;
import uk.co.alumeni.prism.domain.resource.ResourcePreviousState;
import uk.co.alumeni.prism.domain.resource.ResourceState;
import uk.co.alumeni.prism.domain.resource.System;

@Entity
@Table(name = "state")
public class State extends WorkflowDefinition {

    @Id
    @Enumerated(EnumType.STRING)
    private PrismState id;

    @Column(name = "ordinal", nullable = false)
    private Integer ordinal;

    @ManyToOne
    @JoinColumn(name = "state_group_id", nullable = false)
    private StateGroup stateGroup;

    @ManyToOne
    @JoinColumn(name = "state_duration_definition_id")
    private StateDurationDefinition stateDurationDefinition;

    @Column(name = "state_duration_evaluation")
    @Enumerated(EnumType.STRING)
    private PrismStateDurationEvaluation stateDurationEvaluation;

    @Column(name = "hidden")
    private Boolean hidden;

    @Column(name = "parallelizable")
    private Boolean parallelizable;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @OneToMany(mappedBy = "state")
    private Set<StateAction> stateActions = Sets.newHashSet();

    @OneToMany(mappedBy = "state")
    private Set<ResourceState> resourceStates = Sets.newHashSet();

    @OneToMany(mappedBy = "state")
    private Set<ResourcePreviousState> resourcePreviousStates = Sets.newHashSet();

    @OneToMany(mappedBy = "state")
    private Set<System> systems = Sets.newHashSet();

    @OneToMany(mappedBy = "state")
    private Set<Institution> institutions = Sets.newHashSet();

    @OneToMany(mappedBy = "state")
    private Set<Department> departments = Sets.newHashSet();

    @OneToMany(mappedBy = "state")
    private Set<Program> programs = Sets.newHashSet();

    @OneToMany(mappedBy = "state")
    private Set<Project> projects = Sets.newHashSet();

    @OneToMany(mappedBy = "state")
    private Set<Application> applications = Sets.newHashSet();

    @Override
    public PrismState getId() {
        return id;
    }

    public void setId(PrismState id) {
        this.id = id;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public StateGroup getStateGroup() {
        return stateGroup;
    }

    public void setStateGroup(StateGroup stateGroup) {
        this.stateGroup = stateGroup;
    }

    public StateDurationDefinition getStateDurationDefinition() {
        return stateDurationDefinition;
    }

    public void setStateDurationDefinition(StateDurationDefinition stateDurationDefinition) {
        this.stateDurationDefinition = stateDurationDefinition;
    }

    public PrismStateDurationEvaluation getStateDurationEvaluation() {
        return stateDurationEvaluation;
    }

    public void setStateDurationEvaluation(PrismStateDurationEvaluation stateDurationEvaluation) {
        this.stateDurationEvaluation = stateDurationEvaluation;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Boolean getParallelizable() {
        return parallelizable;
    }

    public void setParallelizable(Boolean parallelizable) {
        this.parallelizable = parallelizable;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Set<StateAction> getStateActions() {
        return stateActions;
    }

    public Set<ResourceState> getResourceStates() {
        return resourceStates;
    }

    public Set<ResourcePreviousState> getResourcePreviousStates() {
        return resourcePreviousStates;
    }

    public Set<System> getSystems() {
        return systems;
    }

    public Set<Institution> getInstitutions() {
        return institutions;
    }

    public Set<Department> getDepartments() {
        return departments;
    }

    public Set<Program> getPrograms() {
        return programs;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public Set<Application> getApplications() {
        return applications;
    }

    public State withId(PrismState id) {
        this.id = id;
        return this;
    }

    public State withOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
        return this;
    }

    public State withStateGroup(StateGroup stateGroup) {
        this.stateGroup = stateGroup;
        return this;
    }

    public State withStateDurationDefinition(StateDurationDefinition stateDurationDefinition) {
        this.stateDurationDefinition = stateDurationDefinition;
        return this;
    }

    public State withStateDurationEvaluation(PrismStateDurationEvaluation stateDurationEvaluation) {
        this.stateDurationEvaluation = stateDurationEvaluation;
        return this;
    }

    public State withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

}
