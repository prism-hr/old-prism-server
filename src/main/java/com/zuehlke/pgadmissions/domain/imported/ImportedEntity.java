package com.zuehlke.pgadmissions.domain.imported;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.institution.Institution;

public abstract class ImportedEntity implements IUniqueEntity {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract Institution getInstitution();

    public abstract void setInstitution(Institution institution);

    public abstract String getCode();

    public abstract void setCode(String code);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract Boolean isEnabled();

    public abstract void setEnabled(Boolean enabled);

    public PrismImportedEntity getType() {
        return null;
    }

    @Override
    public String toString() {
        return getId().toString() + "-" + getCode() + "-" + getName();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getInstitution(), getType(), getCode(), getName());
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
        return Objects.equal(getInstitution(), other.getInstitution()) && Objects.equal(getType(), other.getType())
                && Objects.equal(getCode(), other.getCode()) && Objects.equal(getName(), other.getName());
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("institution", getInstitution()).addProperty("importedEntityType", getType()).addProperty("code", getCode());
    }

}
