package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;

public class AdditionalInformationBuilder {

	private Integer infoId;
	private ApplicationForm applicationForm;
	private String convictionsText;
	private Boolean hasConvictions;
	private String infoText;

	public AdditionalInformation toAdditionalInformation() {
		AdditionalInformation info = new AdditionalInformation();
		info.setId(infoId);
		info.setApplication(applicationForm);
		info.setInformationText(infoText);
		if (hasConvictions != null && hasConvictions.booleanValue()) {
			info.setConvictions(true);
			info.setConvictionsText(convictionsText);
		}
		return info;
	}

	public AdditionalInformationBuilder id(Integer id) {
		this.infoId = id;
		return this;
	}

	public AdditionalInformationBuilder applicationForm(ApplicationForm applForm) {
		this.applicationForm = applForm;
		return this;
	}

	public AdditionalInformationBuilder convictionsText(String conText) {
		this.convictionsText = conText;
		return this;
	}

	public AdditionalInformationBuilder setConvictions(Boolean hasConvictions) {
		this.hasConvictions = hasConvictions;
		return this;
	}

	public AdditionalInformationBuilder informationText(String informationText) {
		this.infoText = informationText;
		return this;
	}
}
