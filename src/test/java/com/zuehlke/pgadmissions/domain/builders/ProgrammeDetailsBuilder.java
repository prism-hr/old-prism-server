package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;

public class ProgrammeDetailsBuilder {

	private Integer id;
	private ApplicationForm applicationForm;
	private String programmeName;
	private String studyOption;
	private String studyOptionCode;
	private String projectName;
	private Date startDate;
	private List<SuggestedSupervisor> suggestedSupervisors = new ArrayList<SuggestedSupervisor>();
	private SourcesOfInterest sourcesOfInterest;
    private String sourcesOfInterestText;

    public ProgrammeDetailsBuilder sourcesOfInterestText(String sourcesOfInterestText) {
        this.sourcesOfInterestText = sourcesOfInterestText;
        return this;
    }
    
    public ProgrammeDetailsBuilder sourcesOfInterest(SourcesOfInterest interest) {
        this.sourcesOfInterest = interest;
        return this;
    }
    
	public ProgrammeDetailsBuilder id(Integer id) {
		this.id = id;
		return this;
	}
	
	public ProgrammeDetailsBuilder programmeName(String programmeName) {
		this.programmeName = programmeName;
		return this;
	}
	
	public ProgrammeDetailsBuilder studyOption(String studyOption) {
		this.studyOption = studyOption;
		return this;
	}
	
	public ProgrammeDetailsBuilder studyOptionCode(String studyOptionCode) {
        this.studyOptionCode = studyOptionCode;
        return this;
    }
	
	public ProgrammeDetailsBuilder studyOption(String studyOptionCode, String studyOption) {
        this.studyOptionCode = studyOptionCode;
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
	
	public ProgrammeDetailsBuilder suggestedSupervisors(SuggestedSupervisor... suggestedSupervisors) {
		for (SuggestedSupervisor suggestedSupervisor : suggestedSupervisors) {
			this.suggestedSupervisors.add(suggestedSupervisor);
		}
		return this;
	}
	
	public ProgramDetails build() {
		ProgramDetails programmeDetails = new ProgramDetails();
		programmeDetails.setId(id);
		programmeDetails.setApplication(applicationForm);
		programmeDetails.setProjectName(projectName);
		programmeDetails.setProgrammeName(programmeName);
		programmeDetails.setStartDate(startDate);
		programmeDetails.setStudyOption(studyOption);
		programmeDetails.setStudyOptionCode(studyOptionCode);
		programmeDetails.getSuggestedSupervisors().addAll(suggestedSupervisors);
		programmeDetails.setSourcesOfInterest(sourcesOfInterest);
		programmeDetails.setSourcesOfInterestText(sourcesOfInterestText);
		return programmeDetails;
	}
}
