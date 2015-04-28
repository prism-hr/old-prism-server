package com.zuehlke.pgadmissions.dto;

import org.joda.time.DateTime;

public class SitemapEntryDTO {

    public Integer resourceId;
    
    public DateTime lastModifiedTimestamp;

    public final Integer getResourceId() {
        return resourceId;
    }

    public final void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public final DateTime getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }

    public final void setLastModifiedTimestamp(DateTime lastModifiedTimestamp) {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }
    
}
