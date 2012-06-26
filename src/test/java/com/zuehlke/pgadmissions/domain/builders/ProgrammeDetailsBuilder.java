package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;

public class ProgrammeDetailsBuilder {

	private Integer id;
	private ApplicationForm applicationForm;
	private String programmeName;
	private StudyOption studyOption;
	private String projectName;
	private Date startDate;
	private Referrer referrer;
	private List<SuggestedSupervisor> suggestedSupervisors = new ArrayList<SuggestedSupervisor>();
	
	public ProgrammeDetailsBuilder id(Integer id) {
		this.id = id;
		return this;
	}
	
	public ProgrammeDetailsBuilder programmeName(String programmeName) {
		this.programmeName = programmeName;
		return this;
	}
	
	public ProgrammeDetailsBuilder studyOption(StudyOption studyOption) {
		this.studyOption = studyOption;
		return this;
	}
	
	public ProgrammeDetailsBuilder applicationForm(ApplicationForm applicationForm) {
		this.applicationForm = applicationForm;
		return this;
	}
	
	public ProgrammeDetailsBuilder startDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	
	
	public ProgrammeDetailsBuilder projectName(String projectName) {
		this.projectName = projectName;
		return this;
	}
	
	
	public ProgrammeDetailsBuilder referrer(Referrer referrer) {
		this.referrer = referrer;
		return this;
	}
	public ProgrammeDetailsBuilder suggestedSupervisors(SuggestedSupervisor... suggestedSupervisors) {
		for (SuggestedSupervisor suggestedSupervisor : suggestedSupervisors) {
			this.suggestedSupervisors.add(suggestedSupervisor);
		}
		return this;
	}
	
	public ProgrammeDetails toProgrammeDetails(){
		ProgrammeDetails programmeDetails = new ProgrammeDetails();
		programmeDetails.setId(id);
		programmeDetails.setApplication(applicationForm);
		programmeDetails.setProjectName(projectName);
		programmeDetails.setProgrammeName(programmeName);
		programmeDetails.setReferrer(referrer);
		programmeDetails.setStartDate(startDate);
		programmeDetails.setStudyOption(studyOption);
		programmeDetails.getSuggestedSupervisors().addAll(suggestedSupervisors);
		return programmeDetails;
	}
	

}
