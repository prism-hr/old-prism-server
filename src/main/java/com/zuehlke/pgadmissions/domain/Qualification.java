package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
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
	private Date award_date;
	private String name_of_programme;
	private String institution;
	private String language_of_study;
	private String level;
	private String qualification_type;
	private String grade;
	private String score;
	@Temporal(TemporalType.DATE)
	private Date start_date;
	


	public String getName_of_programme() {
		return name_of_programme;
	}

	public void setName_of_programme(String q_name_of_programme) {
		this.name_of_programme = q_name_of_programme;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String q_institution) {
		this.institution = q_institution;
	}

	public String getLanguage_of_study() {
		return language_of_study;
	}

	public void setLanguage_of_study(String q_language_of_study) {
		this.language_of_study = q_language_of_study;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String q_level) {
		this.level = q_level;
	}

	public String getQualification_type() {
		return qualification_type;
	}

	public void setQualification_type(String q_type) {
		this.qualification_type = q_type;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String q_grade) {
		this.grade = q_grade;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String q_score) {
		this.score = q_score;
	}

	public Date getStart_date() {
		return start_date;
	}

	public void setStart_date(Date q_start_date) {
		this.start_date = q_start_date;
	}

	public Date getAward_date() {
		return award_date;
	}

	public void setAward_date(Date q_award_date) {
		this.award_date = q_award_date;
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
