package com.zuehlke.pgadmissions.rest.dto.resource;

import javax.validation.constraints.NotNull;

public class InstitutionDTO extends ResourceParentDTO {

	@NotNull
	private String currency;

	@NotNull
	private Integer businessYearStartMonth;

	private Integer importedInstitutionId;

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

	public Integer getImportedInstitutionId() {
		return importedInstitutionId;
	}

	public void setImportedInstitutionId(Integer importedInstitutionId) {
		this.importedInstitutionId = importedInstitutionId;
	}
}
