package com.zuehlke.pgadmissions.domain.resource;

import java.math.BigDecimal;
import java.util.Set;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.advert.Advert;

public abstract class ResourceParent extends Resource {
    
    public abstract Advert getAdvert();
    
    public abstract void setAdvert(Advert advert);
    
    public abstract Integer getApplicationCreatedCount();
    
    public abstract void setApplicationCreatedCount(Integer applicationCreatedCount);
    
    public abstract Integer getApplicationSubmittedCount();
    
    public abstract void setApplicationSubmittedCount(Integer applicationSubmittedCount);
    
    public abstract Integer getApplicationApprovedCount();
    
    public abstract void setApplicationApprovedCount(Integer applicationApprovedCount);
    
    public abstract Integer getApplicationRejectedCount();
    
    public abstract void setApplicationRejectedCount(Integer applicationRejectedCount);
    
    public abstract Integer getApplicationWithdrawnCount();
    
    public abstract void setApplicationWithdrawnCount(Integer applicationWithdrawnCount);
    
    public abstract Integer getApplicationRatingCount();
    
    public abstract void setApplicationRatingCount(Integer applicationRatingCount);
    
    public abstract BigDecimal getApplicationRatingCountAverageNonZero();
    
    public abstract void setApplicationRatingCountAverageNonZero(BigDecimal applicationRatingCountAverageNonZero);

    public abstract BigDecimal getApplicationRatingAverage();
    
    public abstract void setApplicationRatingAverage(BigDecimal applicationRatingAverage);
    
    public abstract DateTime getUpdatedTimestampSitemap();
    
    public abstract void setUpdatedTimestampSitemap(DateTime updatedTimestampSitemap);
    
    public abstract Set<Advert> getAdverts();

}
