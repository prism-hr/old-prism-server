package com.zuehlke.pgadmissions.domain.workflow;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;

import javax.persistence.*;

@Entity
@Table(name = "RESOURCE_ACTION", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "action_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "action_id" }), @UniqueConstraint(columnNames = { "program_id", "action_id" }),
        @UniqueConstraint(columnNames = { "project_id", "action_id" }), @UniqueConstraint(columnNames = { "application_id", "action_id" }) })
public class ResourceAction extends WorkflowResourceExecution {

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
    @JoinColumn(name = "action_id")
    private Action action;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final System getSystem() {
        return system;
    }

    public final void setSystem(System system) {
        this.system = system;
    }

    public final Institution getInstitution() {
        return institution;
    }

    public final void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public final Program getProgram() {
        return program;
    }

    public final void setProgram(Program program) {
        this.program = program;
    }

    public final Project getProject() {
        return project;
    }

    public final void setProject(Project project) {
        this.project = project;
    }

    public final Application getApplication() {
        return application;
    }

    public final void setApplication(Application application) {
        this.application = application;
    }

    public final Action getAction() {
        return action;
    }

    public final void setAction(Action action) {
        this.action = action;
    }

    public ResourceAction withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public ResourceAction withAction(Action action) {
        this.action = action;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("action", action);
    }

}
