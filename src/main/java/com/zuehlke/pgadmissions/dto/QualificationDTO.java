package com.zuehlke.pgadmissions.dto;

import java.util.Date;

public class QualificationDTO {

	private Date start_date;
	private Integer qualId;
	private String country;
	private String name_of_programme;
	private String institution;
	private String termination_reason;
	private Date termination_date;
	private String language_of_study;
	private String level;
	private String type;
	private String grade;
	private String score;
	private Date award_date;

	
	public Date getAward_date() {
		return award_date;
	}
	public void setAward_date(Date award_date) {
		this.award_date = award_date;
	}
	
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getName_of_programme() {
		return name_of_programme;
	}
	public void setName_of_programme(String name_of_programme) {
		this.name_of_programme = name_of_programme;
	}
	public String getInstitution() {
		return institution;
	}
	public void setInstitution(String institution) {
		this.institution = institution;
	}
	public String getTermination_reason() {
		return termination_reason;
	}
	public void setTermination_reason(String termination_reason) {
		this.termination_reason = termination_reason;
	}
	public Date getTermination_date() {
		return termination_date;
	}
	public void setTermination_date(Date termination_date) {
		this.termination_date = termination_date;
	}
	public String getLanguage_of_study() {
		return language_of_study;
	}
	public void setLanguage_of_study(String language_of_study) {
		this.language_of_study = language_of_study;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public Date getStart_date() {
		return start_date;
	}
	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}
	public Integer getQualId() {
		return qualId;
	}
	public void setQualId(Integer qualId) {
		this.qualId = qualId;
	}
	
}
