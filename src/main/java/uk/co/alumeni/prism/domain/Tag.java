package uk.co.alumeni.prism.domain;

import org.joda.time.DateTime;

import com.google.common.base.Objects;

public abstract class Tag implements UniqueEntity {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getDescription();

    public abstract Integer getAdoptedCount();

    public abstract void setAdoptedCount(Integer adoptedCount);

    public abstract DateTime getCreatedTimestamp();

    public abstract void setCreatedTimestamp(DateTime createdTimestamp);

    public abstract DateTime getUpdatedTimestamp();

    public abstract void setUpdatedTimestamp(DateTime updatedTimestamp);

    public abstract void setDescription(String description);

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        Tag other = (Tag) object;
        return Objects.equal(getName(), other.getName());
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("name", getName());
    }

}
