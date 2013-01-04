package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.Rejection;

public class RejectionBuilder {
	
	private ApplicationForm applicationForm;
	private RejectReason rejectionReason;
	private boolean includeProspectusLink;
	private Integer id;
	
	public RejectionBuilder id(Integer id){
		this.id = id;
		return this;		
	}
	
	public RejectionBuilder rejectionReason(RejectReason rejectionReason){
		this.rejectionReason = rejectionReason;
		return this;		
	}
	
	public RejectionBuilder includeProspectusLink(boolean includeProspectusLink){
		this.includeProspectusLink = includeProspectusLink;
		return this;		
	}
	

	public RejectionBuilder applicationForm(ApplicationForm applicationForm){
		this.applicationForm = applicationForm;
		return this;		
	}
	
	public Rejection build() {
		Rejection rejection = new Rejection();
		rejection.setApplicationForm(applicationForm);
		rejection.setId(id);
		rejection.setIncludeProspectusLink(includeProspectusLink);
		rejection.setRejectionReason(rejectionReason);
		return rejection;
	}
}
