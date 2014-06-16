package com.zuehlke.pgadmissions.domain.enums;

public enum PrismProgramType {
    
    MRES("MRes"),
    MSC("MSc"),
    RESEARCH_DEGREE("Research Degree"),
    ENGINEERING_DOCTORATE("Engineering Doctorate"),
    INTERNSHIP("Internship"),
    VISITING_RESEARCH("Visiting Research");
    
    private String displayValue;
    
    PrismProgramType(String displayValue) {
        this.setDisplayValue(displayValue);
    }
    
    public String getDisplayValue() {
        return displayValue;
    }
    
    public static PrismProgramType findValueFromString(String toSearchIn) {
        for (PrismProgramType value : PrismProgramType.values()) {
            if (toSearchIn.contains(value.displayValue)) {
                return value;
            }
        }
        return null;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
    
}
