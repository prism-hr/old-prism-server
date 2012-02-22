package com.zuehlke.pgadmissions.domain;

import java.util.List;

public class MainPageModel extends DomainModel{

	private List<Project> projects;

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}
	
}
