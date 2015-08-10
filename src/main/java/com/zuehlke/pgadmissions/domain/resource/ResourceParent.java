package com.zuehlke.pgadmissions.domain.resource;

import java.math.BigDecimal;
import java.util.Set;

import org.joda.time.DateTime;

import uk.co.alumeni.prism.api.model.resource.ResourceParentDefinition;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.workflow.user.PrismUserReassignmentProcessor;

public abstract class ResourceParent<T extends PrismUserReassignmentProcessor> extends Resource<T> implements ResourceParentDefinition<Advert> {

    public abstract String getName();

    public abstract void setName(String name);

    public abstract DateTime getUpdatedTimestampSitemap();

    public abstract void setUpdatedTimestampSitemap(DateTime updatedTimestampSitemap);

    public abstract Integer getApplicationRatingCount();

    public abstract void setApplicationRatingCount(Integer applicationRatingCount);

    public abstract BigDecimal getApplicationRatingFrequency();

    public abstract void setApplicationRatingFrequency(BigDecimal applicationRatingFrequency);

    public abstract BigDecimal getApplicationRatingAverage();

    public abstract void setApplicationRatingAverage(BigDecimal applicationRatingAverage);

    public abstract Set<Advert> getAdverts();

    public void addResourceCondition(ResourceCondition resourceCondition) {
        getResourceConditions().add(resourceCondition);
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("name", getName());
    }

}
