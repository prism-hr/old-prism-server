package com.zuehlke.pgadmissions.dto;

import java.util.Date;

public class Funding {
	
	private String fundingType;
	private String fundingDescription;
	private String fundingValue;
	private Date fundingAwardDate;
	private Integer fundingId;

	public String getFundingDescription() {
		return fundingDescription;
	}
	
	public void setFundingDescription(String fundingDescription) {
		this.fundingDescription = fundingDescription;
	}
	
	public String getFundingValue() {
		return fundingValue;
	}
	
	public void setFundingValue(String fundingValue) {
		this.fundingValue = fundingValue;
	}
	
	public String getFundingType() {
		return fundingType;
	}
	
	public void setFundingType(String fundingType) {
		this.fundingType = fundingType;
	}
	
	public Date getFundingAwardDate() {
		return fundingAwardDate;
	}
	
	public void setFundingAwardDate(Date fundingAwardDate) {
		this.fundingAwardDate = fundingAwardDate;
	}
	
	public Integer getFundingId() {
		return fundingId;
	}
	
	public void setFundingId(Integer fundingId) {
		this.fundingId = fundingId;
	}
	
}
