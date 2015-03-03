package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROGRAM_STUDY_OPTION_FULL_TIME;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROGRAM_STUDY_OPTION_MODULAR_FLEXIBLE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROGRAM_STUDY_OPTION_PART_TIME;

import java.util.List;

import com.google.common.collect.Lists;

public enum PrismStudyOption {

    FULL_TIME(PROGRAM_STUDY_OPTION_FULL_TIME, new String[] { "f+++++" }), //
    PART_TIME(PROGRAM_STUDY_OPTION_PART_TIME, new String[] { "p+++++" }), //
    MODULAR_FLEXIBLE(PROGRAM_STUDY_OPTION_MODULAR_FLEXIBLE, new String[] { "b+++++" });

    private PrismDisplayPropertyDefinition displayProperty;

    private String[] externalCodes;

    private static final List<String> stringValues = Lists.newArrayList();

    static {
        for (PrismStudyOption studyOption : PrismStudyOption.values()) {
            stringValues.add(studyOption.name());
        }
    }

    private PrismStudyOption(PrismDisplayPropertyDefinition displayProperty, String[] externalCodes) {
        this.displayProperty = displayProperty;
        this.externalCodes = externalCodes;
    }

    public final PrismDisplayPropertyDefinition getDisplayProperty() {
        return displayProperty;
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
