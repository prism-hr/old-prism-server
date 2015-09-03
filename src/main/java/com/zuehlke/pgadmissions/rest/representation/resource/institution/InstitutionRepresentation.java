package com.zuehlke.pgadmissions.rest.representation.resource.institution;

import com.zuehlke.pgadmissions.rest.representation.resource.ResourceParentRepresentation;

public class InstitutionRepresentation extends ResourceParentRepresentation {

	private String currency;

	private Integer businessYearStartMonth;

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Integer getBusinessYearStartMonth() {
		return businessYearStartMonth;
	}

	public void setBusinessYearStartMonth(Integer businessYearStartMonth) {
		this.businessYearStartMonth = businessYearStartMonth;
	}

}
