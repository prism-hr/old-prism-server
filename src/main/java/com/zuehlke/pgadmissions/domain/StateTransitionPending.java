package com.zuehlke.pgadmissions.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "STATE_TRANSITION_PENDING", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "state_transition_id" }),
        @UniqueConstraint(columnNames = { "program_id", "state_transition_id" }), @UniqueConstraint(columnNames = { "project_id", "state_transition_id" }),
        @UniqueConstraint(columnNames = { "application_id", "state_transition_id" }) })
public class StateTransitionPending extends WorkflowExecution {

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
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @ManyToOne
    @JoinColumn(name = "state_transition_id", nullable = false)
    private StateTransition stateTransition;

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
    public Application getApplication() {
        return application;
    }

    @Override
    public void setApplication(Application application) {
        this.application = application;
    }

    public StateTransition getStateTransition() {
        return stateTransition;
    }

    public void setStateTransition(StateTransition stateTransition) {
        this.stateTransition = stateTransition;
    }

    public StateTransitionPending withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public StateTransitionPending withStateTransition(StateTransition stateTransition) {
        this.stateTransition = stateTransition;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("stateTransition", stateTransition);
    }

}
