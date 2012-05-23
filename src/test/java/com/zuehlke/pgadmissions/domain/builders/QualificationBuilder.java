package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;

public class QualificationBuilder {

	private String q_name_of_programme;
	private String q_institution;
	private Language q_language_of_study;
	private String q_type;
	private String q_grade;	
	private Date q_start_date;
	private Date q_award_date;
	private ApplicationForm application;
	private Integer id;
	private CheckedStatus completed;
	private Country institutionCountry;
	private Document proofOfAward;
	
	public QualificationBuilder proofOfAward(Document proofOfAward) {
		this.proofOfAward = proofOfAward;
		return this;
	}
	
	public QualificationBuilder institutionCountry(Country institutionCountry) {
		this.institutionCountry = institutionCountry;
		return this;
	}
	
	
	public QualificationBuilder application(ApplicationForm application) {
		this.application = application;
		return this;
	}
	
	public QualificationBuilder awardDate(Date q_award_date) {
		this.q_award_date = q_award_date;
		return this;
	}


	public QualificationBuilder subject(String q_name_of_programme) {
		this.q_name_of_programme = q_name_of_programme;
		return this;
	}

	public QualificationBuilder institution(String q_institution) {
		this.q_institution = q_institution;
		return this;
	}

	public QualificationBuilder isCompleted(CheckedStatus isCompleted) {
		this.completed = isCompleted;
		return this;
	}
	

	public QualificationBuilder languageOfStudy(Language q_language_of_study) {
		this.q_language_of_study = q_language_of_study;
		return this;
	}

	public QualificationBuilder type(String q_type) {
		this.q_type = q_type;
		return this;
	}
	public QualificationBuilder grade(String q_grade) {
		this.q_grade = q_grade;
		return this;
	}

	public QualificationBuilder startDate(Date q_start_date) {
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
		qualification.setQualificationSubject(q_name_of_programme);
		qualification.setQualificationStartDate(q_start_date);
		qualification.setQualificationType(q_type);
		qualification.setId(id);
		qualification.setCompleted(completed);
		qualification.setInstitutionCountry(institutionCountry);
		qualification.setProofOfAward(proofOfAward);
		return qualification;
	}

	public QualificationBuilder id(Integer id) {
		this.id = id;
		return this;
	}

}
