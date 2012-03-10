package com.zuehlke.pgadmissions.dto;

import java.util.Date;

public class ProgrammeDetails {

	private String programmeDetailsProgrammeName;
	private String programmeDetailsProjectName;
	private String programmeDetailsStudyOption;
	private Date programmeDetailsStartDate;
	private String programmeDetailsReferrer;
	
	public String getProgrammeDetailsProgrammeName() {
		return programmeDetailsProgrammeName;
	}
	
	public void setProgrammeDetailsProgrammeName(String programmeDetailsProgrammeName) {
		this.programmeDetailsProgrammeName = programmeDetailsProgrammeName;
	}
	
	public String getProgrammeDetailsProjectName() {
		return programmeDetailsProjectName;
	}
	
	public void setProgrammeDetailsProjectName(String programmeDetailsProjectName) {
		this.programmeDetailsProjectName = programmeDetailsProjectName;
	}
	
	public Date getProgrammeDetailsStartDate() {
		return programmeDetailsStartDate;
	}
	
	public void setProgrammeDetailsStartDate(Date programmeDetailsStartDate) {
		this.programmeDetailsStartDate = programmeDetailsStartDate;
	}
	
	public String getProgrammeDetailsReferrer() {
		return programmeDetailsReferrer;
	}
	
	public void setProgrammeDetailsReferrer(String programmeDetailsReferrer) {
		this.programmeDetailsReferrer = programmeDetailsReferrer;
	}
	
	public String getProgrammeDetailsStudyOption() {
		return programmeDetailsStudyOption;
	}
	
	public void setProgrammeDetailsStudyOption(String programmeDetailsStudyOption) {
		this.programmeDetailsStudyOption = programmeDetailsStudyOption;
	}
	
}
