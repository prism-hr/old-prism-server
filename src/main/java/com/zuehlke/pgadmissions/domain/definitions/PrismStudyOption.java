package com.zuehlke.pgadmissions.domain.definitions;

import java.util.List;

import com.google.common.collect.Lists;

public enum PrismStudyOption {

    FULL_TIME(new String[]{"f+++++"}),
    PART_TIME(new String[]{"p+++++"}),
    MODULAR_FLEXIBLE(new String[]{"b+++++"});
    
    private String[] externalCodes;
    
    private static final List<String> stringValues = Lists.newArrayList();
    
    static {
        for (PrismStudyOption studyOption : PrismStudyOption.values()) {
            stringValues.add(studyOption.name());
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
        return null;
    }
    
    public final String[] getExternalCodes() {
        return externalCodes;
    }
    
}