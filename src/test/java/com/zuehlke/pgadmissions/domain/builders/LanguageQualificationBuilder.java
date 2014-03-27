package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;

public class LanguageQualificationBuilder {

    private LanguageQualificationEnum qualificationType;
    
    private String qualificationTypeName;

    private Date examDate;

    private String overallScore;

    private String readingScore;

    private String writingScore;

    private String speakingcore;

    private String listeningScore;

    private Boolean examOnline;
    
    private Document qualificationDocument;
    
    public LanguageQualificationBuilder languageQualificationDocument(Document document) {
        this.qualificationDocument = document;
        return this;
    }
    
    public LanguageQualificationBuilder examOnline(Boolean online) {
        this.examOnline = online;
        return this;
    }
    
    public LanguageQualificationBuilder languageQualification(LanguageQualificationEnum qualificationType) {
        this.qualificationType = qualificationType;
        return this;
    }
    
    public LanguageQualificationBuilder qualificationTypeName(String name) {
        this.qualificationTypeName = name;
        return this;
    }
    
    public LanguageQualificationBuilder examDate(Date date) {
        this.examDate = date;
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
        qualification.setExamDate(examDate);
        qualification.setExamOnline(examOnline);
        qualification.setListeningScore(listeningScore);
        qualification.setQualificationTypeOther(qualificationTypeName);
        qualification.setOverallScore(overallScore);
        qualification.setQualificationType(qualificationType);
        qualification.setReadingScore(readingScore);
        qualification.setSpeakingScore(speakingcore);
        qualification.setWritingScore(writingScore);
        qualification.setProofOfAward(qualificationDocument);
        return qualification;
    }
}
