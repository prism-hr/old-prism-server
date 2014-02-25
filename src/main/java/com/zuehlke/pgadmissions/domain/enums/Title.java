package com.zuehlke.pgadmissions.domain.enums;

public enum Title {

    MR("Mr"), 
    MISS("Miss"), 
    MRS("Mrs"), 
    DR("Dr"), 
    PROFESSOR("Professor"), 
    REVEREND("Reverend"),
    LORD("Lord"),
    LADY("Lady"),
    SIR("Sir"),
    CAPTAIN("Captain"),
    MAJOR("Major"),
    BROTHER("Brother"),
    LIEUTENANT("Lieutenant"),
    COLONEL("Colonel"),
    RABBI("Rabbi"),
    SISTER("Sister"),
    PROFESSOR_DR("Professor Dr"),
    EUROPEAN_ENGINEER("European Engineer"),
    PROFESSOR_SIR("Professor Sir"),
    REVEREND_DOCTOR("Reverend Doctor"),
    PROFESSOR_LADY("Professor Lady");
    
    private final String title;
    
    Title(String title) {
        this.title = title;
    }
    
    public String getDisplayValue() {
        return title;
    }
}
