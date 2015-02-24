package com.zuehlke.pgadmissions.domain.resource;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismResourceBatchType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowResource;

@Entity
@Table(name = "RESOURCE_BATCH", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "user_id", "resource_batch_type", "name" }),
        @UniqueConstraint(columnNames = { "institution_id", "user_id", "resource_batch_type", "name" }),
        @UniqueConstraint(columnNames = { "program_id", "user_id", "resource_batch_type", "name" }),
        @UniqueConstraint(columnNames = { "project_id", "user_id", "resource_batch_type", "name" }) })
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

	@Enumerated(EnumType.STRING)
	@Column(name = "resource_batch_type", nullable = false)
	private PrismResourceBatchType resourceBatchType;

	@Column(name = "name", nullable = false)
	private String name;

	@Embedded
	private ResourceBatchApplicationShortlisting applicationShortlisting;
	
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resource_batch_id", nullable = false)
    private Set<CommentAssignedUser> assignedUsers = Sets.newHashSet();

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

	public PrismResourceBatchType getResourceBatchType() {
		return resourceBatchType;
	}

	public void setResourceBatchType(PrismResourceBatchType resourceBatchType) {
		this.resourceBatchType = resourceBatchType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ResourceBatchApplicationShortlisting getApplicationShortlisting() {
		return applicationShortlisting;
	}

	public void setApplicationShortlisting(ResourceBatchApplicationShortlisting applicationShortlisting) {
		this.applicationShortlisting = applicationShortlisting;
	}

	public Set<CommentAssignedUser> getAssignedUsers() {
		return assignedUsers;
	}

	public ResourceBatch withResource(Resource resource) {
		setResource(resource);
		return this;
	}

	public ResourceBatch withResourceBatchType(PrismResourceBatchType resourceBatchType) {
		this.resourceBatchType = resourceBatchType;
		return this;
	}

	public ResourceBatch withName(String name) {
		this.name = name;
		return this;
	}

	public ResourceBatch withApplicationShortlisting(ResourceBatchApplicationShortlisting applicationShortlisting) {
		this.applicationShortlisting = applicationShortlisting;
		return this;
	}

	@Override
	public Resource getResource() {
		return project == null ? super.getResource() : project;
	}

	@Override
	public ResourceSignature getResourceSignature() {
		return new ResourceSignature().addProperty("resourceBatchType", resourceBatchType).addProperty("name", name);
	}

}
