package com.zuehlke.pgadmissions.domain.enums;

public enum ProgramTypeId {
    
    MRES("MRes"),
    MSC("MSc"),
    RESEARCH_DEGREE("Research Degree"),
    ENGINEERING_DOCTORATE("Engineering Doctorate"),
    INTERNSHIP("Internship"),
    VISITING_RESEARCH("Visiting Research");
    
    private String displayValue;
    
    ProgramTypeId(String displayValue) {
        this.setDisplayValue(displayValue);
    }
    
    public String getDisplayValue() {
        return displayValue;
    }
    
    public static ProgramTypeId findValueFromString(String toSearchIn) {
        for (ProgramTypeId value : ProgramTypeId.values()) {
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
