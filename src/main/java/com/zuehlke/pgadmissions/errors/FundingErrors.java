package com.zuehlke.pgadmissions.errors;

public class FundingErrors {

	private String fundingType;
	private String fundingDescription;
	private String fundingValue;
	private String fundingAwardDate;
	private String fundingFile;
	
	public String getFundingType() {
		return fundingType;
	}
	
	public void setFundingType(String fundingType) {
		this.fundingType = fundingType;
	}
	
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
	
	public String getFundingAwardDate() {
		return fundingAwardDate;
	}
	
	public void setFundingAwardDate(String fundingAwardDate) {
		this.fundingAwardDate = fundingAwardDate;
	}
	
	public String getFundingFile() {
		return fundingFile;
	}
	
	public void setFundingFile(String fundingFile) {
		this.fundingFile = fundingFile;
	}
}
