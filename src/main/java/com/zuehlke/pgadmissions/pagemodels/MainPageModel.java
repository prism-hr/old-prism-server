package com.zuehlke.pgadmissions.pagemodels;

import java.util.List;

import com.zuehlke.pgadmissions.domain.Project;

public class MainPageModel extends DomainModel{

	private List<Project> projects;

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}
	
}
