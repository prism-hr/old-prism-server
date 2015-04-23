package com.zuehlke.pgadmissions.domain.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowResourceExecution;

@Entity
@Table(name = "RESOURCE_CONDITION", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "action_condition" }),
        @UniqueConstraint(columnNames = { "institution_id", "action_condition" }), @UniqueConstraint(columnNames = { "program_id", "action_condition" }),
        @UniqueConstraint(columnNames = { "project_id", "action_condition" }), @UniqueConstraint(columnNames = { "application_id", "action_condition" }) })
public class ResourceCondition extends WorkflowResourceExecution {

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

	@Column(name = "action_condition")
	@Enumerated(EnumType.STRING)
	private PrismActionCondition actionCondition;

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

	public PrismActionCondition getActionCondition() {
		return actionCondition;
	}

	public void setActionCondition(PrismActionCondition actionCondition) {
		this.actionCondition = actionCondition;
	}

	public ResourceCondition withResource(Resource resource) {
		setResource(resource);
		return this;
	}

	public ResourceCondition withActionCondition(PrismActionCondition actionCondition) {
		this.actionCondition = actionCondition;
		return this;
	}

	@Override
	public ResourceSignature getResourceSignature() {
		return super.getResourceSignature().addProperty("actionCondition", actionCondition);
	}

}
