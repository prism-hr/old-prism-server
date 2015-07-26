package com.zuehlke.pgadmissions.domain.definitions;

import java.util.List;

import com.google.common.collect.Lists;

public enum PrismStudyOption implements PrismLocalizableDefinition {

    FULL_TIME("f+++++"),
    PART_TIME("p+++++"),
    MODULAR_FLEXIBLE("b+++++");

    private String[] externalCodes;

    private static final List<String> stringValues = Lists.newArrayList();

    static {
        for (PrismStudyOption studyOption : PrismStudyOption.values()) {
            stringValues.add(studyOption.name());
        }
    }

    PrismStudyOption(String... externalCodes) {
        this.externalCodes = externalCodes;
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

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_STUDY_OPTION_" + name());
    }

}
