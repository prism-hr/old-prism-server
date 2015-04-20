package com.zuehlke.pgadmissions.dto;

import org.joda.time.DateTime;

public class ProgramProjectDTO {

    private Long projectCount;
    
    private DateTime publishedTimestamp;

    public Long getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Long projectCount) {
        this.projectCount = projectCount;
    }

    public DateTime getPublishedTimestamp() {
        return publishedTimestamp;
    }

    public void setPublishedTimestamp(DateTime publishedTimestamp) {
        this.publishedTimestamp = publishedTimestamp;
    }
    
}
