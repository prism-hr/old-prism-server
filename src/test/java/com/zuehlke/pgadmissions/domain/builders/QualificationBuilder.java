package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Qualification;

public class QualificationBuilder {

	private String q_name_of_programme;
	private String q_institution;
	private String q_language_of_study;
	private String q_level;
	private String q_type;
	private String q_grade;
	private String q_score;
	private Date q_start_date;
	private Date q_award_date;
	private ApplicationForm application;
	private Integer id;

	
	public QualificationBuilder application(ApplicationForm application) {
		this.application = application;
		return this;
	}
	
	public QualificationBuilder q_award_date(Date q_award_date) {
		this.q_award_date = q_award_date;
		return this;
	}


	public QualificationBuilder q_name_of_programme(String q_name_of_programme) {
		this.q_name_of_programme = q_name_of_programme;
		return this;
	}

	public QualificationBuilder q_institution(String q_institution) {
		this.q_institution = q_institution;
		return this;
	}


	public QualificationBuilder q_language_of_study(String q_language_of_study) {
		this.q_language_of_study = q_language_of_study;
		return this;
	}

	
	public QualificationBuilder q_level(String q_level) {
		this.q_level = q_level;
		return this;
	}
	public QualificationBuilder q_type(String q_type) {
		this.q_type = q_type;
		return this;
	}
	public QualificationBuilder q_grade(String q_grade) {
		this.q_grade = q_grade;
		return this;
	}
	public QualificationBuilder q_score(String q_score) {
		this.q_score = q_score;
		return this;
	}
	public QualificationBuilder q_start_date(Date q_start_date) {
		this.q_start_date = q_start_date;
		return this;
	}

	public Qualification toQualification() {
		Qualification qualification = new Qualification();
		qualification.setApplication(application);
		qualification.setQualificationAwardDate(q_award_date);
		qualification.setQualificationGrade(q_grade);
		qualification.setQualificationInstitution(q_institution);
		qualification.setQualificationLanguage(q_language_of_study);
		qualification.setQualificationLevel(q_level);
		qualification.setQualificationProgramName(q_name_of_programme);
		qualification.setQualificationScore(q_score);
		qualification.setQualificationStartDate(q_start_date);
		qualification.setQualificationType(q_type);
		qualification.setId(id);
		return qualification;
	}

	public QualificationBuilder id(Integer id) {
		this.id = id;
		return this;
	}

}
