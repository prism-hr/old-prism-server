package com.zuehlke.pgadmissions.dto;

import org.apache.commons.lang3.ObjectUtils;

import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;

public class UserAdministratorResourceDTO {

	private System system;

	private Institution institution;

	private Program program;

	private Project project;

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

	public Resource getResource() {
		return (Resource) ObjectUtils.firstNonNull(system, institution, program, project);
	}

}
