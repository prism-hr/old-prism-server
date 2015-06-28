package com.zuehlke.pgadmissions.domain.resource;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.imported.ImportedOpportunityType;

public abstract class ResourceParent extends Resource {

    public abstract void setTitle(String title);

    public abstract DateTime getUpdatedTimestampSitemap();

    public abstract void setUpdatedTimestampSitemap(DateTime updatedTimestampSitemap);

    public abstract Integer getApplicationRatingCount();

    public abstract void setApplicationRatingCount(Integer applicationRatingCount);

    public abstract BigDecimal getApplicationRatingFrequency();

    public abstract void setApplicationRatingFrequency(BigDecimal applicationRatingFrequency);

    public abstract BigDecimal getApplicationRatingAverage();

    public abstract void setApplicationRatingAverage(BigDecimal applicationRatingAverage);

    public ImportedOpportunityType getOpportunityType() {
        return null;
    }

    public void addResourceCondition(ResourceCondition resourceCondition) {
        getResourceConditions().add(resourceCondition);
    }
    
    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("title", getTitle());
    }

}
