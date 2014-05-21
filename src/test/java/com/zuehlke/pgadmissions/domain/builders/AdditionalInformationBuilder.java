package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.Application;

public class AdditionalInformationBuilder {

	private Integer infoId;
	private Application applicationForm;
	private String convictionsText;
	private Boolean hasConvictions;

	public AdditionalInformation build() {
		AdditionalInformation info = new AdditionalInformation();
		info.setId(infoId);
		info.setApplication(applicationForm);
		if (hasConvictions != null) {
			info.setConvictions(hasConvictions);
			info.setConvictionsText(convictionsText);
		}
		return info;
	}

	public AdditionalInformationBuilder id(Integer id) {
		this.infoId = id;
		return this;
	}

	public AdditionalInformationBuilder applicationForm(Application applForm) {
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
}
