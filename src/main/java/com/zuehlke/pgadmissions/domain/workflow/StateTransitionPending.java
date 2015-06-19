package com.zuehlke.pgadmissions.domain.workflow;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;

import javax.persistence.*;

@Entity
@Table(name = "state_transition_pending", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "action_id" }),
        @UniqueConstraint(columnNames = { "program_id", "action_id" }), @UniqueConstraint(columnNames = { "project_id", "action_id" }),
        @UniqueConstraint(columnNames = { "application_id", "action_id" }) })
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
	@JoinColumn(name = "program_id")
	private Program program;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private Project project;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id")
	private Application application;

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
	public ResourceSignature getResourceSignature() {
		return super.getResourceSignature().addProperty("action", action);
	}

}
