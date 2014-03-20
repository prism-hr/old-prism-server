package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "APPLICATION_FORM_ADDITIONAL_INFO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AdditionalInformation implements FormSectionObject, Serializable {
    
	private static final long serialVersionUID = -1761742614792933388L;
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@Transient
	private boolean acceptedTerms;

	@OneToOne(mappedBy = "additionalInformation", fetch = FetchType.LAZY)
	private ApplicationForm application;

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
