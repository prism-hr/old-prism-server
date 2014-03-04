package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;

public class LanguageQualificationBuilder {

    private PersonalDetails personalDetails = null;
    
    private LanguageQualificationEnum qualificationType;
    
    private Integer id;
    
    private String otherQualificationTypeName;

    private Date dateOfExamination;

    private String overallScore;

    private String readingScore;

    private String writingScore;

    private String speakingcore;

    private String listeningScore;

    private Boolean examTakenOnline;
    
    private Document qualificationDocument;
    
    public LanguageQualificationBuilder languageQualificationDocument(Document document) {
        this.qualificationDocument = document;
        return this;
    }
    
    public LanguageQualificationBuilder personalDetails(PersonalDetails details) {
        this.personalDetails = details;
        return this;
    }
    
    public LanguageQualificationBuilder examTakenOnline(Boolean online) {
        this.examTakenOnline = online;
        return this;
    }
    
    public LanguageQualificationBuilder id(Integer id) {
        this.id = id;
        return this;
    }
    
    public LanguageQualificationBuilder languageQualification(LanguageQualificationEnum qualificationType) {
        this.qualificationType = qualificationType;
        return this;
    }
    
    public LanguageQualificationBuilder otherQualificationTypeName(String name) {
        this.otherQualificationTypeName = name;
        return this;
    }
    
    public LanguageQualificationBuilder dateOfExamination(Date date) {
        this.dateOfExamination = date;
        return this;
    }
    
    public LanguageQualificationBuilder overallScore(String score) {
        this.overallScore = score;
        return this;
    }
    
    public LanguageQualificationBuilder readingScore(String score) {
        this.readingScore = score;
        return this;
    }
    
    public LanguageQualificationBuilder writingScore(String score) {
        this.writingScore = score;
        return this;
    }
    
    public LanguageQualificationBuilder listeningScore(String score) {
        this.listeningScore = score;
        return this;
    }
    
    public LanguageQualificationBuilder speakingScore(String score) {
        this.speakingcore = score;
        return this;
    }
    
    public LanguageQualification build() {
        LanguageQualification qualification = new LanguageQualification();
        qualification.setDateOfExamination(dateOfExamination);
        qualification.setExamTakenOnline(examTakenOnline);
        qualification.setId(id);
        qualification.setListeningScore(listeningScore);
        qualification.setOtherQualificationTypeName(otherQualificationTypeName);
        qualification.setOverallScore(overallScore);
        qualification.setPersonalDetails(personalDetails);
        qualification.setQualificationType(qualificationType);
        qualification.setReadingScore(readingScore);
        qualification.setSpeakingScore(speakingcore);
        qualification.setWritingScore(writingScore);
        qualification.setLanguageQualificationDocument(qualificationDocument);
        return qualification;
    }
}
