package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;

public class ProgrammeDetailsBuilder {

	private Integer id;
	private Application applicationForm;
	private StudyOption studyOption;
	private LocalDate startDate;
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
	
	public ProgrammeDetailsBuilder studyOption(StudyOption studyOption) {
		this.studyOption = studyOption;
		return this;
	}
	
	public ProgrammeDetailsBuilder applicationForm(Application applicationForm) {
		this.applicationForm = applicationForm;
		return this;
	}
	
	public ProgrammeDetailsBuilder startDate(LocalDate startDate) {
		this.startDate = startDate;
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
		programmeDetails.setStartDate(startDate);
		programmeDetails.setStudyOption(studyOption);
		programmeDetails.getSuggestedSupervisors().addAll(suggestedSupervisors);
		programmeDetails.setSourceOfInterest(sourcesOfInterest);
		programmeDetails.setSourceOfInterestText(sourcesOfInterestText);
		return programmeDetails;
	}
}
