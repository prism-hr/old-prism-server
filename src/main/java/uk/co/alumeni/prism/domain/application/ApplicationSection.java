package uk.co.alumeni.prism.domain.application;

import org.joda.time.DateTime;

import uk.co.alumeni.prism.domain.UniqueEntity;

public abstract class ApplicationSection implements UniqueEntity {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract Application getAssociation();

    public abstract void setAssociation(Application association);

    public abstract DateTime getLastUpdatedTimestamp();

    public abstract void setLastUpdatedTimestamp(DateTime lastUpdatedTimestamp);

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("association", getAssociation());
    }

}
