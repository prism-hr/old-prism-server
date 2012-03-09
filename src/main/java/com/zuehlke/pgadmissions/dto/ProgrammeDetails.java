package com.zuehlke.pgadmissions.dto;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;

public class ProgrammeDetails {

	private String programmeDetailsProgrammeName;
	private String programmeDetailsProjectName;
	private StudyOption programmeDetailsStudyOption;
	private Date programmeDetailsStartDate;
	private Referrer programmeDetailsReferrer;
	
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
	
	public Referrer getProgrammeDetailsReferrer() {
		return programmeDetailsReferrer;
	}
	
	public void setProgrammeDetailsReferrer(Referrer programmeDetailsReferrer) {
		this.programmeDetailsReferrer = programmeDetailsReferrer;
	}
	
	public Date getProgrammeDetailsStartDate() {
		return programmeDetailsStartDate;
	}
	
	public void setProgrammeDetailsStartDate(Date programmeDetailsStartDate) {
		this.programmeDetailsStartDate = programmeDetailsStartDate;
	}
	
	public StudyOption getProgrammeDetailsStudyOption() {
		return programmeDetailsStudyOption;
	}
	
	public void setProgrammeDetailsStudyOption(StudyOption programmeDetailsStudyOption) {
		this.programmeDetailsStudyOption = programmeDetailsStudyOption;
	}
}
