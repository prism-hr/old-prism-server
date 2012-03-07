package com.zuehlke.pgadmissions.dto;

import java.util.Date;

public class QualificationDTO {

	private Date qualificationStartDate;
	private Integer qualificationId;
	private String qualificationProgramName;
	private String qualificationInstitution;
	private String qualificationLanguage;
	private String qualificationLevel;
	private String qualificationType;
	private String qualificationGrade;
	private String qualificationScore;
	private Date qualificationAwardDate;

	public String getQualificationGrade() {
		return qualificationGrade;
	}
	
	public void setQualificationGrade(String qualificationGrade) {
		this.qualificationGrade = qualificationGrade;
	}
	
	public Date getQualificationAwardDate() {
		return qualificationAwardDate;
	}
	
	public void setQualificationAwardDate(Date qualificationAwardDate) {
		this.qualificationAwardDate = qualificationAwardDate;
	}
	
	public String getQualificationInstitution() {
		return qualificationInstitution;
	}
	
	public void setQualificationInstitution(String qualificationInstitution) {
		this.qualificationInstitution = qualificationInstitution;
	}
	
	public String getQualificationLanguage() {
		return qualificationLanguage;
	}
	
	public void setQualificationLanguage(String qualificationLanguage) {
		this.qualificationLanguage = qualificationLanguage;
	}
	
	public String getQualificationLevel() {
		return qualificationLevel;
	}
	
	public void setQualificationLevel(String qualificationLevel) {
		this.qualificationLevel = qualificationLevel;
	}
	
	public String getQualificationProgramName() {
		return qualificationProgramName;
	}
	
	public void setQualificationProgramName(String qualificationProgramName) {
		this.qualificationProgramName = qualificationProgramName;
	}
	
	public String getQualificationScore() {
		return qualificationScore;
	}
	
	public void setQualificationScore(String qualificationScore) {
		this.qualificationScore = qualificationScore;
	}
	
	public Date getQualificationStartDate() {
		return qualificationStartDate;
	}
	
	public String getQualificationType() {
		return qualificationType;
	}
	
	public void setQualificationStartDate(Date qualificationStartDate) {
		this.qualificationStartDate = qualificationStartDate;
	}
	
	public Integer getQualificationId() {
		return qualificationId;
	}
	
	public void setQualificationId(Integer qualificationId) {
		this.qualificationId = qualificationId;
	}
	
	public void setQualificationType(String qualificationType) {
		this.qualificationType = qualificationType;
	}
}
