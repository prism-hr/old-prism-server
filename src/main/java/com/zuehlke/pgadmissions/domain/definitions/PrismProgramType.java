package com.zuehlke.pgadmissions.domain.definitions;


public enum PrismProgramType {

    UNDERGRADUATE_STUDY(new String[]{}),
    POSTGRADUATE_STUDY(new String[]{"mres", "md(res)"}),
    POSTGRADUATE_RESEARCH(new String[]{"research degree", "engineering doctorate"}),
    INTERNSHIP(new String[]{"visiting research"}),
    EMPLOYMENT(new String[]{}),
    UNCLASSIFIED(new String[]{});
    
    private String[] prefixes;
    
    private PrismProgramType(String[] prefixes) {
        this.prefixes = prefixes;
    }

    public static PrismProgramType findValueFromString(String toSearchIn) {
        for (PrismProgramType value : PrismProgramType.values()) {
            if (value.prefixes.length > 0) {
                toSearchIn = toSearchIn.trim().replaceAll("\\s+", " ").toLowerCase();
                
                for (String prefix : value.prefixes) {
                    if (toSearchIn.startsWith(prefix)) {
                        return value;
                    }
                }
            }
        }
        return UNCLASSIFIED;
    }

}
