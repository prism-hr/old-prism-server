package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Supervisor;
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
	private List<Supervisor> supervisors = new ArrayList<Supervisor>();
	
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
	
	public ProgrammeDetailsBuilder referrer(Referrer referrer) {
		this.referrer = referrer;
		return this;
	}
	public ProgrammeDetailsBuilder supervisors(Supervisor... supervisors) {
		for (Supervisor supervisor : supervisors) {
			this.supervisors.add(supervisor);
		}
		return this;
	}
	
	public ProgrammeDetail toProgrammeDetails(){
		ProgrammeDetail programmeDetails = new ProgrammeDetail();
		programmeDetails.setId(id);
		programmeDetails.setApplication(applicationForm);
		programmeDetails.setProjectName(projectName);
		programmeDetails.setProgrammeName(programmeName);
		programmeDetails.setReferrer(referrer);
		programmeDetails.setStartDate(startDate);
		programmeDetails.setStudyOption(studyOption);
		programmeDetails.getSupervisors().addAll(supervisors);
		return programmeDetails;
	}
	

}
