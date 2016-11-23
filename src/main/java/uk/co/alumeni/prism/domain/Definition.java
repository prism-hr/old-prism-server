package uk.co.alumeni.prism.domain;

import com.google.common.base.Objects;
import uk.co.alumeni.prism.domain.definitions.PrismLocalizableDefinition;

public abstract class Definition<T extends PrismLocalizableDefinition> implements UniqueEntity {

    public abstract T getId();

    public abstract void setId(T id);

    public abstract Integer getOrdinal();

    public abstract void setOrdinal(Integer ordinal);

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!getClass().equals(object.getClass())) {
            return false;
        }
        final Definition<T> other = (Definition<T>) object;
        return Objects.equal(getId(), other.getId());
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("id", getId());
    }

}
