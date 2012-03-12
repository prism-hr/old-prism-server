package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name="APPLICATION_FORM_QUALIFICATION")
@Access(AccessType.FIELD) 
public class Qualification extends DomainObject<Integer>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8949535622435302565L;

	@Temporal(TemporalType.DATE)
	@Column(name="award_date")
	private Date qualificationAwardDate;
	
	@Column(name="name_of_programme")
	private String qualificationProgramName;
	
	@Column(name="institution")
	private String qualificationInstitution;
	
	@Column(name="language_of_study")
	private String qualificationLanguage;
	
	@Column(name="level")
	private String qualificationLevel;
	
	@Column(name="qualification_type")
	private String qualificationType;
	
	@Column(name="grade")
	private String qualificationGrade;
	
	@Column(name="score")
	private String qualificationScore;
	
	@Column(name="start_date")
	@Temporal(TemporalType.DATE)
	private Date qualificationStartDate;
	


	public String getQualificationProgramName() {
		return qualificationProgramName;
	}

	public void setQualificationProgramName(String q_name_of_programme) {
		this.qualificationProgramName = q_name_of_programme;
	}

	public String getQualificationInstitution() {
		return qualificationInstitution;
	}

	public void setQualificationInstitution(String q_institution) {
		this.qualificationInstitution = q_institution;
	}

	public String getQualificationLanguage() {
		return qualificationLanguage;
	}

	public void setQualificationLanguage(String q_language_of_study) {
		this.qualificationLanguage = q_language_of_study;
	}

	public String getQualificationLevel() {
		return qualificationLevel;
	}

	public void setQualificationLevel(String q_level) {
		this.qualificationLevel = q_level;
	}

	public String getQualificationType() {
		return qualificationType;
	}

	public void setQualificationType(String q_type) {
		this.qualificationType = q_type;
	}

	public String getQualificationGrade() {
		return qualificationGrade;
	}

	public void setQualificationGrade(String q_grade) {
		this.qualificationGrade = q_grade;
	}

	public String getQualificationScore() {
		return qualificationScore;
	}

	public void setQualificationScore(String q_score) {
		this.qualificationScore = q_score;
	}

	public Date getQualificationStartDate() {
		return qualificationStartDate;
	}

	public void setQualificationStartDate(Date q_start_date) {
		this.qualificationStartDate = q_start_date;
	}

	public Date getQualificationAwardDate() {
		return qualificationAwardDate;
	}

	public void setQualificationAwardDate(Date q_award_date) {
		this.qualificationAwardDate = q_award_date;
	}

	@ManyToOne
	@JoinColumn(name="application_form_id")
	private ApplicationForm application = null;
	

	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
	}

	
	@Override
	public void setId(Integer id) {
		this.id = id;
		
	}

	@Override
	@Id
	@GeneratedValue
	@Access(AccessType.PROPERTY)
	public Integer getId() {
		return id;
	}


}
