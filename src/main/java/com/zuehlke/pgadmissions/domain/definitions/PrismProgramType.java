package com.zuehlke.pgadmissions.domain.definitions;

public enum PrismProgramType {

    MRES, //
    MSC, //
    RESEARCH_DEGREE, //
    ENGINEERING_DOCTORATE, //
    INTERNSHIP, //
    VISITING_RESEARCH, //
    UNCLASSIFIED;

    public static PrismProgramType findValueFromString(String toSearchIn) {
        for (PrismProgramType value : PrismProgramType.values()) {
            if (value != UNCLASSIFIED && toSearchIn.toLowerCase().startsWith(value.toString().toLowerCase().replace("_", " "))) {
                return value;
            }
        }
        return UNCLASSIFIED;
    }

}
