package com.zuehlke.pgadmissions.rest.representation.resource.application;

import java.math.BigDecimal;

public class LanguageQualificationTypeRepresentation {

    private Integer id;

    private String code;

    private String name;

    private BigDecimal minimumOverallScore;

    private BigDecimal maximumOverallScore;

    private BigDecimal minimumReadingScore;

    private BigDecimal maximumReadingScore;

    private BigDecimal minimumWritingScore;

    private BigDecimal maximumWritingScore;

    private BigDecimal minimumSpeakingScore;

    private BigDecimal maximumSpeakingScore;

    private BigDecimal minimumListeningScore;

    private BigDecimal maximumListeningScore;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMinimumOverallScore() {
        return minimumOverallScore;
    }

    public void setMinimumOverallScore(BigDecimal minimumOverallScore) {
        this.minimumOverallScore = minimumOverallScore;
    }

    public BigDecimal getMaximumOverallScore() {
        return maximumOverallScore;
    }

    public void setMaximumOverallScore(BigDecimal maximumOverallScore) {
        this.maximumOverallScore = maximumOverallScore;
    }

    public BigDecimal getMinimumReadingScore() {
        return minimumReadingScore;
    }

    public void setMinimumReadingScore(BigDecimal minimumReadingScore) {
        this.minimumReadingScore = minimumReadingScore;
    }

    public BigDecimal getMaximumReadingScore() {
        return maximumReadingScore;
    }

    public void setMaximumReadingScore(BigDecimal maximumReadingScore) {
        this.maximumReadingScore = maximumReadingScore;
    }

    public BigDecimal getMinimumWritingScore() {
        return minimumWritingScore;
    }

    public void setMinimumWritingScore(BigDecimal minimumWritingScore) {
        this.minimumWritingScore = minimumWritingScore;
    }

    public BigDecimal getMaximumWritingScore() {
        return maximumWritingScore;
    }

    public void setMaximumWritingScore(BigDecimal maximumWritingScore) {
        this.maximumWritingScore = maximumWritingScore;
    }

    public BigDecimal getMinimumSpeakingScore() {
        return minimumSpeakingScore;
    }

    public void setMinimumSpeakingScore(BigDecimal minimumSpeakingScore) {
        this.minimumSpeakingScore = minimumSpeakingScore;
    }

    public BigDecimal getMaximumSpeakingScore() {
        return maximumSpeakingScore;
    }

    public void setMaximumSpeakingScore(BigDecimal maximumSpeakingScore) {
        this.maximumSpeakingScore = maximumSpeakingScore;
    }

    public BigDecimal getMinimumListeningScore() {
        return minimumListeningScore;
    }

    public void setMinimumListeningScore(BigDecimal minimumListeningScore) {
        this.minimumListeningScore = minimumListeningScore;
    }

    public BigDecimal getMaximumListeningScore() {
        return maximumListeningScore;
    }

    public void setMaximumListeningScore(BigDecimal maximumListeningScore) {
        this.maximumListeningScore = maximumListeningScore;
    }
}
