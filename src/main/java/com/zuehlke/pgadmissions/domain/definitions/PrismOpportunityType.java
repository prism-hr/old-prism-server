package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.EXPERIENCE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.FUNDING;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.LEARNING;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.STUDY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.WORK;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOppportunityTypeVisibility.EXTERNAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOppportunityTypeVisibility.INTERNAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType.IMMEDIATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType.SCHEDULED;
import static org.joda.time.DateTimeConstants.MONDAY;
import static org.joda.time.DateTimeConstants.SEPTEMBER;

import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType;
import com.zuehlke.pgadmissions.dto.DefaultStartDateDTO;

public enum PrismOpportunityType {

    STUDY_UNDERGRADUATE(STUDY, 36, 48, EXTERNAL, SCHEDULED, SEPTEMBER, 4, MONDAY, 4, 3, new String[] {}), //
    STUDY_POSTGRADUATE_TAUGHT(STUDY, 12, 24, EXTERNAL, SCHEDULED, SEPTEMBER, 4, MONDAY, 4, 3, new String[] {}), //
    STUDY_POSTGRADUATE_RESEARCH(STUDY, 12, 48, EXTERNAL, SCHEDULED, SEPTEMBER, 4, MONDAY, 4, 3, new String[] {
            "mres", "md(res)", "research degree", "engineering doctorate", "dpa" }), //
    SCHOLARSHIP_UNDERGRADUATE(FUNDING, 36, 48, INTERNAL, SCHEDULED, SEPTEMBER, 4, MONDAY, 4, 1, new String[] {}), //
    SCHOLARSHIP_POSTGRADUATE_TAUGHT(FUNDING, 12, 24, INTERNAL, SCHEDULED, SEPTEMBER, 4, MONDAY, 4, 3, new String[] {}), //
    SCHOLARSHIP_POSTGRADUATE_RESEARCH(FUNDING, 12, 48, INTERNAL, SCHEDULED, SEPTEMBER, 4, MONDAY, 4, 3, new String[] {}), //
    WORK_EXPERIENCE(EXPERIENCE, null, null, EXTERNAL, IMMEDIATE, null, null, MONDAY, 4, 1, new String[] {}), //
    EMPLOYMENT(WORK, null, null, EXTERNAL, IMMEDIATE, null, null, MONDAY, 4, 1, new String[] {}), //
    EMPLOYMENT_SECONDMENT(WORK, null, null, INTERNAL, IMMEDIATE, null, null, MONDAY, 4, 1, new String[] { "visiting research" }), //
    TRAINING(LEARNING, null, null, INTERNAL, IMMEDIATE, null, null, MONDAY, 4, 1, new String[] {}); //

    private PrismOpportunityCategory programCategory;

    private Integer defaultMinimumDurationMonth;

    private Integer defaultMaximumDurationMonth;

    private PrismOppportunityTypeVisibility defaultVisibility;

    private PrismProgramStartType defaultStartType;

    private Integer defaultStartMonth;

    private Integer defaultStartWeek;

    private Integer defaultStartDay;

    private Integer defaultStartDelay;

    private Integer defaultStartBuffer;

    private String[] prefixes;

    private static final HashMultimap<PrismOpportunityType, PrismOpportunityType> relations = HashMultimap.create();

    private static final HashMultimap<PrismOpportunityType, PrismOpportunityTypeRecommendation> recommendations = HashMultimap.create();

    private static final LinkedListMultimap<PrismOpportunityCategory, PrismOpportunityType> byCategory = LinkedListMultimap.create();

    private static final List<String> stringValues = Lists.newArrayList();

    static {
        relations.put(STUDY_UNDERGRADUATE, SCHOLARSHIP_UNDERGRADUATE);
        relations.put(STUDY_POSTGRADUATE_TAUGHT, SCHOLARSHIP_POSTGRADUATE_TAUGHT);
        relations.put(STUDY_POSTGRADUATE_RESEARCH, SCHOLARSHIP_POSTGRADUATE_RESEARCH);

        recommendations.put(STUDY_UNDERGRADUATE, new PrismOpportunityTypeRecommendation(SCHOLARSHIP_UNDERGRADUATE, 1, PrismDurationUnit.WEEK,
                PrismOpportunityTypeRecommendationBaselineType.FROM_OFFER));
        recommendations.put(STUDY_UNDERGRADUATE, new PrismOpportunityTypeRecommendation(TRAINING, 3, PrismDurationUnit.MONTH,
                PrismOpportunityTypeRecommendationBaselineType.FROM_START));
        recommendations.put(STUDY_UNDERGRADUATE, new PrismOpportunityTypeRecommendation(WORK_EXPERIENCE, 6, PrismDurationUnit.MONTH,
                PrismOpportunityTypeRecommendationBaselineType.FROM_START));
        recommendations.put(STUDY_UNDERGRADUATE, new PrismOpportunityTypeRecommendation(STUDY_POSTGRADUATE_TAUGHT, 1, PrismDurationUnit.YEAR,
                PrismOpportunityTypeRecommendationBaselineType.FROM_CLOSE));
        recommendations.put(STUDY_UNDERGRADUATE, new PrismOpportunityTypeRecommendation(STUDY_POSTGRADUATE_RESEARCH, 1, PrismDurationUnit.YEAR,
                PrismOpportunityTypeRecommendationBaselineType.FROM_CLOSE));
        recommendations.put(STUDY_UNDERGRADUATE, new PrismOpportunityTypeRecommendation(EMPLOYMENT, 1, PrismDurationUnit.YEAR,
                PrismOpportunityTypeRecommendationBaselineType.FROM_CLOSE));

        recommendations.put(STUDY_POSTGRADUATE_TAUGHT, new PrismOpportunityTypeRecommendation(SCHOLARSHIP_POSTGRADUATE_TAUGHT, 1, PrismDurationUnit.WEEK,
                PrismOpportunityTypeRecommendationBaselineType.FROM_OFFER));
        recommendations.put(STUDY_POSTGRADUATE_TAUGHT, new PrismOpportunityTypeRecommendation(TRAINING, 3, PrismDurationUnit.MONTH,
                PrismOpportunityTypeRecommendationBaselineType.FROM_START));
        recommendations.put(STUDY_POSTGRADUATE_TAUGHT, new PrismOpportunityTypeRecommendation(WORK_EXPERIENCE, 3, PrismDurationUnit.MONTH,
                PrismOpportunityTypeRecommendationBaselineType.FROM_START));
        recommendations.put(STUDY_POSTGRADUATE_TAUGHT, new PrismOpportunityTypeRecommendation(STUDY_POSTGRADUATE_RESEARCH, 1, PrismDurationUnit.YEAR,
                PrismOpportunityTypeRecommendationBaselineType.FROM_CLOSE));
        recommendations.put(STUDY_POSTGRADUATE_TAUGHT, new PrismOpportunityTypeRecommendation(EMPLOYMENT, 1, PrismDurationUnit.YEAR,
                PrismOpportunityTypeRecommendationBaselineType.FROM_CLOSE));

        recommendations.put(STUDY_POSTGRADUATE_RESEARCH, new PrismOpportunityTypeRecommendation(SCHOLARSHIP_POSTGRADUATE_RESEARCH, 1, PrismDurationUnit.WEEK,
                PrismOpportunityTypeRecommendationBaselineType.FROM_OFFER));
        recommendations.put(STUDY_POSTGRADUATE_RESEARCH, new PrismOpportunityTypeRecommendation(TRAINING, 3, PrismDurationUnit.MONTH,
                PrismOpportunityTypeRecommendationBaselineType.FROM_START));
        recommendations.put(STUDY_POSTGRADUATE_RESEARCH, new PrismOpportunityTypeRecommendation(WORK_EXPERIENCE, 3, PrismDurationUnit.MONTH,
                PrismOpportunityTypeRecommendationBaselineType.FROM_START));
        recommendations.put(STUDY_POSTGRADUATE_RESEARCH, new PrismOpportunityTypeRecommendation(EMPLOYMENT, 1, PrismDurationUnit.YEAR,
                PrismOpportunityTypeRecommendationBaselineType.FROM_CLOSE));

        recommendations.put(EMPLOYMENT, new PrismOpportunityTypeRecommendation(TRAINING, 3, PrismDurationUnit.MONTH,
                PrismOpportunityTypeRecommendationBaselineType.FROM_START));
        recommendations.put(EMPLOYMENT, new PrismOpportunityTypeRecommendation(STUDY_UNDERGRADUATE, 1, PrismDurationUnit.YEAR,
                PrismOpportunityTypeRecommendationBaselineType.FROM_START));
        recommendations.put(EMPLOYMENT, new PrismOpportunityTypeRecommendation(STUDY_POSTGRADUATE_TAUGHT, 1, PrismDurationUnit.YEAR,
                PrismOpportunityTypeRecommendationBaselineType.FROM_START));
        recommendations.put(EMPLOYMENT, new PrismOpportunityTypeRecommendation(STUDY_POSTGRADUATE_RESEARCH, 1, PrismDurationUnit.YEAR,
                PrismOpportunityTypeRecommendationBaselineType.FROM_START));
        recommendations.put(EMPLOYMENT, new PrismOpportunityTypeRecommendation(EMPLOYMENT_SECONDMENT, 1, PrismDurationUnit.YEAR,
                PrismOpportunityTypeRecommendationBaselineType.FROM_START));
        recommendations.put(EMPLOYMENT, new PrismOpportunityTypeRecommendation(EMPLOYMENT, 1, PrismDurationUnit.YEAR,
                PrismOpportunityTypeRecommendationBaselineType.FROM_START));

        for (PrismOpportunityType opportunityType : PrismOpportunityType.values()) {
            stringValues.add(opportunityType.name());
            byCategory.put(opportunityType.getProgramCategory(), opportunityType);
        }
    }

    PrismOpportunityType(PrismOpportunityCategory programClass, Integer defaultMinimumDurationMonth, Integer defaultMaximumDurationMonth,
            PrismOppportunityTypeVisibility defaultVisibility, PrismProgramStartType defaultStartType, Integer defaultStartMonth, Integer defaultStartWeek,
            Integer defaultStartDay, Integer defaultStartDelay, Integer defaultStartBuffer, String[] prefixes) {
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
        this.prefixes = prefixes;
    }

    public PrismOpportunityCategory getProgramCategory() {
        return programCategory;
    }

    public Integer getDefaultMinimumDurationMonth() {
        return defaultMinimumDurationMonth;
    }

    public Integer getDefaultMaximumDurationMonth() {
        return defaultMaximumDurationMonth;
    }

    public PrismOppportunityTypeVisibility getDefaultVisibility() {
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

    public static PrismOpportunityType findValueFromString(String toSearchIn) {
        if (stringValues.contains(toSearchIn)) {
            return PrismOpportunityType.valueOf(toSearchIn);
        }
        for (PrismOpportunityType value : PrismOpportunityType.values()) {
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

    public Set<PrismOpportunityType> getRelations() {
        return relations.get(this);
    }

    public Set<PrismOpportunityTypeRecommendation> getRecommendations() {
        return recommendations.get(this);
    }

    public static List<PrismOpportunityType> getOpportunityTypes(PrismOpportunityCategory programCategory) {
        return byCategory.get(programCategory);
    }

    public static PrismOpportunityType getSystemOpportunityType() {
        return STUDY_POSTGRADUATE_RESEARCH;
    }

}
