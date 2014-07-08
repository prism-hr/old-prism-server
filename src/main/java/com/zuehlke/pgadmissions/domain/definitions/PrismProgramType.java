package com.zuehlke.pgadmissions.domain.definitions;


public enum PrismProgramType {

    MRES("MRes"), //
    MDRES("MD(Res)"), //
    MSC("MSc"), //
    RESEARCH_DEGREE("Research Degree"), //
    ENGINEERING_DOCTORATE("Engineering Doctorate"), //
    INTERNSHIP("Internship"), //
    VISITING_RESEARCH("Research Visit"), //
    UNCLASSIFIED("Unclassified");
    
    private String prefix;
    
    private PrismProgramType(String prefix) {
        this.prefix = prefix;
    }

    public static PrismProgramType findValueFromString(String toSearchIn) {
        for (PrismProgramType value : PrismProgramType.values()) {
            toSearchIn = toSearchIn.trim().replaceAll("\\s+", " ").toLowerCase();
            String prefixForSearch = value.prefix.toLowerCase();
            if (value != UNCLASSIFIED && toSearchIn.toLowerCase().startsWith(prefixForSearch)) {
                return value;
            }
        }
        return UNCLASSIFIED;
    }

}
