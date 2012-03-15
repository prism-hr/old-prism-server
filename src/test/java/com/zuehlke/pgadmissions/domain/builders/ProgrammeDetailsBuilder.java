package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;

public class ProgrammeDetailsBuilder {

	private Integer id;
	private ApplicationForm applicationForm;

	public ProgrammeDetailsBuilder id(Integer id) {
		this.id = id;
		return this;
	}
	
	public ProgrammeDetailsBuilder applicationForm(ApplicationForm applicationForm) {
		this.applicationForm = applicationForm;
		return this;
	}
	
	public ProgrammeDetail toProgrammeDetails(){
		ProgrammeDetail programmeDetails = new ProgrammeDetail();
		programmeDetails.setId(id);
		programmeDetails.setApplication(applicationForm);
		return programmeDetails;
	}
	

}
