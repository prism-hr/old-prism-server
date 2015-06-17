package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("DISABILITY")
public class ImportedDisability extends ImportedEntitySimple {

    public ImportedDisability withName(String name) {
        setName(name);
        return this;
    }

    public ImportedDisability withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
