package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity(name = "APPLICATION_FORM_ADDITIONAL_INFO")
@Access(AccessType.FIELD)
public class AdditionalInformation extends DomainObject<Integer> implements FormSectionObject{
	private static final long serialVersionUID = -1761742614792933388L;

	
	@Transient
	private boolean acceptedTerms;
	@OneToOne
	@JoinColumn(name = "application_form_id")
	private ApplicationForm application = null;

	@Column(name = "info_text")
	private String informationText;

	@Column(name = "has_convictions")
	private Boolean convictions;

	@Column(name = "convictions_text")
	private String convictionsText;

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

	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
	}

	public String getInformationText() {
		return informationText;
	}

	public void setInformationText(String additionalInformation) {
		this.informationText = additionalInformation;
	}

	// no default value for the convictions (the user has to set this property
	// explicitly) therefore it is easier to use this boolean flag as a
	// regular bean property -> no hasConvictions() method.
	public Boolean getConvictions() {
		return convictions;
	}

	public void setConvictions(Boolean convictions) {
		this.convictions = convictions;
	}

	public String getConvictionsText() {
		return convictionsText;
	}

	public void setConvictionsText(String convictionsText) {
		this.convictionsText = convictionsText;
	}

	public boolean isAcceptedTerms() {
		return acceptedTerms;
	}

	public void setAcceptedTerms(boolean acceptedTerms) {
		this.acceptedTerms = acceptedTerms;
	}
}
