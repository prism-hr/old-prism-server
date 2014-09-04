package com.zuehlke.pgadmissions.domain.definitions;

import java.util.List;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramTypeConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramTypeImmediate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramTypeScheduled;

public enum PrismProgramType {

    UNDERGRADUATE_STUDY(new PrismProgramTypeScheduled().withStartMonth(DateTimeConstants.SEPTEMBER).withStartWeek(3).withStartDay(DateTimeConstants.MONDAY), //
            new String[] {}), //
    POSTGRADUATE_STUDY(new PrismProgramTypeScheduled().withStartMonth(DateTimeConstants.SEPTEMBER).withStartWeek(3).withStartDay(DateTimeConstants.MONDAY), //
            new String[] { "mres", "md(res)" }), //
    POSTGRADUATE_RESEARCH(new PrismProgramTypeScheduled().withStartMonth(DateTimeConstants.SEPTEMBER).withStartWeek(3).withStartDay(DateTimeConstants.MONDAY), //
            new String[] { "research degree", "engineering doctorate" }), //
    INTERNSHIP(new PrismProgramTypeImmediate().withStartDay(DateTimeConstants.MONDAY), new String[] {}), //
    SECONDMENT(new PrismProgramTypeImmediate().withStartDay(DateTimeConstants.MONDAY), new String[] { "visiting research" }), //
    EMPLOYMENT(new PrismProgramTypeImmediate().withStartDay(DateTimeConstants.MONDAY), new String[] {}), //
    CONTINUING_PROFESSIONAL_DEVELOPMENT(new PrismProgramTypeImmediate().withStartDay(DateTimeConstants.MONDAY), new String[] {}), //
    UNCLASSIFIED(new PrismProgramTypeImmediate().withStartDay(DateTimeConstants.MONDAY), new String[] {});

    private PrismProgramTypeConfiguration configuration;

    private String[] prefixes;
    
    private static final List<String> stringValues = Lists.newArrayList();

    static {
        for (PrismProgramType programType : PrismProgramType.values()) {
            if (programType != UNCLASSIFIED) {
                stringValues.add(programType.name());
            }
        }
    }
    
    private PrismProgramType(PrismProgramTypeConfiguration configuration, String[] prefixes) {
        this.configuration = configuration;
        this.prefixes = prefixes;
    }

    public static PrismProgramType findValueFromString(String toSearchIn) {
        if (stringValues.contains(toSearchIn)) {
            return PrismProgramType.valueOf(toSearchIn);
        }
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

    public LocalDate getImmediateStartDate() {
        return configuration.getImmediateStartDate();
    }
    
    public LocalDate getRecommendedStartDate() {
        return configuration.getRecommendedStartDate();
    }
    
}
