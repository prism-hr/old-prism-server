package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("NATIONALITY")
public class ImportedNationality extends ImportedEntitySimple {

    public ImportedNationality withName(String name) {
        setName(name);
        return this;
    }

    public ImportedNationality withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
