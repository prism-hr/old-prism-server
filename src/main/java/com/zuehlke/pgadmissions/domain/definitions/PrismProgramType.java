package com.zuehlke.pgadmissions.domain.definitions;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType;
import com.zuehlke.pgadmissions.dto.DefaultStartDateDTO;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Set;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.*;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramCategory.*;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramTypeVisibility.EXTERNAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramTypeVisibility.INTERNAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType.IMMEDIATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType.SCHEDULED;
import static org.joda.time.DateTimeConstants.MONDAY;
import static org.joda.time.DateTimeConstants.SEPTEMBER;

public enum PrismProgramType {

    STUDY_UNDERGRADUATE(STUDY, 36, 48, EXTERNAL, SCHEDULED, SEPTEMBER, 4, MONDAY, 4, 3, SYSTEM_PROGRAM_TYPE_STUDY_UNDERGRADUATE, new String[] {}), //
    STUDY_POSTGRADUATE_TAUGHT(STUDY, 12, 24, EXTERNAL, SCHEDULED, SEPTEMBER, 4, MONDAY, 4, 3, SYSTEM_PROGRAM_TYPE_STUDY_POSTGRADUATE_TAUGHT, new String[] {}), //
    STUDY_POSTGRADUATE_RESEARCH(STUDY, 12, 48, EXTERNAL, SCHEDULED, SEPTEMBER, 4, MONDAY, 4, 3, SYSTEM_PROGRAM_TYPE_STUDY_POSTGRADUATE_RESEARCH, new String[] {
            "mres", "md(res)", "research degree", "engineering doctorate", "dpa" }), //
    SCHOLARSHIP_UNDERGRADUATE(FUNDING, 36, 48, INTERNAL, SCHEDULED, SEPTEMBER, 4, MONDAY, 4, 1, SYSTEM_PROGRAM_TYPE_SCHOLARSHIP_UNDERGRADUATE, new String[] {}), //
    SCHOLARSHIP_POSTGRADUATE_TAUGHT(FUNDING, 12, 24, INTERNAL, SCHEDULED, SEPTEMBER, 4, MONDAY, 4, 3, SYSTEM_PROGRAM_TYPE_SCHOLARSHIP_POSTGRADUATE_TAUGHT,
            new String[] {}), //
    SCHOLARSHIP_POSTGRADUATE_RESEARCH(FUNDING, 12, 48, INTERNAL, SCHEDULED, SEPTEMBER, 4, MONDAY, 4, 3, SYSTEM_PROGRAM_TYPE_SCHOLARSHIP_POSTGRADUATE_RESEARCH,
            new String[] {}), //
    WORK_EXPERIENCE(EXPERIENCE, null, null, EXTERNAL, IMMEDIATE, null, null, MONDAY, 4, 1, SYSTEM_PROGRAM_TYPE_WORK_EXPERIENCE, new String[] {}), //
    EMPLOYMENT(WORK, null, null, EXTERNAL, IMMEDIATE, null, null, MONDAY, 4, 1, SYSTEM_PROGRAM_TYPE_EMPLOYMENT, new String[] {}), //
    EMPLOYMENT_SECONDMENT(WORK, null, null, INTERNAL, IMMEDIATE, null, null, MONDAY, 4, 1, SYSTEM_PROGRAM_TYPE_EMPLOYMENT_SECONDMENT,
            new String[] { "visiting research" }), //
    TRAINING(LEARNING, null, null, INTERNAL, IMMEDIATE, null, null, MONDAY, 4, 1, SYSTEM_PROGRAM_TYPE_TRAINING, new String[] {}); //

    private PrismProgramCategory programCategory;

    private Integer defaultMinimumDurationMonth;

    private Integer defaultMaximumDurationMonth;

    private PrismProgramTypeVisibility defaultVisibility;

    private PrismProgramStartType defaultStartType;

    private Integer defaultStartMonth;

    private Integer defaultStartWeek;

    private Integer defaultStartDay;

    private Integer defaultStartDelay;

    private Integer defaultStartBuffer;

    private PrismDisplayPropertyDefinition displayProperty;

    private String[] prefixes;

    private static final HashMultimap<PrismProgramType, PrismProgramType> relations = HashMultimap.create();

    private static final HashMultimap<PrismProgramType, PrismProgramTypeRecommendation> recommendations = HashMultimap.create();

    private static final LinkedListMultimap<PrismProgramCategory, PrismProgramType> programTypesByCategory = LinkedListMultimap.create();

    private static final List<String> stringValues = Lists.newArrayList();

    static {
        relations.put(STUDY_UNDERGRADUATE, SCHOLARSHIP_UNDERGRADUATE);
        relations.put(STUDY_POSTGRADUATE_TAUGHT, SCHOLARSHIP_POSTGRADUATE_TAUGHT);
        relations.put(STUDY_POSTGRADUATE_RESEARCH, SCHOLARSHIP_POSTGRADUATE_RESEARCH);

        recommendations.put(STUDY_UNDERGRADUATE, new PrismProgramTypeRecommendation(SCHOLARSHIP_UNDERGRADUATE, 1, PrismDurationUnit.WEEK,
                PrismProgramTypeRecommendationBaselineType.FROM_OFFER));
        recommendations.put(STUDY_UNDERGRADUATE, new PrismProgramTypeRecommendation(TRAINING, 3, PrismDurationUnit.MONTH,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(STUDY_UNDERGRADUATE, new PrismProgramTypeRecommendation(WORK_EXPERIENCE, 6, PrismDurationUnit.MONTH,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(STUDY_UNDERGRADUATE, new PrismProgramTypeRecommendation(STUDY_POSTGRADUATE_TAUGHT, 1, PrismDurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_CLOSE));
        recommendations.put(STUDY_UNDERGRADUATE, new PrismProgramTypeRecommendation(STUDY_POSTGRADUATE_RESEARCH, 1, PrismDurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_CLOSE));
        recommendations.put(STUDY_UNDERGRADUATE, new PrismProgramTypeRecommendation(EMPLOYMENT, 1, PrismDurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_CLOSE));

        recommendations.put(STUDY_POSTGRADUATE_TAUGHT, new PrismProgramTypeRecommendation(SCHOLARSHIP_POSTGRADUATE_TAUGHT, 1, PrismDurationUnit.WEEK,
                PrismProgramTypeRecommendationBaselineType.FROM_OFFER));
        recommendations.put(STUDY_POSTGRADUATE_TAUGHT, new PrismProgramTypeRecommendation(TRAINING, 3, PrismDurationUnit.MONTH,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(STUDY_POSTGRADUATE_TAUGHT, new PrismProgramTypeRecommendation(WORK_EXPERIENCE, 3, PrismDurationUnit.MONTH,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(STUDY_POSTGRADUATE_TAUGHT, new PrismProgramTypeRecommendation(STUDY_POSTGRADUATE_RESEARCH, 1, PrismDurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_CLOSE));
        recommendations.put(STUDY_POSTGRADUATE_TAUGHT, new PrismProgramTypeRecommendation(EMPLOYMENT, 1, PrismDurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_CLOSE));

        recommendations.put(STUDY_POSTGRADUATE_RESEARCH, new PrismProgramTypeRecommendation(SCHOLARSHIP_POSTGRADUATE_RESEARCH, 1, PrismDurationUnit.WEEK,
                PrismProgramTypeRecommendationBaselineType.FROM_OFFER));
        recommendations.put(STUDY_POSTGRADUATE_RESEARCH, new PrismProgramTypeRecommendation(TRAINING, 3, PrismDurationUnit.MONTH,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(STUDY_POSTGRADUATE_RESEARCH, new PrismProgramTypeRecommendation(WORK_EXPERIENCE, 3, PrismDurationUnit.MONTH,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(STUDY_POSTGRADUATE_RESEARCH, new PrismProgramTypeRecommendation(EMPLOYMENT, 1, PrismDurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_CLOSE));

        recommendations.put(EMPLOYMENT, new PrismProgramTypeRecommendation(TRAINING, 3, PrismDurationUnit.MONTH,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(EMPLOYMENT, new PrismProgramTypeRecommendation(STUDY_UNDERGRADUATE, 1, PrismDurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(EMPLOYMENT, new PrismProgramTypeRecommendation(STUDY_POSTGRADUATE_TAUGHT, 1, PrismDurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(EMPLOYMENT, new PrismProgramTypeRecommendation(STUDY_POSTGRADUATE_RESEARCH, 1, PrismDurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(EMPLOYMENT, new PrismProgramTypeRecommendation(EMPLOYMENT_SECONDMENT, 1, PrismDurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(EMPLOYMENT, new PrismProgramTypeRecommendation(EMPLOYMENT, 1, PrismDurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_START));

        for (PrismProgramType programType : PrismProgramType.values()) {
            stringValues.add(programType.name());
            programTypesByCategory.put(programType.getProgramCategory(), programType);
        }
    }

    PrismProgramType(PrismProgramCategory programClass, Integer defaultMinimumDurationMonth, Integer defaultMaximumDurationMonth,
            PrismProgramTypeVisibility defaultVisibility, PrismProgramStartType defaultStartType, Integer defaultStartMonth, Integer defaultStartWeek,
            Integer defaultStartDay, Integer defaultStartDelay, Integer defaultStartBuffer, PrismDisplayPropertyDefinition displayProperty, String[] prefixes) {
        this.programCategory = programClass;
        this.defaultMinimumDurationMonth = defaultMinimumDurationMonth;
        this.defaultMaximumDurationMonth = defaultMaximumDurationMonth;
        this.defaultVisibility = defaultVisibility;
        this.defaultStartType = defaultStartType;
        this.defaultStartMonth = defaultStartMonth;
        this.defaultStartWeek = defaultStartWeek;
        this.defaultStartDay = defaultStartDay;
        this.defaultStartDelay = defaultStartDelay;
        this.defaultStartBuffer = defaultStartBuffer;
        this.displayProperty = displayProperty;
        this.prefixes = prefixes;
    }

    public PrismProgramCategory getProgramCategory() {
        return programCategory;
    }

    public Integer getDefaultMinimumDurationMonth() {
        return defaultMinimumDurationMonth;
    }

    public Integer getDefaultMaximumDurationMonth() {
        return defaultMaximumDurationMonth;
    }

    public PrismProgramTypeVisibility getDefaultVisibility() {
        return defaultVisibility;
    }

    public PrismProgramStartType getDefaultStartType() {
        return defaultStartType;
    }

    public Integer getDefaultStartMonth() {
        return defaultStartMonth;
    }

    public Integer getDefaultStartWeek() {
        return defaultStartWeek;
    }

    public Integer getDefaultStartDay() {
        return defaultStartDay;
    }

    public Integer getDefaultStartDelay() {
        return defaultStartDelay;
    }

    public Integer getDefaultStartBuffer() {
        return defaultStartBuffer;
    }

    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return displayProperty;
    }

    public DefaultStartDateDTO getDefaultStartDate(LocalDate baseline) {
        LocalDate immediateInterim = baseline.plusWeeks(defaultStartDelay);
        LocalDate immediate = immediateInterim.withDayOfWeek(defaultStartDay);
        immediate = immediate.isBefore(immediateInterim) ? immediate.plusWeeks(1) : immediate;

        LocalDate scheduled = null;

        if (defaultStartType == SCHEDULED) {
            scheduled = baseline.withDayOfMonth(1).withMonthOfYear(defaultStartMonth).plusWeeks(defaultStartWeek).withDayOfWeek(defaultStartDay);
            scheduled = scheduled.isBefore(baseline) ? baseline.withDayOfMonth(1).plusYears(1).withMonthOfYear(defaultStartMonth).plusWeeks(defaultStartWeek)
                    .withDayOfWeek(defaultStartDay) : scheduled;
        }

        return new DefaultStartDateDTO().withImmediate(immediate).withScheduled(scheduled);
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
        return null;
    }

    public Set<PrismProgramType> getRelations() {
        return relations.get(this);
    }

    public Set<PrismProgramTypeRecommendation> getRecommendations() {
        return recommendations.get(this);
    }

    public static List<PrismProgramType> getProgramTypes(PrismProgramCategory programCategory) {
        return programTypesByCategory.get(programCategory);
    }

    public static PrismProgramType getSystemProgramType() {
        return STUDY_POSTGRADUATE_RESEARCH;
    }

}
