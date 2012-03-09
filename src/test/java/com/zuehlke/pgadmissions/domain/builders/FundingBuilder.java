package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Funding;

public class FundingBuilder {

	
	private ApplicationForm application;
	private String type;
	private String description;
	private String value;
	private Date awardDate;
	
	public FundingBuilder application(ApplicationForm application) {
		this.application = application;
		return this;
	}
	
	public FundingBuilder type(String type) {
		this.type = type;
		return this;
	}
	
	public FundingBuilder description(String description) {
		this.description = description;
		return this;
	}
	
	public FundingBuilder value(String value) {
		this.value = value;
		return this;
	}
	
	public FundingBuilder awardDate(Date awardDate) {
		this.awardDate = awardDate;
		return this;
	}
	
	public Funding toFunding() {
		Funding funding = new Funding();
		funding.setApplication(application);
		funding.setType(type);
		funding.setValue(value);
		funding.setDescription(description);
		funding.setAwardDate(awardDate);
		return funding;
	}
	
	
	
}
