package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.enums.FundingType;

public class FundingBuilder {

	
	private ApplicationForm application;
	private FundingType type;
	private String description;
	private String value;
	private Date awardDate;
	private Integer id;
	
	public FundingBuilder application(ApplicationForm application) {
		this.application = application;
		return this;
	}
	
	public FundingBuilder type(FundingType type) {
		this.type = type;
		return this;
	}
	
	public FundingBuilder id(Integer id) {
		this.id = id;
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
		funding.setId(id);
		funding.setType(type);
		funding.setValue(value);
		funding.setDescription(description);
		funding.setAwardDate(awardDate);
		return funding;
	}
	
	
	
}
