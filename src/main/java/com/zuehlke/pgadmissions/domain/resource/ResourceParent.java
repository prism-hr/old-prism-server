package com.zuehlke.pgadmissions.domain.resource;

import java.math.BigDecimal;
import java.util.Set;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.imported.OpportunityType;

public abstract class ResourceParent extends Resource {

    public abstract String getTitle();

    public abstract void setTitle(String title);

    public abstract DateTime getUpdatedTimestampSitemap();

    public abstract void setUpdatedTimestampSitemap(DateTime updatedTimestampSitemap);

    public abstract Integer getApplicationRatingCount();

    public abstract void setApplicationRatingCount(Integer applicationRatingCount);

    public abstract BigDecimal getApplicationRatingFrequency();

    public abstract void setApplicationRatingFrequency(BigDecimal applicationRatingFrequency);

    public abstract BigDecimal getApplicationRatingAverage();

    public abstract void setApplicationRatingAverage(BigDecimal applicationRatingAverage);

    public abstract Set<ResourceStudyLocation> getStudyLocations();

    public OpportunityType getOpportunityType() {
        return null;
    }

    public Department getDepartment() {
        return null;
    }

    public void addResourceCondition(ResourceCondition resourceCondition) {
        getResourceConditions().add(resourceCondition);
    }

    public void addStudyLocation(ResourceStudyLocation studyLocation) {
        getStudyLocations().add(studyLocation);
    }

}
