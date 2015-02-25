package com.zuehlke.pgadmissions.domain.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowResource;

@Entity
@Table(name = "RESOURCE_BATCH", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "state_group_id", "name" }),
        @UniqueConstraint(columnNames = { "institution_id", "state_group_id", "name" }),
        @UniqueConstraint(columnNames = { "program_id", "state_group_id", "name" }),
        @UniqueConstraint(columnNames = { "project_id", "state_group_id", "name" }) })
public class ResourceBatch extends WorkflowResource {

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

	@ManyToOne
	@Column(name = "resource_batch_process_id", nullable = false)
	private ResourceBatchProcess resourceBatchProcess;

	@Column(name = "name", nullable = false)
	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public System getSystem() {
		return system;
	}

	public void setSystem(System system) {
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

	public ResourceBatchProcess getResourceBatchProcess() {
		return resourceBatchProcess;
	}

	public void setResourceBatchProcess(ResourceBatchProcess resourceBatchProcess) {
		this.resourceBatchProcess = resourceBatchProcess;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ResourceBatch withResource(Resource resource) {
		setResource(resource);
		return this;
	}

	public ResourceBatch withResourceBatchProcess(ResourceBatchProcess resourceBatchProcess) {
		this.resourceBatchProcess = resourceBatchProcess;
		return this;
	}

	public ResourceBatch withName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public Resource getResource() {
		return project == null ? super.getResource() : project;
	}

	@Override
	public ResourceSignature getResourceSignature() {
		return new ResourceSignature().addProperty("resourceBatchProcess", resourceBatchProcess).addProperty("name", name);
	}

}
