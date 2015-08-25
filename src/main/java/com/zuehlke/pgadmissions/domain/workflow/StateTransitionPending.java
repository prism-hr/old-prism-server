package com.zuehlke.pgadmissions.domain.workflow;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.Resume;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.resource.department.Department;

@Entity
@Table(name = "state_transition_pending", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "action_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "action_id" }), @UniqueConstraint(columnNames = { "program_id", "action_id" }),
        @UniqueConstraint(columnNames = { "project_id", "action_id" }), @UniqueConstraint(columnNames = { "application_id", "action_id" }),
        @UniqueConstraint(columnNames = { "resume_id", "action_id" })})
public class StateTransitionPending extends WorkflowResourceExecution {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;
    
    @ManyToOne
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;

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

    @Override
    public Resume getResume() {
        return resume;
    }

    @Override
    public void setResume(Resume resume) {
        this.resume = resume;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public StateTransitionPending withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public StateTransitionPending withAction(Action action) {
        this.action = action;
        return this;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("action", action);
    }

}
