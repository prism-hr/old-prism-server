package com.zuehlke.pgadmissions.domain.enums;

public enum ScoringStage {

    REFERENCE("Reference"), 
    REVIEW("Review"), 
    INTERVIEW("Interview Feedback");

    private String displayValue;

    public String displayValue() {
        return displayValue;
    }

    private ScoringStage(String displayValue) {
        this.displayValue = displayValue;
    }
    
}