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
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_SPONSOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType.IMMEDIATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType.SCHEDULED;
import static org.joda.time.DateTimeConstants.MONDAY;
import static org.joda.time.DateTimeConstants.SEPTEMBER;

import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.DefaultStartDateDTO;

public enum PrismOpportunityType {

    STUDY_UNDERGRADUATE(STUDY, 36, 48, SCHEDULED, //
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_PROGRAM, false), //
                    new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(PrismStudyOption.values()), //
            SEPTEMBER, 4, MONDAY, 4, 3, new String[] {}), //
    STUDY_POSTGRADUATE_TAUGHT(STUDY, 12, 24, SCHEDULED, //
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_PROGRAM, false), //
                    new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(PrismStudyOption.values()), //
            SEPTEMBER, 4, MONDAY, 4, 3, new String[] {}), //
    STUDY_POSTGRADUATE_RESEARCH(STUDY, 12, 48, SCHEDULED, //
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_PROGRAM, false), //
                    new PrismResourceCondition(ACCEPT_PROJECT, false), //
                    new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(PrismStudyOption.values()), //
            SEPTEMBER, 4, MONDAY, 4, 3, new String[] {
                    "mres", "md(res)", "research degree", "engineering doctorate", "dpa" }), //
    SCHOLARSHIP_UNDERGRADUATE(FUNDING, 36, 48, SCHEDULED,
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(PrismStudyOption.values()), //
            SEPTEMBER, 4, MONDAY, 4, 1, new String[] {}), //
    SCHOLARSHIP_POSTGRADUATE_TAUGHT(FUNDING, 12, 24, SCHEDULED, //
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(PrismStudyOption.values()), //
            SEPTEMBER, 4, MONDAY, 4, 3, new String[] {}), //
    SCHOLARSHIP_POSTGRADUATE_RESEARCH(FUNDING, 12, 48, SCHEDULED, //
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(PrismStudyOption.values()), //
            SEPTEMBER, 4, MONDAY, 4, 3, new String[] {}), //
    WORK_EXPERIENCE(EXPERIENCE, null, null, IMMEDIATE, //
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_PROJECT, true), //
                    new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(FULL_TIME), //
            null, null, MONDAY, 4, 1, new String[] {}), //
    VOLUNTEERING(EXPERIENCE, null, null, IMMEDIATE, //
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_PROJECT, true), //
                    new PrismResourceCondition(ACCEPT_SPONSOR, true), //
                    new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(FULL_TIME), //
            null, null, MONDAY, 4, 1, new String[] {}), //
    EMPLOYMENT(WORK, null, null, IMMEDIATE, //
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_PROJECT, false), //
                    new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(FULL_TIME), //
            null, null, MONDAY, 4, 1, new String[] {}), //
    EMPLOYMENT_SECONDMENT(WORK, null, null, IMMEDIATE,
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_PROJECT, true), //
                    new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(FULL_TIME, PART_TIME), //
            null, null, MONDAY, 4, 1, new String[] { "visiting research" }), //
    TRAINING(LEARNING, null, null, IMMEDIATE,
            Lists.newArrayList(new PrismResourceCondition(ACCEPT_PROGRAM, false), //
                    new PrismResourceCondition(ACCEPT_APPLICATION, false)), //
            Lists.newArrayList(PrismStudyOption.values()), //
            null, null, MONDAY, 4, 1, new String[] {}); //

    private PrismOpportunityCategory programCategory;

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

    private String[] prefixes;

    private static final LinkedListMultimap<PrismOpportunityCategory, PrismOpportunityType> byCategory = LinkedListMultimap.create();

    private static final List<String> stringValues = Lists.newArrayList();

    static {
        for (PrismOpportunityType opportunityType : PrismOpportunityType.values()) {
            stringValues.add(opportunityType.name());
            byCategory.put(opportunityType.getProgramCategory(), opportunityType);
        }
    }

    private PrismOpportunityType(PrismOpportunityCategory programCategory, Integer defaultMinimumDurationMonth, Integer defaultMaximumDurationMonth,
            PrismProgramStartType defaultStartType, List<PrismResourceCondition> defaultResourceConditions, List<PrismStudyOption> defaultStudyOptions,
            Integer defaultStartMonth, Integer defaultStartWeek, Integer defaultStartDay, Integer defaultStartDelay, Integer defaultStartBuffer,
            String[] prefixes) {
        this.programCategory = programCategory;
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
