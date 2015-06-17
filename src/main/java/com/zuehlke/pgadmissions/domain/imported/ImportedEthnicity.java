package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("ETHNICITY")
public class ImportedEthnicity extends ImportedEntitySimple {

    public ImportedEthnicity withName(String name) {
        setName(name);
        return this;
    }

    public ImportedEthnicity withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
