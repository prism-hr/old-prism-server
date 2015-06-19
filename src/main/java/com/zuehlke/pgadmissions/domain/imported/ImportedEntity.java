package com.zuehlke.pgadmissions.domain.imported;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

public abstract class ImportedEntity implements UniqueEntity {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract Boolean getEnabled();

    public abstract void setEnabled(Boolean enabled);

    public abstract PrismImportedEntity getType();

    @Override
    public String toString() {
        return getId().toString() + "-" + getType() + "-" + getName();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getType(), getName());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final ImportedEntity other = (ImportedEntity) object;
        return Objects.equal(getType(), other.getType()) && Objects.equal(getName(), other.getName());
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("name", getName());
    }

}
