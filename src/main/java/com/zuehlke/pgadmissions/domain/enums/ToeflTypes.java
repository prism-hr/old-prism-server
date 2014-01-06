package com.zuehlke.pgadmissions.domain.enums;

public enum ToeflTypes {

    INTERNET_BASED("Internet based"),
    PAPER_BASED("Paper based");
    
    private final String type;
    
    ToeflTypes(String type) {
        this.type = type;
    }
    
    public String getDisplayValue() {
        return type;
    }
    
}