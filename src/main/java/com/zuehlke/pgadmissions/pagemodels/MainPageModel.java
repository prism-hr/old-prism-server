package com.zuehlke.pgadmissions.pagemodels;

import java.util.List;

import com.zuehlke.pgadmissions.domain.Project;

public class MainPageModel extends PageModel{

	private List<Project> projects;

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}
	
}
