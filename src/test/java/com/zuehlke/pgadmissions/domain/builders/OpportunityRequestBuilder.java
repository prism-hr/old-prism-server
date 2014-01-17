package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class OpportunityRequestBuilder {

	private Integer id;
	private Domicile institutionCountry;
	private String institutionCode;
	private String otherInstitution;
	private String programTitle;
	private String programDescription;
	private RegisteredUser author;

	public OpportunityRequestBuilder id(Integer id) {
		this.id = id;
		return this;
	}

	public OpportunityRequestBuilder institutionCountry(Domicile institutionCountry) {
		this.institutionCountry = institutionCountry;
		return this;
	}

	public OpportunityRequestBuilder institutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
		return this;
	}

	public OpportunityRequestBuilder otherInstitution(String otherInstitution) {
		this.otherInstitution = otherInstitution;
		return this;
	}

	public OpportunityRequestBuilder programTitle(String programTitle) {
		this.programTitle = programTitle;
		return this;
	}

	public OpportunityRequestBuilder programDescription(String programDescription) {
		this.programDescription = programDescription;
		return this;
	}

	public OpportunityRequestBuilder author(RegisteredUser author) {
		this.author = author;
		return this;
	}

	public OpportunityRequest build() {
		OpportunityRequest request = new OpportunityRequest();
		request.setId(id);
		request.setInstitutionCountry(institutionCountry);
		request.setInstitutionCode(institutionCode);
		request.setOtherInstitution(otherInstitution);
		request.setProgramTitle(programTitle);
		request.setProgramDescription(programDescription);
		request.setAuthor(author);
		return request;
	}
}
