package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("QUALIFICATION_TYPE")
public class ImportedQualificationType extends ImportedEntitySimple {

    public ImportedQualificationType withName(String name) {
        setName(name);
        return this;
    }

    public ImportedQualificationType withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }
}
