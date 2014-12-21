package com.zuehlke.pgadmissions.domain.resource;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.workflow.State;

import javax.persistence.*;

@Entity
@Table(name = "RESOURCE_STATE", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "state_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "state_id" }), @UniqueConstraint(columnNames = { "program_id", "state_id" }),
        @UniqueConstraint(columnNames = { "project_id", "state_id" }), @UniqueConstraint(columnNames = { "application_id", "state_id" }) })
public class ResourceState extends ResourceStateDefinition {

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
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Column(name = "primary_state", nullable = false)
    private Boolean primaryState;

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

    @Override
    public final State getState() {
        return state;
    }

    @Override
    public final void setState(State state) {
        this.state = state;
    }

    @Override
    public final Boolean getPrimaryState() {
        return primaryState;
    }

    @Override
    public final void setPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
    }

    public ResourceState withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public ResourceState withState(State state) {
        this.state = state;
        return this;
    }

    public ResourceState withPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
        return this;
    }

}
