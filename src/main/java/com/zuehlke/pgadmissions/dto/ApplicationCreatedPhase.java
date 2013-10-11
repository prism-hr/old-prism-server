package com.zuehlke.pgadmissions.dto;

public class ApplicationCreatedPhase extends TimelinePhase {

	private String projectTitle;
	private String projectDescription;
	
	public String getProjectTitle() {
		return projectTitle;
	}
	public void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
	}
	
	public String getProjectDescription() {
		return projectDescription;
	}
	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}
	
}
