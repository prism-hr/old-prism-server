package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class QualificationBuilder {
	
	private String degree;
	private String date_taken;
	private String institution;
	private String grade;
	private ApplicationForm application;
	private Integer id;
	
	
	public QualificationBuilder degree (String degree) {
		this.degree = degree;
		return this;
	}
	public QualificationBuilder institution (String institution) {
		this.institution = institution;
		return this;
	}
	public QualificationBuilder date_taken (String date_taken) {
		this.date_taken = date_taken;
		return this;
	}
	public QualificationBuilder grade (String grade) {
		this.grade = grade;
		return this;
	}
	public QualificationBuilder application (ApplicationForm application) {
		this.application = application;
		return this;
	}
	
	public Qualification toQualification() {
		Qualification qualification = new Qualification();
		qualification.setApplication(application);
		qualification.setDate_taken(date_taken);
		qualification.setDegree(degree);
		qualification.setGrade(grade);
		qualification.setInstitution(institution);
		qualification.setId(id);
		return qualification;
	}

	public QualificationBuilder id(Integer id) {
		this.id = id;
		return this;
	}
	
	

}
