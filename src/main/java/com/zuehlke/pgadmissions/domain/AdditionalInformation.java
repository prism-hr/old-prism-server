package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "APPLICATION_FORM_ADDITIONAL_INFO")
public class AdditionalInformation implements FormSectionObject, Serializable {
    
	private static final long serialVersionUID = -1761742614792933388L;
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@Transient
	private boolean acceptedTerms;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_form_id")
	private ApplicationForm application = null;

	@Column(name = "has_convictions")
	private Boolean convictions;

	@Column(name = "convictions_text")
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 400)
	private String convictionsText;

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
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
