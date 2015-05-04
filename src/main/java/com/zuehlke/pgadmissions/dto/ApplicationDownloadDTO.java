package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.ApplicationDownloadMode;

public class ApplicationDownloadDTO {
    
    private Application application;
    
    private ApplicationDownloadMode downloadMode;

    private boolean includeEqualOpportunities = false;
    
    private boolean includeAssessments = false;

    private boolean includeAttachments = false;

    public final Application getApplication() {
        return application;
    }

    public final void setApplication(Application application) {
        this.application = application;
    }

    public final ApplicationDownloadMode getDownloadMode() {
        return downloadMode;
    }

    public final void setDownloadMode(ApplicationDownloadMode downloadMode) {
        this.downloadMode = downloadMode;
    }

    public final boolean isIncludeEqualOpportunities() {
        return includeEqualOpportunities;
    }

    public final void setIncludeEqualOpportunities(boolean includeEqualOpportunitiesData) {
        this.includeEqualOpportunities = includeEqualOpportunitiesData;
    }

    public final boolean isIncludeAssessments() {
        return includeAssessments;
    }

    public final void setIncludeAssessments(boolean includeAssessments) {
        this.includeAssessments = includeAssessments;
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
    
    public ApplicationDownloadDTO withDownloadMode(ApplicationDownloadMode downloadMode) {
        this.downloadMode = downloadMode;
        return this;
    }
    
    public ApplicationDownloadDTO withIncludeEqualOpportunties(boolean includeEqualOpportunities) {
        this.includeEqualOpportunities = includeEqualOpportunities;
        return this;
    }
    
    public ApplicationDownloadDTO withIncludeAssessments(boolean includeAssessments) {
        this.includeAssessments = includeAssessments;
        return this;
    }
    
    public ApplicationDownloadDTO withIncludeAttachments(boolean includeAttachments) {
        this.includeAttachments = includeAttachments;
        return this;
    }
    
}
