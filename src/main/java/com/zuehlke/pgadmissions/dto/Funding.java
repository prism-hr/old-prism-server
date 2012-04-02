package com.zuehlke.pgadmissions.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import com.zuehlke.pgadmissions.domain.enums.FundingType;

public class Funding {
	
	private FundingType fundingType;
	private String fundingDescription;
	private String fundingValue;
	private Date fundingAwardDate;
	private Integer fundingId;
	private MultipartFile fundingFile;

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
	
	public FundingType getFundingType() {
		return fundingType;
	}
	
	public void setFundingType(FundingType fundingType) {
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
	
	public MultipartFile getFundingFile() {
		return fundingFile;
	}
	
	public void setFundingFile(MultipartFile fundingFile) {
		this.fundingFile = fundingFile;
	}
	
}
