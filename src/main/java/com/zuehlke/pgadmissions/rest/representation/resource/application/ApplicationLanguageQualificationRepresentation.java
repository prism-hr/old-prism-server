package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;

import uk.co.alumeni.prism.api.model.imported.response.ImportedLanguageQualificationTypeResponse;

public class ApplicationLanguageQualificationRepresentation extends ApplicationSectionRepresentation {

    private ImportedLanguageQualificationTypeResponse type;

    private LocalDate examDate;

    private String overallScore;

    private String readingScore;

    private String writingScore;

    private String speakingScore;

    private String listeningScore;

    private DocumentRepresentation document;

    public ImportedLanguageQualificationTypeResponse getType() {
        return type;
    }

    public void setType(ImportedLanguageQualificationTypeResponse type) {
        this.type = type;
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

    public ApplicationLanguageQualificationRepresentation withType(final ImportedLanguageQualificationTypeResponse type) {
        this.type = type;
        return this;
    }

    public ApplicationLanguageQualificationRepresentation withExamDate(LocalDate examDate) {
        this.examDate = examDate;
        return this;
    }

    public ApplicationLanguageQualificationRepresentation withOverallScore(String overallScore) {
        this.overallScore = overallScore;
        return this;
    }

    public ApplicationLanguageQualificationRepresentation withReadingScore(String readingScore) {
        this.readingScore = readingScore;
        return this;
    }

    public ApplicationLanguageQualificationRepresentation withWritingScore(String writingScore) {
        this.writingScore = writingScore;
        return this;
    }

    public ApplicationLanguageQualificationRepresentation withSpeakingScore(String speakingScore) {
        this.speakingScore = speakingScore;
        return this;
    }

    public ApplicationLanguageQualificationRepresentation withListeningScore(String listeningScore) {
        this.listeningScore = listeningScore;
        return this;
    }

    public ApplicationLanguageQualificationRepresentation withDocument(DocumentRepresentation document) {
        this.document = document;
        return this;
    }

    public ApplicationLanguageQualificationRepresentation withLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        setLastUpdatedTimestamp(lastUpdatedTimestamp);
        return this;
    }
    
}
