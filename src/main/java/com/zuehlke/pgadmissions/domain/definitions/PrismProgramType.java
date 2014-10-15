package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.PROGRAM_TYPE_EMPLOYMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.PROGRAM_TYPE_EMPLOYMENT_SECONDMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.PROGRAM_TYPE_SCHOLARSHIP_POSTGRADUATE_RESEARCH;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.PROGRAM_TYPE_SCHOLARSHIP_POSTGRADUATE_TAUGHT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.PROGRAM_TYPE_SCHOLARSHIP_UNDERGRADUATE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.PROGRAM_TYPE_STUDY_POSTGRADUATE_RESEARCH;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.PROGRAM_TYPE_STUDY_POSTGRADUATE_TAUGHT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.PROGRAM_TYPE_STUDY_UNDERGRADUATE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.PROGRAM_TYPE_TRAINING;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.PROGRAM_TYPE_WORK_EXPERIENCE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramCategory.EXPERIENCE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramCategory.LEARNING;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramCategory.FUNDING;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramCategory.STUDY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramCategory.WORK;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramTypeVisibility.EXTERNAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramTypeVisibility.INTERNAL;
import static org.joda.time.DateTimeConstants.MONDAY;
import static org.joda.time.DateTimeConstants.SEPTEMBER;

import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartAbstract;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartImmediate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartScheduled;

public enum PrismProgramType {

    STUDY_UNDERGRADUATE(STUDY, 36, 48, EXTERNAL, new PrismProgramStartScheduled().withStartMonth(SEPTEMBER).withStartWeek(3).withStartDay(MONDAY),
            PROGRAM_TYPE_STUDY_UNDERGRADUATE, new String[] {}), //
    STUDY_POSTGRADUATE_TAUGHT(STUDY, 12, 24, EXTERNAL, new PrismProgramStartScheduled().withStartMonth(SEPTEMBER).withStartWeek(3).withStartDay(MONDAY),
            PROGRAM_TYPE_STUDY_POSTGRADUATE_TAUGHT, new String[] {}), //
    STUDY_POSTGRADUATE_RESEARCH(STUDY, 12, 48, EXTERNAL, new PrismProgramStartScheduled().withStartMonth(SEPTEMBER).withStartWeek(3).withStartDay(MONDAY),
            PROGRAM_TYPE_STUDY_POSTGRADUATE_RESEARCH, new String[] { "mres", "md(res)", "research degree", "engineering doctorate" }), //
    SCHOLARSHIP_UNDERGRADUATE(FUNDING, 36, 48, INTERNAL, new PrismProgramStartScheduled().withStartMonth(SEPTEMBER).withStartWeek(3).withStartDay(MONDAY),
            PROGRAM_TYPE_SCHOLARSHIP_UNDERGRADUATE, new String[] {}), //
    SCHOLARSHIP_POSTGRADUATE_TAUGHT(FUNDING, 12, 24, INTERNAL, new PrismProgramStartScheduled().withStartMonth(SEPTEMBER).withStartWeek(3)
            .withStartDay(MONDAY), PROGRAM_TYPE_SCHOLARSHIP_POSTGRADUATE_TAUGHT, new String[] {}), //
    SCHOLARSHIP_POSTGRADUATE_RESEARCH(FUNDING, 12, 48, INTERNAL, new PrismProgramStartScheduled().withStartMonth(SEPTEMBER).withStartWeek(3)
            .withStartDay(MONDAY), PROGRAM_TYPE_SCHOLARSHIP_POSTGRADUATE_RESEARCH, new String[] {}), //
    WORK_EXPERIENCE(EXPERIENCE, null, null, EXTERNAL, new PrismProgramStartImmediate().withStartDay(MONDAY), PROGRAM_TYPE_WORK_EXPERIENCE, new String[] {}), //
    EMPLOYMENT(WORK, null, null, EXTERNAL, new PrismProgramStartImmediate().withStartDay(MONDAY), PROGRAM_TYPE_EMPLOYMENT, new String[] {}), //
    EMPLOYMENT_SECONDMENT(WORK, null, null, INTERNAL, new PrismProgramStartImmediate().withStartDay(MONDAY), PROGRAM_TYPE_EMPLOYMENT_SECONDMENT,
            new String[] { "visiting research" }), //
    TRAINING(LEARNING, null, null, INTERNAL, new PrismProgramStartImmediate().withStartDay(MONDAY), PROGRAM_TYPE_TRAINING, new String[] {}); //

    private PrismProgramCategory programCategory;

    private Integer defaultMinimumDurationMonth;

    private Integer defaultMaximumDurationMonth;

    private PrismProgramTypeVisibility defaultVisibility;

    private PrismProgramStartAbstract programStart;

    private PrismDisplayProperty displayProperty;

    private String[] prefixes;

    private static final HashMultimap<PrismProgramType, PrismProgramType> relations = HashMultimap.create();

    private static final HashMultimap<PrismProgramType, PrismProgramTypeRecommendation> recommendations = HashMultimap.create();

    private static final List<String> stringValues = Lists.newArrayList();

    static {
        relations.put(STUDY_UNDERGRADUATE, SCHOLARSHIP_UNDERGRADUATE);
        relations.put(STUDY_POSTGRADUATE_TAUGHT, SCHOLARSHIP_POSTGRADUATE_TAUGHT);
        relations.put(STUDY_POSTGRADUATE_RESEARCH, SCHOLARSHIP_POSTGRADUATE_RESEARCH);

        recommendations.put(STUDY_UNDERGRADUATE, new PrismProgramTypeRecommendation(SCHOLARSHIP_UNDERGRADUATE, 1, DurationUnit.WEEK,
                PrismProgramTypeRecommendationBaselineType.FROM_OFFER));
        recommendations.put(STUDY_UNDERGRADUATE, new PrismProgramTypeRecommendation(TRAINING, 3, DurationUnit.MONTH,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(STUDY_UNDERGRADUATE, new PrismProgramTypeRecommendation(WORK_EXPERIENCE, 6, DurationUnit.MONTH,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(STUDY_UNDERGRADUATE, new PrismProgramTypeRecommendation(STUDY_POSTGRADUATE_TAUGHT, 1, DurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_CLOSE));
        recommendations.put(STUDY_UNDERGRADUATE, new PrismProgramTypeRecommendation(STUDY_POSTGRADUATE_RESEARCH, 1, DurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_CLOSE));
        recommendations.put(STUDY_UNDERGRADUATE, new PrismProgramTypeRecommendation(EMPLOYMENT, 1, DurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_CLOSE));

        recommendations.put(STUDY_POSTGRADUATE_TAUGHT, new PrismProgramTypeRecommendation(SCHOLARSHIP_POSTGRADUATE_TAUGHT, 1, DurationUnit.WEEK,
                PrismProgramTypeRecommendationBaselineType.FROM_OFFER));
        recommendations.put(STUDY_POSTGRADUATE_TAUGHT, new PrismProgramTypeRecommendation(TRAINING, 3, DurationUnit.MONTH,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(STUDY_POSTGRADUATE_TAUGHT, new PrismProgramTypeRecommendation(WORK_EXPERIENCE, 3, DurationUnit.MONTH,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(STUDY_POSTGRADUATE_TAUGHT, new PrismProgramTypeRecommendation(STUDY_POSTGRADUATE_RESEARCH, 1, DurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_CLOSE));
        recommendations.put(STUDY_POSTGRADUATE_TAUGHT, new PrismProgramTypeRecommendation(EMPLOYMENT, 1, DurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_CLOSE));

        recommendations.put(STUDY_POSTGRADUATE_RESEARCH, new PrismProgramTypeRecommendation(SCHOLARSHIP_POSTGRADUATE_RESEARCH, 1, DurationUnit.WEEK,
                PrismProgramTypeRecommendationBaselineType.FROM_OFFER));
        recommendations.put(STUDY_POSTGRADUATE_RESEARCH, new PrismProgramTypeRecommendation(TRAINING, 3, DurationUnit.MONTH,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(STUDY_POSTGRADUATE_RESEARCH, new PrismProgramTypeRecommendation(WORK_EXPERIENCE, 3, DurationUnit.MONTH,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(STUDY_POSTGRADUATE_RESEARCH, new PrismProgramTypeRecommendation(EMPLOYMENT, 1, DurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_CLOSE));

        recommendations.put(EMPLOYMENT, new PrismProgramTypeRecommendation(TRAINING, 3, DurationUnit.MONTH,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(EMPLOYMENT, new PrismProgramTypeRecommendation(STUDY_UNDERGRADUATE, 1, DurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(EMPLOYMENT, new PrismProgramTypeRecommendation(STUDY_POSTGRADUATE_TAUGHT, 1, DurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(EMPLOYMENT, new PrismProgramTypeRecommendation(STUDY_POSTGRADUATE_RESEARCH, 1, DurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(EMPLOYMENT, new PrismProgramTypeRecommendation(EMPLOYMENT_SECONDMENT, 1, DurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_START));
        recommendations.put(EMPLOYMENT, new PrismProgramTypeRecommendation(EMPLOYMENT, 1, DurationUnit.YEAR,
                PrismProgramTypeRecommendationBaselineType.FROM_START));

        for (PrismProgramType programType : PrismProgramType.values()) {
            stringValues.add(programType.name());
        }
    }

    private PrismProgramType(PrismProgramCategory programClass, Integer defaultMinimumDurationMonth, Integer defaultMaximumDurationMonth,
            PrismProgramTypeVisibility defaultVisibility, PrismProgramStartAbstract programStart, PrismDisplayProperty displayProperty, String[] prefixes) {
        this.programCategory = programClass;
        this.defaultMinimumDurationMonth = defaultMinimumDurationMonth;
        this.defaultMaximumDurationMonth = defaultMaximumDurationMonth;
        this.defaultVisibility = defaultVisibility;
        this.programStart = programStart;
        this.displayProperty = displayProperty;
        this.prefixes = prefixes;
    }

    public final PrismProgramCategory getProgramCategory() {
        return programCategory;
    }

    public final Integer getDefaultMinimumDurationMonth() {
        return defaultMinimumDurationMonth;
    }

    public final Integer getDefaultMaximumDurationMonth() {
        return defaultMaximumDurationMonth;
    }

    public final PrismProgramTypeVisibility getDefaultVisibility() {
        return defaultVisibility;
    }

    public LocalDate getImmediateStartDate() {
        return programStart.getImmediateStartDate();
    }

    public LocalDate getRecommendedStartDate() {
        return programStart.getRecommendedStartDate();
    }

    public final PrismDisplayProperty getDisplayProperty() {
        return displayProperty;
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

    public static Set<PrismProgramType> getRelations(PrismProgramType programType) {
        return relations.get(programType);
    }

    public static Set<PrismProgramTypeRecommendation> getRecommendations(PrismProgramType programType) {
        return recommendations.get(programType);
    }

}
