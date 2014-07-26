package com.zuehlke.pgadmissions.rest.representation.application;

import org.joda.time.LocalDate;

public class LanguageQualificationRepresentation {

    private Integer type;

    private LocalDate examDate;

    private String overallScore;

    private String readingScore;

    private String writingScore;

    private String speakingScore;

    private String listeningScore;

    private DocumentRepresentation proofOfAward;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
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

    public DocumentRepresentation getProofOfAward() {
        return proofOfAward;
    }

    public void setProofOfAward(DocumentRepresentation proofOfAward) {
        this.proofOfAward = proofOfAward;
    }
}
