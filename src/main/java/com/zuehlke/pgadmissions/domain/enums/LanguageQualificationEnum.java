package com.zuehlke.pgadmissions.domain.enums;

public enum LanguageQualificationEnum {
    
    IELTS_ACADEMIC("IELTS (Academic)"),
    TOEFL("TOEFL"),
    OTHER("Other English Language Qualification");
    
    private final String displayValue;

    private LanguageQualificationEnum(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
