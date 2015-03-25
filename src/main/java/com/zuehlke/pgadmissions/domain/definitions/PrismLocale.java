package com.zuehlke.pgadmissions.domain.definitions;

public enum PrismLocale {

    EN_GB,
    PL_PL,
    ES_ES;

    public static PrismLocale getSystemLocale() {
        return EN_GB;
    }

    @Override
    public String toString() {
        String[] parts = getParts();
        return parts[0].toLowerCase() + "_" + parts[1];
    }

    public String getLanguagePart() {
        return getParts()[0].toLowerCase();
    }

    private String[] getParts() {
        return name().split("_");
    }

}
