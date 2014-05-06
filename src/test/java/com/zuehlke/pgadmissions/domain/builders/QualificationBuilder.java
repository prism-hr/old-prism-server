package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.QualificationType;

public class QualificationBuilder {

	private String subject;
	private String title;
	private Institution institution;
	private String language_of_study;
	private QualificationType type;
	private String grade;	
	private Date start_date;
	private Date award_date;
	private ApplicationForm application;
	private Integer id;
	private Boolean completed;
	private Document document;
    private boolean sendToUCL;
    
    public QualificationBuilder id(Integer id) {
        this.id = id;
        return this;
    }
    
    public QualificationBuilder institution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public QualificationBuilder sendToUCL(Boolean sendToUCL) {
        this.sendToUCL = sendToUCL;
        return this;
    }
    
	public QualificationBuilder document(Document document) {
		this.document = document;
		return this;
	}
	
	public QualificationBuilder application(ApplicationForm application) {
		this.application = application;
		return this;
	}
	
	public QualificationBuilder awardDate(Date award_date) {
		this.award_date = award_date;
		return this;
	}

	public QualificationBuilder title(String title) {
        this.title = title;
        return this;
    }

	public QualificationBuilder subject(String subject) {
		this.subject = subject;
		return this;
	}

	public QualificationBuilder isCompleted(Boolean isCompleted) {
		this.completed = isCompleted;
		return this;
	}
	
	public QualificationBuilder languageOfStudy(String languageOfStudy) {
		this.language_of_study = languageOfStudy;
		return this;
	}

	public QualificationBuilder type(QualificationType type) {
		this.type = type;
		return this;
	}
	
	public QualificationBuilder grade(String grade) {
		this.grade = grade;
		return this;
	}

	public QualificationBuilder startDate(Date start_date) {
		this.start_date = start_date;
		return this;
	}

	public Qualification build() {
		Qualification qualification = new Qualification();
		qualification.setApplication(application);
		qualification.setQualificationAwardDate(award_date);
		qualification.setQualificationGrade(grade);
		qualification.setInstitution(institution);
		qualification.setQualificationLanguage(language_of_study);
		qualification.setQualificationSubject(subject);
		qualification.setQualificationTitle(title);
		qualification.setQualificationStartDate(start_date);
		qualification.setQualificationType(type);
		qualification.setId(id);
		qualification.setCompleted(completed);
		qualification.setDocument(document);
		qualification.setExport(sendToUCL);
		return qualification;
	}
}
