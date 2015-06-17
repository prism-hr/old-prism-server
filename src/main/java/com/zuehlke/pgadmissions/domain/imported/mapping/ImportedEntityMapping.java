package com.zuehlke.pgadmissions.domain.imported.mapping;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.institution.Institution;

public abstract class ImportedEntityMapping implements UniqueEntity {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract Institution getInstitution();

    public abstract void setInstitution(Institution institution);

    public abstract String getCode();

    public abstract void setCode(String code);

    public abstract Boolean getEnabled();

    public abstract void setEnabled(Boolean enabled);

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("institution", getInstitution()).addProperty("code", getCode());
    }

}
