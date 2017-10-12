package uk.co.alumeni.prism.domain;

import com.google.common.base.Objects;
import org.joda.time.DateTime;

import static com.google.common.base.Objects.equal;

public abstract class Tag implements UniqueEntity {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract Integer getAdoptedCount();

    public abstract void setAdoptedCount(Integer adoptedCount);

    public abstract DateTime getCreatedTimestamp();

    public abstract void setCreatedTimestamp(DateTime createdTimestamp);

    public abstract DateTime getUpdatedTimestamp();

    public abstract void setUpdatedTimestamp(DateTime updatedTimestamp);

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
        return equal(getName(), other.getName());
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("name", getName());
    }

}
