package com.zuehlke.pgadmissions.domain.definitions;

import java.util.List;

import com.google.common.collect.Lists;

public enum PrismPerformanceIndicator {

    ADVERT_COUNT("count(distinct application.advert_id) as advertCount"),
    SUBMITTED_APPLICATION_COUNT("sum(if(application.submitted_timestamp is not null, 1, 0)) as submittedApplicationCount"),
    APPROVED_APPLICATION_COUNT("sum(if(application.state_id like \"APPLICATION_APPROVED_%\", 1, 0)) as approvedApplicationCount"),
    REJECTED_APPLICATION_COUNT("sum(if(application.state_id like \"APPLICATION_REJECTED_%\", 1, 0)) as rejectedApplicationCount"),
    WITHDRAWN_APPLICATION_COUNT("sum(if(application.state_id like \"APPLICATION_WITHDRAWN%\", 1, 0)) as withdrawnApplicationCount"),
    SUBMITTED_APPLICATION_RATIO(
            "round(sum(if(application.submitted_timestamp is not null, 1, 0)) / count(distinct application.advert_id), 2) as submittedApplicationRatio"),
    APPROVED_APPLICATION_RATIO(
            "round(sum(if(application.state_id like \"APPLICATION_APPROVED_%\", 1, 0)) / count(distinct application.advert_id), 2) as approvedApplicationRatio"),
    REJECTED_APPLICATION_RATIO(
            "round(sum(if(application.state_id like \"APPLICATION_REJECTED_%\", 1, 0)) / count(distinct application.advert_id), 2) as rejectedApplicationRatio"),
    WITHDRAWN_APPLICATION_RATIO(
            "round(sum(if(application.state_id like \"APPLICATION_WITHDRAWN%\", 1, 0)) / count(distinct application.advert_id), 2) as withdrawnApplicationRatio"),
    AVERAGE_RATING("round(avg(application.application_rating_average), 2) as averageRating"),
    AVERAGE_PROCESSING_TIME("round(avg(datediff(application.completion_date, date(application.submitted_timestamp))), 2) as averageProcessingTime");

    private String columnExpression;

    public static List<String> columns = Lists.newArrayList();

    static {
        for (PrismPerformanceIndicator indicator : values()) {
            columns.add(indicator.getColumnExpression());
        }
    }

    PrismPerformanceIndicator(String columnExpression) {
        this.columnExpression = columnExpression;
    }

    public String getColumnExpression() {
        return columnExpression;
    }

    public static List<String> getColumns() {
        return columns;
    }

}
