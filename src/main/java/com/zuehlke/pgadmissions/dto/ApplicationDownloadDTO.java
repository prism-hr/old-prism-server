package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.Application;

public class ApplicationDownloadDTO {
    
    private Application application;

    private boolean includeEqualOpportunitiesData = false;
    
    private boolean includeReferences = false;

    private boolean includeAttachments = false;

    public final Application getApplication() {
        return application;
    }

    public final void setApplication(Application application) {
        this.application = application;
    }

    public final boolean isIncludeEqualOpportunitiesData() {
        return includeEqualOpportunitiesData;
    }

    public final void setIncludeEqualOpportunitiesData(boolean includeEqualOpportunitiesData) {
        this.includeEqualOpportunitiesData = includeEqualOpportunitiesData;
    }

    public final boolean isIncludeReferences() {
        return includeReferences;
    }

    public final void setIncludeReferences(boolean includeReferences) {
        this.includeReferences = includeReferences;
    }
    
    public final boolean isIncludeAttachments() {
        return includeAttachments;
    }

    public final void setIncludeAttachments(boolean includeAttachments) {
        this.includeAttachments = includeAttachments;
    }
    
    public ApplicationDownloadDTO withApplication(Application application) {
        this.application = application;
        return this;
    }
    
    public ApplicationDownloadDTO withIncludeEqualOpportuntiesData(boolean includeEqualOpportunitiesData) {
        this.includeEqualOpportunitiesData = includeEqualOpportunitiesData;
        return this;
    }
    
    public ApplicationDownloadDTO withIncludeReferences(boolean includeReferences) {
        this.includeReferences = includeReferences;
        return this;
    }
    
    public ApplicationDownloadDTO withIncludeAttachments(boolean includeAttachments) {
        this.includeAttachments = includeAttachments;
        return this;
    }
    
}
