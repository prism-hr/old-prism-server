package com.zuehlke.pgadmissions.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.beanutils.PropertyUtils;

@Entity
@Table(name = "STATE_TRANSITION_PENDING", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "state_transition_id" }),
        @UniqueConstraint(columnNames = { "program_id", "state_transition_id" }), @UniqueConstraint(columnNames = { "project_id", "state_transition_id" }),
        @UniqueConstraint(columnNames = { "application_id", "state_transition_id" }) })
public class StateTransitionPending {

    @Id
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "system_id", nullable = false)
    private Institution system;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "program_id", nullable = true)
    private Program program;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = true)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = true)
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

    public Institution getSystem() {
        return system;
    }

    public void setSystem(Institution system) {
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public StateTransition getStateTransition() {
        return stateTransition;
    }

    public void setStateTransition(StateTransition stateTransition) {
        this.stateTransition = stateTransition;
    }

    public PrismResource getResource() {
        if (institution != null) {
            return institution;
        } else if (program != null) {
            return program;
        } else if (project != null) {
            return project;
        }
        return application;
    }
    
    public void setResource(PrismResource resource) {
        try {
            PropertyUtils.setProperty(this, resource.getClass().getSimpleName().toLowerCase(), resource);
        } catch (Exception e) {
            new Error("Tried to queue state transition for invalid prism resource", e);
        }
    }

    public StateTransitionPending withResource(PrismResource resource) {
        setResource(resource);
        return this;
    }

    public StateTransitionPending withStateTransition(StateTransition stateTransition) {
        this.stateTransition = stateTransition;
        return this;
    }

}
