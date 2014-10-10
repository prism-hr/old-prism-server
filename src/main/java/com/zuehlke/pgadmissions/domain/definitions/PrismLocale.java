package com.zuehlke.pgadmissions.domain.definitions;

public enum PrismLocale {

    EN_GB, //
    DE_DE, //
    DE_AT, //
    DE_CH, //
    SR_RS;
    
    public static PrismLocale getSystemLocale() {
        return EN_GB;
    }
    
    public String toString(PrismLocale locale) {
        String[] parts = locale.name().split("_");
        return parts[0].toLowerCase() + "_" + parts[1];
    }
    
}
