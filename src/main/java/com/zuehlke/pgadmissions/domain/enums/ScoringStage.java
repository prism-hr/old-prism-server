package com.zuehlke.pgadmissions.domain.enums;

public enum ScoringStage {

    REFERENCE("Reference Request"), REVIEW("Review Evaluation Request"), INTERVIEW("Interview Evaluation Request");

    private String displayValue;

    public String displayValue() {
        return displayValue;
    }

    private ScoringStage(String displayValue) {
        this.displayValue = displayValue;
    }
}