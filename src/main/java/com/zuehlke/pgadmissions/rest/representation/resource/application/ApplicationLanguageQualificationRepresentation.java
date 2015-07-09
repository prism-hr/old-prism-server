package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

import uk.co.alumeni.prism.api.model.imported.response.ImportedLanguageQualificationTypeResponse;

import com.zuehlke.pgadmissions.rest.representation.FileRepresentation;

public class ApplicationLanguageQualificationRepresentation extends ApplicationSectionRepresentation {

    private ImportedLanguageQualificationTypeResponse languageQualificationType;

    private LocalDate examDate;

    private String overallScore;

    private String readingScore;

    private String writingScore;

    private String speakingScore;

    private String listeningScore;

    private FileRepresentation document;

    public ImportedLanguageQualificationTypeResponse getLanguageQualificationType() {
        return languageQualificationType;
    }

    public void setLanguageQualificationType(ImportedLanguageQualificationTypeResponse languageQualificationType) {
        this.languageQualificationType = languageQualificationType;
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

    public FileRepresentation getDocument() {
        return document;
    }

    public void setDocument(FileRepresentation document) {
        this.document = document;
    }

    public ApplicationLanguageQualificationRepresentation withLanguageQualificationType(ImportedLanguageQualificationTypeResponse languageQualificationType) {
        this.languageQualificationType = languageQualificationType;
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

    public ApplicationLanguageQualificationRepresentation withDocument(FileRepresentation document) {
        this.document = document;
        return this;
    }

}
