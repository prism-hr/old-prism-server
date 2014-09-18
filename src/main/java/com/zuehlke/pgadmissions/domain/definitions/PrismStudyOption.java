package com.zuehlke.pgadmissions.domain.definitions;

import java.util.List;

import com.google.common.collect.Lists;

public enum PrismStudyOption {

    FULL_TIME(new String[]{"F+++++"}),
    PART_TIME(new String[]{"P+++++"}),
    MODULAR_FLEXIBLE(new String[]{"B+++++"}),
    UNCLASSIFIED(new String[]{});
    
    private String[] externalCodes;
    
    private static final List<String> stringValues = Lists.newArrayList();
    
    static {
        for (PrismStudyOption studyOption : PrismStudyOption.values()) {
            if (studyOption != UNCLASSIFIED) {
                stringValues.add(studyOption.name());
            }
        }
    }
    
    private PrismStudyOption(String[] externalCodes) {
        this.externalCodes = externalCodes;
    }

    public static PrismStudyOption findValueFromString(String toSearchIn) {
        if (stringValues.contains(toSearchIn)) {
            return PrismStudyOption.valueOf(toSearchIn);
        }
        for (PrismStudyOption option : PrismStudyOption.values()) {
            if (option.externalCodes.length > 0) {
                toSearchIn = toSearchIn.trim().replaceAll("\\s+", " ").toLowerCase();
                for (String externalCode : option.externalCodes) {
                    if (toSearchIn.equals(externalCode)) {
                        return option;
                    }
                }
            }
        }
        return UNCLASSIFIED;
    }
    
    public final String[] getExternalCodes() {
        return externalCodes;
    }
    
}
