package com.zuehlke.pgadmissions.domain.definitions;

import java.util.List;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramTypeStartConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramTypeStartImmediate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramTypeStartScheduled;

public enum PrismProgramType {

    UNDERGRADUATE_STUDY(true, false, new PrismProgramTypeStartScheduled().withStartMonth(DateTimeConstants.SEPTEMBER).withStartWeek(3).withStartDay(DateTimeConstants.MONDAY), //
            new String[] {}), //
    POSTGRADUATE_STUDY(true, false, new PrismProgramTypeStartScheduled().withStartMonth(DateTimeConstants.SEPTEMBER).withStartWeek(3).withStartDay(DateTimeConstants.MONDAY), //
            new String[] { "mres", "md(res)" }), //
    POSTGRADUATE_RESEARCH(true, true, new PrismProgramTypeStartScheduled().withStartMonth(DateTimeConstants.SEPTEMBER).withStartWeek(3).withStartDay(DateTimeConstants.MONDAY), //
            new String[] { "research degree", "engineering doctorate" }), //
    CONTINUING_PROFESSIONAL_DEVELOPMENT(true, false, new PrismProgramTypeStartImmediate().withStartDay(DateTimeConstants.MONDAY), new String[] {}), //
    STUDY_SCHOLARSHIP(false, true, new PrismProgramTypeStartScheduled().withStartMonth(DateTimeConstants.SEPTEMBER).withStartWeek(3).withStartDay(DateTimeConstants.MONDAY),
            new String[] {}), //
    INTERNSHIP(false, true, new PrismProgramTypeStartImmediate().withStartDay(DateTimeConstants.MONDAY), new String[] {}), //
    SECONDMENT(false, true, new PrismProgramTypeStartImmediate().withStartDay(DateTimeConstants.MONDAY), new String[] { "visiting research" }), //
    EMPLOYMENT(false, true, new PrismProgramTypeStartImmediate().withStartDay(DateTimeConstants.MONDAY), new String[] {}), //
    UNCLASSIFIED(false, false, new PrismProgramTypeStartImmediate().withStartDay(DateTimeConstants.MONDAY), new String[] {});

    private boolean expectFee;
    
    private boolean expectPay;

    private PrismProgramTypeStartConfiguration startConfiguration;

    private String[] prefixes;

    private static final List<String> stringValues = Lists.newArrayList();

    static {
        for (PrismProgramType programType : PrismProgramType.values()) {
            if (programType != UNCLASSIFIED) {
                stringValues.add(programType.name());
            }
        }
    }

    private PrismProgramType(boolean expectFee, boolean expectPay, PrismProgramTypeStartConfiguration startConfiguration, String[] prefixes) {
        this.startConfiguration = startConfiguration;
        this.prefixes = prefixes;
    }

    public final boolean isExpectFee() {
        return expectFee;
    }

    public final void setExpectFee(boolean expectFee) {
        this.expectFee = expectFee;
    }

    public final boolean isExpectPay() {
        return expectPay;
    }

    public final void setExpectPay(boolean expectPay) {
        this.expectPay = expectPay;
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
        return startConfiguration.getImmediateStartDate();
    }

    public LocalDate getRecommendedStartDate() {
        return startConfiguration.getRecommendedStartDate();
    }

}
