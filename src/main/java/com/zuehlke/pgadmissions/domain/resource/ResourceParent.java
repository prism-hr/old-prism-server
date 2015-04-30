package com.zuehlke.pgadmissions.domain.resource;

import java.math.BigDecimal;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.department.Department;

public abstract class ResourceParent extends Resource {

    public abstract String getTitle();
    
    public abstract void setTitle(String title);
    
    public abstract Advert getAdvert();

    public abstract void setAdvert(Advert advert);

    public abstract DateTime getUpdatedTimestampSitemap();

    public abstract void setUpdatedTimestampSitemap(DateTime updatedTimestampSitemap);

    public abstract LocalDate getEndDate();

    public abstract void setEndDate(LocalDate endDate);

    public abstract Integer getApplicationRatingCount();

    public abstract void setApplicationRatingCount(Integer applicationRatingCount);

    public abstract BigDecimal getApplicationRatingFrequency();

    public abstract void setApplicationRatingFrequency(BigDecimal applicationRatingFrequency);

    public abstract BigDecimal getApplicationRatingAverage();

    public abstract void setApplicationRatingAverage(BigDecimal applicationRatingAverage);

    public abstract Set<ResourceStudyOption> getStudyOptions();

    public abstract Set<ResourceStudyLocation> getStudyLocations();

    public Department getDepartment() {
        return null;
    }

    public void addResourceCondition(ResourceCondition resourceCondition) {
        getResourceConditions().add(resourceCondition);
    }

    public void addStudyOption(ResourceStudyOption studyOption) {
        getStudyOptions().add(studyOption);
    }

    public void addStudyLocation(ResourceStudyLocation studyLocation) {
        getStudyLocations().add(studyLocation);
    }

}
