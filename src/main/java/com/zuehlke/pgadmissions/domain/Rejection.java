package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity(name = "REJECTION")
@Access(AccessType.FIELD)
public class Rejection extends DomainObject<Integer> {
	
	private static final long serialVersionUID = 6510744657140247807L;
	
	@OneToOne(mappedBy="rejection")	
	private ApplicationForm applicationForm;
	
	
	@ManyToOne
	@JoinColumn(name = "reject_reason_id")
	private RejectReason rejectionReason;
	
	private boolean includeProspectusLink;
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	@Id
	@GeneratedValue
	@Access(AccessType.PROPERTY)
	public Integer getId() {
		return id;
	}

	public ApplicationForm getApplicationForm() {
		return applicationForm;
	}

	public void setApplicationForm(ApplicationForm applicationForm) {
		this.applicationForm = applicationForm;
	}

	public RejectReason getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(RejectReason rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public boolean isIncludeProspectusLink() {
		return includeProspectusLink;
	}

	public void setIncludeProspectusLink(boolean includeProspectusLink) {
		this.includeProspectusLink = includeProspectusLink;
	}

}
