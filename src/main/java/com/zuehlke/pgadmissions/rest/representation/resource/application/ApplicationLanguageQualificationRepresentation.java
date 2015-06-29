package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedEntitySimpleRepresentation;

public class ApplicationLanguageQualificationRepresentation extends ApplicationSectionRepresentation {
    
    private ImportedEntitySimpleRepresentation languageQualificationTypeMapping;

    private LocalDate examDate;

    private String overallScore;

    private String readingScore;

    private String writingScore;

    private String speakingScore;

    private String listeningScore;

    private DocumentRepresentation document;

    public Integer getType() {
        return languageQualificationTypeMapping.getId();
    }
    
    public void setType(Integer languageQualificationType) {
        this.languageQualificationTypeMapping = new ImportedEntitySimpleRepresentation().withId(languageQualificationType);
    }
    
    public ImportedEntitySimpleRepresentation getLanguageQualificationTypeMapping() {
        return languageQualificationTypeMapping;
    }

    public void setLanguageQualificationTypeMapping(ImportedEntitySimpleRepresentation languageQualificationTypeMapping) {
        this.languageQualificationTypeMapping = languageQualificationTypeMapping;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public String getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(String overallScore) {
        this.overallScore = overallScore;
    }

    public String getReadingScore() {
        return readingScore;
    }

    public void setReadingScore(String readingScore) {
        this.readingScore = readingScore;
    }

    public String getWritingScore() {
        return writingScore;
    }

    public void setWritingScore(String writingScore) {
        this.writingScore = writingScore;
    }

    public String getSpeakingScore() {
        return speakingScore;
    }

    public void setSpeakingScore(String speakingScore) {
        this.speakingScore = speakingScore;
    }

    public String getListeningScore() {
        return listeningScore;
    }

    public void setListeningScore(String listeningScore) {
        this.listeningScore = listeningScore;
    }

    public DocumentRepresentation getDocument() {
        return document;
    }

    public void setDocument(DocumentRepresentation document) {
        this.document = document;
    }
}
