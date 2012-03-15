package com.zuehlke.pgadmissions.dto;

import java.util.Date;


public class EmploymentPosition {

	private String position_employer;
	
	private String position_title;
	
	private String position_remit;
	
	private Date position_startDate;
	
	private Date position_endDate;
	
	private String position_language;
	
	private Integer positionId;
	public String getPosition_employer() {
		return position_employer;
	}

	public void setPosition_employer(String employer) {
		this.position_employer = employer;
	}

	public String getPosition_title() {
		return position_title;
	}

	public void setPosition_title(String title) {
		this.position_title = title;
	}

	public String getPosition_remit() {
		return position_remit;
	}

	public void setPosition_remit(String remit) {
		this.position_remit = remit;
	}

	public Date getPosition_startDate() {
		return position_startDate;
	}

	public void setPosition_startDate(Date startDate) {
		this.position_startDate = startDate;
	}

	public Date getPosition_endDate() {
		return position_endDate;
	}

	public void setPosition_endDate(Date endDate) {
		this.position_endDate = endDate;
	}

	public String getPosition_language() {
		return position_language;
	}

	public void setPosition_language(String language) {
		this.position_language = language;
	}

	public Integer getPositionId() {
		return positionId;
	}

	public void setPositionId(Integer positionId) {
		this.positionId = positionId;
	}




}
