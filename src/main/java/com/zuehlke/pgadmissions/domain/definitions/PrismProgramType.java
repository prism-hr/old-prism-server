package com.zuehlke.pgadmissions.domain.definitions;


public enum PrismProgramType {

    UNDERGRADUATE_STUDY(12, new String[]{}),
    POSTGRADUATE_STUDY(12, new String[]{"mres", "md(res)"}),
    POSTGRADUATE_RESEARCH(12, new String[]{"research degree", "engineering doctorate"}),
    INTERNSHIP(null, new String[]{}),
    SECONDMENT(null, new String[]{"visiting research"}),
    EMPLOYMENT(null, new String[]{}),
    CONTINUING_PROFESSIONAL_DEVELOPMENT(null, new String[]{}),
    UNCLASSIFIED(null, new String[]{});
    
    private Integer groupStartFrequency;
    
    private String[] prefixes;
    
    private PrismProgramType(Integer groupStartFrequency, String[] prefixes) {
        this.groupStartFrequency = groupStartFrequency;
        this.prefixes = prefixes;
    }

    public final Integer getGroupStartFrequency() {
        return groupStartFrequency;
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
