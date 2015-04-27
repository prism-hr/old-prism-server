package com.zuehlke.pgadmissions.domain.resource;

import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.advert.Advert;

public abstract class ResourceParent extends Resource {

    public abstract Advert getAdvert();

    public abstract void setAdvert(Advert advert);

    public abstract DateTime getUpdatedTimestampSitemap();

    public abstract void setUpdatedTimestampSitemap(DateTime updatedTimestampSitemap);
    
    public abstract LocalDate getEndDate();
    
    public abstract void setEndDate(LocalDate endDate);
    
    public abstract Set<ResourceStudyOption> getStudyOptions();
    
    public abstract Set<ResourceStudyLocation> getStudyLocations();
    
    public void addStudyOption(ResourceStudyOption studyOption) {
        getStudyOptions().add(studyOption);
    }
    
    public void addStudyLocation(ResourceStudyLocation studyLocation) {
        getStudyLocations().add(studyLocation);
    }

}
