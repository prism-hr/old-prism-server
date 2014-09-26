package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.validation.annotation.DatePast;

public class ApplicationLanguageQualificationDTO {

    @NotNull
    private Integer type;

    @NotNull
    @DatePast
    private LocalDate examDate;

    @NotEmpty
    private String overallScore;

    @NotEmpty
    private String readingScore;

    @NotEmpty
    private String writingScore;

    @NotEmpty
    private String speakingScore;

    @NotEmpty
    private String listeningScore;

    @NotNull
    private FileDTO proofOfAward;

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

    public FileDTO getProofOfAward() {
        return proofOfAward;
    }

    public void setProofOfAward(FileDTO proofOfAward) {
        this.proofOfAward = proofOfAward;
    }
}