package com.zuehlke.pgadmissions.domain.definitions;

import java.util.List;

import com.google.common.collect.Lists;

public enum PrismPerformanceIndicator {

    ADVERT_COUNT(PrismPerformanceIndicatorGroup.ADVERT_COUNT, //
            "count(distinct application.advert_id) as advertCount"),
    SUBMITTED_APPLICATION_COUNT(PrismPerformanceIndicatorGroup.APPLICATION_COUNT, //
            "sum(if(application.submitted_timestamp is not null, 1, 0)) as submittedApplicationCount"),
    APPROVED_APPLICATION_COUNT(PrismPerformanceIndicatorGroup.APPLICATION_COUNT, //
            "sum(if(application.state_id like \"APPLICATION_APPROVED_%\", 1, 0)) as approvedApplicationCount"),
    REJECTED_APPLICATION_COUNT(PrismPerformanceIndicatorGroup.APPLICATION_COUNT, //
            "sum(if(application.state_id like \"APPLICATION_REJECTED_%\", 1, 0)) as rejectedApplicationCount"),
    WITHDRAWN_APPLICATION_COUNT(PrismPerformanceIndicatorGroup.APPLICATION_COUNT, //
            "sum(if(application.state_id like \"APPLICATION_WITHDRAWN%\", 1, 0)) as withdrawnApplicationCount"),
    SUBMITTED_APPLICATION_RATIO(PrismPerformanceIndicatorGroup.APPLICATION_RATIO,
            "round(sum(if(application.submitted_timestamp is not null, 1, 0)) / count(distinct application.advert_id), 2) as submittedApplicationRatio"),
    APPROVED_APPLICATION_RATIO(PrismPerformanceIndicatorGroup.APPLICATION_RATIO,
            "round(sum(if(application.state_id like \"APPLICATION_APPROVED_%\", 1, 0)) / count(distinct application.advert_id), 2) as approvedApplicationRatio"),
    REJECTED_APPLICATION_RATIO(PrismPerformanceIndicatorGroup.APPLICATION_RATIO,
            "round(sum(if(application.state_id like \"APPLICATION_REJECTED_%\", 1, 0)) / count(distinct application.advert_id), 2) as rejectedApplicationRatio"),
    WITHDRAWN_APPLICATION_RATIO(PrismPerformanceIndicatorGroup.APPLICATION_RATIO,
            "round(sum(if(application.state_id like \"APPLICATION_WITHDRAWN%\", 1, 0)) / count(distinct application.advert_id), 2) as withdrawnApplicationRatio"),
    AVERAGE_RATING(PrismPerformanceIndicatorGroup.AVERAGE_RATING, //
            "round(avg(application.application_rating_average), 2) as averageRating"),
    AVERAGE_PROCESSING_TIME(PrismPerformanceIndicatorGroup.AVERAGE_PROCESSING_TIME, //
            "round(avg(datediff(application.completion_date, date(application.submitted_timestamp))), 2) as averageProcessingTime");

    private PrismPerformanceIndicatorGroup group;

    private String columnExpression;

    public static List<String> columns = Lists.newArrayList();

    static {
        for (PrismPerformanceIndicator indicator : values()) {
            columns.add(indicator.getColumnExpression());
        }
    }

    PrismPerformanceIndicator(PrismPerformanceIndicatorGroup group, String columnExpression) {
        this.group = group;
        this.columnExpression = columnExpression;
    }

    public PrismPerformanceIndicatorGroup getGroup() {
        return group;
    }

    public String getColumnExpression() {
        return columnExpression;
    }

    public static List<String> getColumns() {
        return columns;
    }


    public enum PrismPerformanceIndicatorGroup {
        ADVERT_COUNT(true),
        APPLICATION_COUNT(true),
        APPLICATION_RATIO(false),
        AVERAGE_RATING(false),
        AVERAGE_PROCESSING_TIME(false);

        private boolean cumulative;

        PrismPerformanceIndicatorGroup(boolean cumulative) {
            this.cumulative = cumulative;
        }

        public boolean isCumulative() {
            return cumulative;
        }
    }
}
