package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_STUDY_OPTION_FULL_TIME;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_STUDY_OPTION_MODULAR_FLEXIBLE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_STUDY_OPTION_PART_TIME;

import java.util.List;

import com.google.common.collect.Lists;

public enum PrismStudyOption {

    FULL_TIME(SYSTEM_STUDY_OPTION_FULL_TIME, "f+++++"),
    PART_TIME(SYSTEM_STUDY_OPTION_PART_TIME, "p+++++"),
    MODULAR_FLEXIBLE(SYSTEM_STUDY_OPTION_MODULAR_FLEXIBLE, "b+++++");

    private PrismDisplayPropertyDefinition displayProperty;

    private String[] externalCodes;

    private static final List<String> stringValues = Lists.newArrayList();

    static {
        for (PrismStudyOption studyOption : PrismStudyOption.values()) {
            stringValues.add(studyOption.name());
        }
    }

    PrismStudyOption(PrismDisplayPropertyDefinition displayProperty, String... externalCodes) {
        this.displayProperty = displayProperty;
        this.externalCodes = externalCodes;
    }

    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return displayProperty;
    }

    public String[] getExternalCodes() {
        return externalCodes;
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
    
    public static PrismStudyOption getSystemStudyOption() {
        return FULL_TIME;
    }

}
