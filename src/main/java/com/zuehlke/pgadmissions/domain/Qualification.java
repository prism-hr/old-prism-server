package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;

@Entity(name="APPLICATION_FORM_QUALIFICATION")
@Access(AccessType.FIELD) 
public class Qualification extends DomainObject<Integer>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8949535622435302565L;


	private Date award_date;
	private String country;
	private String name_of_programme;
	private String institution;
	private String termination_reason;
	private Date termination_date;
	private String language_of_study;
	private String level;
	private String qualification_type;
	private String grade;
	private String score;
	private Date start_date;
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String q_country) {
		this.country = q_country;
	}


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

	public String getQualification_termination_reason() {
		return termination_reason;
	}

	public void setQualification_termination_reason(String q_termination_reason) {
		this.termination_reason = q_termination_reason;
	}

	public Date getTermination_date() {
		return termination_date;
	}

	public void setTermination_date(Date q_termination_date) {
		this.termination_date = q_termination_date;
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
	@Cascade( { org.hibernate.annotations.CascadeType.ALL } )
	@JoinColumn(name="application_form_id", insertable=false, updatable=false, nullable=false)
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

	public boolean isAttachedToApplication(
			ApplicationForm applicationForm, Qualification qualification) {
		return qualification.getApplication().equals(applicationForm);
	}

}
