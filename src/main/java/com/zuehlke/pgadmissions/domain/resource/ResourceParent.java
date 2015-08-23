package com.zuehlke.pgadmissions.domain.resource;

import java.math.BigDecimal;
import java.util.Set;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.workflow.user.PrismUserReassignmentProcessor;

import uk.co.alumeni.prism.api.model.resource.ResourceParentDefinition;

public abstract class ResourceParent<T extends PrismUserReassignmentProcessor> extends Resource<T>implements ResourceParentDefinition<Advert> {

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getAdvertIncompleteSection();

    public abstract void setAdvertIncompleteSection(String advertIncompleteSection);

    public abstract DateTime getUpdatedTimestampSitemap();

    public abstract void setUpdatedTimestampSitemap(DateTime updatedTimestampSitemap);

    public abstract Integer getApplicationRatingCount();

    public abstract void setApplicationRatingCount(Integer applicationRatingCount);

    public abstract BigDecimal getApplicationRatingFrequency();

    public abstract void setApplicationRatingFrequency(BigDecimal applicationRatingFrequency);

    public abstract BigDecimal getApplicationRatingAverage();

    public abstract void setApplicationRatingAverage(BigDecimal applicationRatingAverage);

    public abstract BigDecimal getOpportunityRatingAverage();

    public abstract Integer getOpportunityRatingCount();

    public abstract void setOpportunityRatingCount(Integer applicationRatingCount);

    public abstract void setOpportunityRatingAverage(BigDecimal applicationRatingAverage);

    public abstract Set<Advert> getAdverts();

    public void addResourceCondition(ResourceCondition resourceCondition) {
        getResourceConditions().add(resourceCondition);
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("name", getName());
    }

}
