package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("DOMICILE")
public class ImportedDomicile extends ImportedEntitySimple {

    public ImportedDomicile withName(String name) {
        setName(name);
        return this;
    }

    public ImportedDomicile withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
