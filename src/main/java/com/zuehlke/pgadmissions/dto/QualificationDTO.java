package com.zuehlke.pgadmissions.dto;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.QualificationLevel;

public class QualificationDTO {

	private Date qualificationStartDate;
	private Integer qualificationId;
	private String qualificationProgramName;
	private String qualificationInstitution;
	private Integer qualificationLanguage;
	private QualificationLevel qualificationLevel;
	private String qualificationType;
	private String qualificationGrade;
	private String qualificationScore;
	private Date qualificationAwardDate;
	private CheckedStatus completed;

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
	
	public Integer getQualificationLanguage() {
		return qualificationLanguage;
	}
	
	public void setQualificationLanguage(Integer qualificationLanguage) {
		this.qualificationLanguage = qualificationLanguage;
	}
	
	public QualificationLevel getQualificationLevel() {
		return qualificationLevel;
	}
	
	public void setQualificationLevel(QualificationLevel qualificationLevel) {
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

	public CheckedStatus getCompleted() {
		return completed;
	}

	public void setCompleted(CheckedStatus completed) {
		this.completed = completed;
	}
}
