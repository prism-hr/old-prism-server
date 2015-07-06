package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.EXPERIENCE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.FUNDING;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.LEARNING;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.STUDY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.WORK;
import static com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption.FULL_TIME;
import static com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption.PART_TIME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType.IMMEDIATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType.SCHEDULED;
import static org.joda.time.DateTimeConstants.MONDAY;
import static org.joda.time.DateTimeConstants.SEPTEMBER;

import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;

import uk.co.alumeni.prism.api.model.advert.EnumDefinition;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.DefaultStartDateDTO;

public enum PrismOpportunityType implements EnumDefinition<uk.co.alumeni.prism.enums.PrismOpportunityType> {

    STUDY_UNDERGRADUATE(STUDY, 36, 48, SCHEDULED, //
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_PROGRAM, false), //
                    new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(PrismStudyOption.values()), //
            SEPTEMBER, 4, MONDAY, 4, 3), //
    STUDY_POSTGRADUATE_TAUGHT(STUDY, 12, 24, SCHEDULED, //
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_PROGRAM, false), //
                    new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(PrismStudyOption.values()), //
            SEPTEMBER, 4, MONDAY, 4, 3), //
    STUDY_POSTGRADUATE_RESEARCH(STUDY, 12, 48, SCHEDULED, //
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_PROGRAM, false), //
                    new PrismResourceCondition(ACCEPT_PROJECT, false), //
                    new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(PrismStudyOption.values()), //
            SEPTEMBER, 4, MONDAY, 4, 3), //
    SCHOLARSHIP_UNDERGRADUATE(FUNDING, 36, 48, SCHEDULED,
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(PrismStudyOption.values()), //
            SEPTEMBER, 4, MONDAY, 4, 1), //
    SCHOLARSHIP_POSTGRADUATE_TAUGHT(FUNDING, 12, 24, SCHEDULED, //
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(PrismStudyOption.values()), //
            SEPTEMBER, 4, MONDAY, 4, 3), //
    SCHOLARSHIP_POSTGRADUATE_RESEARCH(FUNDING, 12, 48, SCHEDULED, //
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(PrismStudyOption.values()), //
            SEPTEMBER, 4, MONDAY, 4, 3), //
    WORK_EXPERIENCE(EXPERIENCE, null, null, IMMEDIATE, //
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_PROJECT, true), //
                    new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(FULL_TIME), //
            null, null, MONDAY, 4, 1), //
    VOLUNTEERING(EXPERIENCE, null, null, IMMEDIATE, //
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_PROJECT, true), //
                    new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(FULL_TIME), //
            null, null, MONDAY, 4, 1), //
    EMPLOYMENT(WORK, null, null, IMMEDIATE, //
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_PROJECT, false), //
                    new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(FULL_TIME), //
            null, null, MONDAY, 4, 1), //
    EMPLOYMENT_SECONDMENT(WORK, null, null, IMMEDIATE,
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_PROJECT, true), //
                    new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(FULL_TIME, PART_TIME), //
            null, null, MONDAY, 4, 1), //
    TRAINING(LEARNING, null, null, IMMEDIATE,
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_PROGRAM, false), //
                    new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(PrismStudyOption.values()), //
            null, null, MONDAY, 4, 1); //

    private PrismOpportunityCategory category;

    private Integer defaultMinimumDurationMonth;

    private Integer defaultMaximumDurationMonth;

    private PrismProgramStartType defaultStartType;

    private List<PrismResourceCondition> defaultResourceConditions;

    private List<PrismStudyOption> defaultStudyOptions;

    private Integer defaultStartMonth;

    private Integer defaultStartWeek;

    private Integer defaultStartDay;

    private Integer defaultStartDelay;

    private Integer defaultStartBuffer;

    private static final LinkedListMultimap<PrismOpportunityCategory, PrismOpportunityType> byCategory = LinkedListMultimap.create();

    static {
        for (PrismOpportunityType opportunityType : PrismOpportunityType.values()) {
            byCategory.put(opportunityType.getCategory(), opportunityType);
        }
    }

    private PrismOpportunityType(PrismOpportunityCategory category, Integer defaultMinimumDurationMonth, Integer defaultMaximumDurationMonth,
            PrismProgramStartType defaultStartType, List<PrismResourceCondition> defaultResourceConditions, List<PrismStudyOption> defaultStudyOptions,
            Integer defaultStartMonth, Integer defaultStartWeek, Integer defaultStartDay, Integer defaultStartDelay, Integer defaultStartBuffer) {
        this.category = category;
        this.defaultMinimumDurationMonth = defaultMinimumDurationMonth;
        this.defaultMaximumDurationMonth = defaultMaximumDurationMonth;
        this.defaultStartType = defaultStartType;
        this.defaultResourceConditions = defaultResourceConditions;
        this.defaultStudyOptions = defaultStudyOptions;
        this.defaultStartMonth = defaultStartMonth;
        this.defaultStartWeek = defaultStartWeek;
        this.defaultStartDay = defaultStartDay;
        this.defaultStartDelay = defaultStartDelay;
        this.defaultStartBuffer = defaultStartBuffer;
    }

    @Override
    public uk.co.alumeni.prism.enums.PrismOpportunityType getDefinition() {
        return uk.co.alumeni.prism.enums.PrismOpportunityType.valueOf(name());
    }

    public PrismOpportunityCategory getCategory() {
        return category;
    }

    public Integer getDefaultMinimumDurationMonth() {
        return defaultMinimumDurationMonth;
    }

    public Integer getDefaultMaximumDurationMonth() {
        return defaultMaximumDurationMonth;
    }

    public PrismProgramStartType getDefaultStartType() {
        return defaultStartType;
    }

    public List<PrismResourceCondition> getDefaultResourceConditions() {
        return defaultResourceConditions;
    }

    public List<PrismStudyOption> getDefaultStudyOptions() {
        return defaultStudyOptions;
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

    public static List<PrismOpportunityType> getOpportunityTypes(PrismOpportunityCategory programCategory) {
        return byCategory.get(programCategory);
    }

    public static PrismOpportunityType getSystemOpportunityType() {
        return STUDY_POSTGRADUATE_RESEARCH;
    }

    public static List<PrismResourceCondition> getResourceConditions(PrismScope scope) {
        Set<PrismResourceCondition> mergedResourceConditions = Sets.newHashSet();
        for (PrismOpportunityType opportunityType : values()) {
            List<PrismResourceCondition> resourceConditions = opportunityType.getDefaultResourceConditions();
            for (PrismResourceCondition resourceCondition : resourceConditions) {
                if (resourceCondition.getActionCondition().getValidScopes().contains(scope)) {
                    mergedResourceConditions.add(new PrismResourceCondition(resourceCondition.getActionCondition(), false));
                }
            }
        }
        return Lists.newArrayList(mergedResourceConditions);
    }

    public static List<PrismResourceCondition> getResourceConditions(PrismScope scope, PrismOpportunityType opportunityType) {
        Set<PrismResourceCondition> mergedResourceConditions = Sets.newHashSet();
        List<PrismResourceCondition> resourceConditions = opportunityType.getDefaultResourceConditions();
        for (PrismResourceCondition resourceCondition : resourceConditions) {
            if (resourceCondition.getActionCondition().getValidScopes().contains(scope)) {
                mergedResourceConditions.add(resourceCondition);
            }
        }
        return Lists.newArrayList(mergedResourceConditions);
    }

}
