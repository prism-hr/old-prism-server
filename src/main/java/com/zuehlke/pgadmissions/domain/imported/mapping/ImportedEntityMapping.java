package com.zuehlke.pgadmissions.domain.imported.mapping;

import org.joda.time.DateTime;

import uk.co.alumeni.prism.api.model.imported.ImportedEntityMappingDefinition;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.resource.Institution;

public abstract class ImportedEntityMapping<T extends ImportedEntity<?, ?>> implements UniqueEntity, ImportedEntityMappingDefinition {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract Institution getInstitution();

    public abstract void setInstitution(Institution institution);

    public abstract T getImportedEntity();

    @Override
    public abstract String getCode();

    @Override
    public abstract void setCode(String code);

    public abstract Boolean getEnabled();

    public abstract void setEnabled(Boolean enabled);

    public abstract DateTime getImportedTimestamp();

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("institution", getInstitution()).addProperty("code", getCode());
    }

}
