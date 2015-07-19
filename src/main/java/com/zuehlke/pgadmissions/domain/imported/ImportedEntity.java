package com.zuehlke.pgadmissions.domain.imported;

import java.util.Set;

import uk.co.alumeni.prism.api.model.imported.ImportedEntityDefinition;
import uk.co.alumeni.prism.api.model.imported.ImportedEntityResponseDefinition;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedEntityMapping;

public abstract class ImportedEntity<T, U extends ImportedEntityMapping<?>> implements UniqueEntity, ImportedEntityDefinition,
        ImportedEntityResponseDefinition<T> {

    public abstract T getId();

    public abstract void setId(T id);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract Boolean getEnabled();

    public abstract void setEnabled(Boolean enabled);

    public abstract PrismImportedEntity getType();

    public abstract Set<U> getMappings();

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ImportedEntity<?, ?> other = (ImportedEntity<?, ?>) object;
        return Objects.equal(getName(), other.getName());
    }

    @Override
    public int index() {
        return Objects.hashCode(getName());
    }

    @Override
    public String toString() {
        return getId().toString() + "-" + getType() + "-" + getName();
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("name", getName());
    }

}
