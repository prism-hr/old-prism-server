package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("GENDER")
public class ImportedGender extends ImportedEntitySimple {

    public ImportedGender withName(String name) {
        setName(name);
        return this;
    }

    public ImportedGender withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
