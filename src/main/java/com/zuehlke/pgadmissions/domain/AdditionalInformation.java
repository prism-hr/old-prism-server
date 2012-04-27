package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity(name = "APPLICATION_FORM_ADDITIONAL_INFO")
@Access(AccessType.FIELD)
public class AdditionalInformation extends DomainObject<Integer> {
	private static final long serialVersionUID = -1761742614792933388L;

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

	public Boolean hasConvictions() {
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
}
