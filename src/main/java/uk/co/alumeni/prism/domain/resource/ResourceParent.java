package uk.co.alumeni.prism.domain.resource;

import java.math.BigDecimal;
import java.util.Set;

import org.joda.time.DateTime;

import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.application.Application;

import uk.co.alumeni.prism.api.model.resource.ResourceParentDefinition;

public abstract class ResourceParent extends Resource implements ResourceParentDefinition<Advert> {

    public abstract String getImportedCode();

    public abstract void setImportedCode(String importedCode);

    public abstract String getAdvertIncompleteSection();

    public abstract void setAdvertIncompleteSection(String advertIncompleteSection);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract Integer getApplicationRatingCount();

    public abstract void setApplicationRatingCount(Integer applicationRatingCount);

    public abstract BigDecimal getApplicationRatingFrequency();

    public abstract void setApplicationRatingFrequency(BigDecimal applicationRatingFrequency);

    public abstract BigDecimal getApplicationRatingAverage();

    public abstract void setApplicationRatingAverage(BigDecimal applicationRatingAverage);

    public abstract DateTime getUpdatedTimestampSitemap();

    public abstract void setUpdatedTimestampSitemap(DateTime updatedTimestampSitemap);

    public abstract Set<Program> getPrograms();

    public abstract Set<Project> getProjects();

    public abstract Set<Application> getApplications();

    public abstract Set<Advert> getAdverts();

    public void addResourceCondition(ResourceCondition resourceCondition) {
        getResourceConditions().add(resourceCondition);
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("name", getName());
    }

}
